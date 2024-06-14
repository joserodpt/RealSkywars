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
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Collections;

public class RSWSoloCage extends RSWCage {

    protected RSWPlayer p;

    public RSWSoloCage(int id, int x, int y, int z) {
        super(id, x, y, z);
    }

    public RSWSoloCage(int id, Location l) {
        super(id, l);
    }

    @Override
    public boolean isEmpty() {
        return this.p == null;
    }

    @Override
    public void tpPlayer(RSWPlayer p) {
        this.p = p;
        p.teleport(lookAt(getLocation(), super.getMap().getSpectatorLocation()));
    }

    @Override
    public void setCage(Material m) {
        if (m == null) {
            m = Material.GLASS;
        }
        for (int[] pos : new int[][]{{0, -1, 0}, {0, 0, 1}, {0, 0, -1}, {0, 3, 0},
                {0, 1, 1}, {0, 2, 1}, {0, 1, -1}, {0, 2, -1},
                {-1, 0, 0}, {-1, 1, 0}, {-1, 2, 0},
                {1, 0, 0}, {1, 1, 0}, {1, 2, 0}}) {
            map.getRSWWorld().getWorld().getBlockAt(x + pos[0], y + pos[1], z + pos[2]).setType(m);
        }
    }

    @Override
    public void setCage() {
        setCage(this.p.getCageBlock());
    }

    @Override
    public void clearCage() {
        setCage(Material.AIR);
        this.p = null;
    }

    @Override
    public void addPlayer(RSWPlayer pl) {
        this.p = pl;
        pl.setPlayerCage(this);
        this.setCage();
        this.tpPlayer(pl);
    }

    @Override
    public void removePlayer(RSWPlayer p) {
        p.setPlayerCage(null);
        this.p = null;
    }

    @Override
    public Collection<RSWPlayer> getPlayers() {
        return Collections.singletonList(this.p);
    }

    @Override
    public void open() {
        map.getRSWWorld().getWorld().getBlockAt(x, y - 1, z).setType(Material.AIR);

        if (this.p != null) {
            this.p.setInvincible(true);
        }
    }

    // CREDIT open source spigot
    public static Location lookAt(Location loc, Location lookat) {
        // Clone the loc to prevent applied changes to the input loc
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