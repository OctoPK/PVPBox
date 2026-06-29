package fr.octopk.pvpbox.kit.listener;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.KitManager;
import fr.octopk.pvpbox.kit.type.KitExemple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitExempleListener extends KitListener {

    /**
     * L'event qui permet d'activé le pouvoir lorsque l'item est utilisé
     * @param event
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(KitManager.getInstance(PVPBox.getInstance()).getKit(player.getUniqueId()) instanceof KitExemple) {
            KitExemple kit = (KitExemple) KitManager.getInstance(PVPBox.getInstance()).getKit(player.getUniqueId());
            ItemStack item = event.getItem();
            Action action = event.getAction();
            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && item.getType().equals(Material.NETHER_STAR) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6strenght")) {
                //Lorsque c'est le bon item, on appel la méthode useAction de notre CountDownAction
                kit.getStrenghtAction().useAction(player);
            }
        }
    }
}
