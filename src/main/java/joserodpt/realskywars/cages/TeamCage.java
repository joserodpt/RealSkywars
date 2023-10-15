package joserodpt.realskywars.cages;

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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.player.RSWPlayer;
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

        // chao
        w.getBlockAt(x + 1, y - 1, z).setType(m);
        w.getBlockAt(x + 1, y - 1, z + 1).setType(m);
        w.getBlockAt(x + 1, y - 1, z - 1).setType(m);
        w.getBlockAt(x, y - 1, z + 1).setType(m);
        w.getBlockAt(x, y - 1, z - 1).setType(m);
        w.getBlockAt(x, y - 1, z).setType(m);
        w.getBlockAt(x - 1, y - 1, z).setType(m);
        w.getBlockAt(x - 1, y - 1, z - 1).setType(m);
        w.getBlockAt(x - 1, y - 1, z + 1).setType(m);

        y = y + 4;

        // teto
        w.getBlockAt(x + 1, y - 1, z).setType(m);
        w.getBlockAt(x + 1, y - 1, z + 1).setType(m);
        w.getBlockAt(x + 1, y - 1, z - 1).setType(m);
        w.getBlockAt(x, y - 1, z + 1).setType(m);
        w.getBlockAt(x, y - 1, z - 1).setType(m);
        w.getBlockAt(x, y - 1, z).setType(m);
        w.getBlockAt(x - 1, y - 1, z).setType(m);
        w.getBlockAt(x - 1, y - 1, z - 1).setType(m);
        w.getBlockAt(x - 1, y - 1, z + 1).setType(m);

        // paredes 1 e 3
        y = this.loc.getBlockY();
        x = x + 2;
        w.getBlockAt(x, y, z).setType(m);
        w.getBlockAt(x, y + 1, z).setType(m);
        w.getBlockAt(x, y + 2, z).setType(m);
        w.getBlockAt(x, y, z - 1).setType(m);
        w.getBlockAt(x, y + 1, z - 1).setType(m);
        w.getBlockAt(x, y + 2, z - 1).setType(m);
        w.getBlockAt(x, y, z + 1).setType(m);
        w.getBlockAt(x, y + 1, z + 1).setType(m);
        w.getBlockAt(x, y + 2, z + 1).setType(m);

        x = this.loc.getBlockX();
        x = x - 2;
        w.getBlockAt(x, y, z).setType(m);
        w.getBlockAt(x, y + 1, z).setType(m);
        w.getBlockAt(x, y + 2, z).setType(m);
        w.getBlockAt(x, y, z - 1).setType(m);
        w.getBlockAt(x, y + 1, z - 1).setType(m);
        w.getBlockAt(x, y + 2, z - 1).setType(m);
        w.getBlockAt(x, y, z + 1).setType(m);
        w.getBlockAt(x, y + 1, z + 1).setType(m);
        w.getBlockAt(x, y + 2, z + 1).setType(m);

        // paredes 3 e 4
        x = this.loc.getBlockX();
        z = this.loc.getBlockZ();
        z = z - 2;
        w.getBlockAt(x, y, z).setType(m);
        w.getBlockAt(x - 1, y, z).setType(m);
        w.getBlockAt(x + 1, y, z).setType(m);
        w.getBlockAt(x, y + 1, z).setType(m);
        w.getBlockAt(x - 1, y + 1, z).setType(m);
        w.getBlockAt(x + 1, y + 1, z).setType(m);
        w.getBlockAt(x, y + 2, z).setType(m);
        w.getBlockAt(x - 1, y + 2, z).setType(m);
        w.getBlockAt(x + 1, y + 2, z).setType(m);

        z = this.loc.getBlockZ();
        z = z + 2;
        w.getBlockAt(x, y, z).setType(m);
        w.getBlockAt(x - 1, y, z).setType(m);
        w.getBlockAt(x + 1, y, z).setType(m);
        w.getBlockAt(x, y + 1, z).setType(m);
        w.getBlockAt(x - 1, y + 1, z).setType(m);
        w.getBlockAt(x + 1, y + 1, z).setType(m);
        w.getBlockAt(x, y + 2, z).setType(m);
        w.getBlockAt(x - 1, y + 2, z).setType(m);
        w.getBlockAt(x + 1, y + 2, z).setType(m);

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
        this.p.add(p);
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
        this.p.clear();
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

        this.p.forEach(rswPlayer -> rswPlayer.setInvincible(true));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
            this.p.forEach(rswPlayer -> rswPlayer.setInvincible(false));
        }, 200);
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
