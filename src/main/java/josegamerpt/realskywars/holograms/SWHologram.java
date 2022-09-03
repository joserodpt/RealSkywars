package josegamerpt.realskywars.holograms;

import org.bukkit.Location;

public interface SWHologram {
    enum HType {DECENT_HOLOGRAMS, HOLOGRAPHIC_DISPLAYS, NONE}

    void spawnHologram(Location loc);
    void setTime(int seconds);
    void deleteHologram();
    HType getType();

}
