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

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import joserodpt.realskywars.api.managers.holograms.HologramType;
import joserodpt.realskywars.api.managers.holograms.RSWLobbyHologram;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/** DecentHolograms backed lobby hologram. */
public class DHLobbyHologram implements RSWLobbyHologram {

    private final String id;
    private final HologramType type;
    private Hologram holo;
    private Location location;

    public DHLobbyHologram(String id, HologramType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public void spawn(Location loc) {
        this.location = loc;
        if (this.holo == null) {
            this.holo = DHAPI.createHologram("RSW-Lobby-" + this.id, loc, false);
        }
    }

    @Override
    public void setLines(List<String> lines) {
        if (this.holo == null) {
            return;
        }

        List<String> colored = new ArrayList<>();
        for (String line : lines) {
            colored.add(Text.color(line));
        }
        DHAPI.setHologramLines(this.holo, colored);
    }

    @Override
    public void delete() {
        if (this.holo != null) {
            this.holo.delete();
        }
        this.holo = null;
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
        return this.holo != null;
    }
}
