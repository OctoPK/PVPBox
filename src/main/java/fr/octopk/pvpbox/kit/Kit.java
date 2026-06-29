package fr.octopk.pvpbox.kit;

import fr.octopk.pvpbox.PVPBox;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe abstraite pour les kit
 */
public abstract class Kit {
    //l'item a affiché dans notre menu de sélection de kit
    protected ItemStack icon;
    //le nom du kit
    protected String name;
    //référence à l'instance à la classe principal
    protected PVPBox pvpBox;
    //description à affiché dans le chat au moment de la sélection du kit
    protected List<String> description;
    //liste d'effet que contient le kit
    protected List<PotionEffect> effects;

    public Kit(ItemStack icon, String name,  PVPBox pvpBox, List<String> description, List<PotionEffect> effects) {
        this.icon = icon;
        this.name = name;
        this.pvpBox = pvpBox;
        this.description = description;
        this.effects = effects;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    };

    public List<PotionEffect> getEffects() {
        return effects;
    }

    /**
     * Méthode qui donne le kit au joueur au moment de la sélectionn du kit.
     * Elle téléporte le joueur aléatoire sur une liste de spawn différent
     * @param player
     */
    public void giveKit(Player player) {
        ArrayList<ArrayList<Double>> spawns = (ArrayList<ArrayList<Double>>) pvpBox.getConfig().getList("location.arena-spawns");

        ArrayList<Double> coords = spawns.get(new Random().nextInt(spawns.size()));

        double x = coords.get(0);
        double y = coords.get(1);
        double z = coords.get(2);

        Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
        player.teleport(loc);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        for (String line : getDescription()) {
            player.sendMessage(line);
        }
        player.addPotionEffects(effects);
    }

    /**
     * Méthode abstraite pour réduire d'1seconde les cooldowns de tout les pouvoirs rattaché au kit
     */
    public abstract void onTickAsync();

    public abstract Kit clone();
}
