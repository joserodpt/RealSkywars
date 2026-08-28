package joserodpt.realskywars.plugin.hooks;

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

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWScoreboardRenderer;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Pushes RealSkywars' scoreboards to RealScoreboard, so a single plugin owns
 * the sidebar. A pushed board stays up until it is cleared, which is what makes
 * it survive world changes and respawns.
 */
public class RealScoreboardRenderer implements RSWScoreboardRenderer {

    private final Plugin plugin;

    public RealScoreboardRenderer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void render(RSWPlayer player, String title, List<String> lines, boolean inMatch) {
        if (player.getPlayer() == null) {
            return;
        }

        //a player in a match needs the board even with their scoreboard toggled off
        RealScoreboardAPI.getInstance().getExternalScoreboardManagerAPI()
                .setBoard(this.plugin, player.getPlayer(), title, lines, inMatch);
    }

    @Override
    public void clear(RSWPlayer player) {
        if (player.getPlayer() == null) {
            return;
        }

        RealScoreboardAPI.getInstance().getExternalScoreboardManagerAPI()
                .clearBoard(this.plugin, player.getPlayer());
    }
}
