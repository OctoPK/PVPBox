package fr.octopk.pvpbox;

import fr.octopk.pvpbox.commands.CommandSpawn;
import fr.octopk.pvpbox.kit.KitManager;
import fr.octopk.pvpbox.listener.PVPBoxListener;
import fr.octopk.pvpbox.utility.GUI.GUIManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PVPBox extends JavaPlugin {

    private static PVPBox instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        new GUIManager();
        new KitManager(instance);

        getCommand("spawn").setExecutor(new CommandSpawn(this));

        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);
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
