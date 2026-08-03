package fr.octopk.pvpbox.commands;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande pour retourné au spawn
 */
public class CommandSpawn implements CommandExecutor {

    PVPBox instance;

    public CommandSpawn(PVPBox plugin) {
        instance = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            Util.reset(p);

            p.sendMessage(PVPBox.getPrefix() + " §aTeleported to spawn !");

            return true;
        }

        return false;
    }
}
