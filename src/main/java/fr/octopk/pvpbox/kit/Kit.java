package fr.octopk.pvpbox.kit;

import org.bukkit.inventory.ItemStack;

public abstract class Kit {
    private ItemStack item;
    private String name;

    public Kit(ItemStack item, String name) {
        this.item = item;
        this.name = name;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getName() {
        return name;
    }


}
