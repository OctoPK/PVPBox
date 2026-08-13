package fr.octopk.pvpbox.commands;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.manager.KitManager;
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
            case "seteffect":
                return setEffect(sender, args);
            case "setstate":
                return setState(sender, args);
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

    private boolean setEffect(CommandSender sender, String[] args) {
        Player target;

        if (args.length >= 2) {
            target = this.pvpBox.getServer().getPlayer(args[1]);
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un joueur: /debug seteffect <joueur> <effect> <percentage>");
            return true;
        }

        Kit playerKit = KitManager.getInstance(this.pvpBox).getKit(target.getUniqueId());

        if (target == null) {
            sender.sendMessage(PVPBox.getPrefix() + " §cJoueur introuvable");
            return true;
        } else if (playerKit == null) {
            sender.sendMessage(PVPBox.getPrefix() + " §cLe joueur n'a pas de kit");
            return true;
        }

        String effect;
        if (args.length >= 3) {
            effect = args[2];
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un effet: /debug seteffect <joueur> <effect:speed|strenght|resistance> <percentage>");
            return true;
        }

        int percentage;
        if (args.length >= 4) {
            percentage = Integer.parseInt(args[3]);
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un pourcentage: /debug seteffect <joueur> <effect> <percentage>");
            return true;
        }

        switch (effect.toLowerCase(Locale.ROOT)) {
            case "speed":
                playerKit.setSpeedPercentage(percentage);
                break;
            case "strenght":
                playerKit.setStrenghtPercentage(percentage);
                break;
            case "resistance":
                playerKit.setResistancePercentage(percentage);
                break;
            default:
                sender.sendMessage(PVPBox.getPrefix() + " §cEffet invalide: /debug seteffect <joueur> <effect:speed|strength|resistance> <percentage>");
                return true;
        }

        sender.sendMessage(PVPBox.getPrefix() + " §aEffet " + effect + " appliqué à " + target.getName() + " de " + percentage + "%");
        return true;
    }

    private boolean setState(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 2) {
            target = this.pvpBox.getServer().getPlayer(args[1]);
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un joueur: /debug setstate <joueur> <playing|lobby>");
            return true;
        }

        if (target == null) {
            sender.sendMessage(PVPBox.getPrefix() + " §cJoueur introuvable");
            return true;
        }

        String state;
        if (args.length >= 3) {
            state = args[2];
        } else {
            sender.sendMessage(PVPBox.getPrefix() + " §cIndique un état: /debug setstate <joueur> <playing|lobby>");
            return true;
        }

        switch (state.toLowerCase(Locale.ROOT)) {
            case "playing":
                PVPBox.playerStates.put(target.getUniqueId(), PlayerState.PLAYING);
                break;
            case "lobby":
                PVPBox.playerStates.put(target.getUniqueId(), PlayerState.LOBBY);
                break;
            default:
                sender.sendMessage(PVPBox.getPrefix() + " §cÉtat invalide: /debug setstate <joueur> <playing|lobby>");
                return true;
        }

        sender.sendMessage(PVPBox.getPrefix() + " §aÉtat " + state + " appliqué à " + target.getName());
        return true;
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(PVPBox.getPrefix() + " §7/" + label + " §agetstate [joueur]§7|§aseteffect [joueur] [effect] [percentage]§7|§asetstate [joueur] <playing|lobby>");
    }
}
