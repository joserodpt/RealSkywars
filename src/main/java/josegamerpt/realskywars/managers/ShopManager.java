package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Shops;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.misc.DisplayItem;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.logging.Level;

public class ShopManager {

    public ArrayList<DisplayItem> getCategoryContents(RSWPlayer p, ShopManager.Categories t) {
        ArrayList<DisplayItem> items = new ArrayList<>();
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
                for (Kit a : RealSkywars.getKitManager().getKits()) {
                    Boolean bought = RealSkywars.getPlayerManager().boughtItem(p, a.getName(), ShopManager.Categories.KITS);

                    items.add(new DisplayItem(a.getID(), a.getIcon(), a.getName(), a.getPrice(), bought, a.getPermission(), ShopManager.Categories.KITS));
                }
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
                String name = Text.color(parse[2]);
                String perm = parse[3];
                Boolean bought = RealSkywars.getPlayerManager().boughtItem(p, name, t);

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
                    Debugger.print(ShopManager.class, "[FATAL] MATERIAL ISNT VALID: " + material);
                    error = true;
                }

                DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, ShopManager.Categories.CAGE_BLOCKS);
                if (material.equalsIgnoreCase("randomblock")) {
                    s.addInfo("RandomBlock", "RandomBlock");
                }

                if (parse.length == 5) {
                    try {
                        s.addInfo("Particle", Particle.valueOf(parse[4]));
                    } catch (Exception e) {
                        RealSkywars.log(Level.WARNING, parse[4] + " isnt a valid particle! Changed to drip lava.");
                        s.addInfo("Particle", Particle.DRIP_LAVA);
                    }
                }

                if (error) {
                    s.setInteractive(false);
                    s.setName("&4Configuration Error. &cEnable debug for more info. Line &f" + i);
                    s.makeItem();
                }

                items.add(s);
                i++;
            }

            if (items.size() == 0) {
                items.add(new DisplayItem());
            }

            return items;
        }

    }

    public enum Categories {
        CAGE_BLOCKS, BOW_PARTICLES, KITS, WIN_BLOCKS
    }
}
