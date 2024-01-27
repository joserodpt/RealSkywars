package joserodpt.realskywars.plugin.managers;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.HologramManagerAPI;
import joserodpt.realskywars.api.managers.holograms.RSWHologram;
import joserodpt.realskywars.api.managers.holograms.support.DHHologram;
import joserodpt.realskywars.api.managers.holograms.support.HDHologram;
import joserodpt.realskywars.api.managers.holograms.support.NoHologram;
import org.bukkit.Bukkit;

public class HologramManager extends HologramManagerAPI {
    private RealSkywarsAPI rsa;
    private RSWHologram.HType selected = RSWHologram.HType.NONE;

    public HologramManager(RealSkywarsAPI rsa) {
        this.rsa = rsa;
        //select scoreboard plugin
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.selected = RSWHologram.HType.HOLOGRAPHIC_DISPLAYS;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            this.selected = RSWHologram.HType.DECENT_HOLOGRAMS;
        }

        switch (this.selected) {
            case DECENT_HOLOGRAMS:
                rsa.getLogger().info("Hooked on Decent Holograms!");
                break;
            case HOLOGRAPHIC_DISPLAYS:
                rsa.getLogger().info("Hooked on Holographic Displays!");
                break;
        }
    }

    @Override
    public RSWHologram getHologramInstance() {
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
