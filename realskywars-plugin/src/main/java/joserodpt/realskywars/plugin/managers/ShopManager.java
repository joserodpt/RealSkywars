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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWShopsConfig;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopManager extends ShopManagerAPI {
    private final RealSkywarsAPI rs;
    public ShopManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }
    @Override
    public List<RSWShopDisplayItem> getCategoryContents(RSWPlayer p, ShopManager.Categories t) {
        if (t == Categories.KITS) {
            return rs.getKitManagerAPI().getKits().stream()
                    .map(a -> new RSWShopDisplayItem(
                            a.getName(),
                            a.getDisplayName(),
                            a.getIcon(),
                            a.getPrice(),
                            p.boughtItem(a.getName(), ShopManager.Categories.KITS),
                            a.getPermission(),
                            ShopManager.Categories.KITS))
                    .collect(Collectors.toList());
        }

        List<RSWShopDisplayItem> items = new ArrayList<>();
        if (t == Categories.SPEC_SHOP) {
            for (String sa : RSWShopsConfig.file().getStringList("Spectator-Shop")) {
                // MATERIAL>AMOUNT>PRICE>NAME>PERMISSION
                String[] parse = sa.split(">");
                boolean error = false;
                String displayName = Text.color(parse[2]);
                String name = Text.strip(displayName);
                String perm = parse[3];
                String material = parse[0];

                double price;
                try {
                    price = Double.parseDouble(parse[1]);
                } catch (Exception e) {
                    price = 999999D;
                    error = true;
                    RealSkywarsAPI.getInstance().getLogger().severe("Error while parsing price for Spectator Shop Item " + name);
                }

                Material m = Material.getMaterial(material);
                if (m == null) {
                    m = Material.STONE;
                    RealSkywarsAPI.getInstance().getLogger().severe("[FATAL] Material: " + material + " isn't valid for this item shop! Changed to Stone.");
                    error = true;
                }

                RSWShopDisplayItem s = new RSWShopDisplayItem(name, displayName, m, price, perm);
                s.setInteractive(!error);

                items.add(s);
            }
        } else {
            String cat = null;
            switch (t) {
                case CAGE_BLOCKS:
                    cat = "Cage-Blocks";
                    break;
                case WIN_BLOCKS:
                    cat = "Win-Blocks";
                    break;
                case BOW_PARTICLES:
                    cat = "Bow-Particles";
                    break;
            }

            for (String sa : RSWShopsConfig.file().getStringList("Main-Shop." + cat)) {
                String[] parse = sa.split(">");

                boolean error = false;
                double price;
                String material = parse[0];
                String displayName = Text.color(parse[2]);
                String name = Text.strip(displayName);
                String perm = parse[3];

                try {
                    price = Double.parseDouble(parse[1]);
                } catch (Exception e) {
                    price = 999999D;
                    error = true;
                    RealSkywarsAPI.getInstance().getLogger().severe("Error while parsing price for Shop Item " + name);
                }

                Material m;

                if (material.equalsIgnoreCase("randomblock")) {
                    m = Material.COMMAND_BLOCK;
                } else {
                    m = Material.getMaterial(material);
                }

                if (m == null) {
                    m = Material.BARRIER;
                    Debugger.print(ShopManager.class, "[FATAL] Material: " + material + " isn't valid! Changed to Barrier Material.");
                    error = true;
                }

                RSWShopDisplayItem s = new RSWShopDisplayItem(name, displayName, m, price, p.boughtItem(name, t), perm, Categories.CAGE_BLOCKS);
                if (material.equalsIgnoreCase("randomblock")) {
                    s.addInfo("RandomBlock", "RandomBlock");
                }

                if (parse.length == 5) {
                    try {
                        s.addInfo("Particle", Particle.valueOf(parse[4]));
                    } catch (Exception e) {
                        RealSkywarsAPI.getInstance().getLogger().warning("[FATAL] " + parse[4] + " isn't a valid particle! Changed to drip lava.");
                        s.addInfo("Particle", Particle.DRIP_LAVA);
                    }
                }

                s.setInteractive(!error);

                items.add(s);
            }

            if (items.isEmpty()) {
                items.add(new RSWShopDisplayItem());
            }
        }

        return items;

    }

}
