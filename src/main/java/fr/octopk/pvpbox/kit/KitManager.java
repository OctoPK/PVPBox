package fr.octopk.pvpbox.kit;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.type.KitExemple;
import fr.octopk.pvpbox.kit.type.KitMagicalArcher;
import fr.octopk.pvpbox.kit.type.KitTest;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe manager qui gère et instancie les kit
 */
public class KitManager {
    public static ArrayList<Kit> kits = new ArrayList<>();

    /**
     * A chaque nouveau kit, l'enregistrer dans le manager dans le constructeur
     * @param pvpBox
     */
    public KitManager(PVPBox pvpBox) {
        registerKit(new KitTest(pvpBox), pvpBox);
        registerKit(new KitMagicalArcher(pvpBox), pvpBox);
    }

    /**
     * Pour enregistré un nouveau kit dans le manager
     * @param kit le kit à enregistré
     * @param pvpBox l'instance de la classe principale
     */
    public static void registerKit(Kit kit, PVPBox pvpBox) {
        if(kit == null)
            return;
        else if (kits.contains(kit)) return;
        kits.add(kit);
        pvpBox.getServer().getPluginManager().registerEvents(kit, pvpBox);
    }

    /**
     * Pour récupéré un kit en fonction de son nom
     * @param name le nom du kit
     * @return le kit
     */
    public static Kit getKit(String name) {
        AtomicReference<Kit> ref = new AtomicReference<>();
        kits.forEach(k -> {
            if(k.getName().equals(name))
                ref.set(k);
        });
        return ref.get();
    }
}
