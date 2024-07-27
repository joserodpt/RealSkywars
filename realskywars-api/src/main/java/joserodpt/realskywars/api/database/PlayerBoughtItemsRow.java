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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "realscoreboard_player_bought_items")
public class PlayerBoughtItemsRow {
    @DatabaseField(columnName = "id", canBeNull = false, allowGeneratedIdInsert = true, generatedId = true)
    private @NotNull UUID id;

    @DatabaseField(columnName = "player_uuid")
    private @NotNull UUID player_uuid;

    @DatabaseField(columnName = "player_name")
    private String player_name;

    @DatabaseField(columnName = "item_id")
    private String itemID;

    @DatabaseField(columnName = "category")
    private String category;

    @DatabaseField(columnName = "bought_date")
    private String date;

    public PlayerBoughtItemsRow(RSWPlayer p, String itemID, String category) {
        this.player_uuid = p.getUUID();
        this.player_name = p.getName();
        this.itemID = ChatColor.stripColor(itemID);
        this.category = category;
        this.date = Text.getDateAndTime();
    }

    public PlayerBoughtItemsRow() {
        //for ORMLite
    }

    public String getPlayerName() {
        return this.player_name;
    }

    @NotNull
    public UUID getId() {
        return this.id;
    }

    public UUID getPlayerUUID() {
        return this.player_uuid;
    }

    public String getItemID() {
        return itemID;
    }

    public String getCategory() {
        return category;
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
}