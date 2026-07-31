package fr.octopk.pvpbox.meteoroid;

import fr.octopk.pvpbox.PVPBox;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class MeteorEntity {

    public static final String DEBRIS_METADATA_KEY = "pvpbox_meteor_debris";
    public static final String METEOR_ENTITY_NAME  = "PVPBoxMeteor";

    private static final double METEOR_SIZE     = 0.6;
    private static final double MAX_STEP_LENGTH = 0.25;

    private static final double HELMET_OFFSET             = 1.7;
    private static final int    METEOR_MAX_LIFETIME_TICKS = 1200;
    private static final int    PARTICLE_VIEW_DISTANCE    = 128;
    private static final double BLOCK_UPWARD_OFFSET_MIN   = 0.2;
    private static final double BLOCK_UPWARD_OFFSET_MAX   = 0.5;
    private static final float  HEAD_ROTATION_PER_TICK    = 16.0f;

    private final MeteorShowerManager manager;
    private final World               world;

    private ArmorStand stand;
    private Vector     position   = new Vector();
    private Vector     direction  = new Vector();
    private float      headYaw    = 0f;
    private int        aliveTicks = 0;
    private boolean    removed    = false;

    public MeteorEntity(MeteorShowerManager manager, World world) {
        this.manager = manager;
        this.world = world;
    }

    public void launch(Vector position, Vector direction) {
        this.position = position.clone();
        this.direction = direction.clone().normalize();

        this.stand = (ArmorStand) this.world.spawnEntity(toStandLocation(this.position), EntityType.ARMOR_STAND);
        this.stand.setVisible(false);
        this.stand.setGravity(false);
        this.stand.setMarker(true);
        this.stand.setBasePlate(false);
        this.stand.setArms(false);
        this.stand.setCanPickupItems(false);
        this.stand.setRemoveWhenFarAway(false);
        this.stand.setCustomName(METEOR_ENTITY_NAME);
        this.stand.setCustomNameVisible(false);
        this.stand.setHelmet(new ItemStack(this.manager.getMeteorMaterial()));
    }

    public void tick() {
        if (this.removed) {
            return;
        }

        if (this.stand == null || this.stand.isDead()) {
            remove();
            return;
        }

        this.aliveTicks++;
        spawnFlightParticles();

        Vector collision = moveMeteor();
        rotateHelmetSmoothly();

        if (collision != null) {
            explode(collision);
        } else if (isOutOfWorld() || this.aliveTicks > METEOR_MAX_LIFETIME_TICKS || !isChunkLoaded()) {
            remove();
        }
    }

    private void spawnFlightParticles() {
        Location location = toParticleLocation();
        playEffect(location, Effect.FLAME, 4, 0.2f, 0.2f, 0.2f, 0.02f);
        playEffect(location, Effect.SMALL_SMOKE, 4, 0.2f, 0.2f, 0.2f, 0.02f);
    }

    private Vector moveMeteor() {
        double speed = this.manager.getMeteorSpeed();
        int steps = Math.max(1, (int) Math.ceil(speed / MAX_STEP_LENGTH));
        Vector step = this.direction.clone().multiply(speed / steps);

        for (int i = 0; i < steps; i++) {
            this.position.add(step);

            Vector collision = getCollidingBlockPosition();
            if (collision != null) {
                teleportStand();
                return collision;
            }
        }

        teleportStand();
        return null;
    }

    private void teleportStand() {
        if (this.stand != null && this.stand.isValid()) {
            this.stand.teleport(toStandLocation(this.position));
        }
    }

    private void rotateHelmetSmoothly() {
        this.headYaw += HEAD_ROTATION_PER_TICK;
        if (this.stand != null && this.stand.isValid()) {
            this.stand.setHeadPose(new EulerAngle(0, Math.toRadians(this.headYaw), 0));
        }
    }

    public void explode(Vector originPos) {
        remove();
        if (this.manager.isExplosionEnabled()) {
            createExplosionCrater(originPos);
        }
    }

    public void remove() {
        if (this.removed) {
            return;
        }
        this.removed = true;

        if (this.stand != null && !this.stand.isDead()) {
            this.stand.remove();
        }
        this.manager.onMeteorRemoved(this);
    }

    public boolean isRemoved() {
        return this.removed;
    }

    private boolean isOutOfWorld() {
        return this.position.getY() < 0 || this.position.getY() > this.world.getMaxHeight() + 64;
    }

    private boolean isChunkLoaded() {
        return this.world.isChunkLoaded(this.position.getBlockX() >> 4, this.position.getBlockZ() >> 4);
    }

    private Vector getCollidingBlockPosition() {
        double half = METEOR_SIZE / 2;

        double epsilon = 1e-7;
        int minX = (int) Math.floor(this.position.getX() - half);
        int maxX = (int) Math.floor(this.position.getX() + half - epsilon);
        int minY = (int) Math.floor(this.position.getY() - half);
        int maxY = (int) Math.floor(this.position.getY() + half - epsilon);
        int minZ = (int) Math.floor(this.position.getZ() - half);
        int maxZ = (int) Math.floor(this.position.getZ() + half - epsilon);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (y < 0 || y >= this.world.getMaxHeight()) {
                    continue;
                }
                for (int z = minZ; z <= maxZ; z++) {
                    if (this.world.getBlockAt(x, y, z).getType().isSolid()) {
                        return new Vector(x, y, z);
                    }
                }
            }
        }

        return null;
    }

    private void createExplosionCrater(Vector originPos) {
        double x = originPos.getX();
        double y = originPos.getY();
        double z = originPos.getZ();

        int power = this.manager.getExplosionPower();
        this.world.createExplosion(x, y, z, power, false, false);

        boolean createFire = this.manager.isExplosionProducingFire();
        MeteorExplosion explosion = new MeteorExplosion(this.world, power, x, y, z, createFire);
        explosion.explode(block -> {
            if (block.isEmpty()) {
                return;
            }

            if (ThreadLocalRandom.current().nextInt(100) >= manager.getExplosionDestructionChance()) {
                return;
            }

            TrackedBlock trackedBlock = TrackedBlock.of(block);
            block.setType(Material.AIR);
            manager.trackDestroyedBlock(trackedBlock);

            Location location = block.getLocation();
            spawnFlyingBlock(
                new Vector(
                    location.getX() + 0.5,
                    location.getY() + 0.5,
                    location.getZ() + 0.5
                ), originPos, power, trackedBlock);
        });
    }

    @SuppressWarnings("deprecation")
    private void spawnFlyingBlock(Vector blockPos, Vector explosionPos, int power, TrackedBlock trackedBlock) {
        Vector velocityDir = blockPos.clone().subtract(explosionPos);
        if (velocityDir.lengthSquared() < 1.0E-8) {
            velocityDir = new Vector(0, 1, 0);
        } else {
            velocityDir.normalize();
        }

        double upward = BLOCK_UPWARD_OFFSET_MIN
            + ThreadLocalRandom.current().nextDouble() * (BLOCK_UPWARD_OFFSET_MAX - BLOCK_UPWARD_OFFSET_MIN);

        double strength = power / 3.9;
        Vector velocity = new Vector(
            velocityDir.getX() * strength,
            velocityDir.getY() * strength + upward,
            velocityDir.getZ() * strength
        );

        Material type = Material.getMaterial(trackedBlock.getType());
        if (type == null || type == Material.AIR) {
            return;
        }

        FallingBlock fallingBlock = this.world.spawnFallingBlock(
            new Location(this.world,
                blockPos.getX(),
                blockPos.getY() + 0.01,
                blockPos.getZ()),
            type,
            trackedBlock.getData());

        fallingBlock.setDropItem(false);
        fallingBlock.setVelocity(velocity);
        fallingBlock.setMetadata(DEBRIS_METADATA_KEY, new FixedMetadataValue(PVPBox.getInstance(), true));
    }

    private Location toStandLocation(Vector position) {
        return new Location(this.world, position.getX(), position.getY() - HELMET_OFFSET, position.getZ());
    }

    private Location toParticleLocation() {
        return new Location(this.world, this.position.getX(), this.position.getY(), this.position.getZ());
    }

    private void playEffect(Location location, Effect effect, int count, float offsetX, float offsetY, float offsetZ, float speed) {
        this.world.spigot().playEffect(location, effect, 0, 0, offsetX, offsetY, offsetZ, speed, count, PARTICLE_VIEW_DISTANCE);
    }
}
