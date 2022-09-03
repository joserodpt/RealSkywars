package josegamerpt.realskywars.api;

import josegamerpt.realskywars.api.events.RSWAPIRoomStateChange;
import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.Bukkit;

public class RSWEventsAPI {

    public void callRoomStateChange(SWGameMode g) {
        Bukkit.getPluginManager().callEvent(new RSWAPIRoomStateChange(g));
    }

}
