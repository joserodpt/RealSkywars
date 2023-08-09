package josegamerpt.realskywars;

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
 */

import josegamerpt.realskywars.achievements.AchievementsManager;
import josegamerpt.realskywars.api.RSWEventsAPI;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.chests.TierViewer;
import josegamerpt.realskywars.commands.PartyCMD;
import josegamerpt.realskywars.commands.RealSkywarsCMD;
import josegamerpt.realskywars.commands.SairCMD;
import josegamerpt.realskywars.configuration.*;
import josegamerpt.realskywars.configuration.chests.BasicChest;
import josegamerpt.realskywars.configuration.chests.EPICChest;
import josegamerpt.realskywars.configuration.chests.NormalChest;
import josegamerpt.realskywars.database.DatabaseManager;
import josegamerpt.realskywars.database.SQL;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.guis.*;
import josegamerpt.realskywars.kits.KitSettings;
import josegamerpt.realskywars.player.PlayerGUI;
import josegamerpt.realskywars.player.ProfileContent;
import josegamerpt.realskywars.shop.ShopManager;
import josegamerpt.realskywars.utils.holograms.HologramManager;
import josegamerpt.realskywars.kits.KitManager;
import josegamerpt.realskywars.leaderboards.LeaderboardManager;
import josegamerpt.realskywars.listeners.EventListener;
import josegamerpt.realskywars.listeners.GameRoomListeners;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.nms.NMS114R1tov116R3;
import josegamerpt.realskywars.nms.NMS117R1;
import josegamerpt.realskywars.nms.NMS118R2andUP;
import josegamerpt.realskywars.nms.RSWnms;
import josegamerpt.realskywars.party.PartyManager;
import josegamerpt.realskywars.player.PlayerEvents;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.utils.GUIBuilder;
import josegamerpt.realskywars.utils.PlayerInput;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.world.SWWorld;
import josegamerpt.realskywars.world.WorldManager;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RealSkywars extends JavaPlugin {
    private static RealSkywars pl;
    public static RealSkywars getPlugin() {
        return pl;
    }
    private boolean newUpdate;

    private RSWnms nms;
    private final WorldManager wm = new WorldManager(this);
    private final LanguageManager lm = new LanguageManager();
    private final PlayerManager playerm = new PlayerManager(this);
    private final MapManager mapm = new MapManager(this);
    private final GameManager gamem = new GameManager(this);
    private final ShopManager shopm = new ShopManager(this);
    private final KitManager kitm = new KitManager();
    private final PartyManager partym = new PartyManager(this);
    private final LeaderboardManager lbm = new LeaderboardManager(this);
    private final AchievementsManager am = new AchievementsManager(this);
    public final RSWEventsAPI rswapie = new RSWEventsAPI();
    private final Random rand = new Random();
    private ChestManager chestManager;
    private DatabaseManager databaseManager;
    private HologramManager hologramManager;
    private final PluginManager pm = Bukkit.getPluginManager();
    public RSWnms getNMS() { return nms; }
    public WorldManager getWorldManager() {
        return wm;
    }
    public RSWEventsAPI getEventsAPI() { return rswapie; }
    public LanguageManager getLanguageManager() {
        return lm;
    }
    public PlayerManager getPlayerManager() {
        return playerm;
    }
    public MapManager getMapManager() {
        return mapm;
    }
    public GameManager getGameManager() {
        return gamem;
    }
    public ShopManager getShopManager() {
        return shopm;
    }
    public KitManager getKitManager() {
        return kitm;
    }
    public PartyManager getPartyManager() {
        return partym;
    }
    public Random getRandom() {
        return rand;
    }
    public ChestManager getChestManager() {
        return chestManager;
    }
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    public LeaderboardManager getLeaderboardManager() {
        return lbm;
    }
    public AchievementsManager getAchievementsManager() {
        return am;
    }
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public void onEnable() {
        long start = System.currentTimeMillis();
        pl = this;

        String star = "<------------- RealSkywars PT ------------->".replace("PT", "| " + this.getDescription().getVersion());
        log(star);

        //setup metrics
        new Metrics(this, 16365);

        //verify nms version
        if (!setupNMS()) {
            getLogger().severe("Your server version is not currently supported by RealSkywars.");
            getLogger().severe("If you think this is a bug, contact JoseGamer_PT.");
            log(star);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        log("Loading languages.");
        Languages.setup(this);

        lm.loadLanguages();
        if (lm.areLanguagesEmpty()) {
            log(Level.SEVERE, "[ERROR] No Languages have been Detected. Stopped loading.");
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        log("Setting up configuration.");
        Config.setup(this);

        Debugger.debug = Config.file().getBoolean("Debug-Mode");
        Debugger.print(RealSkywars.class, "DEBUG MODE ENABLED");
        Debugger.execute();
        this.getGameManager().loadLobby();

        //config
        Achievements.setup(this);
        Maps.setup(this);
        SQL.setup(this);
        Shops.setup(this);
        Kits.setup(this);

        hologramManager = new HologramManager();

        //chests
        BasicChest.setup(this);
        NormalChest.setup(this);
        EPICChest.setup(this);

        try {
            databaseManager = new DatabaseManager(this);
        } catch (SQLException a) {
            getLogger().severe("Error while creating Database Manager for RealSkywars: " + a.getMessage());
        }

        chestManager = new ChestManager();

        log("Setting up events.");
        pm.registerEvents(new PlayerEvents(this), this);
        pm.registerEvents(new EventListener(this), this);
        pm.registerEvents(new GameRoomListeners(), this);
        pm.registerEvents(PlayerInput.getListener(), this);
        pm.registerEvents(GUIBuilder.getListener(), this);
        pm.registerEvents(GameLogViewer.getListener(), this);
        pm.registerEvents(SetupRoomSettings.getListener(), this);
        pm.registerEvents(MapSettings.getListener(), this);
        pm.registerEvents(PlayerGUI.getListener(), this);
        pm.registerEvents(ShopViewer.getListener(), this);
        pm.registerEvents(ProfileContent.getListener(), this);
        pm.registerEvents(KitSettings.getListener(), this);
        pm.registerEvents(MapsViewer.getListener(), this);
        pm.registerEvents(TierViewer.getListener(), this);
        pm.registerEvents(AchievementViewer.getListener(), this);
        pm.registerEvents(GameLogViewer.getListener(), this);
        pm.registerEvents(VoteGUI.getListener(), this);

        kitm.loadKits();
        log("Loaded " + kitm.getKits().size() + " kits.");

        log("Loading maps.");
        mapm.loadMaps();
        log("Loaded " + this.getGameManager().getGames(GameManager.GameModes.ALL).size() + " maps.");
        playerm.loadPlayers();

        am.loadAchievements();

        //load leaderboard
        lbm.refreshLeaderboards();


        CommandManager commandManager = new CommandManager(this);
        commandManager.hideTabComplete(true);
        //command suggestions
        commandManager.getCompletionHandler().register("#createsuggestions", input -> IntStream.range(0, 200)
                .mapToObj(i -> "Map" + i)
                .collect(Collectors.toCollection(ArrayList::new)));

        commandManager.getCompletionHandler().register("#maps", input -> this.getGameManager().getRoomNames());
        commandManager.getCompletionHandler().register("#boolean", input -> Arrays.asList("false", "true"));
        commandManager.getCompletionHandler().register("#worldtype", input -> Arrays.asList("DEFAULT", "SCHEMATIC"));
        commandManager.getCompletionHandler().register("#kits", input -> kitm.getKits().stream()
                .map(kit -> Text.strip(kit.getName()))
                .collect(Collectors.toList()));

        commandManager.getParameterHandler().register(SWChest.Tier.class, argument -> {
            try {
                SWChest.Tier tt = SWChest.Tier.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(SWChest.Type.class, argument -> {
            try {
                SWChest.Type tt = SWChest.Type.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(SWGameMode.Mode.class, argument -> {
            try {
                SWGameMode.Mode tt = SWGameMode.Mode.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(SWWorld.WorldType.class, argument -> {
            try {
                SWWorld.WorldType tt = SWWorld.WorldType.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(CurrencyManager.Operations.class, argument -> {
            try {
                CurrencyManager.Operations tt = CurrencyManager.Operations.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(RealSkywarsCMD.KIT_OPERATION.class, argument -> {
            try {
                RealSkywarsCMD.KIT_OPERATION tt = RealSkywarsCMD.KIT_OPERATION.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });

        //command messages
        commandManager.getMessageHandler().register("cmd.no.exists", sender -> sender.sendMessage(lm.getPrefix() + Text.color("&cThe command you're trying to run doesn't exist!")));
        commandManager.getMessageHandler().register("cmd.no.permission", sender -> sender.sendMessage(lm.getPrefix() + Text.color("&fYou &cdon't &fhave permission to execute this command!")));
        commandManager.getMessageHandler().register("cmd.wrong.usage", sender -> sender.sendMessage(lm.getPrefix() + Text.color("&cWrong usage for the command!")));

        //registo de comandos #portugal
        commandManager.register(new RealSkywarsCMD(this), new SairCMD(this), new PartyCMD(this));

        //placeholderAPI support
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hooked on PlaceholderAPI!");
            new RealSkywarsPlaceholderAPI(this).register();
        }

        //refresh leaderboards
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lbm::refreshLeaderboards, Config.file().getInt("Config.Refresh-Leaderboards"), Config.file().getInt("Config.Refresh-Leaderboards"));

        long elapsedTimeMillis = System.currentTimeMillis() - start;

        new UpdateChecker(this, 105115).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.getLogger().info("The plugin is updated to the latest version.");
            } else {
                this.newUpdate = true;
                this.getLogger().warning("There is a new update available! Version: " + version + " https://www.spigotmc.org/resources/105115/");
            }
        });

        float elapsedTimeSec = elapsedTimeMillis / 1000F;
        log("Finished loading in " + elapsedTimeSec + " seconds.");
        log(star);
    }

    public void onDisable() {
        this.getGameManager().endGames();
        this.getGameManager().getGames(GameManager.GameModes.ALL).forEach(SWGameMode::clear);

        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void reload() {
        gamem.endGames();

        Config.reload();
        Maps.reload();
        Languages.reload();

        //chests
        BasicChest.reload();
        NormalChest.reload();
        EPICChest.reload();

        Debugger.debug = Config.file().getBoolean("Debug-Mode");

        lm.loadLanguages();
        playerm.stopScoreboards();
        playerm.loadPlayers();
        Shops.reload();
        Kits.reload();
        kitm.loadKits();

        am.loadAchievements();
        lbm.refreshLeaderboards();

        mapm.loadMaps();
        gamem.loadLobby();
    }

    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    private boolean setupNMS() {
        String version = getServerVersion();
        getLogger().info("Your server is running version " + version);

        switch (version) {
            case "v1_20_R1":
            case "v1_19_R3":
            case "v1_18_R2":
                nms = new NMS118R2andUP();
                break;
            case "v1_17_R1":
                nms = new NMS117R1();
            case "v1_16_R3":
            case "v1_15_R1":
            case "v1_14_R1":
                nms = new NMS114R1tov116R3();
                break;
        }
        return nms != null;
    }

    public void warning(String s) {
        log(Level.WARNING, s);
    }

    public void severe(String s) {
        log(Level.SEVERE, s);
    }

    public void log(String s) {
        log(Level.INFO, s);
    }

    public void log(Level l, String s) {
        Bukkit.getLogger().log(l, "[RealSkywars] " + s);
    }

    public boolean hasNewUpdate() {
        return this.newUpdate;
    }
}