package fr.octopk.pvpbox.kit.cooldown;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class CountDownAction {
    private String name;
    private Consumer<Player> action;
    private int actualCooldown;
    private final int originalCooldown;

    public CountDownAction(String name, Consumer<Player> action, int cooldown) {
        this.actualCooldown = 0;
        this.action = action;
        this.name = name;
        this.originalCooldown = cooldown;
    }

    public void tickSecond() {
        if (actualCooldown > 0) {
            actualCooldown--;
        }
    }

    public void useAction(Player p) {
        if(actualCooldown == 0) {
            action.accept(p);
            actualCooldown = originalCooldown;
        } else {
            p.sendMessage(ChatColor.RED + "Votre pouvoir est en cooldown de "+ actualCooldown +"s restantes !");
        }
    }
}
