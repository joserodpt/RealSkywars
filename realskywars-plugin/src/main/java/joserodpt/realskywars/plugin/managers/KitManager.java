package joserodpt.realskywars.plugin.managers;

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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWKitsConfig;
import joserodpt.realskywars.api.database.PlayerBoughtItemsRow;
import joserodpt.realskywars.api.kits.KitInventory;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.KitManagerAPI;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitManager extends KitManagerAPI {

    private final Map<String, RSWKit> kits = new HashMap<>();

    @Override
    public void loadKits() {
        this.kits.clear();

        if (RSWKitsConfig.file().isSection("Kits")) {
            Debugger.print(KitManager.class, "KITS: " + RSWKitsConfig.file().getSection("Kits").getRoutesAsStrings(false));

            for (String name : RSWKitsConfig.file().getSection("Kits").getRoutesAsStrings(false)) {
                Debugger.print(KitManager.class, "Loading KIT " + name);

                try {
                    String displayName = Text.color(RSWKitsConfig.file().getString("Kits." + name + ".Display-Name"));
                    Double price = RSWKitsConfig.file().getDouble("Kits." + name + ".Price");

                    String matString = RSWKitsConfig.file().getString("Kits." + name + ".Icon");
                    Material mat;
                    RSWKit rswKit;
                    try {
                        mat = Material.getMaterial(matString);
                    } catch (Exception e) {
                        mat = Material.BARRIER;
                        RealSkywarsAPI.getInstance().getLogger().warning(matString + " isn't a valid material [KIT]");
                    }

                    List<Map<String, Object>> inv = (List<Map<String, Object>>) RSWKitsConfig.file().getList("Kits." + name + ".Contents");

                    if (inv.isEmpty()) {
                        Debugger.printerr(KitManager.class, "Inventory Itens on " + "Kits." + name + ".Contents" + " are empty! Skipping kit.");
                        continue;
                    }

                    rswKit = new RSWKit(name, displayName, price, mat, new KitInventory(ItemStackSpringer.getItemsDeSerialized(inv)), RSWKitsConfig.file().getString("Kits." + name + ".Permission"));

                    if (RSWKitsConfig.file().isList("Kits." + name + ".Perks")) {
                        RSWKitsConfig.file().getStringList("Kits." + name + ".Perks")
                                .forEach(rswKit::addPerk);
                    }

                    this.kits.put(name, rswKit);

                    Debugger.print(KitManager.class, "Loaded " + rswKit);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Error loading kit: " + name + "! Skipping kit.");
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }
    }

    @Override
    public void registerKit(RSWKit k) {
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Display-Name", k.getDisplayName());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Price", k.getPrice());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Icon", k.getMaterial().name());
        RSWKitsConfig.file().set("Kits." + k.getName() + ".Permission", k.getPermission());

        if (!k.getKitPerks().isEmpty()) {
            RSWKitsConfig.file().set("Kits." + k.getName() + ".Perks", k.getKitPerks().stream().map(Enum::name).collect(Collectors.toList()));
        }

        RSWKitsConfig.file().set("Kits." + k.getName() + ".Contents", k.getKitInventory().getSerialized());
        RSWKitsConfig.save();
    }

    @Override
    public void unregisterKit(RSWKit k) {
        this.getKits().remove(k);
        RSWKitsConfig.file().remove("Kits");
        this.getKits().forEach(this::registerKit);
        RSWKitsConfig.save();
    }

    @Override
    public Collection<RSWKit> getKits() {
        return this.kits.values();
    }

    @Override
    public Collection<RSWBuyableItem> getKitsAsBuyables() {
        return new ArrayList<>(this.kits.values());
    }

    @Override
    public RSWKit getKit(String string) {
        return this.kits.get(string);
    }

    @Override
    public RSWKit getKit(PlayerBoughtItemsRow playerBoughtItemsRow) {
        return getKit(playerBoughtItemsRow.getItemID());
    }
}