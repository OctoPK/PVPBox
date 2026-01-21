package fr.octopk.pvpbox.listener;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.utility.GUI.GUIManager;
import fr.octopk.pvpbox.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PVPBoxListener implements Listener {

    private final PVPBox pvpBox;
    private final Double[] spawn;


    public PVPBoxListener(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
        this.spawn = this.pvpBox.getConfig().getDoubleList("location.lobby-spawn").toArray(new Double[0]);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Util.clear(p);

        p.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
        e.setJoinMessage(pvpBox.getConfig().getString("messages.join").replace("%player%", p.getName()).replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size())).replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(pvpBox.getConfig().getString("messages.quit").replace("%player%", e.getPlayer().getName()).replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size()-1)).replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }

    @EventHandler
    public void onKill(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player entity = (Player) e.getEntity();
            if (entity.getHealth() - e.getDamage() <= 0) {
                Util.clear(entity);
                entity.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
                Bukkit.broadcastMessage(pvpBox.getConfig().getString("messages.killed").replace("%killer", damager.getName()).replace("%dead%", entity.getName()));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        ItemStack item = e.getItem();

        if (item == null) return;

        if(item.getType().equals(Material.COMPASS) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Kit Selector §7- Clic Droit")) {
            GUIManager.openMenu(p, "§8Kit Selector Menu");
        }
    }

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(p.getHealth() - e.getDamage() <= 0) {
                Util.clear(p);
                p.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
                Bukkit.broadcastMessage(pvpBox.getConfig().getString("messages.death").replace("%dead%", p.getName()));
                e.setCancelled(true);
            }
        }
    }
}
