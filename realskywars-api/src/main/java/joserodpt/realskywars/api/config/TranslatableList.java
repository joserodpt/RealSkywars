package joserodpt.realskywars.api.config;

import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

import java.util.List;

public enum TranslatableList {

    ARENA_START(".Messages.Map.Start"),
    ARENA_END(".Messages.Map.End-Log"),
    INITSETUP_ARENA(".Messages.Map.Init-Setup"),
    SCOREBOARD_LOBBY_LINES(".Scoreboards.Lobby.Lines"),
    SCOREBOARD_CAGE_LINES(".Scoreboards.Cage.Lines"),
    SCOREBOARD_PLAYING_LINES(".Scoreboards.Game.Lines"),
    SCOREBOARD_SPECTATOR_LINES(".Scoreboards.Spectate.Lines"),
    ITEMS_MAP_DESCRIPTION(".Itens.Map.Description"),
    TITLE_ROOMJOIN(".Titles.Join-Room"),
    STATS_ITEM_LORE(".Itens.Statistics.Lore"),
    REFILL_EVENT_TITLE(".Messages.Map.Events.Refill"),
    TNTRAIN_EVENT_TITLE(".Messages.Map.Events.TNTRain"),
    TAB_HEADER_MATCH(".Tab.In-Game.Header"),
    TAB_FOOTER_MATCH(".Tab.In-Game.Footer"),
    TAB_HEADER_OTHER(".Tab.Other.Header"),
    TAB_FOOTER_OTHER(".Tab.Other.Footer");

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
