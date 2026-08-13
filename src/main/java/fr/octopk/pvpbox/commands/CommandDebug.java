package fr.octopk.pvpbox.commands;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CommandDebug implements CommandExecutor {

    private final PVPBox pvpBox;

    public CommandDebug(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "getstate":
                return getState(sender, args);
            /*case "seteffect":
                return setEffect(sender, args);
            case "setstate":
                return setState(sender, args);*/
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    private boolean getState(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 2) {
            target = this.pvpBox.getServer().getPlayer(args[1]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un joueur: /debug getstate <joueur>");
            return true;
        }

        if (target == null) {
            sender.sendMessage(PVPBox.getPrefix() + " §cJoueur introuvable");
            return true;
        }

        PlayerState state = PVPBox.playerStates.get(target.getUniqueId());

        sender.sendMessage(PVPBox.getPrefix() + " §a" + target.getName() + " §7est actuellement en état: §e" + state);
        return true;
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(PVPBox.getPrefix() + " §7/" + label + " §agetstate [joueur]§7|§aseteffect [joueur] [effect] [percentage]§7|§asetstate [joueur] <playing|lobby>");
    }
}
