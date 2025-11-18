package fr.octopk.pvpbox.utility.GUI;

import fr.octopk.pvpbox.PVPBox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class GUIManager {
    public static ArrayList<GUI> menus = new ArrayList<GUI>();

    public GUIManager() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIClickEvent(), PVPBox.getInstance());
    }

    public static void registerMenu(GUI gui) {
        if(gui == null) return;
        if(menus.contains(gui)) return;
        menus.add(gui);
    }

    public static GUI getMenus(String nom) {
        AtomicReference<GUI> ref = new AtomicReference<>();
        menus.forEach(gui -> {
            if(gui.getName().equalsIgnoreCase(nom)) ref.set(gui);
        });
        return ref.get();
    }

    public static void openMenu(Player player, String nom) {
        GUI gui = getMenus(nom);
        if(gui != null) gui.open(player);
        else player.sendMessage(PVPBox.getPrefix()+"§cLe menu que vous cherchez à ouvrir n'existe pas !");
    }
}
