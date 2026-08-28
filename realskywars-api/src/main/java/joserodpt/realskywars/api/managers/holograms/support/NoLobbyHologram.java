package joserodpt.realskywars.api.managers.holograms.support;

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
 * @author José Rodrigues © 2019-2026
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.managers.holograms.HologramType;
import joserodpt.realskywars.api.managers.holograms.RSWLobbyHologram;
import org.bukkit.Location;

import java.util.List;

/** Used when no hologram plugin is installed. Keeps the location so the
 * hologram still shows up in the GUI and survives a config save. */
public class NoLobbyHologram implements RSWLobbyHologram {

    private final String id;
    private final HologramType type;
    private Location location;

    public NoLobbyHologram(String id, HologramType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public void spawn(Location loc) {
        this.location = loc;
    }

    @Override
    public void setLines(List<String> lines) {
    }

    @Override
    public void delete() {
    }

    @Override
    public HologramType getType() {
        return this.type;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
