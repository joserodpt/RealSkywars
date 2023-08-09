package josegamerpt.realskywars.shop;

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

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Shops;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ShopManager {
    private RealSkywars rs;
    public ShopManager(RealSkywars rs) {
        this.rs = rs;
    }
    public ArrayList<ShopDisplayItem> getCategoryContents(RSWPlayer p, ShopManager.Categories t) {
        ArrayList<ShopDisplayItem> items = new ArrayList<>();
        int i = 1;

        String cat = null;
        boolean rapidReturn = false;
        switch (t) {
            case CAGE_BLOCKS:
                cat = "Cage-Blocks";
                break;
            case WIN_BLOCKS:
                cat = "Win-Blocks";
                break;
            case KITS:
                items.addAll(rs.getKitManager().getKits().stream()
                        .map(a -> new ShopDisplayItem(
                                a.getName(),
                                a.getDisplayName(),
                                a.getIcon(),
                                a.getPrice(),
                                p.boughtItem(a.getName(), ShopManager.Categories.KITS),
                                a.getPermission(),
                                ShopManager.Categories.KITS))
                        .collect(Collectors.toList()));
                rapidReturn = true;
                break;
            case BOW_PARTICLES:
                cat = "Bow-Particles";
                break;
        }

        if (rapidReturn) {
            return items;
        } else {
            for (String sa : Shops.file().getStringList("Main-Shop." + cat)) {
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

                ShopDisplayItem s = new ShopDisplayItem(name, displayName, m, price, p.boughtItem(name, t), perm, ShopManager.Categories.CAGE_BLOCKS);
                if (material.equalsIgnoreCase("randomblock")) {
                    s.addInfo("RandomBlock", "RandomBlock");
                }

                if (parse.length == 5) {
                    try {
                        s.addInfo("Particle", Particle.valueOf(parse[4]));
                    } catch (Exception e) {
                        RealSkywars.getPlugin().log(Level.WARNING, "[FATAL] " + parse[4] + " isn't a valid particle! Changed to drip lava.");
                        s.addInfo("Particle", Particle.DRIP_LAVA);
                    }
                }

                if (error) {
                    s.setInteractive(false);
                    s.setName("&4Configuration Error. &cEnable debug for more info. Line &f" + i);
                }

                items.add(s);
                ++i;
            }

            if (items.isEmpty()) {
                items.add(new ShopDisplayItem());
            }

            return items;
        }

    }

    public enum Categories {
        CAGE_BLOCKS, BOW_PARTICLES, KITS, WIN_BLOCKS
    }
}
