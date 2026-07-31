package fr.octopk.pvpbox.meteoroid;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class MeteorShowerManager implements Listener {

    private static final int    SPAWN_TICK_INTERVAL       = 20;
    private static final int    MAX_PLAYER_GROUP_DISTANCE = 60;
    private static final double METEOR_SPREAD_Y_DEGREES   = 8;
    private static final double METEOR_SPREAD_X_DEGREES   = 10;
    private static final int    METEOR_MAX_ATTEMPTS       = 10;

    private static final Vector METEOR_DIRECTION = new Vector(1, -0.8, 1).normalize();

    private static MeteorShowerManager instance;

    private final PVPBox                pvpBox;
    private final Set<MeteorEntity>     meteorEntities  = new HashSet<>();
    private final Deque<PendingRestore> pendingRestores = new ArrayDeque<>();

    private BukkitTask task;
    private long       tickCounter = 0;

    private String   worldName;
    private Material meteorMaterial;
    private int      randomPositionRadius;
    private int      meteorSpawnAttemptPerInterval;
    private int      meteorSpawnDistance;
    private int      meteorSpawningChance;
    private double   meteorSpeed;
    private int      maxMeteors;
    private boolean  meteorExplosion;
    private int      meteorExplosionPower;
    private boolean  explosionProduceFire;
    private int      explosionDestructionChance;
    private int      blockRestoreDelay;
    private boolean  debrisPlaceBlocks;

    public MeteorShowerManager(PVPBox pvpBox) {
        instance = this;
        this.pvpBox = pvpBox;
        pvpBox.getServer().getPluginManager().registerEvents(this, pvpBox);
        reload();
    }

    public static MeteorShowerManager getInstance() {
        return instance;
    }

    public void reload() {
        ConfigurationSection section = this.pvpBox.getConfig().getConfigurationSection("meteor-shower");
        this.worldName = getString(section, "world", "world");
        this.meteorMaterial = parseMaterial(getString(section, "meteor-block", "NETHERRACK"));
        this.randomPositionRadius = getInt(section, "random-position-radius", 60);
        this.meteorSpawnAttemptPerInterval = getInt(section, "spawn-attempt-per-interval", 2);
        this.meteorSpawnDistance = getInt(section, "spawn-distance", 96);
        this.meteorSpawningChance = getInt(section, "spawning-chance", 10);
        this.meteorSpeed = getDouble(section, "meteor-speed", 1.0);
        this.maxMeteors = getInt(section, "max-meteors", 40);
        this.meteorExplosion = getBoolean(section, "explosion", true);
        this.meteorExplosionPower = getInt(section, "explosion-power", 4);
        this.explosionProduceFire = getBoolean(section, "explosion-produce-fire", false);
        this.explosionDestructionChance = getInt(section, "explosion-destruction-chance", 40);
        this.blockRestoreDelay = getInt(section, "block-restore-delay", 60);
        this.debrisPlaceBlocks = getBoolean(section, "debris-place-blocks", false);
        if (getBoolean(section, "enabled", true)) {
            start();
        } else {
            stop();
        }
    }

    public void start() {
        stop();
        removeLeftoverMeteors();
        this.tickCounter = 0;
        this.task = this.pvpBox.getServer().getScheduler().runTaskTimer(this.pvpBox, this::onTick, 1L, 1L);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        for (MeteorEntity meteor : new ArrayList<>(this.meteorEntities)) {
            meteor.remove();
        }
        this.meteorEntities.clear();
        while (!this.pendingRestores.isEmpty()) {
            this.pendingRestores.poll().block.restore();
        }
    }

    public boolean isRunning() {
        return this.task != null;
    }

    private void onTick() {
        this.tickCounter++;

        for (MeteorEntity meteor : new ArrayList<>(this.meteorEntities)) {
            try {
                meteor.tick();
            } catch (Throwable e) {
                this.pvpBox.getLogger().warning("Erreur pendant le tick d'un météore: " + e);
                meteor.remove();
            }
        }

        if (this.tickCounter % SPAWN_TICK_INTERVAL == 0) {
            try {
                spawnCycle();
            } catch (Throwable e) {
                this.pvpBox.getLogger().warning("Erreur pendant l'apparition des météores: " + e);
            }
        }

        restorePendingBlocks();
    }

    private void spawnCycle() {
        World world = getWorld();
        if (world == null) {
            return;
        }

        List<Player> players = getPlayingPlayers(world);
        if (players.isEmpty()) {
            return;
        }

        for (Location groupCenter : computePlayerGroups(players, world)) {
            summonMeteors(groupCenter);
        }
    }

    private void summonMeteors(Location location) {
        int attempts = this.meteorSpawnAttemptPerInterval;
        int chance = this.meteorSpawningChance;

        if (chance >= 100) {
            for (int i = 0; i < attempts; i++) {
                summonMeteor(location);
            }
        } else {
            Random random = ThreadLocalRandom.current();
            for (int i = 0; i < attempts; i++) {
                if (random.nextInt(100) < chance) {
                    summonMeteor(location);
                }
            }
        }
    }

    public boolean summonMeteor(Location location) {
        World world = location.getWorld();
        if (world == null || this.meteorEntities.size() >= this.maxMeteors) {
            return false;
        }

        for (int attempt = 0; attempt < METEOR_MAX_ATTEMPTS; attempt++) {
            Vector direction = getRandomDirectionVariance();
            Vector impactPos = getRandomImpactPosition(location);

            Vector spawnPos = impactPos.clone().add(direction.clone().multiply(-this.meteorSpawnDistance));
            if (isSafeSpawn(world, spawnPos)) {
                createMeteor(world).launch(spawnPos, direction);
                return true;
            }
        }

        return false;
    }

    private MeteorEntity createMeteor(World world) {
        MeteorEntity meteor = new MeteorEntity(this, world);
        this.meteorEntities.add(meteor);
        return meteor;
    }

    void onMeteorRemoved(MeteorEntity meteor) {
        this.meteorEntities.remove(meteor);
    }

    private Vector getRandomDirectionVariance() {
        Random random = ThreadLocalRandom.current();
        double yAngle = Math.toRadians(METEOR_SPREAD_Y_DEGREES);
        double xAngle = Math.toRadians(METEOR_SPREAD_X_DEGREES);

        Vector direction = METEOR_DIRECTION.clone();
        rotateAroundY(direction, (random.nextDouble() * 2.0 - 1.0) * yAngle);
        rotateAroundX(direction, (random.nextDouble() * 2.0 - 1.0) * xAngle);
        return direction.normalize();
    }

    private void rotateAroundY(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;
        vector.setX(x).setZ(z);
    }

    private void rotateAroundX(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = vector.getY() * cos - vector.getZ() * sin;
        double z = vector.getY() * sin + vector.getZ() * cos;
        vector.setY(y).setZ(z);
    }

    private Vector getRandomImpactPosition(Location location) {
        Random random = ThreadLocalRandom.current();

        double r = random.nextDouble() * this.randomPositionRadius;
        double a = random.nextDouble() * 2.0 * Math.PI;
        double dx = r * Math.cos(a);
        double dz = r * Math.sin(a);

        return new Vector(location.getX() + dx, location.getY(), location.getZ() + dz);
    }

    private boolean isSafeSpawn(World world, Vector position) {
        int cx = position.getBlockX();
        int cy = position.getBlockY();
        int cz = position.getBlockZ();

        if (cy < 0 || cy >= world.getMaxHeight()) {
            return false;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int x = cx + dx;
                    int y = cy + dy;
                    int z = cz + dz;
                    if (y < 0 || y >= world.getMaxHeight()) {
                        continue;
                    }
                    if (world.getBlockAt(x, y, z).getType().isSolid()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private List<Location> computePlayerGroups(List<Player> players, World world) {
        int maxDistanceSquared = MAX_PLAYER_GROUP_DISTANCE * MAX_PLAYER_GROUP_DISTANCE;
        List<Group> groupList = new ArrayList<>();

        for (Player player : players) {
            Location location = player.getLocation();

            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            Group chosen = null;
            for (Group group : groupList) {
                double dx = x - group.cx;
                double dy = y - group.cy;
                double dz = z - group.cz;
                if ((dx * dx + dy * dy + dz * dz) <= maxDistanceSquared) {
                    chosen = group;
                    break;
                }
            }

            if (chosen == null) {
                groupList.add(new Group(x, y, z));
            } else {
                chosen.add(x, y, z);
            }
        }

        List<Location> out = new ArrayList<>(groupList.size());
        for (Group group : groupList) {
            out.add(group.toLocation(world));
        }

        return out;
    }

    private List<Player> getPlayingPlayers(World world) {
        List<Player> players = new ArrayList<>();
        for (Player player : this.pvpBox.getServer().getOnlinePlayers()) {
            if (player.getWorld() != world) {
                continue;
            }
            if (PVPBox.playerStates.get(player.getUniqueId()) == PlayerState.PLAYING) {
                players.add(player);
            }
        }
        return players;
    }

    void trackDestroyedBlock(TrackedBlock block) {
        if (this.blockRestoreDelay <= 0) {
            return;
        }
        this.pendingRestores.add(new PendingRestore(block, this.tickCounter));
    }

    private void restorePendingBlocks() {
        if (this.blockRestoreDelay <= 0 || this.pendingRestores.isEmpty()) {
            return;
        }

        long delayTicks = this.blockRestoreDelay * 20L;

        while (!this.pendingRestores.isEmpty()) {
            PendingRestore pending = this.pendingRestores.peek();
            if (this.tickCounter - pending.createdAt < delayTicks) {
                break;
            }
            this.pendingRestores.poll().block.restore();
        }
    }

    @EventHandler
    public void onDebrisLand(EntityChangeBlockEvent event) {
        if (this.debrisPlaceBlocks) {
            return;
        }
        if (event.getEntity().hasMetadata(MeteorEntity.DEBRIS_METADATA_KEY)) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    private void removeLeftoverMeteors() {
        World world = getWorld();
        if (world == null) {
            return;
        }

        for (Entity entity : world.getEntities()) {
            if (entity instanceof ArmorStand
                && MeteorEntity.METEOR_ENTITY_NAME.equals(entity.getCustomName())) {
                entity.remove();
            }
        }
    }

    public World getWorld() {
        return this.pvpBox.getServer().getWorld(this.worldName);
    }

    public Material getMeteorMaterial() {
        return this.meteorMaterial;
    }

    public double getMeteorSpeed() {
        return this.meteorSpeed;
    }

    public boolean isExplosionEnabled() {
        return this.meteorExplosion;
    }

    public int getExplosionPower() {
        return this.meteorExplosionPower;
    }

    public boolean isExplosionProducingFire() {
        return this.explosionProduceFire;
    }

    public int getExplosionDestructionChance() {
        return this.explosionDestructionChance;
    }

    public int getMeteorCount() {
        return this.meteorEntities.size();
    }

    private Material parseMaterial(String name) {
        Material material = Material.matchMaterial(name);
        if (material == null || !material.isBlock()) {
            this.pvpBox.getLogger().warning("Matériau de météore invalide: " + name + ", NETHERRACK utilisé à la place");
            return Material.NETHERRACK;
        }
        return material;
    }

    private String getString(ConfigurationSection section, String path, String def) {
        return section == null ? def : section.getString(path, def);
    }

    private int getInt(ConfigurationSection section, String path, int def) {
        return section == null ? def : section.getInt(path, def);
    }

    private double getDouble(ConfigurationSection section, String path, double def) {
        return section == null ? def : section.getDouble(path, def);
    }

    private boolean getBoolean(ConfigurationSection section, String path, boolean def) {
        return section == null ? def : section.getBoolean(path, def);
    }

    private static final class Group {

        double cx;
        double cy;
        double cz;
        int    count;

        Group(double x, double y, double z) {
            this.cx = x;
            this.cy = y;
            this.cz = z;
            this.count = 1;
        }

        void add(double x, double y, double z) {
            int n = ++this.count;
            this.cx += (x - this.cx) / n;
            this.cy += (y - this.cy) / n;
            this.cz += (z - this.cz) / n;
        }

        Location toLocation(World world) {
            return new Location(world, this.cx, this.cy, this.cz);
        }
    }

    private static final class PendingRestore {

        final TrackedBlock block;
        final long         createdAt;

        PendingRestore(TrackedBlock block, long createdAt) {
            this.block = block;
            this.createdAt = createdAt;
        }
    }
}
