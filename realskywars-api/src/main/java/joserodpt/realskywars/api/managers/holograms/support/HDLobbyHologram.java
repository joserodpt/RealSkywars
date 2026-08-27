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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.holograms.HologramType;
import joserodpt.realskywars.api.managers.holograms.RSWLobbyHologram;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;

import java.util.List;

/** HolographicDisplays backed lobby hologram. */
public class HDLobbyHologram implements RSWLobbyHologram {

    private final String id;
    private final HologramType type;
    private Hologram holo;
    private Location location;

    public HDLobbyHologram(String id, HologramType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public void spawn(Location loc) {
        this.location = loc;
        if (this.holo == null || this.holo.isDeleted()) {
            this.holo = HologramsAPI.createHologram(RealSkywarsAPI.getInstance().getPlugin(), loc);
        }
    }

    @Override
    public void setLines(List<String> lines) {
        if (this.holo == null || this.holo.isDeleted()) {
            return;
        }

        this.holo.clearLines();
        for (String line : lines) {
            this.holo.appendTextLine(Text.color(line));
        }
    }

    @Override
    public void delete() {
        if (this.holo != null && !this.holo.isDeleted()) {
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
        return this.holo != null && !this.holo.isDeleted();
    }
}
