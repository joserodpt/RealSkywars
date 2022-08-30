package josegamerpt.realskywars.holograms.classes;

import josegamerpt.realskywars.holograms.HologramType;
import josegamerpt.realskywars.holograms.SWHologram;
import org.bukkit.Location;

public class NoHologram implements SWHologram {

    @Override
    public void spawnHologram(Location loc) {}

    @Override
    public void setTime(int seconds) {}

    @Override
    public void deleteHologram() {}

    @Override
    public HologramType getType() {return HologramType.NONE;}
}
