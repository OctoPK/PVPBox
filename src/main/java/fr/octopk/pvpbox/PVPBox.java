package fr.octopk.pvpbox;

import org.bukkit.plugin.java.JavaPlugin;

public final class PVPBox extends JavaPlugin {

    private static PVPBox instance;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    public static PVPBox getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return "§a[PVPBox]§r";
    }
}
