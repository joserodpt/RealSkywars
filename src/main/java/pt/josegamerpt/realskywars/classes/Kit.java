package pt.josegamerpt.realskywars.classes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pt.josegamerpt.realskywars.managers.KitManager;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    public String name;
    public Double price;
    public ItemStack[] contents;
    public int id;
    public Material icon;
    public String permission;
    public boolean buyable;
    public boolean doubleJump = false;
    public boolean enderPearlGive = false;

    public Kit(int ID, String n, Double cost, Material ic, ItemStack[] contents, String perm) {
        this.id = ID;
        this.name = n;
        this.price = cost;
        this.icon = ic;
        this.contents = contents;
        this.permission = perm;
        this.buyable = true;
    }

    public void saveKit() {
        KitManager.getKits().add(this);
        KitManager.registerKit(this);
    }

    public void save() {
        KitManager.getKits().remove(this);
        KitManager.getKits().add(this);
    }

    public void deleteKit() {
        KitManager.unregisterKit(this);
        KitManager.getKits().remove(this);
    }

    public List<String> getDescription(boolean shop) {
        ArrayList<String> desc = new ArrayList<>();
        if (shop) {
            desc.add("&fPrice: &b" + this.price);
        }
        desc.add("&eThis kit contains:");

        for (ItemStack s : contents) {
            if (s != null) {
                desc.add("&fx" + s.getAmount() + " &9" + s.getType().name());
            }
        }

        desc.add("");
        if (!shop) {
            desc.add("&fClick to select this kit.");
        } else {
            desc.add("&fClick to buy this kit.");
        }

        return desc;
    }
}
