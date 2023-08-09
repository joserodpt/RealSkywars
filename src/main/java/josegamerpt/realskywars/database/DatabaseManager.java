package josegamerpt.realskywars.database;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class DatabaseManager {

    private final Dao<PlayerData, UUID> playerDataDao;
    private final HashMap<UUID, PlayerData> playerDataCache = new HashMap<>();
    private final JavaPlugin javaPlugin;

    public DatabaseManager(JavaPlugin javaPlugin) throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        this.javaPlugin = javaPlugin;
        String databaseURL = getDatabaseURL();

        ConnectionSource connectionSource = new JdbcConnectionSource(
                databaseURL,
                SQL.file().getString("username"),
                SQL.file().getString("password"),
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, PlayerData.class);

        this.playerDataDao = DaoManager.createDao(connectionSource, PlayerData.class);

        getPlayerData();
    }

    /**
     * Database connection String used for establishing a connection.
     *
     * @return The database URL String
     */
    private @NotNull String getDatabaseURL() {
        final String driver = SQL.file().getString("driver").toLowerCase(Locale.ROOT);

        switch (driver) {
            case "mysql":
            case "mariadb":
            case "postgresql":
                return "jdbc:" + driver + "://" + SQL.file().getString("host") + ":" + SQL.file().getInt("port") + "/" + SQL.file().getString("database");
            case "sqlserver":
                return "jdbc:sqlserver://" + SQL.file().getString("host") + ":" + SQL.file().getInt("port") + ";databaseName=" + SQL.file().getString("database");
            default:
                return "jdbc:sqlite:" + new File(javaPlugin.getDataFolder(), SQL.file().getString("database") + ".db");
        }
    }

    private void getPlayerData() {
        try {
            playerDataDao.queryForAll().forEach(playerData -> playerDataCache.put(playerData.getUUID(), playerData));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public PlayerData getPlayerData(Player p) {
        return playerDataCache.getOrDefault(p.getUniqueId(), new PlayerData(p));
    }

    public void savePlayerData(PlayerData playerData, boolean async) {
        playerDataCache.put(playerData.getUUID(), playerData);
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> savePlayerData(playerData, false));
        } else {
            try {
                playerDataDao.createOrUpdate(playerData);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void deletePlayerData(PlayerData playerData, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> deletePlayerData(playerData, false));
        } else {
            try {
                playerDataDao.delete(playerData);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public Dao<PlayerData, UUID> getQueryDao() {
        return this.playerDataDao;
    }
}