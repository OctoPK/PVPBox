package fr.octopk.pvpbox.kit.type;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitExemple extends Kit {
    public KitExemple(PVPBox pvpBox) {
        super(new ItemBuilder(Material.IRON_SWORD).setName("§3ExempleKit").setLore(Arrays.asList("C'est un kit d'exemple pour montré comment fonctionne les kits", "Dans ce kit vous retrouverez un inventaire très basique")).toItem(), "KitExemple", pvpBox);
    }

    @Override
    public void giveKit(Player player) {
        super.giveKit(player);

        player.getInventory().addItem(
                new ItemBuilder(Material.IRON_SWORD).toItem(),
                new ItemBuilder(Material.COOKED_BEEF, 64).toItem(),
                new ItemBuilder(Material.COBBLESTONE, 64).toItem()
        );

        player.getInventory().setArmorContents(new ItemStack[] {
                new ItemBuilder(Material.IRON_BOOTS).toItem(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).toItem(),
                new ItemBuilder(Material.IRON_CHESTPLATE).toItem(),
                new ItemBuilder(Material.CHAINMAIL_HELMET).toItem(),
        });
    }


}
