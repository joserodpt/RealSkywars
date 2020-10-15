package pt.josegamerpt.realskywars.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

public class Holograms {

    public static ArrayList<ArmorStand> holograms = new ArrayList<>();

    public static void add(String nome, Location loc) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setCustomName(Text.addColor(nome));
        holograms.add(as);
    }

    public static void remove(String nome) {
        for (ArmorStand a : holograms) {
            if (a.getName().equalsIgnoreCase(nome)) {
                a.remove();
            }
        }
    }

    public static void removeAll() {
        for (ArmorStand w : holograms) {
            w.remove();
        }
    }

}
