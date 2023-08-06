package josegamerpt.realskywars.utils.holograms.support;

import josegamerpt.realskywars.utils.holograms.SWHologram;
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
