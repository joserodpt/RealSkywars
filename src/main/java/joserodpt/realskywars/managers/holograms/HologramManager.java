package joserodpt.realskywars.managers.holograms;

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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.managers.holograms.support.DHHologram;
import joserodpt.realskywars.managers.holograms.support.HDHologram;
import joserodpt.realskywars.managers.holograms.support.NoHologram;
import org.bukkit.Bukkit;

public class HologramManager {
    private SWHologram.HType selected = SWHologram.HType.NONE;

    public HologramManager() {
        //select scoreboard plugin
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.selected = SWHologram.HType.HOLOGRAPHIC_DISPLAYS;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            this.selected = SWHologram.HType.DECENT_HOLOGRAMS;
        }

        switch (this.selected) {
            case DECENT_HOLOGRAMS:
                RealSkywars.getPlugin().log("Hooked on Decent Holograms!");
                break;
            case HOLOGRAPHIC_DISPLAYS:
                RealSkywars.getPlugin().log("Hooked on Holographic Displays!");
                break;
        }
    }

    public SWHologram getHologramInstance() {
        switch (this.selected) {
            case DECENT_HOLOGRAMS:
                return new DHHologram();
            case HOLOGRAPHIC_DISPLAYS:
                return new HDHologram();
            default:
                return new NoHologram();
        }
    }
}
