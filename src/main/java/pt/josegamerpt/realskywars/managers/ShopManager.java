package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.classes.Enum.ShopCategory;
import pt.josegamerpt.realskywars.classes.Kit;
import pt.josegamerpt.realskywars.configuration.Shops;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.utils.Text;
import pt.josegamerpt.realskywars.classes.ShopItem;

public class ShopManager {

    public static ArrayList<ShopItem> getCategoryContents(GamePlayer p, ShopCategory t) {
        ArrayList<ShopItem> items = new ArrayList<ShopItem>();
        if (t == ShopCategory.CAGEBLOCK) {
            int i = 1;
            for (String sa : Shops.file().getStringList("Main-Shop.Cage-Blocks")) {
                String[] parse = sa.split(">");

                Double price;
                String material = parse[0];
                String name = Text.addColor(parse[2]);
                String perm = parse[3];
                Boolean bought = PlayerManager.boughtItem(p, name, ShopCategory.CAGEBLOCK);

                try {
                    price = Double.parseDouble(parse[1]);
                } catch (Exception e) {
                    price = 999999D;
                    bought = true;
                }

                Material m = Material.getMaterial(parse[0]);
                if (m == null) {
                    m = Material.BARRIER;
                    System.out.print("[FATAL] [REALSKYWARS] MATERIAL ISNT VALID: " + material);
                }

                ItemStack item;
                if (bought == true) {
                    item = Itens.createItemLoreEnchanted(m, 1, name, Arrays.asList("&aYou already bought this!"));
                } else {
                    item = Itens.createItemLore(m, 1, name, Arrays.asList("&fPrice: &b" + price, "&fClick to Buy!"));
                }


                ShopItem s = new ShopItem(item, i, price, bought, name, perm);
                items.add(s);
                i++;
            }

        } else if (t == ShopCategory.KITS) {
            for (Kit a : KitManager.getKits()) {
                Boolean bought = PlayerManager.boughtItem(p, a.id + "", ShopCategory.KITS);

                ShopItem s = new ShopItem(makeKitsIcon(a.icon, a.permission, a.price, a.name, bought), a.id, a.price,
                        bought, a.name, a.permission);
                items.add(s);
            }
        }
        if (items.size() == 0) {
            items.add(new ShopItem(Itens.createItemLore(Material.DEAD_BUSH, 1, "&9Empty",
                    Arrays.asList("&fNothing found in this category.")), -1, false));
        }
        return items;
    }

    private static ItemStack makeKitsIcon(Material icon, String permission, Double cost, String name, Boolean bought) {
        if (bought == false) {
            return Itens.createItemLore(icon, 1, name, KitManager.getKit(name).getDescription(true));
        } else {
            return Itens.createItemLoreEnchanted(icon, 1, name, Arrays.asList("&aYou already bought this!"));
        }
    }

}
