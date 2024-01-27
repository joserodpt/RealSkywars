package joserodpt.realskywars.api;

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

import joserodpt.realskywars.api.events.RSWRoomStateChangeEvent;
import joserodpt.realskywars.api.game.modes.RSWGame;
import org.bukkit.Bukkit;

public class RSWEventsAPI {

    public void callRoomStateChange(RSWGame g) {
        Bukkit.getPluginManager().callEvent(new RSWRoomStateChangeEvent(g));
        g.updateSigns();
    }
}
