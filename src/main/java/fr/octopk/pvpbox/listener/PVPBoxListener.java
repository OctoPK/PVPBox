package fr.octopk.pvpbox.listener;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.PlayerState;
import fr.octopk.pvpbox.utility.AutoBreakManager;
import fr.octopk.pvpbox.utility.GUI.GUIManager;
import fr.octopk.pvpbox.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener principale du plugin
 */
public class PVPBoxListener implements Listener {

    private final PVPBox pvpBox;
    private final Double[] spawn;


    public PVPBoxListener(PVPBox pvpBox) {
        this.pvpBox = pvpBox;
        this.spawn = this.pvpBox.getConfig().getDoubleList("location.lobby-spawn").toArray(new Double[0]);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Util.reset(p);

        p.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
        e.setJoinMessage(pvpBox.getConfig()
                .getString("messages.join")
                .replace("%player%", p.getName())
                .replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size()))
                .replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PVPBox.playerStates.remove(p.getUniqueId());

        e.setQuitMessage(pvpBox.getConfig()
                .getString("messages.quit")
                .replace("%player%", e.getPlayer().getName())
                .replace("%connected%", Integer.toString(Bukkit.getOnlinePlayers().size()-1))
                .replace("%maxslot%", Integer.toString(Bukkit.getMaxPlayers())));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player entity = (Player) e.getEntity();
            Player damager = null;

            if (PVPBox.playerStates.get(entity.getUniqueId()) == PlayerState.LOBBY) {
                e.setCancelled(true);
                return;
            }


            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();

                if (PVPBox.playerStates.get(damager.getUniqueId()) == PlayerState.LOBBY) {
                    e.setCancelled(true);
                    return;
                }


                //System.out.println("[Listener principal] Player health : " + entity.getHealth() + " | Damager damage : " + e.getFinalDamage() + " | ma condition : " + (entity.getHealth() - e.getFinalDamage()));

                if (entity.getHealth() - e.getFinalDamage() <= 0) {
                    Util.reset(entity);
                    entity.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
                    Bukkit.broadcastMessage(pvpBox.getConfig().getString("messages.kill").replace("%killer%", damager.getName()).replace("%dead%", entity.getName()));
                    e.setCancelled(true);
                }
            } else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
                damager =  (Player) ((Projectile) e.getDamager()).getShooter();

                if (PVPBox.playerStates.get(damager.getUniqueId()) == PlayerState.LOBBY) {
                    e.setCancelled(true);
                    return;
                }

                // System.out.println("Player health : " + entity.getHealth() + " | Damager damage : " + e.getFinalDamage() + " | ma condition : " + (entity.getHealth() - e.getFinalDamage()));

                if (entity.getHealth() - e.getFinalDamage() <= 0) {
                    Util.reset(entity);
                    entity.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
                    Bukkit.broadcastMessage(pvpBox.getConfig().getString("messages.shoot").replace("%killer%", damager.getName()).replace("%dead%", entity.getName()));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        ItemStack item = e.getItem();

        if (item == null) return;

        if(item.getType().equals(Material.COMPASS) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Kit Selector §7- Clic Droit")) {
            GUIManager.openMenu(p, "§8Kit Selector Menu");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            Player p = (Player) e.getEntity();

            if(PVPBox.playerStates.get(p.getUniqueId()) == PlayerState.LOBBY) {
                e.setCancelled(true);
                return;
            }

            if(p.getHealth() - e.getFinalDamage() <= 0) {
                Util.reset(p);
                p.teleport(new Location(pvpBox.getServer().getWorld("world"), spawn[0], spawn[1], spawn[2], 0, 0));
                Bukkit.broadcastMessage(pvpBox.getConfig().getString("messages.death").replace("%dead%", p.getName()));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoseFood(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if(PVPBox.playerStates.get(p.getUniqueId()) == PlayerState.LOBBY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();

        if(PVPBox.playerStates.get(p.getUniqueId()) == PlayerState.PLAYING) {
            Location loc = block.getLocation();

            BlockState oldState = event.getBlockReplacedState();

            if (AutoBreakManager.contains(oldState.getBlock())) {
                AutoBreakManager.addBlock(block, loc, Material.AIR);
            } else {
                AutoBreakManager.addBlock(block, loc, oldState.getType());
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWaterPlace(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Material bucketType = event.getBucket();

        if (PVPBox.playerStates.get(player.getUniqueId()) == PlayerState.PLAYING) {
            if (bucketType == Material.WATER_BUCKET || bucketType == Material.LAVA_BUCKET) {
                Block placed = event.getBlockClicked().getRelative(event.getBlockFace());
                
                AutoBreakManager.addBlock(placed, placed.getLocation(), placed.getType());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(player.getGameMode() != GameMode.CREATIVE && !AutoBreakManager.contains(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFarmlandTrampling(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.SOIL) {
                event.setCancelled(true);
            }
        }
    }
}
