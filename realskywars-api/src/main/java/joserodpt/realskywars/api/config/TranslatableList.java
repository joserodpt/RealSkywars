package joserodpt.realskywars.api.config;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

import java.util.List;

public enum TranslatableList {

    MAP_START(".Messages.Map.Start"),
    MAP_END_LOG(".Messages.Map.End-Log"),
    EDIT_MAP(".Messages.Map.Edit-Map"),
    SCOREBOARD_LOBBY_LINES(".Scoreboards.Lobby.Lines"),
    SCOREBOARD_CAGE_LINES(".Scoreboards.Cage.Lines"),
    SCOREBOARD_PLAYING_LINES(".Scoreboards.Game.Lines"),
    SCOREBOARD_SPECTATOR_LINES(".Scoreboards.Spectate.Lines"),
    ITEMS_MAP_DESCRIPTION(".Itens.Map.Description"),
    TITLE_ROOMJOIN(".Titles.Join-Room"),
    STATS_ITEM_LORE(".Itens.Statistics.Lore"),
    REFILL_EVENT_TITLE(".Messages.Map.Events.Refill"),
    TNTRAIN_EVENT_TITLE(".Messages.Map.Events.TNTRain"),
    STATISTIC_PLAYER_LIST(".Statistics.Player-List"),
    STATISTIC_GAMES_LIST(".Statistics.Games-List"),
    TAB_HEADER_MATCH(".Tab.In-Game.Header"),
    TAB_FOOTER_MATCH(".Tab.In-Game.Footer"),
    TAB_HEADER_OTHER(".Tab.Other.Header"),
    TAB_FOOTER_OTHER(".Tab.Other.Footer"), GAME_LOG_LIST(".Statistics.Game-Log-List");

    private final String configPath;

    TranslatableList(String configPath) {
        this.configPath = configPath;
    }

    public List<String> getInLanguage(String lang) {
        return Text.color(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getLanguage(lang).getStringList("Languages." + lang + this.configPath));
    }

    public List<String> get(RSWPlayer player) {
        return getInLanguage(player.getLanguage());
    }

}
