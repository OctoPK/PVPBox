package fr.octopk.pvpbox.kit.listener;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.KitManager;
import fr.octopk.pvpbox.kit.type.KitMagicalArcher;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitMagicalArcherListener extends KitListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(KitManager.getInstance(PVPBox.getInstance()).getKit(player.getUniqueId()) instanceof KitMagicalArcher) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractWithBow(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(KitManager.getInstance(PVPBox.getInstance()).getKit(player.getUniqueId()) instanceof KitMagicalArcher) {
            KitMagicalArcher kit = (KitMagicalArcher) KitManager.getInstance(PVPBox.getInstance()).getKit(player.getUniqueId());
            ItemStack item = event.getItem();
            Action action = event.getAction();
            if (item != null && item.getType().equals(Material.BOW) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Magical Bow")) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    kit.nextMode();
                } else {
                    event.setCancelled(true);
                    if (System.currentTimeMillis() - kit.LAST_SHOOT >= kit.getBowMode().getCooldown() * 1000L/20) {
                        kit.shoot(player);
                        kit.LAST_SHOOT = System.currentTimeMillis();
                    }
                }
            }
        }
    }
}
