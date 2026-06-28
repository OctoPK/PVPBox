package fr.octopk.pvpbox.kit.type;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.kit.cooldown.CountDownAction;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Kit d'exemple, pour montré comment créé un kit
 */
public class KitExemple extends Kit {

    //Le pouvoir du kit avec le nom de l'action, la méthode à appelé lors de son activation et le cooldown du pouvoir (ici 60 seconde)
    private final CountDownAction strenghtAction = new CountDownAction("Strenght", this::useTestStrenght, 60);

    private final Set<UUID> player = new HashSet<>();


    public KitExemple(PVPBox pvpBox) {
        /**
         * - l'item affiché dans la sélection des kit
         * - le nom du kit
         * - l'instance de la classe principale
         * - la description du kit à affiché dans le chat du joueur
         * - liste d'effet du kit
         */
        super(new ItemBuilder(Material.IRON_SWORD)
                .setName("§3ExempleKit")
                .setLore(Arrays.asList(
                        "C'est un kit d'exemple pour montré comment fonctionne les kits",
                        "Dans ce kit vous retrouverez un inventaire très basique"
                )).toItem(),

                "KitExemple",

                pvpBox,

                Arrays.asList(
                        "-".repeat(10),
                        "§3ExempleKit",
                        "\n",
                        "Ceci est un kit pour but d'exemple de création de kit.",
                        "Il posséde force et speed 1.",
                        "-".repeat(10)
                ),

                Arrays.asList(
                        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false),
                        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false)
                ));
    }

    /**
     * Le contenu du kit à donné.
     * Pour les pouvoir, il faut mettre un item avec un nom bien définie
     * @param player le joueur à qui il faut donné le kit
     */
    @Override
    public void giveKit(Player player) {
        super.giveKit(player);

        this.player.add(player.getUniqueId());

        player.getInventory().addItem(
                new ItemBuilder(Material.IRON_SWORD).toItem(),
                new ItemBuilder(Material.COOKED_BEEF, 64).toItem(),
                new ItemBuilder(Material.COBBLESTONE, 64).toItem(),
                //l'item qui permet l'activation du pouvoir
                new ItemBuilder(Material.NETHER_STAR).setName("§6Strenght").toItem()
        );

        player.getInventory().setArmorContents(new ItemStack[] {
                new ItemBuilder(Material.IRON_BOOTS).toItem(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).toItem(),
                new ItemBuilder(Material.IRON_CHESTPLATE).toItem(),
                new ItemBuilder(Material.CHAINMAIL_HELMET).toItem(),
        });
    }

    /**
     * Ce que fait le pouvoir.
     * Ici, on donne force 2 au joueur pendant 10 seconde
     * @param player le joueur qui faut donné force
     */
    public void useTestStrenght(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*10, 1, false, false), true);
    }

    /**
     * On a qu'un seul pouvoir donc on ne réduit 1 tick que de un pouvoir
     */
    @Override
    public void onTickAsync() {
        strenghtAction.tickSecond();
    }

    /**
     * L'event qui permet d'activé le pouvoir lorsque l'item est utilisé
     * @param event
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(this.player.contains(player.getUniqueId())) {
            ItemStack item = event.getItem();
            Action action = event.getAction();
            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && item.getType().equals(Material.NETHER_STAR) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6strenght")) {
                //Lorsque c'est le bon item, on appel la méthode useAction de notre CountDownAction
                strenghtAction.useAction(player);
            }
        }
    }

    public boolean containsPlayer(UUID uuid) {
        return this.player.contains(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.player.remove(uuid);
    }
}
