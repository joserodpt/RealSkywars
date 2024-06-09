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

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class RSWTeamCage implements RSWCage {

    private final int id;
    private final int maxPlayers;
    private final List<RSWPlayer> p = new ArrayList<>();
    private final int x, y, z;
    private RSWMap map;

    public RSWTeamCage(int i, int x, int y, int z, int maxM) {
        this.id = i;
        this.x = x;
        this.y = y;
        this.z = z;
        this.maxPlayers = maxM;
    }

    public void setMap(RSWMap map) {
        this.map = map;
    }

    public int getID() {
        return this.id;
    }

    public Location getLocation() {
        return new Location(map.getRSWWorld().getWorld(), this.x, this.y, this.z).add(0.5, 0, 0.5);
    }

    public boolean isEmpty() {
        return !p.isEmpty();
    }

    public void setCage(Material m) {
        World w = map.getRSWWorld().getWorld();

        int xCage = x;
        int yCage = y;
        int zCage = z;

        // chao
        w.getBlockAt(xCage + 1, yCage - 1, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage - 1, zCage + 1).setType(m);
        w.getBlockAt(xCage + 1, yCage - 1, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage - 1, zCage + 1).setType(m);
        w.getBlockAt(xCage, yCage - 1, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage - 1, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage - 1, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage - 1, zCage - 1).setType(m);
        w.getBlockAt(xCage - 1, yCage - 1, zCage + 1).setType(m);

        yCage = yCage + 4;

        // teto
        w.getBlockAt(xCage + 1, yCage - 1, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage - 1, zCage + 1).setType(m);
        w.getBlockAt(xCage + 1, yCage - 1, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage - 1, zCage + 1).setType(m);
        w.getBlockAt(xCage, yCage - 1, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage - 1, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage - 1, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage - 1, zCage - 1).setType(m);
        w.getBlockAt(xCage - 1, yCage - 1, zCage + 1).setType(m);

        // paredes 1 e 3
        yCage = y;
        xCage = xCage + 2;
        w.getBlockAt(xCage, yCage, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage).setType(m);
        w.getBlockAt(xCage, yCage, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage, zCage + 1).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage + 1).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage + 1).setType(m);

        xCage = x;
        xCage = xCage - 2;
        w.getBlockAt(xCage, yCage, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage).setType(m);
        w.getBlockAt(xCage, yCage, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage - 1).setType(m);
        w.getBlockAt(xCage, yCage, zCage + 1).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage + 1).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage + 1).setType(m);

        // paredes 3 e 4
        xCage = x;
        zCage = z;
        zCage = zCage - 2;
        w.getBlockAt(xCage, yCage, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage + 2, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage + 2, zCage).setType(m);

        zCage = z;
        zCage = zCage + 2;
        w.getBlockAt(xCage, yCage, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage + 1, zCage).setType(m);
        w.getBlockAt(xCage, yCage + 2, zCage).setType(m);
        w.getBlockAt(xCage - 1, yCage + 2, zCage).setType(m);
        w.getBlockAt(xCage + 1, yCage + 2, zCage).setType(m);

    }

    public void addPlayer(RSWPlayer p) {
        this.p.add(p);
        this.setCage();
    }

    public void removePlayer(RSWPlayer p) {
        this.p.remove(p);
    }

    public void tpPlayer(RSWPlayer p) {
        p.teleport(getLocation());
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

        int[] xOffset = {0, 1, -1};
        int[] zOffset = {0, 1, -1};

        for (int dx : xOffset) {
            for (int dz : zOffset) {
                map.getRSWWorld().getWorld().getBlockAt(x + dx, y - 1, z + dz).setType(m);
            }
        }

        this.p.forEach(rswPlayer -> rswPlayer.setInvincible(true));
    }
}
