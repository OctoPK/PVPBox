package fr.octopk.pvpbox;

import fr.octopk.pvpbox.commands.CommandMeteor;
import fr.octopk.pvpbox.commands.CommandSpawn;
import fr.octopk.pvpbox.manager.KitManager;
import fr.octopk.pvpbox.listener.PVPBoxListener;
import fr.octopk.pvpbox.manager.AutoBreakManager;
import fr.octopk.pvpbox.manager.TabManager;
import fr.octopk.pvpbox.meteoroid.MeteorShowerManager;
import fr.octopk.pvpbox.utility.GUI.GUIManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Classe principale du plugin
 */
public final class PVPBox extends JavaPlugin {

    //je garde une seule instance de la classe
    private static PVPBox instance;
    //liste des états des joueurs
    public static Map<UUID, PlayerState> playerStates = new HashMap<>();

    private MeteorShowerManager meteorShowerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        //j'initialise mes classe manager
        new GUIManager();
        KitManager kitManager = KitManager.getInstance(instance);
        TabManager tabManager = TabManager.getInstance(instance);
        meteorShowerManager = new MeteorShowerManager(this);

        //j'enregistre une nouvelle commande
        getCommand("spawn").setExecutor(new CommandSpawn(this));
        getCommand("meteor").setExecutor(new CommandMeteor(this));

        //j'enregistre un nouveau listener
        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);

        //boucle principale qui tourne en continue et qui gère les cooldowns des pouvoir et l'autobreak
        getServer().getScheduler().runTaskTimer(this, new BukkitRunnable() {
            @Override
            public void run() {
                kitManager.getPlayerKits().forEach((uuid, kit) -> {
                    if(kit != null) {
                        kit.onTickAsync();
                    }
                });
                AutoBreakManager.onTickAsync();
            }
        }, 0L, 20L);
    }

    @Override
    public void onDisable() {
        if (meteorShowerManager != null) {
            meteorShowerManager.stop();
        }
        AutoBreakManager.stop();
    }

    //pour récupéré une instance de la classe
    public static PVPBox getInstance() {
        return instance;
    }

    //pour récupéré le prefix à mettre dans le chat pour différencier le plugin
    public static String getPrefix() {
        return "§2[§aPVPBox§2]§r";
    }


}
