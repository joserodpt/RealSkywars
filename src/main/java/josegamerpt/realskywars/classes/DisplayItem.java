package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.managers.KitManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DisplayItem {

    private int id;
    private Material m;
    private ItemStack i;
    private Double price;
    private Boolean bought = false;
    private String name;
    private String permission;
    private Boolean interactive;
    private ShopManager.Categories it;
    private HashMap<String, Object> info = new HashMap<>();

    public DisplayItem(int id, Material ma, String n, Double per, Boolean b, String perm, ShopManager.Categories t) {
        this.id = id;
        this.price = per;
        this.bought = b;
        this.name = n;
        this.permission = perm;
        this.interactive = true;
        this.it = t;
        this.m = ma;
        makeItemStack(ma);
    }

    public DisplayItem() {
        this.interactive = false;
        this.i = Itens.createItemLore(Material.BUCKET, 1, "&aEmpty", Collections.singletonList("&fNothing found in this category."));
    }

    public void makeItem() {
        makeItemStack(m);
    }

    public void addInfo(String a, Object b) {
        info.put(a, b);
    }

    private void makeItemStack(Material m) {
        if (it == ShopManager.Categories.KITS) {
            Kit k = KitManager.getKit(name);
            if (!bought) {
                i = Itens.createItemLore(m, 1, k.getName(), k.getDescription(true));
            } else {
                i = Itens.createItemLoreEnchanted(m, 1, k.getName(), k.getDescription(false));
            }
        } else {
            if (bought) {
                i = Itens.createItemLoreEnchanted(m, 1, name, Collections.singletonList("&aYou already bought this."));
            } else {
                i = Itens.createItemLore(m, 1, name, Arrays.asList("&fPrice: &b" + price, "&fClick to Buy."));
            }
        }
    }

    public Object getInfo(String s) {
        if (info.containsKey(s)) {
            return info.get(s);
        }
        return null;
    }

    public boolean containsInfo(String randomBlock) {
        return info.containsKey(randomBlock);
    }

    public boolean isBought() {
        return this.bought;
    }

    public void setBought(boolean b) {
        this.bought = b;
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    public void setInteractive(boolean b) {
        this.interactive = b;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        name = s;
    }

    public Double getPrice() {
        return this.price;
    }

    public ItemStack getItemStack() {
        return this.i;
    }

    public int getID() {
        return this.id;
    }

    public Material getMaterial() {
        return this.m;
    }
}
