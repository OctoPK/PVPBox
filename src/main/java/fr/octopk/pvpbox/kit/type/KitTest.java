package fr.octopk.pvpbox.kit.type;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.kit.cooldown.CountDownAction;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitTest extends Kit {

    private final CountDownAction strenghtAction = new CountDownAction("Strenght", this::useTestStrenght, 60);

    public KitTest(PVPBox pvpBox) {
        super(new ItemBuilder(Material.COOKIE)
                .setName("§cTest")
                .setLore("Ceci est un kit test.", "Il est pas sensé être vue ni prit.")
                .toItem(),
                "KitTest",
                pvpBox,
                Arrays.asList(
                        "-".repeat(10),
                        "§cTest",
                        "\n",
                        "Ceci est un kit test pour faire des test.",
                        "-".repeat(10)
                ),
                List.of());
    }

    @Override
    public void giveKit(Player player) {
        super.giveKit(player);

        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true).addEnchantment(Enchantment.DAMAGE_ALL, 2).toItem(),
                new ItemBuilder(Material.WATER_BUCKET).toItem(),
                new ItemBuilder(Material.GOLDEN_APPLE, 12).toItem(),
                new ItemBuilder(Material.COOKED_BEEF, 64).toItem(),
                new ItemBuilder(Material.COBBLESTONE, 64).toItem(),
                new ItemBuilder(Material.NETHER_STAR).setName("§6Strenght").toItem()
        );

        player.getInventory().setArmorContents(new ItemStack[] {
                new ItemBuilder(Material.DIAMOND_BOOTS).toItem(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).toItem(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).toItem(),
                new ItemBuilder(Material.LEATHER_HELMET).toItem(),
        });
    }

    public void useTestStrenght(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*10, 1, false, false), true);
    }

    @Override
    public void onTickAsync() {
        strenghtAction.tickSecond();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && item.getType().equals(Material.NETHER_STAR) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6strenght")) {
            strenghtAction.useAction(player);
        }
    }
}
