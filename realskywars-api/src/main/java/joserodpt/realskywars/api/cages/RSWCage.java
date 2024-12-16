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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collection;

public abstract class RSWCage {
    protected final int id;
    protected final int x, y, z;
    protected RSWMap map;

    public RSWCage(int id, int x, int y, int z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RSWCage(int id, Location l) {
        this(id, l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public int getID() {
        return this.id;
    }

    public Location getLocation() {
        return new Location(this.map.getRSWWorld().getWorld(), this.x, this.y, this.z).add(0.5, 0, 0.5);
    }

    public RSWMap getMap() {
        return this.map;
    }

    public void setMap(RSWMap map) {
        this.map = map;
    }

    public abstract boolean isEmpty();

    public abstract void setCage();

    public abstract void addPlayer(RSWPlayer p);

    public abstract void removePlayer(RSWPlayer p);

    public abstract void tpPlayer(RSWPlayer p);

    public abstract Collection<RSWPlayer> getPlayers();

    public abstract void clearCage();

    public abstract void setCage(Material m);

    public abstract void open();
}