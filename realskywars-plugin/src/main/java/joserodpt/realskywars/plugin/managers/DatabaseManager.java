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
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DatabaseManager extends DatabaseManagerAPI {

    private static final String PLAYER_DATA_TABLE = "realskywars_playerdata";
    private static final String GAME_HISTORY_TABLE = "realskywars_player_game_history";
    private static final String BOUGHT_ITEMS_TABLE = "realskywars_player_bought_items";

    //the names used before 1.x, which collided with RealScoreboard's own realscoreboard_playerdata on a shared schema
    private static final String LEGACY_PLAYER_DATA_TABLE = "realscoreboard_playerdata";
    private static final String LEGACY_GAME_HISTORY_TABLE = "realscoreboard_player_game_history";
    private static final String LEGACY_BOUGHT_ITEMS_TABLE = "realscoreboard_player_bought_items";

    private final ConnectionSource connectionSource;
    private final boolean sqlServer;
    private final Dao<PlayerDataRow, UUID> playerDataDao;
    private final Dao<PlayerGameHistoryRow, UUID> playerGameHistoryDao;
    private final Dao<PlayerBoughtItemsRow, UUID> playerBoughtItemsDao;

    /**
     * Only players who are online (or logging in). Everything else is read from the database when it
     * is needed, so a shared MySQL behind a Bungee network never serves another server's stale row.
     */
    private final Map<UUID, PlayerDataRow> playerDataCache = new ConcurrentHashMap<>();
    private final Map<UUID, List<PlayerBoughtItemsRow>> boughtItemsCache = new ConcurrentHashMap<>();

    /**
     * Players who quit and are waiting to be evicted. A rejoin before the eviction runs takes them
     * back out, so the fresh entry isn't thrown away.
     */
    private final Set<UUID> unloaded = ConcurrentHashMap.newKeySet();

    /**
     * Every read and write goes through this one thread: writes land in the order they were made, a
     * rejoin's load always sees the quit's save, and a single SQLite connection is never shared
     * between threads.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "RealSkywars-Database");
        t.setDaemon(true);
        return t;
    });

    private final RealSkywarsAPI rsa;

    public DatabaseManager(RealSkywarsAPI rsa) throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        this.rsa = rsa;
        String databaseURL = getDatabaseURL();
        String username = RSWSQLConfig.file().getString("username");
        String password = RSWSQLConfig.file().getString("password");
        this.sqlServer = databaseURL.startsWith("jdbc:sqlserver:");

        if (databaseURL.startsWith("jdbc:sqlite:")) {
            //a local file that never times out, and more than one connection to it only buys "database is locked"
            this.connectionSource = new JdbcConnectionSource(databaseURL, username, password, DatabaseTypeUtils.createDatabaseType(databaseURL));
        } else {
            //a single connection is never reopened once the server drops it (wait_timeout, a restart)
            JdbcPooledConnectionSource pooled = new JdbcPooledConnectionSource(databaseURL, username, password, DatabaseTypeUtils.createDatabaseType(databaseURL));
            pooled.setTestBeforeGet(true);
            pooled.setMaxConnectionAgeMillis(TimeUnit.MINUTES.toMillis(30));
            this.connectionSource = pooled;
        }

        try {
            //before the tables are created, or an empty new table would hide the old data
            migrateLegacyTable(LEGACY_PLAYER_DATA_TABLE, PLAYER_DATA_TABLE, "stats_wins_solo", PlayerDataRow.class, "name IS NOT NULL");
            migrateLegacyTable(LEGACY_GAME_HISTORY_TABLE, GAME_HISTORY_TABLE, "ranked", PlayerGameHistoryRow.class, null);
            migrateLegacyTable(LEGACY_BOUGHT_ITEMS_TABLE, BOUGHT_ITEMS_TABLE, "item_id", PlayerBoughtItemsRow.class, null);

            TableUtils.createTableIfNotExists(this.connectionSource, PlayerDataRow.class);
            this.playerDataDao = DaoManager.createDao(this.connectionSource, PlayerDataRow.class);

            createColumnIfNotExists("choosen_kit", "VARCHAR(255)"); // add new choosen_kit (v0.8)
            createColumnIfNotExists("first_join", "VARCHAR(255)"); //add first_join (v1)
            createColumnIfNotExists("last_join", "VARCHAR(255)"); //add last_join (v1)

            TableUtils.createTableIfNotExists(this.connectionSource, PlayerGameHistoryRow.class);
            this.playerGameHistoryDao = DaoManager.createDao(this.connectionSource, PlayerGameHistoryRow.class);

            TableUtils.createTableIfNotExists(this.connectionSource, PlayerBoughtItemsRow.class);
            this.playerBoughtItemsDao = DaoManager.createDao(this.connectionSource, PlayerBoughtItemsRow.class);
        } catch (SQLException e) {
            this.executor.shutdownNow();
            try {
                this.connectionSource.close();
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    // ---------------------------------------------------------------- schema

    /**
     * Renames a table from the old realscoreboard_* name to its realskywars_* one. Only a table that
     * carries a RealSkywars column is touched, so RealScoreboard's own table is never renamed, and a
     * table both plugins ended up writing to is copied from instead of moved.
     */
    private void migrateLegacyTable(String legacy, String current, String rswColumn, Class<?> rowClass, String copyFilter) throws SQLException {
        if (tableExists(current) || !tableExists(legacy)) {
            return;
        }

        if (!doesColumnExist(legacy, rswColumn)) {
            rsa.getLogger().info("Found a " + legacy + " table without RealSkywars' columns, leaving it alone.");
            return;
        }

        if (doesColumnExist(legacy, "scoreboard_on")) {
            //RealScoreboard shares this table: copy RealSkywars' columns out and leave the original for it
            rsa.getLogger().warning("Table " + legacy + " is shared with RealScoreboard. Copying RealSkywars' data into " + current + "...");
            TableUtils.createTableIfNotExists(this.connectionSource, rowClass);

            List<String> columns = new ArrayList<>();
            for (String column : getColumnNames(rowClass)) {
                if (doesColumnExist(legacy, column)) {
                    columns.add(column);
                }
            }
            String cols = String.join(", ", columns);
            execute("INSERT INTO " + current + " (" + cols + ") SELECT " + cols + " FROM " + legacy
                    + (copyFilter == null ? "" : " WHERE " + copyFilter));
            rsa.getLogger().warning("Copied " + legacy + " into " + current + ". The old table was left untouched.");
            return;
        }

        rsa.getLogger().warning("Renaming table " + legacy + " to " + current + "...");
        execute(this.sqlServer
                ? "EXEC sp_rename '" + legacy + "', '" + current + "'"
                : "ALTER TABLE " + legacy + " RENAME TO " + current);
        rsa.getLogger().warning("Renamed " + legacy + " to " + current + ".");
    }

    private static List<String> getColumnNames(Class<?> rowClass) {
        List<String> columns = new ArrayList<>();
        for (Field f : rowClass.getDeclaredFields()) {
            DatabaseField df = f.getAnnotation(DatabaseField.class);
            if (df != null) {
                columns.add(df.columnName().isEmpty() ? f.getName() : df.columnName());
            }
        }
        return columns;
    }

    private boolean tableExists(String table) {
        return probe("SELECT 1 FROM " + table + " WHERE 1=0");
    }

    /**
     * A probe query instead of DatabaseMetaData: metadata lookups with a null catalog search every
     * database on a MySQL server, and would find another server's table with the same name.
     */
    public boolean doesColumnExist(String table, String columnName) {
        return probe("SELECT " + columnName + " FROM " + table + " WHERE 1=0");
    }

    private boolean probe(String sql) {
        try {
            DatabaseConnection connection = this.connectionSource.getReadWriteConnection(null);
            try (Statement st = connection.getUnderlyingConnection().createStatement();
                 ResultSet ignored = st.executeQuery(sql)) {
                return true;
            } catch (SQLException e) {
                return false;
            } finally {
                //handed back, or a pooled source would lose the connection for good
                this.connectionSource.releaseConnection(connection);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void execute(String sql) throws SQLException {
        DatabaseConnection connection = this.connectionSource.getReadWriteConnection(null);
        try (Statement st = connection.getUnderlyingConnection().createStatement()) {
            st.execute(sql);
        } finally {
            this.connectionSource.releaseConnection(connection);
        }
    }

    public void createColumnIfNotExists(String columnName, String columnType) {
        try {
            if (!doesColumnExist(PLAYER_DATA_TABLE, columnName)) {
                rsa.getLogger().warning("Upgrading SQL table to add " + columnName + " to " + PLAYER_DATA_TABLE + "...");
                playerDataDao.executeRaw("ALTER TABLE " + PLAYER_DATA_TABLE + " ADD COLUMN " + columnName + " " + columnType);
                rsa.getLogger().warning("Upgrade complete!");
            }
        } catch (SQLException e) {
            rsa.getLogger().severe("Couldn't add column " + columnName + ": " + e.getMessage());
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

    // ---------------------------------------------------------------- player cache

    @Override
    public void preloadPlayer(UUID uuid, String name) {
        try {
            this.executor.submit(() -> {
                load(uuid, name);
                return null;
            }).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            rsa.getLogger().warning("Couldn't preload the data of " + name + ": " + e.getMessage());
        }
    }

    /**
     * Runs on the database thread.
     */
    private void load(UUID uuid, String name) throws SQLException {
        this.unloaded.remove(uuid);
        PlayerDataRow row = this.playerDataDao.queryForId(uuid);
        if (row == null) {
            row = new PlayerDataRow(uuid, name);
        }
        List<PlayerBoughtItemsRow> bought = new CopyOnWriteArrayList<>(this.playerBoughtItemsDao.queryForEq("player_uuid", uuid));
        this.playerDataCache.put(uuid, row);
        this.boughtItemsCache.put(uuid, bought);
    }

    @Override
    public void unloadPlayer(UUID uuid) {
        this.unloaded.add(uuid);
        //queued behind the quit's own save, so the row is on disk before it leaves the cache
        runAsync(() -> {
            if (this.unloaded.remove(uuid)) {
                this.playerDataCache.remove(uuid);
                this.boughtItemsCache.remove(uuid);
            }
        });
    }

    @Override
    public PlayerDataRow getPlayerData(OfflinePlayer p) {
        PlayerDataRow row = this.playerDataCache.get(p.getUniqueId());
        if (row != null) {
            return row;
        }

        if (p.isOnline()) {
            //the async pre login preload didn't happen or didn't finish (a reload, a timeout), so pay for it here
            try {
                await(() -> {
                    load(p.getUniqueId(), p.getName());
                    return null;
                });
            } catch (Exception e) {
                //never hand out a blank row here: saving it would overwrite the player's real stats
                throw new IllegalStateException("Couldn't load the data of " + p.getName() + ": " + e.getMessage(), e);
            }
            row = this.playerDataCache.get(p.getUniqueId());
            if (row != null) {
                return row;
            }
        }

        //an offline player: not cached, so it can't go stale
        return new PlayerDataRow(p);
    }

    @Override
    public void savePlayerData(PlayerDataRow playerDataRow, boolean async) {
        //copied here, on the thread that mutates it, so the writer never sees a half updated row
        final PlayerDataRow snapshot = playerDataRow.copy();
        Runnable write = () -> {
            try {
                playerDataDao.createOrUpdate(snapshot);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while saving the player data:" + throwables.getMessage());
            }
        };
        submit(write, async);
    }

    // ---------------------------------------------------------------- history and bought items

    @Override
    public Pair<Collection<PlayerGameHistoryRow>, RSWGameHistoryStats> getPlayerGameHistory(Player p) {
        try {
            Collection<PlayerGameHistoryRow> res = await(() -> playerGameHistoryDao.queryForEq("player_uuid", p.getUniqueId())).stream().sorted((o1, o2) -> o2.getFormattedDateObject().compareTo(o1.getFormattedDateObject())).collect(Collectors.toList());
            return new Pair<>(res, new RSWGameHistoryStats(res));
        } catch (Exception exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
        }
        return new Pair<>(Collections.emptyList(), new RSWGameHistoryStats());
    }

    private List<PlayerBoughtItemsRow> boughtItems(UUID uuid) {
        List<PlayerBoughtItemsRow> cached = this.boughtItemsCache.get(uuid);
        if (cached != null) {
            return cached;
        }
        try {
            return await(() -> playerBoughtItemsDao.queryForEq("player_uuid", uuid));
        } catch (Exception exception) {
            rsa.getLogger().severe("Error while getting the player data:" + exception.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<PlayerBoughtItemsRow> getPlayerBoughtItems(Player p) {
        return boughtItems(p.getUniqueId()).stream().sorted((o1, o2) -> o2.getFormattedDateObject().compareTo(o1.getFormattedDateObject())).collect(Collectors.toList());
    }

    @Override
    public List<PlayerBoughtItemsRow> getPlayerBoughtItemsCategory(Player p, RSWBuyableItem.ItemCategory cat) {
        return boughtItems(p.getUniqueId()).stream().filter(a -> cat.name().equals(a.getCategory())).sorted((o1, o2) -> o2.getFormattedDateObject().compareTo(o1.getFormattedDateObject())).collect(Collectors.toList());
    }

    @Override
    public Pair<Boolean, String> didPlayerBoughtItem(RSWPlayer p, RSWBuyableItem item) {
        String itemID = ChatColor.stripColor(item.getConfigKey());
        String category = item.getCategory().name();
        //served from the cache, so the shop doesn't run a query per icon on the main thread
        Optional<PlayerBoughtItemsRow> search = boughtItems(p.getUUID()).stream()
                .filter(row -> itemID.equals(row.getItemID()) && category.equals(row.getCategory()))
                .findFirst();
        return search.map(row -> new Pair<>(true, row.getDate())).orElseGet(() -> new Pair<>(false, null));
    }

    @Override
    public void saveNewGameHistory(PlayerGameHistoryRow playerGameHistoryRow, boolean async) {
        submit(() -> {
            try {
                playerGameHistoryDao.createOrUpdate(playerGameHistoryRow);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while saving the player data:" + throwables.getMessage());
            }
        }, async);
    }

    @Override
    public void saveNewBoughtItem(PlayerBoughtItemsRow playerBoughtItemsRow, boolean async) {
        List<PlayerBoughtItemsRow> cached = this.boughtItemsCache.get(playerBoughtItemsRow.getPlayerUUID());
        if (cached != null) {
            cached.add(playerBoughtItemsRow);
        }
        submit(() -> {
            try {
                playerBoughtItemsDao.createOrUpdate(playerBoughtItemsRow);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while saving the player data:" + throwables.getMessage());
            }
        }, async);
    }

    @Override
    public void deletePlayerData(UUID playerUUID, boolean async) {
        submit(() -> {
            try {
                playerDataDao.deleteById(playerUUID);
                playerDataCache.remove(playerUUID);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while deleting the player data:" + throwables.getMessage());
            }
        }, async);
    }

    @Override
    public void deletePlayerGameHistory(UUID playerUUID, boolean async) {
        submit(() -> {
            try {
                var deleteBuilder = playerGameHistoryDao.deleteBuilder();
                deleteBuilder.where().eq("player_uuid", playerUUID);
                deleteBuilder.delete();
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while deleting the player data:" + throwables.getMessage());
            }
        }, async);
    }

    @Override
    public void deletePlayerBoughtItems(UUID playerUUID, boolean async) {
        submit(() -> {
            try {
                var deleteBuilder = playerBoughtItemsDao.deleteBuilder();
                deleteBuilder.where().eq("player_uuid", playerUUID);
                deleteBuilder.delete();
                boughtItemsCache.remove(playerUUID);
            } catch (SQLException throwables) {
                rsa.getLogger().severe("Error while deleting the player data:" + throwables.getMessage());
            }
        }, async);
    }

    @Override
    public Dao<PlayerDataRow, UUID> getQueryDao() {
        return this.playerDataDao;
    }

    @Override
    public void getTopPlayers(String column, long limit, Consumer<List<PlayerDataRow>> callback) {
        runAsync(() -> {
            try {
                QueryBuilder<PlayerDataRow, UUID> qb = playerDataDao.queryBuilder();
                qb.orderBy(column, false).limit(limit);
                List<PlayerDataRow> rows = playerDataDao.query(qb.prepare());
                runSync(() -> callback.accept(rows));
            } catch (SQLException e) {
                rsa.getLogger().severe("Error while loading the leaderboard for " + column + " -> " + e.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- plumbing

    private void submit(Runnable runnable, boolean async) {
        if (async) {
            runAsync(runnable);
        } else {
            //still through the executor, so it can't overtake a write queued before it
            try {
                await(() -> {
                    runnable.run();
                    return null;
                });
            } catch (Exception e) {
                rsa.getLogger().severe("Database task failed: " + e.getMessage());
            }
        }
    }

    private void runAsync(Runnable runnable) {
        try {
            this.executor.execute(runnable);
        } catch (RejectedExecutionException e) {
            //already shutting down, so there is nowhere left to hand it to
            runnable.run();
        }
    }

    private <T> T await(Callable<T> callable) throws Exception {
        try {
            return this.executor.submit(callable).get(10, TimeUnit.SECONDS);
        } catch (RejectedExecutionException e) {
            return callable.call();
        }
    }

    private void runSync(Runnable runnable) {
        if (rsa.getPlugin().isEnabled()) {
            Bukkit.getScheduler().runTask(rsa.getPlugin(), runnable);
        }
    }

    @Override
    public void close() {
        //let whatever was already handed off (quit saves, history rows) finish first
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                rsa.getLogger().warning("Gave up waiting on pending database writes.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            this.connectionSource.close();
        } catch (Exception e) {
            rsa.getLogger().warning("Couldn't close the database connection: " + e.getMessage());
        }
    }
}
