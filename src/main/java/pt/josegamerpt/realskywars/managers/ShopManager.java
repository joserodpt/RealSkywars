package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;

import org.bukkit.Material;

import org.bukkit.Particle;
import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.classes.DisplayItem;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Kit;
import pt.josegamerpt.realskywars.configuration.Shops;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

public class ShopManager {

    public static ArrayList<DisplayItem> getCategoryContents(GamePlayer p, Enum.Categories t) {
        ArrayList<DisplayItem> items = new ArrayList<>();
        switch (t) {
            case CAGEBLOCK:
                int i = 1;
                for (String sa : Shops.file().getStringList("Main-Shop.Cage-Blocks")) {
                    String[] parse = sa.split(">");

                    double price;
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
                        Debugger.print("[FATAL] MATERIAL ISNT VALID: " + material);
                    }

                    DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, Enum.Categories.CAGEBLOCK);
                    items.add(s);
                    i++;
                }
                break;
            case KITS:
                for (Kit a : KitManager.getKits()) {
                    Boolean bought = PlayerManager.boughtItem(p, a.name, Enum.Categories.KITS);

                    DisplayItem s = new DisplayItem(a.id, a.icon, a.name, a.price,
                            bought, a.permission, Enum.Categories.KITS);
                    items.add(s);
                }
                break;
            case BOWPARTICLE:
                int i2 = 1;
                for (String sa : Shops.file().getStringList("Main-Shop.Bow-Particles")) {
                    String[] parse = sa.split(">");

                    double price;
                    String particle = parse[0];
                    String material = parse[4];
                    String name = Text.addColor(parse[2]);
                    String perm = parse[3];
                    Boolean bought = PlayerManager.boughtItem(p, name, Enum.Categories.BOWPARTICLE);

                    try {
                        price = Double.parseDouble(parse[1]);
                    } catch (Exception e) {
                        price = 999999D;
                        bought = true;
                    }

                    Material m = Material.getMaterial(material);
                    if (m == null) {
                        m = Material.BARRIER;
                        Debugger.print("[FATAL] MATERIAL ISNT VALID: " + material);
                    }

                    DisplayItem s = new DisplayItem(i2, m, name, price, bought, perm, Enum.Categories.BOWPARTICLE);
                    s.addInfo("Particle", Particle.valueOf(particle));
                    items.add(s);
                    i2++;
                }
                break;
        }
        if (items.size() == 0) {
            items.add(new DisplayItem());
        }
        return items;
    }
}
