package fr.octopk.pvpbox.utility;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import fr.octopk.pvpbox.kit.KitManager;
import fr.octopk.pvpbox.kit.type.KitExemple;
import fr.octopk.pvpbox.kit.type.KitMagicalArcher;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Util {

    /**
     * Méthode utilitaire pour réinitialisé le joueur à quand il spawn
     * @param p le joueur à réinitialisé
     */
    public static void clear(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setHeldItemSlot(4);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);
        p.getActivePotionEffects().forEach(effect -> {
            p.removePotionEffect(effect.getType());
        });

        if (((KitMagicalArcher) KitManager.getKit("kitMagicalArcher")).containsPlayer(p.getUniqueId())) {
            ((KitMagicalArcher) KitManager.getKit("kitMagicalArcher")).removePlayer(p.getUniqueId());
        } else if (((KitExemple) KitManager.getKit("KitExemple")).containsPlayer(p.getUniqueId())) {
            ((KitExemple) KitManager.getKit("KitExemple")).removePlayer(p.getUniqueId());
        }
        ItemBuilder builder = new ItemBuilder(Material.COMPASS);
        builder.setName("§6Kit Selector §7- Clic Droit");
        p.getInventory().setItem(4, builder.toItem());

        PVPBox.playerStates.put(p.getUniqueId(), PlayerState.LOBBY);
    }
}
