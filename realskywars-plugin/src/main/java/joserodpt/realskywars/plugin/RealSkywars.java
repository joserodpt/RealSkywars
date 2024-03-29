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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RSWEventsAPI;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWKitsConfig;
import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.config.RSWMapsConfig;
import joserodpt.realskywars.api.config.RSWShopsConfig;
import joserodpt.realskywars.api.config.chests.BasicChestConfig;
import joserodpt.realskywars.api.config.chests.EPICChestConfig;
import joserodpt.realskywars.api.config.chests.NormalChestConfig;
import joserodpt.realskywars.api.managers.AchievementsManagerAPI;
import joserodpt.realskywars.api.managers.ChestManagerAPI;
import joserodpt.realskywars.api.managers.DatabaseManagerAPI;
import joserodpt.realskywars.api.managers.GameManagerAPI;
import joserodpt.realskywars.api.managers.HologramManagerAPI;
import joserodpt.realskywars.api.currency.CurrencyAdapter;
import joserodpt.realskywars.api.managers.KitManagerAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.LeaderboardManagerAPI;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.PartiesManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.managers.WorldManagerAPI;
import joserodpt.realskywars.plugin.managers.*;
import joserodpt.realskywars.api.nms.RSWnms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Logger;

public class RealSkywars extends RealSkywarsAPI {

    private final Random rand = new Random();
    private final Logger logger;
    private final RealSkywarsPlugin plugin;
    private RSWnms rswNMS;
    private final WorldManagerAPI worldManagerAPI;
    private final LanguageManagerAPI languageManagerAPI = new LanguageManager();
    private final PlayerManagerAPI playerManagerAPI;
    private final MapManagerAPI mapManagerAPI;
    private final GameManagerAPI gameManagerAPI;
    private final ShopManagerAPI shopManagerAPI;
    private final KitManagerAPI kitManager = new KitManager();
    private final PartiesManagerAPI partiesManagerAPI;
    private final LeaderboardManagerAPI leaderboardManagerAPI;
    private final AchievementsManagerAPI achievementsManagerAPI;
    public final RSWEventsAPI rswEventsAPI = new RSWEventsAPI();
    private final ChestManagerAPI chestManagerAPI;
    private DatabaseManagerAPI databaseManagerAPI;
    private final HologramManagerAPI hologramManagerAPI;
    private CurrencyAdapter currencyAdapter;

    public RealSkywars(RealSkywarsPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        worldManagerAPI = new WorldManager(this);
        playerManagerAPI = new PlayerManager(this);
        mapManagerAPI = new MapManager(this);
        gameManagerAPI = new GameManager(this);
        shopManagerAPI = new ShopManager(this);
        partiesManagerAPI = new PartiesManager(this);
        leaderboardManagerAPI = new LeaderboardManager(this);
        achievementsManagerAPI = new AchievementsManager(this);
        chestManagerAPI = new ChestManager(this);
        hologramManagerAPI = new HologramManager(this);
    }

    @Override
    public Logger getLogger() { return logger; }
    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
    @Override
    public RSWnms getNMS() { return rswNMS; }

    @Override
    public WorldManagerAPI getWorldManagerAPI() {
        return worldManagerAPI;
    }
    @Override
    public RSWEventsAPI getEventsAPI() { return rswEventsAPI; }

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
    public GameManagerAPI getGameManagerAPI() {
        return this.gameManagerAPI;
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
    public ChestManagerAPI getChestManagerAPI() {
        return this.chestManagerAPI;
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
    public CurrencyAdapter getCurrencyAdapter() {
        return this.currencyAdapter;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getServerVersion() {
        return this.plugin.getServerVersion();
    }

    @Override
    public boolean hasNewUpdate() {
        return this.plugin.hasNewUpdate();
    }

    @Override
    public void reload() {
        gameManagerAPI.endGames();

        RSWConfig.reload();
        RSWMapsConfig.reload();
        RSWLanguagesConfig.reload();

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
        gameManagerAPI.loadLobby();
    }

    @Override
    public Economy getVaultEconomy() {
        return this.plugin.getEconomy();
    }

    public void setCurrencyAdapter(CurrencyAdapter c) {
        this.currencyAdapter = c;
    }

    public void setNMS(RSWnms nms) {
        this.rswNMS = nms;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManagerAPI = databaseManager;
    }
}