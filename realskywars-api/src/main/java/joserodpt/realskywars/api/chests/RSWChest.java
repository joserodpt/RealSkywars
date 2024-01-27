package joserodpt.realskywars.api.chests;

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
import joserodpt.realskywars.api.game.RSWCountdown;
import joserodpt.realskywars.api.game.SWEvent;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.managers.holograms.RSWHologram;
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

public class RSWChest {
    private final int x, y, z;
    private final String worldName;
    private final Type type;
    private final BlockFace bf;
    private final RSWHologram hologram;
    private List<RSWChestItem> items = new ArrayList<>();
    private Boolean opened = false;
    private int maxItemsPerChest;
    private RSWCountdown chestCTD;

    public RSWChest(Type ct, String worldName, int x, int y, int z, BlockFace bf) {
        this.type = ct;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.bf = bf;
        this.hologram = RealSkywarsAPI.getInstance().getHologramManagerAPI().getHologramInstance();
        this.clear();
    }

    @Override
    public String toString() {
        return "SWChest{" + "x=" + x + ", y=" + y + ", z=" + z + ", worldName='" + worldName + '\'' + ", items=" + items + ", type=" + type + ", opened=" + opened + ", maxItemsPerChest=" + maxItemsPerChest + ", chestCTD=" + chestCTD + '}';
    }

    public void setLoot(List<RSWChestItem> chest, int maxItemsPerChest) {
        this.items.clear();
        this.items = chest;
        this.maxItemsPerChest = maxItemsPerChest;
    }

    public Type getType() {
        return this.type;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z);
    }

    public void clear() {
        this.cancelTasks();
        if (this.hologram != null) {
            this.hologram.deleteHologram();
        }
        this.setChest();
        ((Chest) this.getChestBlock().getState()).getInventory().clear();
        this.opened = false;
    }

    public void setChest() {
        this.getLocation().getWorld().getBlockAt(this.getLocation()).setType(Material.CHEST);
        Block b = this.getLocation().getWorld().getBlockAt(this.getLocation());
        BlockData blockData = b.getBlockData();
        ((Directional) blockData).setFacing(this.bf);
        b.setBlockData(blockData);
    }

    public Block getChestBlock() {
        return this.getLocation().getBlock();
    }

    public Boolean isChest() {
        return this.getLocation().getBlock().getType().equals(Material.CHEST);
    }

    public void populate() {
        if (!isOpened() && isChest()) {
            this.opened = true;
            Inventory inv = ((Chest) getChestBlock().getState()).getInventory();

            List<ItemStack> tmp = new ArrayList<>();
            for (RSWChestItem item : this.items) {
                int chance = RealSkywarsAPI.getInstance().getRandom().nextInt(100);
                if (chance < item.getChance()) {
                    tmp.add(item.getItemStack());
                }
            }

            Collections.shuffle(tmp);
            if (tmp.size() > this.maxItemsPerChest) {
                tmp = tmp.subList(0, this.maxItemsPerChest);
            }

            if (RSWConfig.file().getBoolean("Config.Shuffle-Items-In-Chest")) {
                boolean[] chosen = new boolean[inv.getSize()];
                for (ItemStack itemStack : tmp) {
                    int slot;

                    do {
                        slot = RealSkywarsAPI.getInstance().getRandom().nextInt(inv.getSize());
                    } while (chosen[slot]);

                    chosen[slot] = true;
                    inv.setItem(RealSkywarsAPI.getInstance().getRandom().nextInt(inv.getSize()), itemStack);
                }
            } else {
                tmp.forEach(inv::addItem);
            }
        }
    }

    public void startTasks(RSWGame sgm) {
        if (this.chestCTD == null && this.isChest()) {
            int time = RSWConfig.file().getInt("Config.Default-Refill-Time");

            Optional<SWEvent> e = getRefillTime(sgm);
            if (e.isPresent()) {
                time = e.get().getTimeLeft();
            }

            this.hologram.spawnHologram(this.getLocation());
            this.hologram.setTime(time);

            this.chestCTD = new RSWCountdown(RealSkywarsAPI.getInstance().getPlugin(), time, () -> {
                //
            }, () -> {
                this.getLocation().getWorld().spawnParticle(Particle.CLOUD, this.getLocation().add(0.5, 0, 0.5), 5);
                if (this.isChest()) {
                    RealSkywarsAPI.getInstance().getNMS().playChestAnimation(this.getChestBlock(), false);
                }
                this.clear();
                this.hologram.deleteHologram();
            }, (t) -> {
                this.hologram.setTime(t.getSecondsLeft());
                if (this.isChest()) {
                    RealSkywarsAPI.getInstance().getNMS().playChestAnimation(this.getChestBlock(), true);
                }
            });

            this.chestCTD.scheduleTimer();
        }
    }

    private Optional<SWEvent> getRefillTime(RSWGame sgm) {
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

    public void clearHologram() {
        this.hologram.deleteHologram();
    }
    public enum Tier { BASIC, NORMAL, EPIC }

    public enum Type {NORMAL, MID}

}
