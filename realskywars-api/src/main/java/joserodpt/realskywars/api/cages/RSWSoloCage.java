package joserodpt.realskywars.api.cages;

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

import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;

public class RSWSoloCage implements RSWCage {

    private final int id;
    private final int x, y, z;
    private final int specx, specy, specz;
    private final String worldName;
    private RSWPlayer p;

    public RSWSoloCage(int i, int x, int y, int z, String worldName, int specx, int specy, int specz) {
        this.id = i;
        this.x = x;
        this.y = y;
        this.z = z;
        this.specx = specx;
        this.specy = specy;
        this.specz = specz;
        this.worldName = worldName;
    }

    public Location getLoc() {
        return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z).add(0.5, 0, 0.5);
    }

    public void tpPlayer(RSWPlayer p) {
        this.p = p;
        Location lookat = new Location(Bukkit.getWorld(this.worldName), this.specx, this.specy, this.specz);
        p.teleport(lookAt(getLoc(), lookat));
    }

    public int getID() {
        return this.id;
    }

    public boolean isEmpty() {
        return p == null;
    }

    public void setCage(Material m) {
        World w = Bukkit.getWorld(this.worldName);

        int[][] positions = {
                {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, {0, 3, 0},
                {0, 1, 1}, {0, 2, 1}, {0, 1, -1}, {0, 2, -1},
                {-1, 0, 0}, {-1, 1, 0}, {-1, 2, 0},
                {1, 0, 0}, {1, 1, 0}, {1, 2, 0}
        };

        for (int[] pos : positions) {
            w.getBlockAt(x + pos[0], y + pos[1], z + pos[2]).setType(m);
        }
    }


    public void setCage() {
        setCage((Material) this.p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK));
    }

    public void clearCage() {
        setCage(Material.AIR);
        this.p = null;
    }

    public void addPlayer(RSWPlayer pl) {
        this.p = pl;
        pl.setCage(this);
        this.setCage();
        this.tpPlayer(pl);
    }

    public void removePlayer(RSWPlayer p) {
        p.setCage(null);
        this.p = null;
    }

    public int getMaxPlayers() {
        return 1;
    }

    public List<RSWPlayer> getPlayers() {
        return Collections.singletonList(this.p);
    }

    public void open() {
        World w = Bukkit.getWorld(this.worldName);
        w.getBlockAt(x, y - 1, z).setType(Material.AIR);

        if (this.p != null)
            this.p.setInvincible(true);

    }

    //CREDIT open source spigot
    public static Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }
}
