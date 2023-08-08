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

public class Kit {
    public enum Perks {ENDER}

    private final String name, displayname;
    private Double price;
    private KitInventory kitInventory;
    private Material icon;
    private String permission;
    private List<Perks> kit_perks = new ArrayList<>();
    private boolean buyable = true;
    private int enderTask = -2;

    public Kit(String name, String displayname, Double cost, Material ic, KitInventory kitInventory, String perm) {
        this.name = name;
        this.displayname = displayname;
        this.price = cost;
        this.icon = ic;
        this.kitInventory = kitInventory;
        this.permission = perm;
    }

    public Kit(String name, String displayname, Double cost, KitInventory kitInventory) {
        this.name = name;
        this.displayname = displayname;
        this.price = cost;
        this.icon = Material.LEATHER_CHESTPLATE;
        this.kitInventory = kitInventory;
        this.permission = "RealSkywars.Kit";
    }

    public Kit() {
        this.name = "none";
        this.displayname = this.name;
        this.buyable = false;
    }


    public List<String> getDescription(boolean shop) {
        if (!this.buyable) {
            return Collections.emptyList();
        }

        ArrayList<String> desc = new ArrayList<>();

        desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_PRICE).replace("%price%", this.price.toString()));

        //contents
        if (this.hasItems()) {
            desc.add("");
            desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_CONTAINS));

            for (ItemStack s : this.getKitInventory().getListInventory()) {
                desc.add(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_ITEM).replace("%amount%", s.getAmount() + "").replace("%item%", RealSkywars.getPlugin().getNMS().getItemName(s)));
            }
        }

        if (this.hasPerk(Perks.ENDER)) {
            desc.add("&fx1 Perk: &dEnder");
        }

        desc.add("");
        desc.add(shop ? RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_BUY) : RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.KIT_SELECT));

        return desc;
    }

    public List<Perks> getKitPerks() {
        return this.kit_perks;
    }

    public boolean hasPerk(Perks perk) {
        return this.getKitPerks().contains(perk);
    }

    public boolean hasItems() {
        return this.kitInventory.hasItems();
    }

    public String getDisplayName() {
        return this.displayname;
    }

    public String getName() {
        return this.name;
    }

    public Double getPrice() {
        return this.price;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getPermission() {
        return this.permission;
    }


    public void addPerk(Perks perk) {
        if (!this.hasPerk(perk)) {
            this.getKitPerks().add(perk);
        }
    }

    public void removePerk(Perks perk) {
        if (this.hasPerk(perk)) {
            this.getKitPerks().remove(perk);
        }
    }

    public void addPerk(String perkName) {
        Kit.Perks kp;
        try {
            kp = Kit.Perks.valueOf(perkName.toUpperCase());
            this.addPerk(kp);
        } catch (Exception e) {
            Bukkit.getLogger().severe(perkName + " isn't a valid Kit Perk. Ignoring.");
        }
    }

    public void give(RSWPlayer p) {
        if (p.getPlayer().isOp()) {
            this.getKitInventory().giveToPlayer(p);
            return;
        }

        if (!p.isBot() && p.hasKit()) {
            this.getKitInventory().giveToPlayer(p);
            this.startTasks(p);
        }
    }

    public KitInventory getKitInventory() {
        return this.kitInventory;
    }

    private void startTasks(RSWPlayer p) {
        if (this.hasPerk(Perks.ENDER)) {
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

    @Override
    public String toString() {
        return "Kit{" +
                "name='" + name + '\'' +
                ", displayname='" + displayname + '\'' +
                ", price=" + price +
                ", kitInventory=" + kitInventory +
                ", icon=" + icon +
                ", permission='" + permission + '\'' +
                ", kit_perks=" + kit_perks +
                ", buyable=" + buyable +
                ", enderTask=" + enderTask +
                '}';
    }
}