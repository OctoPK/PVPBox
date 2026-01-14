package fr.octopk.pvpbox.kit;

import fr.octopk.pvpbox.PVPBox;
import fr.octopk.pvpbox.kit.type.KitExemple;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class KitManager {
    public static ArrayList<Kit> kits = new ArrayList<>();

    public KitManager(PVPBox pvpBox) {
        registerKit(new KitExemple(pvpBox));
    }

    public static void registerKit(Kit kit) {
        if(kit == null)
            return;
        else if (kits.contains(kit)) return;
        kits.add(kit);
    }

    public static Kit getKit(String name) {
        AtomicReference<Kit> ref = new AtomicReference<>();
        kits.forEach(k -> {
            if(k.getName().equals(name))
                ref.set(k);
        });
        return ref.get();
    }
}
