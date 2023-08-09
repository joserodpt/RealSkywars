package josegamerpt.realskywars.utils.holograms.support;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import josegamerpt.realskywars.utils.holograms.SWHologram;
import org.bukkit.Location;

public class NoHologram implements SWHologram {
    @Override
    public void spawnHologram(Location loc) {}
    @Override
    public void setTime(int seconds) {}
    @Override
    public void deleteHologram() {}
    @Override
    public HType getType() {
        return HType.NONE;
    }
}
