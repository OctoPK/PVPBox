package fr.octopk.pvpbox;

import fr.octopk.pvpbox.utility.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PVPBoxListener implements Listener {

    private final PVPBox pvpBox;

    public PVPBoxListener(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getInventory().clear();
        p.getInventory().setHeldItemSlot(4);
        p.setHealth(20);
        p.setFoodLevel(20);
        Double[] spawn = pvpBox.getConfig().getDoubleList("location.spawn").toArray(new Double[0]);
        p.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
        e.setJoinMessage(pvpBox.getConfig().getString("messages.join").replace("%player%", p.getName()).replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size())).replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));

        ItemBuilder builder = new ItemBuilder(Material.COMPASS);
        builder.setName("§6Kit Selector §7- Clic Droit");
        p.getInventory().setItem(4, builder.toItem());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        /*
        check if player is in fight
         */
        e.setQuitMessage(pvpBox.getConfig().getString("messages.quit").replace("%player%", e.getPlayer().getName()).replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size()-1)).replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        ItemStack item = e.getItem();

        if (item == null) return;

        if(item.getType().equals(Material.COMPASS) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Kit Selector §7- Clic Droit")) {
            Inventory inv = Bukkit.createInventory(null,9*6, "§8Kit Selector Menu");

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
            }

            p.openInventory(inv);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item == null) return;

        if (inv.getName().equalsIgnoreCase("§8Kit Selector Menu")) {
            e.setCancelled(true);
            switch (item.getType()) {
                case BARRIER:
                    p.closeInventory();
                    break;
                case PAPER:
                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Page suivante")) {
                        inv.setItem(48, new ItemBuilder(Material.PAPER).setName("§7Page precedent").toItem());
                        p.openInventory(inv);
                    }
                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Page precedent")) {
                        inv.clear(48);
                    }
                    break;
                case NETHER_STAR:
                    p.sendMessage("§0§k##§r§1Bravo vous avez trouvé un easter egg !§0§k##");
                    break;
                default:
                    break;
            }
        }
    }
}
