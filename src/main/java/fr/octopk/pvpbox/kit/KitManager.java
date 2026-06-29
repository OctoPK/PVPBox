package fr.octopk.pvpbox.kit;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.listener.KitExempleListener;
import fr.octopk.pvpbox.kit.listener.KitListener;
import fr.octopk.pvpbox.kit.listener.KitMagicalArcherListener;
import fr.octopk.pvpbox.kit.type.KitExemple;
import fr.octopk.pvpbox.kit.type.KitMagicalArcher;
import fr.octopk.pvpbox.kit.type.KitTest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe manager qui gère et instancie les kit
 */
public class KitManager {
    public static ArrayList<Kit> diferentKit = new ArrayList<>();
    private static HashMap<UUID, Kit> playerKits = new HashMap<>();

    private static KitManager kitManager;

    /**
     * A chaque nouveau kit, l'enregistrer dans le manager dans le constructeur
     * @param pvpBox
     */
    private KitManager(PVPBox pvpBox) {
        registerKit(null, new KitTest(pvpBox), pvpBox);
        registerKit(new KitMagicalArcherListener(), new KitMagicalArcher(pvpBox), pvpBox);
        registerKit(new KitExempleListener(), new KitExemple(pvpBox), pvpBox);
    }

    public static KitManager getInstance(PVPBox pvpBox) {
        if(kitManager == null) {
            kitManager = new KitManager(pvpBox);
        }
        return kitManager;
    }

    /**
     * Pour enregistré un nouveau kit dans le manager
     * @param kit le kit à enregistré
     * @param pvpBox l'instance de la classe principale
     */
    public void registerKit(KitListener kitListener, Kit kit, PVPBox pvpBox) {
        if(kit == null)
            return;
        else if (diferentKit.contains(kit)) return;
        diferentKit.add(kit);
        if (kitListener != null) pvpBox.getServer().getPluginManager().registerEvents(kitListener, pvpBox);
    }

    public void giveKit(String name, Player player) {
        for(Kit kit : diferentKit) {
            if(kit.getName().equalsIgnoreCase(name)) {
                playerKits.put(player.getUniqueId(), kit.clone());
                kit.giveKit(player);
            }
        }
    }

    public void removePlayer(UUID uuid) {
        playerKits.remove(uuid);
    }

    public HashMap<UUID, Kit> getPlayerKits() {
        return playerKits;
    }

    public Kit getKit(UUID uuid) {
        return playerKits.get(uuid);
    }
}
