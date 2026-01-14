package fr.octopk.pvpbox.menu;

import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.kit.KitManager;
import fr.octopk.pvpbox.utility.GUI.GUI;
import fr.octopk.pvpbox.utility.GUI.GUIClick;
import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MenuKit extends GUI {

    private final Map<Integer, Kit> slotKits = new HashMap<>();

    private MenuKit() {
        super("§8Kit Selector Menu");
    }

    private static MenuKit instance = null;
    private Inventory inv;


    public static MenuKit getInstance() {
        if(instance == null) instance = new MenuKit();
        return instance;
    }

    @Override
    public void open(Player player) {
        inv = Bukkit.createInventory(null,9*6, "§8Kit Selector Menu");
        Iterator<Kit> it = KitManager.kits.iterator();

        for(int i = 0; i < inv.getSize(); i++) {
            if(i ==  4) {
                inv.setItem(i, new ItemBuilder(Material.NETHER_STAR).setName("§6Kit Menu").setLore("Choisissez un kit parmis ceux proposé", "ce kit sera votre équipement de combat", "vous serez téléporté alléatoirement sur la map après avoir choisie votre kit").toItem());
            }
            if(i < 3 || (i>5 && i <= 8) || (i%9 == 0 && !(i == 18 || i == 27)) || (i%9 == 8 && !(i == 26 || i == 35)) || (i >= 9*5 && !(i == 48 || i == 49 || i == 50))) {
                inv.setItem(i, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0)).setName("").toItem());
            }
            if(i == 49) {
                inv.setItem(i, new ItemBuilder(Material.BARRIER).setName("§l§4Quitter").toItem());
            }
            if(i == 50) {
                inv.setItem(i, new ItemBuilder(Material.PAPER).setName("§7Page suivante").toItem());
            }
            if ((i != 27 && i != 26) && i > 9 && i < 44) {
                if(it.hasNext()) {
                    Kit kit = it.next();
                    inv.setItem(i, kit.getIcon());
                    slotKits.put(i, kit);
                }
            }
        }

        player.openInventory(inv);
    }

    @Override
    public void handleClick(Player player, GUIClick click, ItemStack item, int rawSlot) {
        if (item == null) return;
        switch (item.getType()) {
            case BARRIER:
                player.closeInventory();
                break;
            case PAPER:
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Page suivante")) {
                    inv.setItem(48, new ItemBuilder(Material.PAPER).setName("§7Page precedent").toItem());
                    player.openInventory(inv);
                }
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Page precedent")) {
                    inv.clear(48);
                }
                break;
            case NETHER_STAR:
                player.sendMessage("§0§k##§r§1Bravo vous avez trouvé un easter egg !§0§k##");
                break;
            default:
                if(slotKits.containsKey(rawSlot)) {
                    Kit kit = slotKits.get(rawSlot);
                    kit.giveKit(player);
                    player.closeInventory();
                }
                break;
        }
    }
}
