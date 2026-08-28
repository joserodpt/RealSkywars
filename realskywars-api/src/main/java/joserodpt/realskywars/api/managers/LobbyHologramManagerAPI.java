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
 * @author José Rodrigues © 2019-2026
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.managers.holograms.HologramType;
import joserodpt.realskywars.api.managers.holograms.RSWLobbyHologram;
import org.bukkit.Location;

import java.util.Collection;

public abstract class LobbyHologramManagerAPI {

    /** Reads holograms.yml and spawns every hologram it lists. */
    public abstract void loadHolograms();

    /**
     * Creates a hologram under the given id at the given location and persists it.
     * Returns null if the id is already taken or the hologram could not be placed.
     */
    public abstract RSWLobbyHologram createHologram(String id, HologramType type, Location loc);

    /** Removes a hologram from the world and from holograms.yml. */
    public abstract void removeHologram(String id);

    public abstract RSWLobbyHologram getHologram(String id);

    public abstract Collection<RSWLobbyHologram> getHolograms();

    /** Deletes every hologram from the world, leaving holograms.yml untouched. */
    public abstract void clear();

    public abstract void refreshAll();

    public abstract void refresh(RSWLobbyHologram holo);

    /** Records the winner shown by the LAST_WINNER holograms. */
    public abstract void setLastWinner(String winner, String map);

    /** True if the id is free and made only of letters, digits, - and _. */
    public abstract boolean isValidNewId(String id);

    public abstract void startRefreshTask();

    public abstract void stopRefreshTask();
}
