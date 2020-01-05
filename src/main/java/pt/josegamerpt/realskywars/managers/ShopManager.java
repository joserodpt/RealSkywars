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
        int i = 0;
        switch (t) {
            case CAGEBLOCK:
                for (String sa : Shops.file().getStringList("Main-Shop.Cage-Blocks")) {
                    String[] parse = sa.split(">");

                    Boolean error = false;
                    double price;
                    String material = parse[0];
                    String name = Text.addColor(parse[2]);
                    String perm = parse[3];
                    Boolean bought = PlayerManager.boughtItem(p, name, Enum.Categories.CAGEBLOCK);

                    try {
                        price = Double.parseDouble(parse[1]);
                    } catch (Exception e) {
                        price = 999999D;
                        error = true;
                    }

                    Material m = Material.getMaterial(material);
                    if (m == null) {
                        m = Material.BARRIER;
                        Debugger.print("[FATAL] MATERIAL ISNT VALID: " + material);
                        error = true;
                    }

                    DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, Enum.Categories.CAGEBLOCK);

                    if (error == true) {
                        s.interactable = false;
                        s.setName("&4Configuration Error. &cEnable debug for more info. Line &f" + i);
                        s.makeItem();
                    }

                    items.add(s);
                    i++;
                }
                break;
            case WINBLOCKS:
                for (String sa : Shops.file().getStringList("Main-Shop.Win-Blocks")) {
                    String[] parse = sa.split(">");

                    Boolean error = false;
                    double price;
                    String material = parse[1];
                    String name = Text.addColor(parse[0]);
                    String perm = parse[3];
                    Boolean bought = PlayerManager.boughtItem(p, name, Enum.Categories.WINBLOCKS);

                    try {
                        price = Double.parseDouble(parse[2]);
                    } catch (Exception e) {
                        price = 999999D;
                        error = true;
                    }

                    if (!material.equalsIgnoreCase("randomblock")) {
                        Material m = Material.getMaterial(material);
                        if (m == null) {
                            m = Material.BARRIER;
                            Debugger.print("[FATAL] MATERIAL ISNT VALID: " + material);
                            error = true;
                        }

                        DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, Enum.Categories.WINBLOCKS);

                        if (error == true) {
                            s.interactable = false;
                            s.setName("&4Configuration Error. &cEnable debug for more info. Line &f" + i);
                            s.makeItem();
                        }

                        items.add(s);
                        i++;
                    } else {
                        DisplayItem s = new DisplayItem(i, Material.ENDER_CHEST, name, price, bought, perm, Enum.Categories.WINBLOCKS);
                        s.addInfo("RandomBlock", "RandomBlock");
                        if (error == true) {
                            s.interactable = false;
                            s.setName("&4Configuration Error. &cEnable debug for more info. Line &f" + i);
                            s.makeItem();
                        }

                        items.add(s);
                        i++;
                    }
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
                for (String sa : Shops.file().getStringList("Main-Shop.Bow-Particles")) {
                    String[] parse = sa.split(">");

                    double price;
                    String particle = parse[0];
                    String material = parse[4];
                    String name = Text.addColor(parse[2]);
                    String perm = parse[3];
                    Boolean bought = PlayerManager.boughtItem(p, name, Enum.Categories.BOWPARTICLE);

                    boolean error = false;
                    try {
                        price = Double.parseDouble(parse[1]);
                    } catch (Exception e) {
                        price = 9999999999999D;
                        error = true;
                    }

                    Material m = Material.getMaterial(material);
                    if (m == null) {
                        m = Material.BARRIER;
                        Debugger.print("[FATAL] MATERIAL ISNT VALID: " + material);
                        error = true;
                    }

                    DisplayItem s = new DisplayItem(i, m, name, price, bought, perm, Enum.Categories.BOWPARTICLE);
                    s.addInfo("Particle", Particle.valueOf(particle));

                    if (error == true) {
                        s.interactable = false;
                        s.setName("&4Configuration Error. &cEnable debug for more info. Line &f" + i);
                        s.makeItem();
                    }

                    items.add(s);
                    i++;
                }
                break;
        }
        if (items.size() == 0) {
            items.add(new DisplayItem());
        }
        return items;
    }
}
