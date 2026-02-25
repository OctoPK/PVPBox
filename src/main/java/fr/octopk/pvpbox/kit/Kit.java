package fr.octopk.pvpbox.kit;

import fr.octopk.pvpbox.PVPBox;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Kit implements Listener {
    private ItemStack icon;
    private String name;
    private PVPBox pvpBox;
    private List<String> description;
    private List<PotionEffect> effects;

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

    public abstract void onTickAsync();
}
