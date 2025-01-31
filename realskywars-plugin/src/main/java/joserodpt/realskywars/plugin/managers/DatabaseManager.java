package joserodpt.realskywars.plugin.managers;

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
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWSQLConfig;
import joserodpt.realskywars.api.database.PlayerBoughtItemsRow;
import joserodpt.realskywars.api.database.PlayerDataRow;
import joserodpt.realskywars.api.database.PlayerGameHistoryRow;
import joserodpt.realskywars.api.managers.DatabaseManagerAPI;
import joserodpt.realskywars.api.player.RSWGameHistoryStats;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DatabaseManager extends DatabaseManagerAPI {

    private final Dao<PlayerDataRow, UUID> playerDataDao;
    private final Dao<PlayerGameHistoryRow, UUID> playerGameHistoryDao;
    private final Dao<PlayerBoughtItemsRow, UUID> playerBoughtItemsDao;

    private final Map<UUID, PlayerDataRow> playerDataCache = new HashMap<>();

    private final RealSkywarsAPI rsa;

    public DatabaseManager(RealSkywarsAPI rsa) throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        this.rsa = rsa;
        String databaseURL = getDatabaseURL();

        ConnectionSource connectionSource = new JdbcConnectionSource(
                databaseURL,
                RSWSQLConfig.file().getString("username"),
                RSWSQLConfig.file().getString("password"),
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, PlayerDataRow.class);
        this.playerDataDao = DaoManager.createDao(connectionSource, PlayerDataRow.class);

        createColumnIfNotExists(connectionSource, "choosen_kit", "VARCHAR"); // add new choosen_kit (v0.8)
        createColumnIfNotExists(connectionSource, "first_join", "VARCHAR"); //add first_join (v1)
        createColumnIfNotExists(connectionSource, "last_join", "VARCHAR"); //add last_join (v1)

        getPlayerData();

        TableUtils.createTableIfNotExists(connectionSource, PlayerGameHistoryRow.class);
        this.playerGameHistoryDao = DaoManager.createDao(connectionSource, PlayerGameHistoryRow.class);

        TableUtils.createTableIfNotExists(connectionSource, PlayerBoughtItemsRow.class);
        this.playerBoughtItemsDao = DaoManager.createDao(connectionSource, PlayerBoughtItemsRow.class);
    }

    public void createColumnIfNotExists(ConnectionSource cs, String columnName, String columnType) {
        try {
            if (!doesColumnExist(cs, columnName)) {
                Bukkit.getLogger().warning("[RealSkywars] RealSkywars.db: Upgrading SQL table to add " + columnName + " to realscoreboard_playerdata...");
                playerDataDao.executeRaw("ALTER TABLE realscoreboard_playerdata ADD COLUMN " + columnName + " " + columnType);
                Bukkit.getLogger().warning("[RealSkywars] RealSkywars.db: Upgrade complete!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesColumnExist(ConnectionSource cs, String columnName) {
        try {
            DatabaseMetaData metaData = cs.getReadOnlyConnection(null).getUnderlyingConnection().getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "realscoreboard_playerdata", columnName);
            return columns.next(); // Return true if the column exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Database connection String used for establishing a connection.
     *
     * @return The database URL String
     */
    @Override
    @NotNull
    protected String getDatabaseURL() {
        final String driver = RSWSQLConfig.file().getString("driver").toLowerCase();

        switch (driver) {
            case "mysql":
            case "mariadb":
            case "postgresql":
                return "jdbc:" + driver + "://" + RSWSQLConfig.file().getString("host") + ":" + RSWSQLConfig.file().getInt("port") + "/" + RSWSQLConfig.file().getString("database");
            case "sqlserver":
                return "jdbc:sqlserver://" + RSWSQLConfig.file().getString("host") + ":" + RSWSQLConfig.file().getInt("port") + ";databaseName=" + RSWSQLConfig.file().getString("database");
            default:
                return "jdbc:sqlite:" + new File(rsa.getPlugin().getDataFolder(), RSWSQLConfig.file().getString("database") + ".db");
        }
    }

    @Override
    protected void getPlayerData() {
        try {
            playerDataDao.queryForAll().forEach(playerData -> playerDataCache.put(playerData.getUUID(), playerData));
        } catch (SQLException exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
        }
    }

    @Override
    public Pair<Collection<PlayerGameHistoryRow>, RSWGameHistoryStats> getPlayerGameHistory(Player p) {
        try {
            Collection<PlayerGameHistoryRow> res = playerGameHistoryDao.queryForEq("player_uuid", p.getUniqueId()).stream().sorted((o1, o2) -> o2.getFormattedDateObject().compareTo(o1.getFormattedDateObject())).collect(Collectors.toList());
            return new Pair<>(res, new RSWGameHistoryStats(res));
        } catch (SQLException exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
        }
        return new Pair<>(Collections.emptyList(), new RSWGameHistoryStats());
    }

    @Override
    public List<PlayerBoughtItemsRow> getPlayerBoughtItems(Player p) {
        try {
            return playerBoughtItemsDao.queryForEq("player_uuid", p.getUniqueId()).stream().sorted((o1, o2) -> o2.getFormattedDateObject().compareTo(o1.getFormattedDateObject())).collect(Collectors.toList());
        } catch (SQLException exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<PlayerBoughtItemsRow> getPlayerBoughtItemsCategory(Player p, RSWBuyableItem.ItemCategory cat) {
        try {
            return playerBoughtItemsDao.queryForEq("player_uuid", p.getUniqueId()).stream().filter(a -> a.getCategory().equals(cat.name())).sorted((o1, o2) -> o2.getFormattedDateObject().compareTo(o1.getFormattedDateObject())).collect(Collectors.toList());
        } catch (SQLException exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public PlayerDataRow getPlayerData(Player p) {
        return playerDataCache.getOrDefault(p.getUniqueId(), new PlayerDataRow(p));
    }

    @Override
    public void savePlayerData(PlayerDataRow playerDataRow, boolean async) {
        playerDataCache.put(playerDataRow.getUUID(), playerDataRow);
        if (async && !rsa.getMapManagerAPI().shutdown) {
            Bukkit.getScheduler().runTaskAsynchronously(rsa.getPlugin(), () -> savePlayerData(playerDataRow, false));
        } else {
            try {
                playerDataDao.createOrUpdate(playerDataRow);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while saving the player data:" + throwables.getMessage());
            }
        }
    }

    @Override
    public void saveNewGameHistory(PlayerGameHistoryRow playerGameHistoryRow, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(rsa.getPlugin(), () -> saveNewGameHistory(playerGameHistoryRow, false));
        } else {
            try {
                playerGameHistoryDao.createOrUpdate(playerGameHistoryRow);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while saving the player data:" + throwables.getMessage());
            }
        }
    }

    @Override
    public void saveNewBoughtItem(PlayerBoughtItemsRow playerBoughtItemsRow, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(rsa.getPlugin(), () -> saveNewBoughtItem(playerBoughtItemsRow, false));
        } else {
            try {
                playerBoughtItemsDao.createOrUpdate(playerBoughtItemsRow);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while saving the player data:" + throwables.getMessage());
            }
        }
    }

    @Override
    public void deletePlayerData(UUID playerUUID, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(rsa.getPlugin(), () -> deletePlayerData(playerUUID, false));
        } else {
            try {
                playerDataDao.deleteById(playerUUID);
                playerDataCache.remove(playerUUID);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while deleting the player data:" + throwables.getMessage());
            }
        }
    }

    @Override
    public void deletePlayerGameHistory(UUID playerUUID, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(rsa.getPlugin(), () -> deletePlayerGameHistory(playerUUID, false));
        } else {
            try {
                var deleteBuilder = playerGameHistoryDao.deleteBuilder();
                deleteBuilder.where().eq("player_uuid", playerUUID);
                deleteBuilder.delete();
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while deleting the player data:" + throwables.getMessage());

            }
        }
    }

    @Override
    public void deletePlayerBoughtItems(UUID playerUUID, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(rsa.getPlugin(), () -> deletePlayerBoughtItems(playerUUID, false));
        } else {
            try {
                var deleteBuilder = playerBoughtItemsDao.deleteBuilder();
                deleteBuilder.where().eq("player_uuid", playerUUID);
                deleteBuilder.delete();
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while deleting the player data:" + throwables.getMessage());

            }
        }
    }

    @Override
    public Dao<PlayerDataRow, UUID> getQueryDao() {
        return this.playerDataDao;
    }

    @Override
    public Pair<Boolean, String> didPlayerBoughtItem(RSWPlayer p, RSWBuyableItem item) {
        try {
            PlayerBoughtItemsRow search = playerBoughtItemsDao.queryBuilder().where()
                    .eq("player_uuid", p.getUUID())
                    .and()
                    .eq("item_id", ChatColor.stripColor(item.getConfigKey()))
                    .and()
                    .eq("category", item.getCategory().name())
                    .queryForFirst();

            if (search != null) {
                return new Pair<>(true, search.getDate());
            }
        } catch (SQLException exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
        }
        return new Pair<>(false, null);
    }
}