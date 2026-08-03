package fr.octopk.pvpbox.utility;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import fr.octopk.pvpbox.manager.KitManager;
import fr.octopk.pvpbox.manager.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Util {

    /**
     * Méthode utilitaire pour réinitialisé le joueur à quand il spawn
     * @param p le joueur à réinitialisé
     */
    public static void reset(Player p) {
        clearStuff(p);

        resetState(p);

        teleportSpawn(p);
    }

    public static void clearStuff(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getActivePotionEffects().forEach(effect -> {
            p.removePotionEffect(effect.getType());
        });
    }

    public static void resetState(Player p) {
        p.getInventory().setHeldItemSlot(4);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);

        KitManager.getInstance(PVPBox.getInstance()).removePlayer(p.getUniqueId());

        TabManager.getInstance(PVPBox.getInstance()).clearEffect(p);

        ItemBuilder builder = new ItemBuilder(Material.COMPASS);
        builder.setName("§6Kit Selector §7- Clic Droit");
        p.getInventory().setItem(4, builder.toItem());

        PVPBox.playerStates.put(p.getUniqueId(), PlayerState.LOBBY);
    }

    public static Location getSpawn() {
        if(Bukkit.getWorld("lobby") == null) {
            Double[] spawn = PVPBox.getInstance().getConfig().getDoubleList("location.map-spawn").toArray(new Double[0]);
            return new Location(Bukkit.getWorld("world"), spawn[0], spawn[1], spawn[2]);
        }
        Double[] spawn = PVPBox.getInstance().getConfig().getDoubleList("location.lobby-spawn").toArray(new Double[0]);
        return new Location(Bukkit.getWorld("lobby"), spawn[0], spawn[1], spawn[2], -90.5F, 0.0F);
    }

    public static void teleportSpawn(Player p) {
        p.teleport(getSpawn());
    }
}
