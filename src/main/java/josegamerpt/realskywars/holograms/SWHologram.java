package josegamerpt.realskywars.holograms;

import org.bukkit.Location;

public interface SWHologram {

    void spawnHologram(Location loc);
    void setTime(int seconds);
    void deleteHologram();
    HologramType getType();

}
