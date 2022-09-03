package josegamerpt.realskywars.api.events;

import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.Bukkit;

public class RSWEventsAPI {

    public void callRoomStateChange(SWGameMode g) {
        Bukkit.getPluginManager().callEvent(new RSWAPIRoomStateChange(g));
    }

}
