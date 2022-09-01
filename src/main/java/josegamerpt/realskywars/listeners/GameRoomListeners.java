package josegamerpt.realskywars.listeners;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.api.events.RSWAPIArenaStateChange;
import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameRoomListeners implements Listener {

    @EventHandler
    public void roomStateChanged(RSWAPIArenaStateChange e) {
        SWGameMode ent = e.getRoom();

        Debugger.print(GameRoomListeners.class, ent.getName() + " changed to " + ent.getState().name());
    }
}