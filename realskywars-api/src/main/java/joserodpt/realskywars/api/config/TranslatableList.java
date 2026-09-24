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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every multi-line text the plugin shows, as a constant pointing at its route in the language
 * files.
 *
 * <p>Placeholders are filled with {@link #with(TranslatableListPlaceholder, Object)}, which hands
 * back a new {@link Message}; every line is filled, then coloured:</p>
 *
 * <pre>{@code
 * TranslatableList.EDIT_MAP.with(CAGES, teams).get(p);
 * }</pre>
 */
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

    /** Starts a message from this list with one placeholder filled; chain more with {@link Message#with}. */
    public Message with(TranslatableListPlaceholder placeholder, Object value) {
        return new Message(this).with(placeholder, value);
    }

    public List<String> getInLanguage(String lang) {
        return new Message(this).getInLanguage(lang);
    }

    public List<String> get(RSWPlayer player) {
        return new Message(this).get(player);
    }

    /** The tokens a list may contain. {@code WINS_PERCENTAGE} is written {@code %wins_percentage%}. */
    public enum TranslatableListPlaceholder {
        AVERAGE_KILLS, AVERAGE_TIME, CAGE, CAGES, CHESTS, COINS, DISPLAYNAME, FIRSTJOIN, GAMES, KILLS, KIT,
        LANG, LASTJOIN, LONGEST_TIME, LOOSES, LOOSES_PERCENTAGE, MAP, MAXPLAYERS, MOST_KILLS, PLAYERS,
        PROJECT, RANKED, RANKED_PERCENTAGE, RECVCOINS, SHORTEST_TIME, SPACE, TIME, TOTALCOINS, WIN, WINS,
        WINS_PERCENTAGE;

        private final String token = "%" + this.name().toLowerCase() + "%";

        public String getToken() {
            return this.token;
        }
    }

    /**
     * One list with its placeholders filled in. A new one per message and never shared, so nothing
     * set here can leak into the next.
     */
    public static final class Message {
        private final TranslatableList list;
        private final Map<TranslatableListPlaceholder, String> values = new LinkedHashMap<>();

        private Message(TranslatableList list) {
            this.list = list;
        }

        /** Fills a placeholder on every line. Setting the same one again replaces its value. */
        public Message with(TranslatableListPlaceholder placeholder, Object value) {
            this.values.put(placeholder, String.valueOf(value));
            return this;
        }

        /** The filled and coloured lines, as a new list the caller may change. */
        public List<String> getInLanguage(String lang) {
            final List<String> lines = new ArrayList<>();
            for (String line : RealSkywarsAPI.getInstance().getLanguageManagerAPI().getLanguage(lang).getStringList(this.list.configPath)) {
                for (final Map.Entry<TranslatableListPlaceholder, String> entry : this.values.entrySet()) {
                    line = line.replace(entry.getKey().getToken(), entry.getValue());
                }
                lines.add(line);
            }
            return new ArrayList<>(Text.color(lines));
        }

        public List<String> get(RSWPlayer player) {
            final List<String> lines = this.getInLanguage(player.getLanguage());
            if (this.list == TITLE_ROOMJOIN && lines.size() != 2) {
                RealSkywarsAPI.getInstance().getLogger().warning("Title RoomJoin must have 2 lines, but has " + lines.size());

                while (lines.size() != 2) {
                    lines.add("SEE CONSOLE");
                }
            }
            return lines;
        }
    }
}
