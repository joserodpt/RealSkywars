package josegamerpt.realskywars.api.events;

import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.Bukkit;

public class RSWAPIEvents {

    public static void callRoomStateChange(SWGameMode g) {
        Bukkit.getPluginManager().callEvent(new RSWAPIArenaStateChange(g));
    }

}
