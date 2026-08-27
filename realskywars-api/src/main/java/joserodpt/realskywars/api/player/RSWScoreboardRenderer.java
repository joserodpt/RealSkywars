package joserodpt.realskywars.api.player;

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

import java.util.List;

/**
 * Draws the scoreboards RealSkywars builds.
 * <p>
 * By default RealSkywars draws its own, but when a scoreboard plugin is
 * installed it takes over instead, so the two never fight over the sidebar.
 * Both methods are called from the game's main-thread refresh task with the
 * lines already translated and expanded.
 */
public interface RSWScoreboardRenderer {

    /**
     * @param inMatch whether the player is in a match, and therefore needs to
     *                see this board even with their scoreboard toggled off
     */
    void render(RSWPlayer player, String title, List<String> lines, boolean inMatch);

    /**
     * Called when RealSkywars has nothing to show this player, so the renderer
     * can go back to whatever it would show otherwise.
     */
    void clear(RSWPlayer player);
}
