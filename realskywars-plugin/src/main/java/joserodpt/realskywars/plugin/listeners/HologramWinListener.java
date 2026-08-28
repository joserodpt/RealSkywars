package joserodpt.realskywars.plugin.listeners;

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
 * @author José Rodrigues © 2019-2026
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.events.RSWPlayerWinEvent;
import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/** Feeds match wins into the last winner hologram. */
public class HologramWinListener implements Listener {

    private final RealSkywarsAPI rs;

    public HologramWinListener(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWin(RSWPlayerWinEvent e) {
        if (this.rs.getLobbyHologramManagerAPI() == null) {
            return;
        }

        this.rs.getLobbyHologramManagerAPI().setLastWinner(e.getWinner().getDisplayName(), e.getMap().getDisplayName());
    }
}
