package josegamerpt.realskywars.kits;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Kit {

    private final String name;
    private Double price;
    private ItemStack[] contents;
    private int id;
    private Material icon;
    private String permission;
    private final boolean buyable;
    private boolean enderPearlGive = false;
    private int enderTask = -2;

    public Kit(int ID, String n, Double cost, Material ic, ItemStack[] contents, String perm) {
        this.id = ID;
        this.name = n;
        this.price = cost;
        this.icon = ic;
        this.contents = contents;
        this.permission = perm;
        this.buyable = true;
    }

    public Kit(int ID, String n, Double cost, ItemStack[] contents) {
        this.id = ID;
        this.name = n;
        this.price = cost;
        this.icon = Material.LEATHER_CHESTPLATE;
        this.contents = contents;
        this.permission = "RealSkywars.Kit";
        this.buyable = true;
    }

    public Kit() {
        this.name = "none";
        this.buyable = false;
    }

    public void saveKit() {
        RealSkywars.getPlugin().getKitManager().getKits().add(this);
        RealSkywars.getPlugin().getKitManager().registerKit(this);
    }

    public void save() {
        RealSkywars.getPlugin().getKitManager().getKits().remove(this);
        RealSkywars.getPlugin().getKitManager().getKits().add(this);
    }

    public void deleteKit() {
        RealSkywars.getPlugin().getKitManager().unregisterKit(this);
        RealSkywars.getPlugin().getKitManager().getKits().remove(this);
    }

    public List<String> getDescription(boolean shop) {
        if (!this.buyable) {
            return Collections.emptyList();
        }

        ArrayList<String> desc = new ArrayList<>();

        desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_PRICE).replace("%price%", this.price.toString()));

        if (this.enderPearlGive) {
            desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_ENDERPERK));
        }

        desc.add("");
        desc.add(shop ? RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_BUY) : RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_SELECT));

        //contents
        if (this.hasItems()) {
            desc.add("");
            desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_CONTAINS));
            for (ItemStack s : this.contents) {
                if (s != null) {
                    desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_ITEM).replace("%amount%", s.getAmount() + "").replace("%item%", RealSkywars.getPlugin().getNMS().getItemName(s)));
                }
            }
        }

        return desc;
    }

    public boolean hasItems() {
        for (ItemStack s : this.contents) {
            if (s != null) return true;
        }
        return false;
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
            if (i == KitManager.KitPerks.ENDER_PEARl) {
                return this.enderPearlGive;
            }
            throw new Exception(i.name() + " perk doesnt exist in the code!!!!");
        } catch (Exception e) {
            RealSkywars.getPlugin().log(Level.SEVERE, "Error in getting Kit's perk: " + e.getMessage());
        }
        return false;
    }

    public void setPerk(KitManager.KitPerks perk, boolean b) {
        if (perk == KitManager.KitPerks.ENDER_PEARl) {
            this.enderPearlGive = b;
        }
    }

    public void give(RSWPlayer p) {
        if (!p.isBot() && p.hasKit()) {
            p.getPlayer().getInventory().setContents(this.contents);
            this.startTasks(p);
        }
    }

    private void startTasks(RSWPlayer p) {
        if (this.enderPearlGive) {
            this.enderTask = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                if (p.isInMatch()) {
                    p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                } else {
                    Bukkit.getScheduler().cancelTask(enderTask);
                }
            }, Config.file().getInt("Config.Kits.Ender-Pearl-Perk-Give-Interval"));
        }
    }

    public void cancelTasks() {
        if (this.enderTask != -2) {
            Bukkit.getScheduler().cancelTask(this.enderTask);
        }
    }
}