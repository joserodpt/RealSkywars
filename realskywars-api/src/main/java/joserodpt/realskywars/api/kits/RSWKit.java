package joserodpt.realskywars.api.kits;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RSWKit {
    public enum Perks {ENDER}

    private final String name, displayname;
    private Double price;
    private KitInventory kitInventory;
    private Material icon;
    private String permission;
    private final List<Perks> kitPerks = new ArrayList<>();
    private boolean buyable = true;
    private boolean none = false;
    private int enderTask = -2;

    public RSWKit(String name, String displayname, Double cost, Material ic, KitInventory kitInventory, String perm) {
        this.name = name;
        this.displayname = displayname;
        this.price = cost;
        this.icon = ic;
        this.kitInventory = kitInventory;
        this.permission = perm;
    }

    public RSWKit(String name, String displayname, Double cost, KitInventory kitInventory) {
        this.name = name;
        this.displayname = displayname;
        this.price = cost;
        this.icon = Material.LEATHER_CHESTPLATE;
        this.kitInventory = kitInventory;
        this.permission = "rsw.kit";
    }

    public RSWKit() {
        this.name = "None";
        this.none = true;
        this.displayname = this.name;
        this.buyable = false;
    }

    public List<String> getDescription(RSWPlayer p, Pair<Boolean, String> boughtPair) {
        if (!this.buyable) {
            return Collections.emptyList();
        }

        List<String> desc = new ArrayList<>();

        desc.add(TranslatableLine.KIT_PRICE.get(p).replace("%price%", this.price.toString()));

        //contents
        if (this.hasItems()) {
            desc.add("");
            desc.add(TranslatableLine.KIT_CONTAINS.get(p));

            for (ItemStack s : this.getKitInventory().getListInventory()) {
                desc.add(TranslatableLine.KIT_ITEM.get(p).replace("%amount%", s.getAmount() + "").replace("%item%", RealSkywarsAPI.getInstance().getLanguageManagerAPI().getMaterialName(p, s.getType())));
            }
        }

        if (this.hasPerk(Perks.ENDER)) {
            desc.add("&fx1 Perk: &dEnder");
        }

        desc.add("");
        if (boughtPair.getKey()) {
            desc.add(TranslatableLine.SHOP_BOUGHT_ON.get(p) + boughtPair.getValue());
        }
        desc.add(boughtPair.getKey() ? TranslatableLine.KIT_SELECT.get(p) : TranslatableLine.KIT_BUY.get(p));

        return desc;
    }

    public List<Perks> getKitPerks() {
        return this.kitPerks;
    }

    public boolean hasPerk(Perks perk) {
        return this.getKitPerks().contains(perk);
    }

    public boolean hasItems() {
        return this.getKitInventory() != null && this.getKitInventory().hasItems();
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
        RSWKit.Perks kp;
        try {
            kp = RSWKit.Perks.valueOf(perkName.toUpperCase());
            this.addPerk(kp);
        } catch (Exception e) {
            RealSkywarsAPI.getInstance().getLogger().severe(perkName + " isn't a valid Kit Perk. Ignoring.");
        }
    }

    public void give(RSWPlayer p) {
        if (this.none) {
            return;
        }

        if (this.getKitInventory() == null) {
            RealSkywarsAPI.getInstance().getLogger().severe(this.getName() + " kit content's are null (?) Skipping give order.");
            return;
        }

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
            this.enderTask = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealSkywarsAPI.getInstance().getPlugin(), () -> {
                if (p.isInMatch()) {
                    p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                } else {
                    Bukkit.getScheduler().cancelTask(enderTask);
                }
            }, RSWConfig.file().getInt("Config.Kits.Ender-Pearl-Perk-Give-Interval"));
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
                ", kit_perks=" + kitPerks +
                ", buyable=" + buyable +
                ", enderTask=" + enderTask +
                '}';
    }
}