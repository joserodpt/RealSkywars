package josegamerpt.realskywars.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final ConnectionSource connectionSource;

    private final Dao<PlayerData, UUID> playerDataDao;

    private final JavaPlugin javaPlugin;

    public DatabaseManager(JavaPlugin javaPlugin) throws SQLException {
        this.javaPlugin = javaPlugin;
        String databaseURL = getDatabaseURL();

        connectionSource = new JdbcConnectionSource(
                databaseURL,
                SQL.file().getString("username"),
                SQL.file().getString("password"),
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, PlayerData.class);

        this.playerDataDao = DaoManager.createDao(connectionSource, PlayerData.class);
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

    public CompletableFuture<PlayerData> getPlayerData(Player p) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData playerData = null;
            try {
                playerData = playerDataDao.queryForId(p.getUniqueId());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            if (playerData == null) {
                playerData = new PlayerData(p);
                savePlayerData(playerData, true);
            }
            return playerData;
        });
    }

    public void savePlayerData(PlayerData playerData, boolean async) {
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