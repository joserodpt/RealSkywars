package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.managers.KitManager;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.mackan.ItemNames.ItemNames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kit {

    private String name;
    private Double price;
    private ItemStack[] contents;
    private int id;
    private Material icon;
    private String permission;
    private boolean buyable;
    private boolean enderPearlGive = false;
    private BukkitTask enderTask;

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
        this.buyable = false;
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
        if (!buyable)
        {
            return Collections.emptyList();
        }

        ArrayList<String> desc = new ArrayList<>();

        desc.add("&fCusto: &9" + this.price);

        if (enderPearlGive) {
            desc.add("&fThis kit has the &5Ender &dPerk");
        }

        desc.add("");
        if (shop) {
            desc.add("&fClick to buy this kit.");
        } else {
            desc.add("&fClick to select this kit.");
        }

        desc.add("");

        //contents
        desc.add("&eThis kit contains:");
        for (ItemStack s : contents) {
            if (s != null) {
                desc.add("&fx" + s.getAmount() + " &9" + ItemNames.getItemName(s));
            }
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
                case ENDER_PEARl:
                    return this.enderPearlGive;
                default:
                    throw new Exception(i.name() + " perk doesnt exist in the code!!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPerk(KitManager.KitPerks perk, boolean b) {
        switch (perk) {
            case ENDER_PEARl:
                this.enderPearlGive = b;
                break;
        }
    }

    public void give(RSWPlayer p) {
        if (!p.isBot()) {
            p.getPlayer().getInventory().setContents(this.contents);
            this.startTasks(p);
        }
    }

    private void startTasks(RSWPlayer p) {
        if (this.enderPearlGive) {
            this.enderTask = new BukkitRunnable() {
                public void run() {
                    p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                }
            }.runTaskTimerAsynchronously(RealSkywars.getPlugin(), Config.file().getInt("Config.Kits.Ender-Pearl-Perk-Give-Interval:"), 20); // Spelled Async wrong and I know it, deal with it haha
        }
    }

    public void cancelTasks() {
        if (this.enderTask != null) {
            this.enderTask.cancel();
        }
    }
}
