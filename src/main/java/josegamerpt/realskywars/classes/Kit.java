package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.managers.KitManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private String name;
    private Double price;
    private ItemStack[] contents;
    private int id;
    private Material icon;
    private String permission;
    private boolean buyable = false;
    private boolean doubleJump = false;
    private boolean enderPearlGive = false;
    private boolean empty = false;

    public Kit(int ID, String n, Double cost, Material ic, ItemStack[] contents, String perm) {
        this.id = ID;
        this.name = n;
        this.price = cost;
        this.icon = ic;
        this.contents = contents;
        this.permission = perm;
        this.buyable = true;
    }

    public Kit() {
        this.name = "None";
        this.empty = true;
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

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.id;
    }

    public Double getPrice() {
        return this.price;
    }

    public Material getIcon() {
        return this.icon;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public String getPermission() {
        return this.permission;
    }

    public Boolean getPerk(KitManager.KitPerks i) {
        try {
            switch (i) {
                case DOUBLE_JUMP:
                    return doubleJump;
                case ENDER_PEARl:
                    return enderPearlGive;
                default:
                    throw new Exception(i.name() + " doesnt exist in the code!!!!");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void setPerk(KitManager.KitPerks perk, boolean b) {
        switch (perk)
        {
            case DOUBLE_JUMP:
                doubleJump = b;
                break;
            case ENDER_PEARl:
                enderPearlGive = b;
                break;
        }
    }
}
