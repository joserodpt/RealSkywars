package josegamerpt.realskywars.holograms.support;

import josegamerpt.realskywars.holograms.SWHologram;
import org.bukkit.Location;

public class NoHologram implements SWHologram {

    @Override
    public void spawnHologram(Location loc) {
    }

    @Override
    public void setTime(int seconds) {
    }

    @Override
    public void deleteHologram() {
    }

    @Override
    public HType getType() {
        return HType.NONE;
    }
}
