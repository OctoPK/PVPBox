package fr.octopk.pvpbox.kit.type;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.kit.cooldown.CountDownAction;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class KitMagicalArcher extends Kit {

    private enum BowMode {
        SNIPER(20){
            @Override
            public BowMode next() {
                return MITRAILLETTE;
            }
        },
        MITRAILLETTE(2) {
            @Override
            public BowMode next() {
                return FUSIL_A_POMPE;
            }
        },
        FUSIL_A_POMPE(10) {
            @Override
            public BowMode next() {
                return SNIPER;
            }
        };

        int cooldown;

        BowMode(int cooldown) {
            this.cooldown = cooldown;
        }

        public int getCooldown() {
            return cooldown;
        }

        public abstract BowMode next();
    }


    private final CountDownAction shoot = new CountDownAction("shoot", this::shoot, 20);
    private HashMap<UUID, BowMode> bowMode = new HashMap<>();

    public KitMagicalArcher(PVPBox pvpBox) {
        super(
                new ItemBuilder(Material.BOW)
                        .setName("§6Magical Archer")
                        .setLore(Arrays.asList(
                                "Un mystérieux arc enchanté va faire des ravages.",
                                "retrouvé ce kit avec une base d'archer mais avec un arc spécial."
                        ))
                        .toItem(),
                "kitMagicalArcher",
                pvpBox,
                Arrays.asList(
                        "----------------------------------------",
                        "Magical Archer",
                        "\n",
                        "Ce kit posséde un arc spécial qui a plusieur mode de tir.",
                        "Pour changer de mode faite un clic droit avec l'arc.",
                        "Premier mode : fusil sniper, cadence de tir lent mais de gros dégat",
                        "deuxième mode : mitraillette, cadence de tir élevée mais faible dégat",
                        "troisième mode : fusil à pompe, cadence de tir moyenne, dégat moyen",
                        "----------------------------------------"
                ),
                Arrays.asList(
                        new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false)
                )
        );
    }

    @Override
    public void giveKit(Player player) {
        super.giveKit(player);

        bowMode.put(player.getUniqueId(), BowMode.SNIPER);

        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true).toItem(),
                new ItemBuilder(Material.BOW).setUnbreakable(true).setName("§6Magical Bow").setLore(Arrays.asList(
                        "Mode : Sniper",
                        "\n",
                        "Cet arc est magique, il possède des caractéristique unique !",
                        "Faites un clic droit pour changer de mode !"
                )).toItem(),
                new ItemBuilder(Material.WATER_BUCKET).toItem(),
                new ItemBuilder(Material.GOLDEN_APPLE, 12).toItem(),
                new ItemBuilder(Material.COOKED_BEEF, 64).toItem(),
                new ItemBuilder(Material.COBBLESTONE, 64*4).toItem()
        );

        player.getInventory().setArmorContents(new ItemStack[] {
                new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).toItem(),
                new ItemBuilder(Material.IRON_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).toItem(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).toItem(),
                new ItemBuilder(Material.IRON_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).toItem(),
        });
    }

    @Override
    public void onTickAsync() {
        shoot.tickSecond();
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(bowMode.containsKey(player.getUniqueId())) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteractWithBow(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(bowMode.containsKey(player.getUniqueId())) {
            ItemStack item = event.getItem();
            Action action = event.getAction();
            if (item.getType().equals(Material.BOW) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Magical Bow")) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    bowMode.put(player.getUniqueId(), bowMode.get(player.getUniqueId()).next());
                    shoot.changeCooldown(bowMode.get(player.getUniqueId()).getCooldown());
                } else {
                    shoot.useAction(player);
                }
            }
        }
    }

    public void shoot(Player player) {
        switch (bowMode.get(player.getUniqueId())) {

            case SNIPER:
                Arrow sniperArrow = player.launchProjectile(Arrow.class);
                sniperArrow.setVelocity(player.getLocation().getDirection().multiply(4));
                sniperArrow.spigot().setDamage(10);
                break;

            case MITRAILLETTE:
                Arrow mitrailletArrow = player.launchProjectile(Arrow.class);
                mitrailletArrow.setVelocity(player.getLocation().getDirection().multiply(2));
                mitrailletArrow.spigot().setDamage(3);
                break;

            case FUSIL_A_POMPE:
                for (int i = 0; i < 5; i++) {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(player.getLocation().getDirection().multiply(2));
                    arrow.spigot().setDamage(4);
                }
                break;
        }
    }
}
