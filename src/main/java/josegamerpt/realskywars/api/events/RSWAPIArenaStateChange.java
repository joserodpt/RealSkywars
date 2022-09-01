package josegamerpt.realskywars.api.events;

import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RSWAPIArenaStateChange extends Event implements Cancellable {
    private final SWGameMode room;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public RSWAPIArenaStateChange(SWGameMode gm){
        this.room = gm;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public SWGameMode getRoom() {
        return this.room;
    }

}