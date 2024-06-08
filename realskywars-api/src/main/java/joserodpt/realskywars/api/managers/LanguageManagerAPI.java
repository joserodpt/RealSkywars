package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.List;

public abstract class LanguageManagerAPI {
    public abstract void loadLanguages();

    public abstract String getDefaultLanguage();

    public abstract boolean areLanguagesEmpty();

    public abstract List<String> getLanguages();


    public abstract String getPrefix();

    public abstract List<String> getList(RSWPlayer p, TL tl);

    public enum TL {
        ARENA_START, ARENA_END, SCOREBOARD_LOBBY_LINES, SCOREBOARD_CAGE_LINES, SCOREBOARD_SPECTATOR_LINES, SCOREBOARD_PLAYING_LINES, ITEMS_MAP_DESCRIPTION, TITLE_ROOMJOIN, STATS_ITEM_LORE, REFILL_EVENT_TITLE, TNTRAIN_EVENT_TITLE, TAB_HEADER_MATCH, TAB_FOOTER_MATCH, TAB_HEADER_OTHER, TAB_FOOTER_OTHER, INITSETUP_ARENA
    }
}
