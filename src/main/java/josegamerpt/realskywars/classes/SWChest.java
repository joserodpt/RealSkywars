package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SWChest {

    private final int x;
    private final int y;
    private final int z;
    private final String worldName;
    private List<SWChestItem> items = new ArrayList<>();
    private ChestTYPE type;
    private Boolean filled = false;
    private int maxItemsPerChest;

    public SWChest(ChestTYPE ct, String worldName, int x, int y, int z) {
        this.type = ct;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public void setLoot(List<SWChestItem> chest, int maxItemsPerChest) {
        this.items.clear();
        this.items = chest;
        this.maxItemsPerChest = maxItemsPerChest;
    }

    public ChestTYPE getType() {
        return this.type;
    }

    public Boolean isMiddle() {
        return this.type == ChestTYPE.MID;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z);
    }

    public void clear() {
        this.getChest().getInventory().clear();
        this.filled = false;
    }

    public Chest getChest() {
        return (Chest) getLocation().getBlock().getState();
    }

    public void fill() {
        if (!filled) {

            this.filled = true;
            Inventory inv = getChest().getInventory();

            List<ItemStack> tmp = new ArrayList<>();
            for (SWChestItem item : this.items) {
                int chance = RealSkywars.getRandom().nextInt(100);
                if (chance < item.getChance()) {
                    tmp.add(item.getItemStack());
                }
            }

            Collections.shuffle(tmp);
            if (tmp.size() > 5) {
                tmp = tmp.subList(0, this.maxItemsPerChest);
            }

            if (Config.file().getBoolean("Config.Shuffle-Items-In-Chest")) {
                boolean[] chosen = new boolean[inv.getSize()];

                for (ItemStack itemStack : tmp) {
                    int slot;

                    do {
                        slot = RealSkywars.getRandom().nextInt(inv.getSize());
                    } while (chosen[slot]);

                    chosen[slot] = true;
                    inv.setItem(RealSkywars.getRandom().nextInt(inv.getSize()), itemStack);
                }
            } else {
                tmp.forEach(inv::addItem);
            }

        }
    }

    public enum ChestTYPE {NORMAL, MID}

}
