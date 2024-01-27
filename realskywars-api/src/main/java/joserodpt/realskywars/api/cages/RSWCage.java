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

import java.util.List;

public interface RSWCage {
    int getID();
    Location getLoc();
    boolean isEmpty();
    void setCage();
    void addPlayer(RSWPlayer p);
    void removePlayer(RSWPlayer p);
    void tpPlayer(RSWPlayer p);
    int getMaxPlayers();
    List<RSWPlayer> getPlayers();
    void clearCage();
    void setCage(Material m);
    void open();
}
