package josegamerpt.realskywars.chests;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.holograms.SWHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SWChest {
    private final int x;
    private final int y;
    private final int z;
    private final String worldName;
    private List<SWChestItem> items = new ArrayList<>();
    private final ChestTYPE type;
    private Boolean opened = false;
    private int maxItemsPerChest;
    private final BlockFace bf;
    private final SWHologram hologram;
    private Countdown chestCTD;

    public SWChest(ChestTYPE ct, String worldName, int x, int y, int z, BlockFace bf, boolean real) {
        this.type = ct;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.bf = bf;
        this.hologram = RealSkywars.getHologramManager().getHologramInstance();
        if (real) this.clear();
    }

    @Override
    public String toString() {
        return "SWChest{" + "x=" + x + ", y=" + y + ", z=" + z + ", worldName='" + worldName + '\'' + ", items=" + items + ", type=" + type + ", opened=" + opened + ", maxItemsPerChest=" + maxItemsPerChest + ", chestCTD=" + chestCTD + '}';
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
        this.cancelTasks();
        this.hologram.deleteHologram();
        if (!this.isChest()) {
            this.setChest();
        }
        this.getChest().getInventory().clear();
        this.opened = false;
    }

    private void setChest() {
        this.getLocation().getWorld().getBlockAt(this.getLocation()).setType(Material.CHEST);
        Block b = this.getLocation().getWorld().getBlockAt(this.getLocation());
        BlockData blockData = b.getBlockData();
        ((Directional) blockData).setFacing(this.bf);
        b.setBlockData(blockData);
    }

    public Chest getChest() {
        return (Chest) this.getLocation().getBlock().getState();
    }

    public Boolean isChest() {
        return this.getLocation().getBlock().getType().equals(Material.CHEST);
    }

    public void populate() {
        if (!isOpened() && isChest()) {
            this.opened = true;
            Inventory inv = getChest().getInventory();

            List<ItemStack> tmp = new ArrayList<>();
            for (SWChestItem item : this.items) {
                int chance = RealSkywars.getRandom().nextInt(100);
                if (chance < item.getChance()) {
                    tmp.add(item.getItemStack());
                }
            }

            Collections.shuffle(tmp);
            if (tmp.size() > this.maxItemsPerChest) {
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

    public void startTasks(SWGameMode sgm) {
        if (this.chestCTD == null && this.isChest()) {
            int time = Config.file().getInt("Config.Default-Refill-Time");

            Optional<SWEvent> e = getRefillTime(sgm);
            if (e.isPresent()) {
                time = e.get().getTimeLeft();
            }

            this.hologram.spawnHologram(this.getLocation());
            this.hologram.setTime(time);

            this.chestCTD = new Countdown(RealSkywars.getPlugin(RealSkywars.class), time, () -> {
                //
            }, () -> {
                this.getLocation().getWorld().spawnParticle(Particle.CLOUD, this.getLocation().add(0.5, 0, 0.5), 5);
                if (this.isChest()) {
                    RealSkywars.getNMS().chestAnimation(this.getChest(), false);
                }
                this.clear();
                this.hologram.deleteHologram();
            }, (t) -> {
                this.hologram.setTime(t.getSecondsLeft());
                if (this.isChest()) {
                    RealSkywars.getNMS().chestAnimation(this.getChest(), true);
                }
            });

            this.chestCTD.scheduleTimer();
        }
    }

    private Optional<SWEvent> getRefillTime(SWGameMode sgm) {
        return sgm.getEvents().stream().filter(c -> c.getEventType().equals(SWEvent.EventType.REFILL)).findFirst();
    }

    public void cancelTasks() {
        if (this.chestCTD != null) {
            this.chestCTD.killTask();
            this.chestCTD = null;
        }
    }

    public boolean isOpened() {
        return this.opened;
    }

    public enum ChestTYPE {NORMAL, MID}

}
