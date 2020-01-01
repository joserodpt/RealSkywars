package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;

import org.bukkit.Material;

import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.classes.DisplayItem;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Kit;
import pt.josegamerpt.realskywars.configuration.Shops;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

public class ShopManager {

    public static ArrayList<DisplayItem> getCategoryContents(GamePlayer p, Enum.Categories t) {
        ArrayList<DisplayItem> items = new ArrayList<DisplayItem>();
        if (t == Enum.Categories.CAGEBLOCK) {
            int i = 1;
            for (String sa : Shops.file().getStringList("Main-Shop.Cage-Blocks")) {
                String[] parse = sa.split(">");

                Double price;
                String material = parse[0];
                String name = Text.addColor(parse[2]);
                String perm = parse[3];
                Boolean bought = PlayerManager.boughtItem(p, name, Enum.Categories.CAGEBLOCK);

                try {
                    price = Double.parseDouble(parse[1]);
                } catch (Exception e) {
                    price = 999999D;
                    bought = true;
                }

                Material m = Material.getMaterial(parse[0]);
                if (m == null) {
                    m = Material.BARRIER;
                    Debugger.print("[FATAL] [REALSKYWARS] MATERIAL ISNT VALID: " + material);
                }

                DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, Enum.Categories.CAGEBLOCK);
                items.add(s);
                i++;
            }

        } else if (t == Enum.Categories.KITS) {
            for (Kit a : KitManager.getKits()) {
                Boolean bought = PlayerManager.boughtItem(p, a.id + "", Enum.Categories.KITS);

                DisplayItem s = new DisplayItem(a.id, a.icon, a.name, a.price,
                        bought, a.permission, Enum.Categories.KITS);
                items.add(s);
            }
        }
        if (items.size() == 0) {
            items.add(new DisplayItem());
        }
        return items;
    }
}
