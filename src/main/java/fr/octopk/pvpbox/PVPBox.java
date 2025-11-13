package fr.octopk.pvpbox;

import org.bukkit.plugin.java.JavaPlugin;

public final class PVPBox extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);
    }

    @Override
    public void onDisable() {

    }
}
