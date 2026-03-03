package fr.octopk.pvpbox;

import fr.octopk.pvpbox.commands.CommandSpawn;
import fr.octopk.pvpbox.kit.Kit;
import fr.octopk.pvpbox.kit.KitManager;
import fr.octopk.pvpbox.listener.PVPBoxListener;
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

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        //j'initialise mes classe manager
        new GUIManager();
        new KitManager(instance);

        //j'enregistre une nouvelle commande
        getCommand("spawn").setExecutor(new CommandSpawn(this));

        //j'enregistre un nouveau listener
        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);

        //boucle principale qui tourne en continue et qui gère les cooldowns des pouvoir
        getServer().getScheduler().runTaskTimer(this, new BukkitRunnable() {
            @Override
            public void run() {
                KitManager.kits.forEach(Kit::onTickAsync);
            }
        }, 1L, 1L);
    }

    @Override
    public void onDisable() {

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
