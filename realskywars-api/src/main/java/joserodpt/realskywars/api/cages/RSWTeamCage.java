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

import java.util.ArrayList;
import java.util.List;

public class RSWTeamCage implements RSWCage {

    private final int id;
    private final String worldName;
    private final int maxPlayers;
    private final List<RSWPlayer> p = new ArrayList<>();
    private final Location loc;

    public RSWTeamCage(int i, int x, int y, int z, String worldName, int maxM) {
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

    public List<RSWPlayer> getPlayers() {
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
    }
}
