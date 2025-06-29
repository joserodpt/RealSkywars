package joserodpt.realskywars.api.database;

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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_playerdata")
public class PlayerDataRow {
    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private @NotNull UUID uuid;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "language")
    private String lang;

    @DatabaseField(columnName = "coins")
    private Double coins;

    @DatabaseField(columnName = "prefs_mapviewer")
    private String prefs_mapviewer;

    @DatabaseField(columnName = "choosen_kit")
    private String choosen_kit;

    @DatabaseField(columnName = "prefs_cage_material")
    private String prefs_cage_material;

    @DatabaseField(columnName = "stats_wins_solo")
    private int stats_wins_solo;

    @DatabaseField(columnName = "stats_wins_ranked_solo")
    private int stats_wins_ranked_solo;

    @DatabaseField(columnName = "stats_wins_teams")
    private int stats_wins_teams;

    @DatabaseField(columnName = "stats_wins_ranked_teams")
    private int stats_wins_ranked_teams;

    @DatabaseField(columnName = "kills")
    private int kills;

    @DatabaseField(columnName = "ranked_kills")
    private int ranked_kills;

    @DatabaseField(columnName = "deaths")
    private int deaths;

    @DatabaseField(columnName = "ranked_deaths")
    private int ranked_deaths;

    @DatabaseField(columnName = "loses")
    private int losses;

    @DatabaseField(columnName = "loses_ranked")
    private int ranked_loses;

    @DatabaseField(columnName = "games_played")
    private int games_played;

    @DatabaseField(columnName = "ranked_games_played")
    private int ranked_games_played;

    @DatabaseField(columnName = "first_join")
    private String first_join;

    @DatabaseField(columnName = "last_join")
    private String last_join;

    @DatabaseField(columnName = "bought_items") //legacy
    private String bought_items_legacy;

    @DatabaseField(columnName = "games_list")
    private String games_list_legacy;

    public PlayerDataRow(OfflinePlayer p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.coins = 0D;
        this.lang = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getDefaultLanguage();
        this.prefs_mapviewer = "MAPV_ALL";
        this.prefs_cage_material = "GLASS";
        this.first_join = Text.getDateAndTime();
        this.last_join = this.first_join;
    }

    public PlayerDataRow() {
        //for ORMLite
    }

    public String getLastJoin() {
        return last_join;
    }

    public String getGamesListLegacy() {
        return this.games_list_legacy;
    } //legacy

    public Collection<String> getBoughtItemsLegacy() { //legacy
        if (this.bought_items_legacy == null || this.bought_items_legacy.isEmpty()) return null;

        return Arrays.asList(this.bought_items_legacy.split("/"));
    }

    public String getChoosen_kit() {
        return choosen_kit;
    }

    public String getName() {
        return this.name;
    }

    public String getMapViewerPref() {
        return this.prefs_mapviewer;
    }

    public String getCageMaterial() {
        return this.prefs_cage_material;
    }

    public int getStats_wins_ranked_solo() {
        return this.stats_wins_ranked_solo;
    }

    public int getStats_wins_teams() {
        return this.stats_wins_teams;
    }

    public int getStats_wins_ranked_teams() {
        return this.stats_wins_ranked_teams;
    }

    public int getKills() {
        return this.kills;
    }

    public int getRanked_kills() {
        return this.ranked_kills;
    }

    public int getLosses() {
        return this.losses;
    }

    public int getLoses_ranked() {
        return this.ranked_loses;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getStats_wins_solo() {
        return this.stats_wins_solo;
    }

    public int getRanked_deaths() {
        return this.ranked_deaths;
    }

    public int getGames_played() {
        return this.games_played;
    }

    public int getRanked_games_played() {
        return this.ranked_games_played;
    }

    public Double getCoins() {
        return this.coins;
    }

    public String getLanguage() {
        return this.lang;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) {
        this.lang = language;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void setCageBlock(String name) {
        this.prefs_cage_material = name;
    }

    public void setMapViewerPref(String name) {
        this.prefs_mapviewer = name;
    }

    public String getFirstJoin() {
        return first_join;
    }

    public void setStatistics(RSWPlayer.PlayerStatistics pp, Boolean ranked, int value) {
        switch (pp) {
            case LOSES:
                if (ranked) {
                    this.ranked_loses = value;
                } else {
                    this.losses = value;
                }
                break;
            case DEATHS:
                if (ranked) {
                    this.ranked_deaths = value;
                } else {
                    this.deaths = value;
                }
                break;
            case WINS_SOLO:
                if (ranked) {
                    this.stats_wins_ranked_solo = value;
                } else {
                    this.stats_wins_solo = value;
                }
                break;
            case WINS_TEAMS:
                if (ranked) {
                    this.stats_wins_ranked_teams = value;
                } else {
                    this.stats_wins_teams = value;
                }
                break;
            case KILLS:
                if (ranked) {
                    this.ranked_kills = value;
                } else {
                    this.kills = value;
                }
                break;
            case GAMES_PLAYED:
                if (ranked) {
                    this.ranked_games_played = value;
                } else {
                    this.games_played = value;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid PlayerStatistics value");
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setChoosenKit(String name) {
        this.choosen_kit = name;
    }

    public void setFirstJoin() {
        this.first_join = Text.getDateAndTime();
    }

    public void setGamesListLegacy(String s) {
        this.games_list_legacy = s;
    }

    public void setBoughtItemsLegacy(String s) {
        this.bought_items_legacy = s;
    }

    public void setLastJoin() {
        this.last_join = Text.getDateAndTime();
    }
}