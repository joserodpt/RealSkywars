package josegamerpt.realskywars.holograms.classes;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import josegamerpt.realskywars.holograms.HologramType;
import josegamerpt.realskywars.holograms.SWHologram;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Location;
import org.bukkit.Material;

public class DHHologram implements SWHologram {
    private Hologram holo;

    @Override
    public void spawnHologram(Location loc) {
        if (this.holo == null) {
            this.holo = DHAPI.createHologram(loc.toString(), loc.add(0.5, 2, 0.5));
            DHAPI.addHologramLine(this.holo, Material.CLOCK);
            DHAPI.addHologramLine(this.holo, "~");
        }
    }

    @Override
    public void setTime(int seconds) {
        if (this.holo != null) {
            DHAPI.setHologramLine(this.holo, 1, Text.formatSeconds(seconds));
        }
    }

    @Override
    public void deleteHologram() {
        if (this.holo != null) {
            this.holo.delete();
        }
        this.holo = null;
    }


    @Override
    public HologramType getType() {
        return HologramType.DECENT_HOLOGRAMS;
    }
}
