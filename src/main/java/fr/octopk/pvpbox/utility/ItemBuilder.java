package fr.octopk.pvpbox.utility;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * classe utilitaire pour la création d'un item custom
 */
public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder setQuantity(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = item.getItemMeta();
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = item.getItemMeta();
        im.setLore(lore);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        ItemMeta im = item.getItemMeta();
        im.getLore().addAll(Arrays.asList(lore));
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        ItemMeta im = item.getItemMeta();
        im.getLore().addAll(lore);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta im = item.getItemMeta();
        im.spigot().setUnbreakable(unbreakable);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantement(Enchantment enchantment) {
        return addEnchantment(enchantment, 1);
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta im = item.getItemMeta();
        im.addEnchant(enchantment, level, true);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag... flag) {
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(flag);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addAllFlags() {
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(ItemFlag.values());
        item.setItemMeta(im);
        return this;
    }

    public ItemStack toItem() {
        return item;
    }
}
