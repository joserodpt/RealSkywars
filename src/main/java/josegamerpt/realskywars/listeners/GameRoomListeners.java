package josegamerpt.realskywars.listeners;

import josegamerpt.realskywars.api.events.RSWAPIRoomStateChange;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameRoomListeners implements Listener {
    @EventHandler
    public void roomStateChanged(RSWAPIRoomStateChange e) {
        e.getRoom().updateSigns();
    }
}