package fr.octopk.pvpbox.utility;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import fr.octopk.pvpbox.kit.KitManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Util {

    /**
     * Méthode utilitaire pour réinitialisé le joueur à quand il spawn
     * @param p le joueur à réinitialisé
     */
    public static void reset(Player p) {
        Util.clearStuff(p);

        Util.resetState(p);
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

        ItemBuilder builder = new ItemBuilder(Material.COMPASS);
        builder.setName("§6Kit Selector §7- Clic Droit");
        p.getInventory().setItem(4, builder.toItem());

        PVPBox.playerStates.put(p.getUniqueId(), PlayerState.LOBBY);
    }
}
