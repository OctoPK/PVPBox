package fr.octopk.pvpbox.kit.listener;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.kit.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class KitListener implements KitListenerInterface {

    private static final double BASIC_FORCE_PERCENTAGE = 1.30;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player entity = (Player) e.getEntity();

            Kit kit = KitManager.getInstance(PVPBox.getInstance()).getKit(entity.getUniqueId());

            if (PVPBox.playerStates.get(entity.getUniqueId()) == PlayerState.LOBBY || (damager != null && PVPBox.playerStates.get(damager.getUniqueId()) == PlayerState.LOBBY)) {
                e.setCancelled(true);
                return;
            }

            double damage = e.getDamage();

            e.setDamage(damage * (1+BASIC_FORCE_PERCENTAGE * ((double) kit.getStrenghtPercentage() /100)) * (1 - (double) kit.getResistancePercentage() / 100));

            // System.out.println("[Listener damage] Player basic damage : " + damage + " strenght : " +  kit.getStrenghtPercentage() + " resistance : " + kit.getResistancePercentage() + " final damage : " + e.getDamage());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Kit kit = KitManager.getInstance(PVPBox.getInstance()).getKit(player.getUniqueId());

        if (kit != null && PVPBox.playerStates.get(player.getUniqueId()) != PlayerState.LOBBY) {
            float speed = player.getWalkSpeed();
            player.setWalkSpeed(0.2f * (1 + kit.getSpeedPercentage() / 100f));
        } else player.setWalkSpeed(0.2f);
    }
}
