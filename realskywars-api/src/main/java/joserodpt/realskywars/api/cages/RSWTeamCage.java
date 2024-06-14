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
import joserodpt.realskywars.api.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RSWTeamCage extends RSWCage {

    private final List<RSWPlayer> players = new ArrayList<>();

    public RSWTeamCage(int id, int x, int y, int z) {
        super(id, x, y, z);
    }

    @Override
    public boolean isEmpty() {
        return this.getPlayers().isEmpty();
    }

    @Override
    public void tpPlayer(RSWPlayer p) {
        // Adjust tpLocation based on the number of players to avoid overlapping
        Location tpLocation = getLocation().clone().add(0, 1, 0);
        p.teleport(MathUtils.lookAt(tpLocation, super.getMap().getSpectatorLocation()));
        this.players.add(p);
    }

    @Override
    public void setCage(Material m) {
        if (m == null) {
            m = Material.GLASS;
        }

        for (int[] pos : new int[][]{
                {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, {0, 3, 0},
                {0, 1, 1}, {0, 2, 1}, {0, 1, -1}, {0, 2, -1},
                {-1, 0, 0}, {-1, 1, 0}, {-1, 2, 0},
                {1, 0, 0}, {1, 1, 0}, {1, 2, 0}
        }) {
            map.getRSWWorld().getWorld().getBlockAt(x + pos[0], y + pos[1], z + pos[2]).setType(m);
        }
    }

    @Override
    public void setCage() {
        setCage(this.players.isEmpty() ? Material.GLASS : (Material) this.players.get(0).getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK));
    }

    @Override
    public void clearCage() {
        setCage(Material.AIR);
        this.players.clear();
    }

    @Override
    public void addPlayer(RSWPlayer pl) {
        this.players.add(pl);
        pl.setPlayerCage(this);
        this.setCage();
        tpPlayer(pl);
    }

    @Override
    public void removePlayer(RSWPlayer p) {
        this.players.remove(p);
        p.setPlayerCage(null);
    }

    @Override
    public Collection<RSWPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public void open() {
        this.map.getRSWWorld().getWorld().getBlockAt(x, y - 1, z).setType(Material.AIR);

        this.players.forEach(rswPlayer -> rswPlayer.setInvincible(true));
    }
}