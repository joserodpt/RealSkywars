package joserodpt.realskywars.kits;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.Debugger;
import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.config.RSWKitsConfig;
import joserodpt.realskywars.utils.ItemStackSpringer;
import joserodpt.realskywars.utils.Text;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class KitManager {

    private final ArrayList<SWKit> SWKits = new ArrayList<>();

    public void loadKits() {
        this.SWKits.clear();

        if (RSWKitsConfig.file().isSection("Kits")) {
            Debugger.print(KitManager.class, "KITS: " + RSWKitsConfig.file().getSection("Kits").getRoutesAsStrings(false));

            for (String name : RSWKitsConfig.file().getSection("Kits").getRoutesAsStrings(false)) {
                Debugger.print(KitManager.class, "Loading KIT " + name);
                String displayName = Text.color(RSWKitsConfig.file().getString("Kits." + name + ".Display-Name"));
                Double price = RSWKitsConfig.file().getDouble("Kits." + name + ".Price");

                String matString = RSWKitsConfig.file().getString("Kits." + name + ".Icon");
                Material mat;
                SWKit SWKit;
                try {
                    mat = Material.getMaterial(matString);
                } catch (Exception e) {
                    mat = Material.BARRIER;
                    RealSkywars.getPlugin().log(Level.WARNING, matString + " isn't a valid material [KIT]");
                }

                List<Map<String, Object>> inv = (List<Map<String, Object>>) RSWKitsConfig.file().getList("Kits." + name + ".Contents");

                if (inv.isEmpty()) {
                    Debugger.printerr(KitManager.class, "Inventory Itens on " + "Kits." + name + ".Contents" + " are empty! Skipping kit.");
                    continue;
                }

                SWKit = new SWKit(name, displayName, price, mat, new KitInventory(ItemStackSpringer.getItemsDeSerialized(inv)), RSWKitsConfig.file().getString("Kits." + name + ".Permission"));

                if (RSWKitsConfig.file().isList("Kits." + name + ".Perks")) {
                    RSWKitsConfig.file().getStringList("Kits." + name + ".Perks")
                            .forEach(SWKit::addPerk);
                }

                this.getKits().add(SWKit);

                Debugger.print(KitManager.class, "Loaded " + SWKit);
            }
        }
    }

    public void registerKit(SWKit k) {
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Display-Name", k.getDisplayName());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Price", k.getPrice());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Icon", k.getIcon().name());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Permission", k.getPermission());

        if (!k.getKitPerks().isEmpty()) {
            RSWKitsConfig.file().set("Kits." + k.getName() + ".Perks", k.getKitPerks().stream().map(Enum::name).collect(Collectors.toList()));
        }

        RSWKitsConfig.file().set("Kits." + k.getName() + ".Contents", k.getKitInventory().getSerialized());
        RSWKitsConfig.save();
    }

    public void unregisterKit(SWKit k) {
        this.getKits().remove(k);
        RSWKitsConfig.file().set("Kits", null);
        this.getKits().forEach(this::registerKit);
        RSWKitsConfig.save();
    }

    public ArrayList<SWKit> getKits() {
        return this.SWKits;
    }

    public SWKit getKit(String string) {
        return this.SWKits.stream()
                .filter(k -> k.getName().equalsIgnoreCase(string))
                .findFirst()
                .orElse(null);
    }
}