package fr.octopk.pvpbox;

import org.bukkit.plugin.java.JavaPlugin;

public final class PVPBox extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PVPBoxListener(this), this);
    }

    @Override
    public void onDisable() {

    }
}
