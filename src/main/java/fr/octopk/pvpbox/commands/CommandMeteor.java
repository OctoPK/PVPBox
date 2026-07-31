package fr.octopk.pvpbox.commands;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.meteoroid.MeteorShowerManager;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMeteor implements CommandExecutor {

    private final PVPBox pvpBox;

    public CommandMeteor(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MeteorShowerManager manager = MeteorShowerManager.getInstance();

        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "on":
                manager.start();
                sender.sendMessage(PVPBox.getPrefix() + " §aPluie de météores activée");
                return true;
            case "off":
                manager.stop();
                sender.sendMessage(PVPBox.getPrefix() + " §aPluie de météores désactivée");
                return true;
            case "status":
                sender.sendMessage(PVPBox.getPrefix() + " §aÉtat: " + (manager.isRunning() ? "§2actif" : "§cinactif")
                    + " §7| §améteores en vol: §2" + manager.getMeteorCount());
                return true;
            case "reload":
                this.pvpBox.reloadConfig();
                manager.reload();
                sender.sendMessage(PVPBox.getPrefix() + " §aConfiguration des météores rechargée");
                return true;
            case "summon":
                return summon(sender, args);
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    private boolean summon(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 2) {
            target = this.pvpBox.getServer().getPlayer(args[1]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un joueur: /meteor summon <joueur>");
            return true;
        }

        if (target == null) {
            sender.sendMessage(PVPBox.getPrefix() + " §cJoueur introuvable");
            return true;
        }

        MeteorShowerManager manager = MeteorShowerManager.getInstance();
        Location location = target.getLocation();

        if (manager.summonMeteor(location)) {
            sender.sendMessage(PVPBox.getPrefix() + " §aMétéore envoyé sur §2" + target.getName());
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cImpossible de faire apparaitre un météore ici");
        }
        return true;
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(PVPBox.getPrefix() + " §7/" + label + " §aon§7|§aoff§7|§astatus§7|§areload§7|§asummon [joueur]");
    }
}
