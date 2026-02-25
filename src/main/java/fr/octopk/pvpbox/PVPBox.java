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

public final class PVPBox extends JavaPlugin {

    private static PVPBox instance;
    public static Map<UUID, PlayerState> playerStates = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        new GUIManager();
        new KitManager(instance);

        getCommand("spawn").setExecutor(new CommandSpawn(this));

        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);

        getServer().getScheduler().runTaskTimer(this, new BukkitRunnable() {
            @Override
            public void run() {
                KitManager.kits.forEach(Kit::onTickAsync);
            }
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {

    }

    public static PVPBox getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return "§2[§aPVPBox§2]§r";
    }


}
