package joserodpt.realskywars.api.managers;

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

import com.j256.ormlite.dao.Dao;
import joserodpt.realskywars.api.database.PlayerBoughtItemsRow;
import joserodpt.realskywars.api.database.PlayerDataRow;
import joserodpt.realskywars.api.database.PlayerGameHistoryRow;
import joserodpt.realskywars.api.player.RSWGameHistoryStats;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.Pair;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class DatabaseManagerAPI {
    @NotNull
    protected abstract String getDatabaseURL();

    protected abstract void getPlayerData();

    public abstract Pair<Collection<PlayerGameHistoryRow>, RSWGameHistoryStats> getPlayerGameHistory(Player p);

    public abstract List<PlayerBoughtItemsRow> getPlayerBoughtItems(Player p);

    public abstract List<PlayerBoughtItemsRow> getPlayerBoughtItemsCategory(Player p, RSWBuyableItem.ItemCategory cat);

    public abstract PlayerDataRow getPlayerData(OfflinePlayer p);

    public abstract void savePlayerData(PlayerDataRow playerDataRow, boolean async);

    public abstract void saveNewGameHistory(PlayerGameHistoryRow playerGameHistoryRow, boolean async);

    public abstract void saveNewBoughtItem(PlayerBoughtItemsRow playerBoughtItemsRow, boolean async);

    public abstract void deletePlayerData(UUID playerUUID, boolean async);

    public abstract void deletePlayerGameHistory(UUID playerUUID, boolean async);

    public abstract void deletePlayerBoughtItems(UUID playerUUID, boolean async);

    public abstract Dao<PlayerDataRow, UUID> getQueryDao();

    public abstract Pair<Boolean, String> didPlayerBoughtItem(RSWPlayer p, RSWBuyableItem item);
}
