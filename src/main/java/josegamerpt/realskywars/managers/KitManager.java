package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.classes.Kit;
import josegamerpt.realskywars.configuration.Kits;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitManager {

    private static ArrayList<Kit> kits = new ArrayList<>();

    public static void loadKits() {
        kits.clear();
        if (Kits.file().getConfigurationSection("Kits") != null) {
            for (String s : Kits.file().getConfigurationSection("Kits").getKeys(false)) {
                int id = Integer.parseInt(s);
                String name = Text.color(Kits.file().getString("Kits." + id + ".Name"));
                Double price = Kits.file().getDouble("Kits." + id + ".Price");
                Material mat = Material.getMaterial(Kits.file().getString("Kits." + id + ".Icon"));
                ItemStack[] k = getKitContents(id);
                String perm = Kits.file().getString("Kits." + id + ".Permission");
                Kit kit = new Kit(id, name, price, mat, k, perm);

                if (Kits.file().getBoolean("Kits." + id + ".Perks.EnderPearl"))
                {
                    kit.setPerk(KitPerks.ENDER_PEARl, true);
                }

                kit.save();
            }
        }
    }

    public static void registerKit(Kit k) {
        Kits.file().set("Kits." + k.getID() + ".Name", k.getName());
        Kits.file().set("Kits." + k.getID() + ".Price", k.getPrice());
        Kits.file().set("Kits." + k.getID() + ".Icon", k.getIcon().name());
        Kits.file().set("Kits." + k.getID() + ".Contents", k.getContents());
        Kits.file().set("Kits." + k.getID() + ".Permission", k.getPermission());
        Kits.file().set("Kits." + k.getID() + ".Perks.EnderPearl", k.getPerk(KitPerks.ENDER_PEARl));
        Kits.save();
    }

    private static ItemStack[] getKitContents(int asd) {
        List<?> list = Kits.file().getList("Kits." + asd + ".Contents");
        return list.toArray(new ItemStack[0]);
    }

    public static int getNewID() {
        if (Kits.file().getConfigurationSection("Kits") != null) {
            return Kits.file().getConfigurationSection("Kits").getKeys(false).size() + 1;
        } else {
            return 1;
        }
    }

    public static ArrayList<Kit> getKits() {
        return kits;
    }

    public static Kit getKit(int id) {
        for (Kit k : kits) {
            if (k.getID() == id) {
                return k;
            }
        }
        return null;
    }

    public static void unregisterKit(Kit k) {
        Kits.file().set("Kits." + k.getID(), null);
        Kits.save();
    }

    public static Kit getKit(String string) {
        for (Kit k : kits) {
            if (k.getName().equalsIgnoreCase(string)) {
                return k;
            }
        }
        return new Kit();
    }

    public static int getKitCount() {
        return kits.size();
    }

    public static List<String> getKitNames() {
        List<String> sugests = new ArrayList<>();
        kits.forEach(kit -> sugests.add(ChatColor.stripColor(kit.getName())));
        return sugests;

    }

    public enum KitPerks {ENDER_PEARl}
}
