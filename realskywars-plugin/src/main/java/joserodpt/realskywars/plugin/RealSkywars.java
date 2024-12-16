package joserodpt.realskywars.plugin;

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

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RSWEventsAPI;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWKitsConfig;
import joserodpt.realskywars.api.config.RSWLanguagesOldConfig;
import joserodpt.realskywars.api.config.RSWMapsConfig;
import joserodpt.realskywars.api.config.RSWShopsConfig;
import joserodpt.realskywars.api.config.chests.BasicChestConfig;
import joserodpt.realskywars.api.config.chests.EPICChestConfig;
import joserodpt.realskywars.api.config.chests.NormalChestConfig;
import joserodpt.realskywars.api.currency.CurrencyAdapterAPI;
import joserodpt.realskywars.api.managers.AchievementsManagerAPI;
import joserodpt.realskywars.api.managers.DatabaseManagerAPI;
import joserodpt.realskywars.api.managers.HologramManagerAPI;
import joserodpt.realskywars.api.managers.KitManagerAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.LeaderboardManagerAPI;
import joserodpt.realskywars.api.managers.LobbyManagerAPI;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.PartiesManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.managers.WorldManagerAPI;
import joserodpt.realskywars.api.nms.RSWnms;
import joserodpt.realskywars.plugin.managers.AchievementsManager;
import joserodpt.realskywars.plugin.managers.DatabaseManager;
import joserodpt.realskywars.plugin.managers.HologramManager;
import joserodpt.realskywars.plugin.managers.KitManager;
import joserodpt.realskywars.plugin.managers.LanguageManager;
import joserodpt.realskywars.plugin.managers.LeaderboardManager;
import joserodpt.realskywars.plugin.managers.LobbyManager;
import joserodpt.realskywars.plugin.managers.MapManager;
import joserodpt.realskywars.plugin.managers.PartiesManager;
import joserodpt.realskywars.plugin.managers.PlayerManager;
import joserodpt.realskywars.plugin.managers.ShopManager;
import joserodpt.realskywars.plugin.managers.WorldManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Logger;

public class RealSkywars extends RealSkywarsAPI {

    private final Random rand = new Random();
    private final Logger logger;
    private final RealSkywarsPlugin plugin;
    private RSWnms rswNMS;
    private final WorldManagerAPI worldManagerAPI;
    private final LanguageManagerAPI languageManagerAPI;
    private final PlayerManagerAPI playerManagerAPI;
    private final MapManagerAPI mapManagerAPI;
    private final LobbyManagerAPI lobbyManagerAPI;
    private final ShopManagerAPI shopManagerAPI;
    private final KitManagerAPI kitManager = new KitManager();
    private final PartiesManagerAPI partiesManagerAPI;
    private final LeaderboardManagerAPI leaderboardManagerAPI;
    private final AchievementsManagerAPI achievementsManagerAPI;
    public final RSWEventsAPI rswEventsAPI = new RSWEventsAPI();
    private DatabaseManagerAPI databaseManagerAPI;
    private final HologramManagerAPI hologramManagerAPI;
    private CurrencyAdapterAPI currencyAdapterAPI;

    public RealSkywars(RealSkywarsPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        lobbyManagerAPI = new LobbyManager(this);
        worldManagerAPI = new WorldManager(this);
        playerManagerAPI = new PlayerManager(this);
        languageManagerAPI = new LanguageManager(this);
        mapManagerAPI = new MapManager(this);
        shopManagerAPI = new ShopManager(this);
        partiesManagerAPI = new PartiesManager(this);
        leaderboardManagerAPI = new LeaderboardManager(this);
        achievementsManagerAPI = new AchievementsManager(this);
        hologramManagerAPI = new HologramManager(this);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public RSWnms getNMS() {
        return rswNMS;
    }

    @Override
    public WorldManagerAPI getWorldManagerAPI() {
        return worldManagerAPI;
    }

    @Override
    public RSWEventsAPI getEventsAPI() {
        return rswEventsAPI;
    }

    @Override
    public LanguageManagerAPI getLanguageManagerAPI() {
        return this.languageManagerAPI;
    }

    @Override
    public PlayerManagerAPI getPlayerManagerAPI() {
        return this.playerManagerAPI;
    }

    @Override
    public MapManagerAPI getMapManagerAPI() {
        return this.mapManagerAPI;
    }

    @Override
    public LobbyManagerAPI getLobbyManagerAPI() {
        return this.lobbyManagerAPI;
    }

    @Override
    public ShopManagerAPI getShopManagerAPI() {
        return this.shopManagerAPI;
    }

    @Override
    public KitManagerAPI getKitManagerAPI() {
        return this.kitManager;
    }

    @Override
    public PartiesManagerAPI getPartiesManagerAPI() {
        return this.partiesManagerAPI;
    }

    @Override
    public Random getRandom() {
        return this.rand;
    }

    @Override
    public DatabaseManagerAPI getDatabaseManagerAPI() {
        return this.databaseManagerAPI;
    }

    @Override
    public LeaderboardManagerAPI getLeaderboardManagerAPI() {
        return this.leaderboardManagerAPI;
    }

    @Override
    public AchievementsManagerAPI getAchievementsManagerAPI() {
        return this.achievementsManagerAPI;
    }

    @Override
    public HologramManagerAPI getHologramManagerAPI() {
        return this.hologramManagerAPI;
    }

    @Override
    public CurrencyAdapterAPI getCurrencyAdapterAPI() {
        return this.currencyAdapterAPI;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    @Override
    public String getSimpleServerVersion() {
        return Bukkit.getServer().getBukkitVersion().split("-")[0];
    }

    @Override
    public boolean hasNewUpdate() {
        return this.plugin.hasNewUpdate();
    }

    @Override
    public void reload() {
        mapManagerAPI.endMaps();

        RSWConfig.reload();
        RSWMapsConfig.reload();
        RSWLanguagesOldConfig.reload();

        Debugger.debug = RSWConfig.file().getBoolean("Debug-Mode");

        //chests
        BasicChestConfig.reload();
        NormalChestConfig.reload();
        EPICChestConfig.reload();

        languageManagerAPI.loadLanguages();
        playerManagerAPI.stopScoreboards();
        playerManagerAPI.loadPlayers();
        RSWShopsConfig.reload();
        RSWKitsConfig.reload();
        kitManager.loadKits();

        achievementsManagerAPI.loadAchievements();
        leaderboardManagerAPI.refreshLeaderboards();

        mapManagerAPI.loadMaps();
        lobbyManagerAPI.loadLobby();
    }

    @Override
    public Economy getVaultEconomy() {
        return this.plugin.getEconomy();
    }

    public void setCurrencyAdapter(CurrencyAdapterAPI c) {
        this.currencyAdapterAPI = c;
    }

    public void setNMS(RSWnms nms) {
        this.rswNMS = nms;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManagerAPI = databaseManager;
    }
}