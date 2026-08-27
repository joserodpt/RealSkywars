package joserodpt.realskywars.api.events;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collections;
import java.util.List;

/**
 * Called when a match is won, right after the map enters the FINISHING state.
 * In team maps every member of the winning team is reported, with the first one
 * as the representative winner.
 */
public class RSWPlayerWinEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final RSWPlayer winner;
    private final List<RSWPlayer> winners;
    private final RSWMap map;

    public RSWPlayerWinEvent(RSWPlayer winner, RSWMap map) {
        this(winner, Collections.singletonList(winner), map);
    }

    public RSWPlayerWinEvent(RSWPlayer winner, List<RSWPlayer> winners, RSWMap map) {
        this.winner = winner;
        this.winners = winners;
        this.map = map;
    }

    /** The winning player, or the first member of the winning team. */
    public RSWPlayer getWinner() {
        return this.winner;
    }

    /** Every winning player. Always holds at least the winner. */
    public List<RSWPlayer> getWinners() {
        return Collections.unmodifiableList(this.winners);
    }

    public RSWMap getMap() {
        return this.map;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
