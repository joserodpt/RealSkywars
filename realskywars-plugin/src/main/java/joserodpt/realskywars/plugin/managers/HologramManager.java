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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.HologramManagerAPI;
import joserodpt.realskywars.api.managers.holograms.RSWHologram;
import org.bukkit.Bukkit;

public class HologramManager extends HologramManagerAPI {

    private RSWHologram.HType selected = RSWHologram.HType.NONE;

    public HologramManager(RealSkywarsAPI rsa) {

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
        return this.selected.getHologramInstance();
    }
}
