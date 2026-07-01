package fr.octopk.pvpbox.kit.type;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class KitTest extends Kit {

    public KitTest(PVPBox pvpBox) {
        super(new ItemBuilder(Material.COOKIE)
                .setName("§cTest")
                .setLore("Ceci est un kit test.", "Il est pas sensé être vue ni prit.")
                .toItem(),
                "KitTest",
                pvpBox,
                Arrays.asList(
                        "----------------------------------------",
                        "§cTest",
                        "\n",
                        "Ceci est un kit test pour faire des test.",
                        "----------------------------------------"
                )
        );
    }

    @Override
    public void onTickAsync(){
    }

    @Override
    public Kit clone() {
        return new KitTest(pvpBox);
    }
}
