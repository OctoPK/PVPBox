package fr.octopk.pvpbox.utility.GUI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * listener principale qui gère les interaction dans les inventaires
 */
public class GUIClickEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        GUIClick typeClick = null;

        if(e.getAction().equals(InventoryAction.PICKUP_ALL)) typeClick = GUIClick.LEFT_CLICK;
        else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) typeClick = GUIClick.RIGHT_CLICK;
        if(typeClick == null) return;

        ItemStack item = e.getCurrentItem();

        if(item == null || item.getType().equals(Material.AIR)) return;

        GUI gui = GUIManager.getMenus(e.getInventory().getName());

        if(gui == null) return;
        e.setCancelled(true);
        gui.handleClick(player, typeClick, item, e.getRawSlot());

    }
}
