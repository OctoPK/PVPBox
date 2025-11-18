package fr.octopk.pvpbox.utility.GUI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUI {
    private String name;

    public GUI(String name) {
        this.name = name;
    }

    public abstract void open(Player player);
    public abstract void handleClick(Player player, GUIClick click, ItemStack item, int rawSlot);

    public String getName() {
        return name;
    }
}
