package josegamerpt.realskywars.kits;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Kits;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KitManager {

    private final ArrayList<Kit> kits = new ArrayList<>();

    public void loadKits() {
        kits.clear();
        if (Kits.file().getConfigurationSection("Kits") != null) {
            for (String s : Kits.file().getConfigurationSection("Kits").getKeys(false)) {
                int id = Integer.parseInt(s);
                String name = Text.color(Kits.file().getString("Kits." + id + ".Name"));
                Double price = Kits.file().getDouble("Kits." + id + ".Price");

                String matString = Kits.file().getString("Kits." + id + ".Icon");
                Material mat;
                Kit kit;
                try {
                    mat = Material.getMaterial(matString);
                } catch (Exception e) {
                    mat = Material.BARRIER;
                    RealSkywars.log(Level.WARNING, matString + " isnt a valid material [KIT]");
                }
                ItemStack[] k = getKitContents(id);
                String perm = Kits.file().getString("Kits." + id + ".Permission");
                kit = new Kit(id, name, price, mat, k, perm);

                if (Kits.file().getBoolean("Kits." + id + ".Perks.EnderPearl")) {
                    kit.setPerk(KitPerks.ENDER_PEARl, true);
                }

                kit.save();
            }
        }
    }

    public void registerKit(Kit k) {
        Kits.file().set("Kits." + k.getID() + ".Name", k.getName());
        Kits.file().set("Kits." + k.getID() + ".Price", k.getPrice());
        Kits.file().set("Kits." + k.getID() + ".Icon", k.getIcon().name());
        Kits.file().set("Kits." + k.getID() + ".Contents", k.getContents());
        Kits.file().set("Kits." + k.getID() + ".Permission", k.getPermission());
        Kits.file().set("Kits." + k.getID() + ".Perks.EnderPearl", k.getPerk(KitPerks.ENDER_PEARl));
        Kits.save();
    }

    private ItemStack[] getKitContents(int asd) {
        List<?> list = Kits.file().getList("Kits." + asd + ".Contents");
        return list.toArray(new ItemStack[0]);
    }

    public int getNewID() {
        if (Kits.file().getConfigurationSection("Kits") != null) {
            return Kits.file().getConfigurationSection("Kits").getKeys(false).size() + 1;
        } else {
            return 1;
        }
    }

    public ArrayList<Kit> getKits() {
        return kits;
    }

    public Kit getKit(int id) {
        for (Kit k : kits) {
            if (k.getID() == id) {
                return k;
            }
        }
        return null;
    }

    public void unregisterKit(Kit k) {
        Kits.file().set("Kits." + k.getID(), null);
        Kits.save();
    }

    public Kit getKit(String string) {
        for (Kit k : kits) {
            if (k.getName().equalsIgnoreCase(string)) {
                return k;
            }
        }
        return new Kit();
    }

    public int getKitCount() {
        return kits.size();
    }

    public List<String> getKitNames() {
        List<String> sugests = new ArrayList<>();
        kits.forEach(kit -> sugests.add(ChatColor.stripColor(kit.getName())));
        return sugests;

    }

    public enum KitPerks {ENDER_PEARl}

}