package josegamerpt.realskywars.cages;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;

public class TeamCage implements Cage {

    private final int id;
    private final String worldName;
    private final int maxPlayers;
    private final ArrayList<RSWPlayer> p = new ArrayList<>();
    private final Location loc;

    public TeamCage(int i, int x, int y, int z, String worldName, int maxM) {
        this.id = i;
        this.loc = new Location(Bukkit.getWorld(worldName), x, y, z);
        this.worldName = worldName;
        this.maxPlayers = maxM;
    }

    public int getID() {
        return this.id;
    }

    public Location getLoc() {
        return new Location(Bukkit.getWorld(this.worldName), this.loc.getBlockX(), this.loc.getBlockY(), this.loc.getBlockZ()).add(0.5, 0, 0.5);
    }

    public boolean isEmpty() {
        return !p.isEmpty();
    }

    public void setCage(Material m) {
        World w = Bukkit.getWorld(this.worldName);
        int x = this.loc.getBlockX();
        int y = this.loc.getBlockY();
        int z = this.loc.getBlockZ();

        int[] yOffset = {-1, 4};
        int[] xOffset = {-1, 0, 1};
        int[] zOffset = {-1, 0, 1};

        for (int dy : yOffset) {
            for (int dx : xOffset) {
                for (int dz : zOffset) {
                    w.getBlockAt(x + dx, y + dy, z + dz).setType(m);
                }
            }
        }

        x = this.loc.getBlockX() + 2;
        for (int dx : xOffset) {
            for (int dy = 0; dy <= 2; dy++) {
                w.getBlockAt(x, y + dy, z + dx).setType(m);
            }
        }

        x = this.loc.getBlockX() - 2;
        for (int dx : xOffset) {
            for (int dy = 0; dy <= 2; dy++) {
                w.getBlockAt(x, y + dy, z + dx).setType(m);
            }
        }

        z = this.loc.getBlockZ() + 2;
        for (int dz : zOffset) {
            for (int dy = 0; dy <= 2; dy++) {
                w.getBlockAt(x + dz, y + dy, z).setType(m);
            }
        }

        z = this.loc.getBlockZ() - 2;
        for (int dz : zOffset) {
            for (int dy = 0; dy <= 2; dy++) {
                w.getBlockAt(x + dz, y + dy, z).setType(m);
            }
        }
    }

    public void addPlayer(RSWPlayer p) {
        this.p.add(p);
        this.setCage();
    }

    public void removePlayer(RSWPlayer p) {
        this.p.remove(p);
    }

    public void tpPlayer(RSWPlayer p) {
        p.teleport(getLoc());
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public ArrayList<RSWPlayer> getPlayers() {
        return this.p;
    }

    @Override
    public void clearCage() {
        this.setCage(Material.AIR);
    }

    @Override
    public void setCage() {
        this.setCage((Material) this.p.get(0).getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK));
    }

    public void open() {
        Material m = Material.AIR;
        World w = Bukkit.getWorld(worldName);

        int x = this.loc.getBlockX();
        int y = this.loc.getBlockY();
        int z = this.loc.getBlockZ();

        int[] xOffset = {0, 1, -1};
        int[] zOffset = {0, 1, -1};

        for (int dx : xOffset) {
            for (int dz : zOffset) {
                w.getBlockAt(x + dx, y - 1, z + dz).setType(m);
            }
        }
    }

}
