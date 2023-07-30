package josegamerpt.realskywars;

import josegamerpt.realskywars.achievements.AchievementsManager;
import josegamerpt.realskywars.api.RSWEventsAPI;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.commands.PartyCMD;
import josegamerpt.realskywars.commands.RealSkywarsCMD;
import josegamerpt.realskywars.commands.SairCMD;
import josegamerpt.realskywars.configuration.*;
import josegamerpt.realskywars.configuration.checkers.ConfigChecker;
import josegamerpt.realskywars.configuration.checkers.LangChecker;
import josegamerpt.realskywars.configuration.chests.BasicChest;
import josegamerpt.realskywars.configuration.chests.EPICChest;
import josegamerpt.realskywars.configuration.chests.NormalChest;
import josegamerpt.realskywars.database.DatabaseManager;
import josegamerpt.realskywars.database.SQL;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.guis.*;
import josegamerpt.realskywars.holograms.HologramManager;
import josegamerpt.realskywars.kits.KitManager;
import josegamerpt.realskywars.leaderboards.LeaderboardManager;
import josegamerpt.realskywars.listeners.EventListener;
import josegamerpt.realskywars.listeners.GameRoomListeners;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.party.PartyManager;
import josegamerpt.realskywars.player.PlayerEvents;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.sign.SignManager;
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

public class RealSkywars extends JavaPlugin {
    private static RealSkywars pl;

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
    private final SignManager sm = new SignManager(this);
    public final RSWEventsAPI rswapie = new RSWEventsAPI();
    private final Random rand = new Random();
    private ChestManager chestManager;
    private DatabaseManager databaseManager;
    private HologramManager hologramManager;
    private final PluginManager pm = Bukkit.getPluginManager();

    public static RealSkywars getPlugin() {
        return pl;
    }

    public static void log(String s) {
        Bukkit.getLogger().log(Level.INFO, "[RealSkywars] " + s);
    }

    public static void log(Level l, String s) {
        Bukkit.getLogger().log(l, "[RealSkywars] " + s);
    }

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

    public SignManager getSignManager() {
        return sm;
    }

