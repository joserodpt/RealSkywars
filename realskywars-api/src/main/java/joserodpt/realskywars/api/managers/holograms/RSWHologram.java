package joserodpt.realskywars.api.managers.holograms;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.managers.holograms.support.DHHologram;
import joserodpt.realskywars.api.managers.holograms.support.HDHologram;
import joserodpt.realskywars.api.managers.holograms.support.NoHologram;
import org.bukkit.Location;

public interface RSWHologram {
    enum HType {
        DECENT_HOLOGRAMS, HOLOGRAPHIC_DISPLAYS, NONE;

        public RSWHologram getHologramInstance() {
            switch (this) {
                case DECENT_HOLOGRAMS:
                    return new DHHologram();
                case HOLOGRAPHIC_DISPLAYS:
                    return new HDHologram();
                default:
                    return new NoHologram();
            }
        }
    }

    void spawnHologram(Location loc);

    void setTime(int seconds);

    void deleteHologram();

    HType getType();
}
