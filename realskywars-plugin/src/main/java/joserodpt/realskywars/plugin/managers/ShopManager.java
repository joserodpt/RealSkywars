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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWShopsConfig;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.shop.items.RSWParticleItem;
import joserodpt.realskywars.api.shop.items.RSWSpectatorShopItem;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopManager extends ShopManagerAPI {
    private final RealSkywarsAPI rs;

    private final Map<String, RSWBuyableItem> shopItems = new HashMap<>();

    public ShopManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Override
    public void loadShopItems() {
        shopItems.clear();

        if (RSWShopsConfig.file().contains("Main-Shop")
                || RSWShopsConfig.file().getInt("Version") == 1
                || RSWShopsConfig.file().getInt("Version") == 2) {
            rs.getLogger().warning("Starting upgrade of Shop Items to new config format...");
            upgradeV2toV3();
            RSWShopsConfig.file().remove("Main-Shop");
            RSWShopsConfig.file().remove("Spectator-Shop");
            RSWShopsConfig.file().set("Version", 3);
            RSWShopsConfig.save();
            return;
        }

        for (String category : RSWShopsConfig.file().getSection("Shops").getRoutesAsStrings(false)) {
            RSWBuyableItem.ItemCategory cat = RSWBuyableItem.ItemCategory.getCategoryByName(category);
            for (String item : RSWShopsConfig.file().getSection("Shops." + category).getRoutesAsStrings(false)) {
                //verify if item already exists
                if (shopItems.containsKey(item)) {
                    rs.getLogger().warning("Item " + item + " already exists in the shop! Skipping.");
                    continue;
                }

                String displayname = RSWShopsConfig.file().getString("Shops." + category + "." + item + ".Displayname");
                String material = RSWShopsConfig.file().getString("Shops." + category + "." + item + ".Material");
                double price = RSWShopsConfig.file().getDouble("Shops." + category + "." + item + ".Price");
                String permission = RSWShopsConfig.file().getString("Shops." + category + "." + item + ".Permission");
                Map<String, Object> extras = new HashMap<>();
                if (RSWShopsConfig.file().contains("Shops." + category + "." + item + ".Extras")) {
                    for (String extra : RSWShopsConfig.file().getSection("Shops." + category + "." + item + ".Extras").getRoutesAsStrings(false)) {
                        extras.put(extra, RSWShopsConfig.file().getString("Shops." + category + "." + item + ".Extras." + extra));
                    }
                }

                RSWBuyableItem buyableItem;
                if (cat == RSWBuyableItem.ItemCategory.BOW_PARTICLE) {
                    String particle = RSWShopsConfig.file().getString("Shops." + category + "." + item + ".Extras.Particle");
                    buyableItem = new RSWParticleItem(item, displayname, Material.valueOf(material), price, permission, particle);
                } else {
                    buyableItem = new RSWBuyableItem(item, displayname, Material.valueOf(material), price, permission, cat, extras);
                }
                shopItems.put(item, buyableItem);
            }
        }
    }

    private void upgradeV2toV3() {
        int itemCounter = 1;
        for (String shopCategory : RSWShopsConfig.file().getSection("Main-Shop").getRoutesAsStrings(false)) {
            for (String itemInsideCategoryPath : RSWShopsConfig.file().getStringList("Main-Shop." + shopCategory)) {
                String[] parse = itemInsideCategoryPath.split(">");

                if (parse.length != 4 && parse.length != 5) {
                    rs.getLogger().warning("Invalid item format for old config: " + itemInsideCategoryPath + " in category: " + shopCategory + "! Skipping.");
                    continue;
                }

                String material = parse[0];
                String displayname = parse[2];
                String perm = parse[3];
                double price;

                try {
                    price = Double.parseDouble(parse[1]);
                } catch (Exception e) {
                    rs.getLogger().warning("Error while parsing price for Shop Item " + material + "! Skipping.");
                    continue;
                }

                Material m;

                if (parse[0].equalsIgnoreCase("randomblock")) {
                    m = Material.COMMAND_BLOCK;
                } else {
                    try {
                        m = Material.valueOf(parse[0]);
                    } catch (Exception e) {
                        rs.getLogger().warning("Error while parsing material for Shop Item " + material + "! Skipping.");
                        continue;
                    }
                }

                //try to convert displayname to translatable line
                try {
                    Material displayNameMat = Material.valueOf(ChatColor.stripColor(Text.color(displayname).toUpperCase()));
                    displayname = "&b" + rs.getLanguageManagerAPI().getMaterialName(displayNameMat);
                } catch (Exception ignored) {

                }

                RSWBuyableItem item;
                String configPath = "item" + itemCounter;
                switch (parse.length) {
                    case 5:
                        try {
                            Particle.valueOf(parse[4]);
                            item = new RSWParticleItem(configPath, displayname, m, price, perm, parse[4]);
                            ++itemCounter;
                        } catch (Exception e) {
                            rs.getLogger().warning("Error while parsing particle for Legacy Shop Item " + material + "! Skipping.");
                            continue;
                        }

                        break;
                    case 4:
                        item = new RSWBuyableItem(configPath, displayname, m, price, perm, RSWBuyableItem.ItemCategory.getCategoryByName(shopCategory));
                        ++itemCounter;
                        break;
                    default:
                        rs.getLogger().warning("Error while parsing Legacy Shop Item " + material + "! Skipping.");
                        continue;
                }
                shopItems.put(configPath, item);
                item.saveToConfig(false);
            }
        }

        for (String itemInsideCategoryPath : RSWShopsConfig.file().getStringList("Spectator-Shop")) {
            String[] parse = itemInsideCategoryPath.split(">");

            if (parse.length != 4) {
                rs.getLogger().warning("Invalid item format for old config: " + itemInsideCategoryPath + " in category: Spectator-Shop! Skipping.");
                continue;
            }

            String material = parse[0];
            String displayname = parse[2];
            String perm = parse[3];
            double price;

            try {
                price = Double.parseDouble(parse[1]);
            } catch (Exception e) {
                rs.getLogger().warning("Error while parsing price for Shop Item " + material + "! Skipping.");
                continue;
            }

            Material m;

            if (parse[0].equalsIgnoreCase("randomblock")) {
                m = Material.COMMAND_BLOCK;
            } else {
                try {
                    m = Material.valueOf(parse[0]);
                } catch (Exception e) {
                    rs.getLogger().warning("Error while parsing material for Shop Item " + material + "! Skipping.");
                    continue;
                }
            }

            //try to convert displayname to translatable line
            try {
                Material displayNameMat = Material.valueOf(ChatColor.stripColor(Text.color(displayname).toUpperCase()));
                displayname = "&b" + rs.getLanguageManagerAPI().getMaterialName(displayNameMat);
            } catch (Exception ignored) {
            }

            String configPath = "item" + itemCounter;

            RSWSpectatorShopItem item = new RSWSpectatorShopItem(configPath, displayname, m, price, perm);
            ++itemCounter;

            shopItems.put(configPath, item);
            item.saveToConfig(false);
        }

        rs.getLogger().warning("Upgrade of Legacy Shop Items to new config format finished!");
    }

    @Override
    public Collection<RSWBuyableItem> getCategoryContents(RSWBuyableItem.ItemCategory cat) {
        return cat != RSWBuyableItem.ItemCategory.KIT ? this.shopItems.values().stream().filter(a -> a.getCategory() == cat).collect(Collectors.toList()) : rs.getKitManagerAPI().getKitsAsBuyables();
    }

    @Override
    public Collection<RSWBuyableItem> getBoughtItems(RSWBuyableItem.ItemCategory t, RSWPlayer p) {
        if (t == RSWBuyableItem.ItemCategory.KIT) {
            List<RSWBuyableItem> kits = rs.getDatabaseManagerAPI().getPlayerBoughtItemsCategory(p.getPlayer(), t).stream().map(playerBoughtItemsRow -> rs.getKitManagerAPI().getKit(playerBoughtItemsRow)).collect(Collectors.toList());
            if (getCategoryContents(RSWBuyableItem.ItemCategory.KIT).stream().anyMatch(rswBuyableItem -> rswBuyableItem.getPrice() == 0)) {
                kits.addAll(rs.getKitManagerAPI().getKitsAsBuyables().stream().filter(rswBuyableItem -> rswBuyableItem.getPrice() == 0).collect(Collectors.toList()));
            }
            return kits;
        }
        return rs.getDatabaseManagerAPI().getPlayerBoughtItemsCategory(p.getPlayer(), t).stream().map(playerBoughtItemsRow -> this.shopItems.get(playerBoughtItemsRow.getItemID())).collect(Collectors.toList());
    }
}
