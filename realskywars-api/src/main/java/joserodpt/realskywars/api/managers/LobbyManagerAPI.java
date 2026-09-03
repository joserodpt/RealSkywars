package joserodpt.realskywars.api.managers;

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

import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class LobbyManagerAPI {
    public abstract void loadLobby();

    public abstract void tpToLobby(RSWPlayer p);

    public abstract Location getLobbyLocation();

    public abstract boolean scoreboardInLobby();

    public abstract void setLobbyLoc(Location location);

    public abstract boolean tpLobbyOnJoin();

    public abstract boolean isInLobby(World w);

    public abstract void tpToLobby(Player player);

    /**
     * Loads the waiting lobby, the place where players wait while picking a team on maps with
     * manual team selection enabled. Called from {@link #loadLobby()}.
     */
    public abstract void loadWaitingLobby();

    public abstract Location getWaitingLobbyLocation();

    public abstract void setWaitingLobbyLoc(Location location);

    /**
     * Teleports a player to the waiting lobby. Unlike {@link #tpToLobby(RSWPlayer)} this does not
     * hand out the lobby items - the caller is in a match and gives the cage set instead.
     *
     * @return true if the player was teleported.
     */
    public abstract boolean tpToWaitingLobby(RSWPlayer p);

    /**
     * Whether the waiting lobby is set up and its world is loaded right now. Manual team selection
     * has nowhere to hold players without it, so rooms that use it refuse to be joined while this
     * is false - see {@link joserodpt.realskywars.api.map.modes.teams.TeamsMode#addPlayer(RSWPlayer)}.
     */
    public abstract boolean isWaitingLobbyReady();

    public abstract boolean isInWaitingLobby(World w);
}
