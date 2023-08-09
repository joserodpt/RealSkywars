package josegamerpt.realskywars.kits;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Kits;
import josegamerpt.realskywars.utils.ItemStackSpringer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class KitManager {

    private final ArrayList<Kit> kits = new ArrayList<>();

    public void loadKits() {
        this.kits.clear();

        if (Kits.file().isSection("Kits")) {
            Debugger.print(KitManager.class, "KITS: " + Kits.file().getSection("Kits").getRoutesAsStrings(false));

            for (String name : Kits.file().getSection("Kits").getRoutesAsStrings(false)) {
                Debugger.print(KitManager.class, "Loading KIT " + name);
                String displayName = Text.color(Kits.file().getString("Kits." + name + ".Display-Name"));
                Double price = Kits.file().getDouble("Kits." + name + ".Price");

                String matString = Kits.file().getString("Kits." + name + ".Icon");
                Material mat;
                Kit kit;
                try {
                    mat = Material.getMaterial(matString);
                } catch (Exception e) {
                    mat = Material.BARRIER;
                    RealSkywars.getPlugin().log(Level.WARNING, matString + " isn't a valid material [KIT]");
                }

                List<Map<String, Object>> inv = (List<Map<String, Object>>) Kits.file().getList("Kits." + name + ".Contents");

                if (inv.isEmpty()) {
                    Debugger.printerr(KitManager.class, "Inventory Itens on " + "Kits." + name + ".Contents" + " are empty! Skipping kit.");
                    continue;
                }

                kit = new Kit(name, displayName, price, mat, new KitInventory(ItemStackSpringer.getItemsDeSerialized(inv)), Kits.file().getString("Kits." + name + ".Permission"));

                if (Kits.file().isList("Kits." + name + ".Perks")) {
                    Kits.file().getStringList("Kits." + name + ".Perks")
                            .forEach(kit::addPerk);
                }

                this.getKits().add(kit);

                Debugger.print(KitManager.class, "Loaded " + kit);
            }
        }
    }

    public void registerKit(Kit k) {
        Kits.file().set("Kits." + k.getName() + ".Display-Name", k.getDisplayName());
        Kits.file().set("Kits." + k.getName() + ".Price", k.getPrice());
        Kits.file().set("Kits." + k.getName() + ".Icon", k.getIcon().name());
        Kits.file().set("Kits." + k.getName() + ".Permission", k.getPermission());

        if (!k.getKitPerks().isEmpty()) {
            Kits.file().set("Kits." + k.getName() + ".Perks", k.getKitPerks().stream().map(Enum::name).collect(Collectors.toList()));
        }

        Kits.file().set("Kits." + k.getName() + ".Contents", k.getKitInventory().getSerialized());
        Kits.save();
    }

    public void unregisterKit(Kit k) {
        this.getKits().remove(k);
        Kits.file().set("Kits", null);
        this.getKits().forEach(this::registerKit);
        Kits.save();
    }

    public ArrayList<Kit> getKits() {
        return this.kits;
    }

    public Kit getKit(String string) {
        return this.kits.stream()
                .filter(k -> k.getName().equalsIgnoreCase(string))
                .findFirst()
                .orElse(null);
    }
}