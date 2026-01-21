package fr.octopk.pvpbox.commands;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn implements CommandExecutor {

    PVPBox instance;

    public CommandSpawn(PVPBox plugin) {
        instance = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            Double[] spawn = instance.getConfig().getDoubleList("location.lobby-spawn").toArray(new Double[0]);
            Location loc = new Location(Bukkit.getWorld("world"), spawn[0], spawn[1], spawn[2]);
            p.teleport(loc);

            Util.clear(p);

            p.sendMessage(PVPBox.getPrefix() + " §aTeleported to spawn !");

            return true;
        }

        return false;
    }
}
