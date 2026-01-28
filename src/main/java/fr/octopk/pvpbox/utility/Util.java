package fr.octopk.pvpbox.utility;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Util {

    public static void clear(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setHeldItemSlot(4);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);

        ItemBuilder builder = new ItemBuilder(Material.COMPASS);
        builder.setName("§6Kit Selector §7- Clic Droit");
        p.getInventory().setItem(4, builder.toItem());

        PVPBox.playerStates.put(p.getUniqueId(), PlayerState.LOBBY);
    }
}
