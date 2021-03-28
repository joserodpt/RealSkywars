package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.classes.DisplayItem;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.Kit;
import josegamerpt.realskywars.configuration.Shops;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.ArrayList;

public class ShopManager {

    public static ArrayList<DisplayItem> getCategoryContents(RSWPlayer p, Enum.Categories t) {
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
                for (Kit a : KitManager.getKits()) {
                    Boolean bought = PlayerManager.boughtItem(p, a.getName(), Enum.Categories.KITS);

                    DisplayItem s = new DisplayItem(a.getID(), a.getIcon(), a.getName(), a.getPrice(),
                            bought, a.getPermission(), Enum.Categories.KITS);
                    items.add(s);
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
                Boolean bought = PlayerManager.boughtItem(p, name, t);

                try {
                    price = Double.parseDouble(parse[1]);
                } catch (Exception e) {
                    price = 999999D;
                    error = true;
                }

                Material m;

                if (material.equalsIgnoreCase("randomblock"))
                {
                    m = Material.COMMAND_BLOCK;
                } else {
                    m = Material.getMaterial(material);
                }

                if (m == null) {
                    m = Material.BARRIER;
                    Debugger.print(ShopManager.class, "[FATAL] MATERIAL ISNT VALID: " + material);
                    error = true;
                }

                DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, Enum.Categories.CAGE_BLOCKS);
                if (material.equalsIgnoreCase("randomblock"))
                {
                    s.addInfo("RandomBlock", "RandomBlock");
                }

                if (parse.length == 5) {
                    s.addInfo("Particle", Particle.valueOf(parse[4]));
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
}
