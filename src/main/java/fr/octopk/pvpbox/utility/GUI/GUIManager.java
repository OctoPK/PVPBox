package fr.octopk.pvpbox.utility.GUI;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.menu.MenuKit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manager pour nos inventaires
 */
public class GUIManager {
    public static ArrayList<GUI> menus = new ArrayList<GUI>();

    /**
     * initialise la classe
     */
    public GUIManager() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIClickEvent(), PVPBox.getInstance());
        registerMenu(MenuKit.getInstance());
    }

    /**
     * pour enregistré un nouveau inventaire dans notre manager
     * @param gui l'inventaire a enregistré
     */
    public static void registerMenu(GUI gui) {
        if(gui == null) return;
        if(menus.contains(gui)) return;
        menus.add(gui);
    }

    /**
     * Pour récupéré un inventaire à partir de son nom
     * @param nom le nom de l'inventaire à cherché
     * @return l'inventaire associé au nom donné
     */
    public static GUI getMenus(String nom) {
        AtomicReference<GUI> ref = new AtomicReference<>();
        menus.forEach(gui -> {
            if(gui.getName().equalsIgnoreCase(nom)) ref.set(gui);
        });
        return ref.get();
    }

    /**
     * Ouvrir l'inventaire au joueur donné en fonction du nom donné
     * @param player le joueur où il faut ouvrir l'inventaire
     * @param nom le nom de l'inventaire
     */
    public static void openMenu(Player player, String nom) {
        GUI gui = getMenus(nom);
        if(gui != null) gui.open(player);
        else player.sendMessage(PVPBox.getPrefix()+"§cLe menu que vous cherchez à ouvrir n'existe pas !");
    }
}
