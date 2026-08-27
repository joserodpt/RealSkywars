package joserodpt.realskywars.api.managers.holograms;

/*
 *   _____            _  _____ _
 *  |  __ \\          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \\/ _` | |\\___ \\| |/ / | | \\ \\ /\\ / / _` | '__/ __|
 *  | | \\ \\  __/ (_| | |____) |   <| |_| |\\ V  V / (_| | |  \\__ \\
 *  |_|  \\_\\___|\\__,_|_|_____/|_|\\_\\\\__, | \\_/\\_/ \\__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.managers.holograms.support.DHLobbyHologram;
import joserodpt.realskywars.api.managers.holograms.support.HDLobbyHologram;
import joserodpt.realskywars.api.managers.holograms.support.NoLobbyHologram;
import org.bukkit.Location;

import java.util.List;

/**
 * A multi-line hologram placed in the lobby: a leaderboard, the last winner or
 * server info. Unlike {@link RSWHologram}, which shows a map's countdown, these
 * are persisted in holograms.yml and refreshed on a timer.
 */
public interface RSWLobbyHologram {

    static RSWLobbyHologram of(RSWHologram.HType backend, String id, HologramType type) {
        switch (backend) {
            case DECENT_HOLOGRAMS:
                return new DHLobbyHologram(id, type);
            case HOLOGRAPHIC_DISPLAYS:
                return new HDLobbyHologram(id, type);
            default:
                return new NoLobbyHologram(id, type);
        }
    }

    /** Creates the hologram in the world. */
    void spawn(Location loc);

    /** Replaces every line at once. */
    void setLines(List<String> lines);

    /** Removes the hologram from the world. */
    void delete();

    HologramType getType();

    String getId();

    Location getLocation();

    boolean isActive();
}
