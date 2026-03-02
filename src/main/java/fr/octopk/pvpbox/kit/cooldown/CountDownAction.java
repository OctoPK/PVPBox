package fr.octopk.pvpbox.kit.cooldown;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Classe qui contient un pouvoir avec un cooldown
 */
public class CountDownAction {
    //Le nom de l'action
    private String name;
    //L'action à faire à l'activation du pouvoir
    private Consumer<Player> action;
    //le cooldown restant du pouvoir
    private int actualCooldown;
    //Le cooldown originel du pouvoir
    private final int originalCooldown;

    public CountDownAction(String name, Consumer<Player> action, int cooldown) {
        this.actualCooldown = 0;
        this.action = action;
        this.name = name;
        this.originalCooldown = cooldown;
    }

    /**
     * Méthode qui réduit d'1seconde le cooldown
     */
    public void tickSecond() {
        if (actualCooldown > 0) {
            actualCooldown--;
        }
    }

    /**
     * La méthode qui est appelée lorsque le pouvoir est utilisé.
     * Lorsque le cooldown n'est pas prêt, un message d'erreur est envoyé dans le chat du joueur
     * @param p le joueur qui active le pouvoir
     */
    public void useAction(Player p) {
        if(actualCooldown == 0) {
            action.accept(p);
            actualCooldown = originalCooldown;
        } else {
            p.sendMessage(ChatColor.RED + "Votre pouvoir est en cooldown de "+ actualCooldown +"s restantes !");
        }
    }
}
