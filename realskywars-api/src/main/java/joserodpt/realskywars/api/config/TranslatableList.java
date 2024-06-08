package joserodpt.realskywars.api.config;

import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

import java.util.List;

public enum TranslatableList {

    ARENA_START(".Messages.Arena.Start"),
    ARENA_END(".Messages.Arena.End-Log"),
    INITSETUP_ARENA(".Messages.System.Arena.Init-Setup"),
    SCOREBOARD_LOBBY_LINES(".Scoreboards.Lobby.Lines"),
    SCOREBOARD_CAGE_LINES(".Scoreboards.Cage.Lines"),
    SCOREBOARD_PLAYING_LINES(".Scoreboards.Game.Lines"),
    SCOREBOARD_SPECTATOR_LINES(".Scoreboards.Spectate.Lines"),
    ITEMS_MAP_DESCRIPTION(".Itens.Map.Description"),
    TITLE_ROOMJOIN(".Messages.System.Titles.Join-Room"),
    STATS_ITEM_LORE(".Itens.Statistics.Lore"),
    REFILL_EVENT_TITLE(".Messages.Arena.Events.Refill"),
    TNTRAIN_EVENT_TITLE(".Messages.Arena.Events.TNTRain"),
    TAB_HEADER_MATCH(".Messages.System.TAB.InGame.Header"),
    TAB_FOOTER_MATCH(".Messages.System.TAB.InGame.Footer"),
    TAB_HEADER_OTHER(".Messages.System.TAB.Other.Header"),
    TAB_FOOTER_OTHER(".Messages.System.TAB.Other.Footer");

    private final String configPath;

    TranslatableList(String configPath) {
        this.configPath = configPath;
    }

    public List<String> getInLanguage(String lang) {
        return Text.color(RSWLanguagesConfig.file().getStringList("Languages." + lang + this.configPath));
    }

    public List<String> get(RSWPlayer player) {
        return getInLanguage(player.getLanguage());
    }

}