    public void onEnable() { //TODO: passar instancia disto em vez de tudo static
        long start = System.currentTimeMillis();
        pl = this;

        String star = "<------------- RealSkywars PT ------------->".replace("PT", "| " + this.getDescription().getVersion());
        log(star);

        //setup metrics
        new Metrics(this, 16365);

        log("Loading languages.");
        Languages.setup(this);
        LangChecker.updateConfig();

        if (LangChecker.checkForErrors()) {
            log(Level.SEVERE, "There are some problems with your languages.yml: " + LangChecker.getErrors() + "\nPlease check this errors. Plugin is disabled due to config errors.");
            log(Level.INFO, star);
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        lm.loadLanguages();
        if (!lm.checkSelect()) {
            log(Level.SEVERE, "[ERROR] No Languages have been Detected. Stopped loading.");
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        log("Setting up configuration.");
        saveDefaultConfig();
        Config.setup(this);
        ConfigChecker.updateConfig();

        if (ConfigChecker.checkForErrors()) {
            log(Level.SEVERE, "There are some problems with your config.yml: " + ConfigChecker.getErrors() + "\nPlease check this errors. Plugin is disabled due to config errors.");
            log(Level.INFO, star);
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
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
            Signs.setup(this);

            hologramManager = new HologramManager();

            //chests
            BasicChest.setup(this);
            NormalChest.setup(this);
            EPICChest.setup(this);

            try {
                databaseManager = new DatabaseManager(this);
            } catch (SQLException a) {
                a.printStackTrace();
            }

            chestManager = new ChestManager();

            log("Setting up events.");
            pm.registerEvents(new PlayerEvents(this), this);
            pm.registerEvents(new EventListener(this), this);
            pm.registerEvents(new GameRoomListeners(), this);
            pm.registerEvents(PlayerInput.getListener(), this);
            pm.registerEvents(GUIBuilder.getListener(), this);
            pm.registerEvents(GameLogViewer.getListener(), this);
            pm.registerEvents(MapSettings.getListener(), this);
            pm.registerEvents(RoomSettings.getListener(), this);
            pm.registerEvents(PlayerGUI.getListener(), this);
            pm.registerEvents(ShopViewer.getListener(), this);
            pm.registerEvents(ProfileContent.getListener(), this);
            pm.registerEvents(KitSettings.getListener(), this);
            pm.registerEvents(MapsViewer.getListener(), this);
            pm.registerEvents(TierViewer.getListener(), this);
            pm.registerEvents(AchievementViewer.getListener(), this);
            pm.registerEvents(GameLogViewer.getListener(), this);

            //load leaderboard
            lbm.refreshLeaderboards();

            log("Loading maps.");
            mapm.loadMaps();
            log("Loaded " + this.getGameManager().getLoadedInt() + " maps.");
            playerm.loadPlayers();
            kitm.loadKits();
            log("Loaded " + kitm.getKitCount() + " kits.");
            am.loadAchievements();

            CommandManager commandManager = new CommandManager(this);
            commandManager.hideTabComplete(true);
            //command suggestions
            commandManager.getCompletionHandler().register("#createsuggestions", input -> {
                ArrayList<String> sugests = new ArrayList<>();
                for (int i = 0; i < 200; ++i) {
                    sugests.add("Room" + i);
                }

                return sugests;
            });

            commandManager.getCompletionHandler().register("#maps", input -> this.getGameManager().getRoomNames());
            commandManager.getCompletionHandler().register("#boolean", input -> Arrays.asList("false", "true"));
            commandManager.getCompletionHandler().register("#worldtype", input -> Arrays.asList("DEFAULT", "SCHEMATIC"));
            commandManager.getCompletionHandler().register("#kits", input -> kitm.getKitNames());

            commandManager.getParameterHandler().register(SWChest.Tier.class, argument -> {
                SWChest.Tier tt = SWChest.Tier.valueOf(argument.toString().toUpperCase());
                if (tt == null) return new TypeResult(argument);
                return new TypeResult(tt, argument);
            });
            commandManager.getParameterHandler().register(SWGameMode.Mode.class, argument -> {
                try {
                    SWGameMode.Mode tt = SWGameMode.Mode.valueOf(argument.toString().toUpperCase());
                    if (tt == null) return new TypeResult(argument);
                    return new TypeResult(tt, argument);
                } catch (Exception e) {
                    return new TypeResult(argument);
                }
            });
            commandManager.getParameterHandler().register(SWWorld.WorldType.class, argument -> {
                try {
                    SWWorld.WorldType tt = SWWorld.WorldType.valueOf(argument.toString().toUpperCase());
                    if (tt == null) return new TypeResult(argument);
                    return new TypeResult(tt, argument);
                } catch (Exception e) {
                    return new TypeResult(argument);
                }
            });
            commandManager.getParameterHandler().register(CurrencyManager.Operations.class, argument -> {
                CurrencyManager.Operations tt = CurrencyManager.Operations.valueOf(argument.toString().toLowerCase());
                if (tt == null) return new TypeResult(argument);
                return new TypeResult(tt, argument);
            });
            commandManager.getParameterHandler().register(RealSkywarsCMD.KIT.class, argument -> {
                RealSkywarsCMD.KIT tt;
                try {
                    tt = RealSkywarsCMD.KIT.valueOf(argument.toString().toLowerCase());
                    if (tt == null) return new TypeResult(argument);
                } catch (IllegalArgumentException e) {
                    return new TypeResult(argument);
                }
                return new TypeResult(tt, argument);
            });

            //command messages
            commandManager.getMessageHandler().register("cmd.no.exists", sender -> sender.sendMessage(lm.getPrefix() + Text.color("&cThe command you're trying to run doesn't exist!")));
            commandManager.getMessageHandler().register("cmd.no.permission", sender -> sender.sendMessage(lm.getPrefix() + Text.color("&fYou &cdon't &fhave permission to execute this command!")));
            commandManager.getMessageHandler().register("cmd.wrong.usage", sender -> sender.sendMessage(lm.getPrefix() + Text.color("&cWrong usage for the command!")));

            //registo de comandos #portugal
            commandManager.register(new RealSkywarsCMD(this), new SairCMD(this), new PartyCMD(this));

            //placeholderAPI support
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                RealSkywars.log("Hooked on PlaceholderAPI!");
                new RealSkywarsPlaceholderAPI(this).register();
            }

            //load signs
            sm.loadSigns();

            //refresh leaderboards
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lbm::refreshLeaderboards, Config.file().getInt("Config.Refresh-Leaderboards"), Config.file().getInt("Config.Refresh-Leaderboards"));

            long elapsedTimeMillis = System.currentTimeMillis() - start;

            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            log("Finished loading in " + elapsedTimeSec + " seconds.");
            log(star);
        }
    }

    public void onDisable() {
        this.getGameManager().endGames();
        this.getGameManager().getGames(PlayerManager.Modes.ALL).forEach(SWGameMode::clear);
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
        Signs.reload();
        kitm.loadKits();

        am.loadAchievements();
        lbm.refreshLeaderboards();

        mapm.loadMaps();
        gamem.loadLobby();
    }
}