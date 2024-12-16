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
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_player_game_history")
public class PlayerGameHistoryRow {
    @DatabaseField(columnName = "id", canBeNull = false, allowGeneratedIdInsert = true, generatedId = true)
    private @NotNull UUID id;

    @DatabaseField(columnName = "player_uuid")
    private @NotNull UUID player_uuid;

    @DatabaseField(columnName = "player_name")
    private String player_name;

    @DatabaseField(columnName = "map")
    private String map;

    @DatabaseField(columnName = "mode")
    private String mode;

    @DatabaseField(columnName = "ranked")
    private Boolean ranked;

    @DatabaseField(columnName = "players")
    private int players;

    @DatabaseField(columnName = "kills")
    private int kills;

    @DatabaseField(columnName = "win")
    private Boolean win;

    @DatabaseField(columnName = "time")
    private int time;

    @DatabaseField(columnName = "date")
    private String date;

    //from conversion
    public PlayerGameHistoryRow(Player p, String map, String mode, Boolean ranked, int players, Boolean win, int time, String date) {
        this.player_uuid = p.getUniqueId();
        this.player_name = p.getName();
        this.map = map;
        this.mode = mode;
        this.ranked = ranked;
        this.players = players;
        this.kills = kills;
        this.win = win;
        this.time = time;
        this.date = date;
    }

    //new
    public PlayerGameHistoryRow(Player p, String map, String mode, Boolean ranked, int players, int kills, Boolean win, int time) {
        this.player_uuid = p.getUniqueId();
        this.player_name = p.getName();
        this.map = map;
        this.mode = mode;
        this.ranked = ranked;
        this.players = players;
        this.kills = kills;
        this.win = win;
        this.time = time;
        this.date = Text.getDateAndTime();
    }

    public PlayerGameHistoryRow() {
        //for ORMLite
    }

    public String getPlayerName() {
        return this.player_name;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    public UUID getPlayerUUID() {
        return this.player_uuid;
    }

    public Boolean isRanked() {
        return this.ranked;
    }

    public int getPlayerCount() {
        return players;
    }

    public String getMap() {
        return map;
    }

    public String getMode() {
        return mode;
    }

    public int getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public Date getFormattedDateObject() {
        DateFormat dateFormat = new SimpleDateFormat(RSWConfig.file().getString("Config.Time.Formatting"));
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }

    public Boolean wasWin() {
        return win;
    }

    public int getKills() {
        return kills;
    }
}