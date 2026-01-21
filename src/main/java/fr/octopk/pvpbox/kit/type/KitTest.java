package fr.octopk.pvpbox.kit.type;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitTest extends Kit {
    public KitTest(PVPBox pvpBox) {
        super(new ItemBuilder(Material.COOKIE).setName("§cTest").setLore("Ceci est un kit test.", "Il est pas sensé être vue ni prit.").toItem(), "KitTest", pvpBox);
    }

    @Override
    public void giveKit(Player player) {
        super.giveKit(player);

        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true).addEnchantment(Enchantment.DAMAGE_ALL, 2).toItem(),
                new ItemBuilder(Material.WATER_BUCKET).toItem(),
                new ItemBuilder(Material.GOLDEN_APPLE, 12).toItem(),
                new ItemBuilder(Material.COOKED_BEEF, 64).toItem(),
                new ItemBuilder(Material.COBBLESTONE, 64).toItem()
        );

        player.getInventory().setArmorContents(new ItemStack[] {
                new ItemBuilder(Material.DIAMOND_BOOTS).toItem(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).toItem(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).toItem(),
                new ItemBuilder(Material.LEATHER_HELMET).toItem(),
        });
    }
}
