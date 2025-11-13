package fr.octopk.pvpbox;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PVPBoxListener implements Listener {

    private final PVPBox pvpBox;

    public PVPBoxListener(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Double[] spawn = pvpBox.getConfig().getDoubleList("spawn").toArray(new Double[0]);
        p.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));

        e.setJoinMessage(pvpBox.getConfig().getString("join").replace("%player%", p.getName()).replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size())).replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(pvpBox.getConfig().getString("quit").replace("%player%", p.getName()).replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size())).replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }
}
