package josegamerpt.realskywars;

import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.commands.PartyCMD;
import josegamerpt.realskywars.commands.RealSkywarsCMD;
import josegamerpt.realskywars.commands.SairCMD;
import josegamerpt.realskywars.configuration.*;
import josegamerpt.realskywars.configuration.checkers.ConfigChecker;
import josegamerpt.realskywars.configuration.checkers.LangChecker;
import josegamerpt.realskywars.configuration.chests.*;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.kits.KitManager;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.nms.*;
import josegamerpt.realskywars.party.PartyManager;
import josegamerpt.realskywars.player.EntityEvents;
import josegamerpt.realskywars.player.PlayerEvents;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.utils.GUIBuilder;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.managers.WorldManager;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;

public class RealSkywars extends JavaPlugin {

    public static Boolean hdInstalado;
    private static Plugin pl;
    private static WorldManager wm = new WorldManager();
    private static LanguageManager lm = new LanguageManager();
    private static PlayerManager playerm = new PlayerManager();
    private static MapManager mapm = new MapManager();
    private static GameManager gamem = new GameManager();
    private static ShopManager shopm = new ShopManager();
    private static KitManager kitm = new KitManager();
    private static PartyManager partym = new PartyManager();

    private static ChestManager chestManager;
    private static RSWnms nms;
    private static Random rand = new Random();
    private PluginManager pm = Bukkit.getPluginManager();
    private CommandManager commandManager;

    public static Plugin getPlugin() {
        return pl;
    }

    public static void log(String s) {
        Bukkit.getLogger().log(Level.INFO, "[RealSkywars] " + s);
    }

    public static void log(Level l, String s) {
        Bukkit.getLogger().log(l, "[RealSkywars] " + s);
    }

    public static WorldManager getWorldManager() {
        return wm;
    }

    public static LanguageManager getLanguageManager() {
        return lm;
    }

    public static PlayerManager getPlayerManager() { return playerm; }

    public static MapManager getMapManager() {
        return mapm;
    }

    public static GameManager getGameManager() {
        return gamem;
    }

    public static ShopManager getShopManager() {
        return shopm;
    }

    public static KitManager getKitManager() {
        return kitm;
    }

    public static PartyManager getPartyManager() {
        return partym;
    }

    public static RSWnms getNMS() {
        return nms;
    }

    public static Random getRandom() {
        return rand;
    }

    public static ChestManager getChestManager() {
        return chestManager;
    }

    public void onEnable() {
        long start = System.currentTimeMillis();
        pl = this;

        String star = "<------------- RealSkywars PT ------------->".replace("PT", "| " +
                this.getDescription().getVersion());
        log(star);

        Debugger.print(RealSkywars.class, "DEBUG MODE ENABLED");
        Debugger.execute();

        hdInstalado = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

        if (setupNMS()) {
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
                Debugger.execute();
                RealSkywars.getGameManager().loadLobby();

                //config
                Maps.setup(this);
                Players.setup(this);
                Shops.setup(this);
                Kits.setup(this);

                //chests
                BasicChest.setup(this);
                BasicChestMiddle.setup(this);
                NormalChest.setup(this);
                NormalChestMiddle.setup(this);
                OPChest.setup(this);
                OPChestMiddle.setup(this);
                CAOSchest.setup(this);
                CAOSchestMiddle.setup(this);

                chestManager = new ChestManager();

                log("Setting up events.");
                pm.registerEvents(new PlayerEvents(), this);
                pm.registerEvents(new EntityEvents(), this);
                pm.registerEvents(GUIBuilder.getListener(), this);
                pm.registerEvents(MapSettings.getListener(), this);
                pm.registerEvents(RoomSettings.getListener(), this);
                pm.registerEvents(PlayerGUI.getListener(), this);
                pm.registerEvents(ShopViewer.getListener(), this);
                pm.registerEvents(ProfileContent.getListener(), this);
                pm.registerEvents(KitSettings.getListener(), this);
                pm.registerEvents(MapsViewer.getListener(), this);

                log("Loading maps.");
                mapm.loadMaps();
                log("Loaded " + RealSkywars.getGameManager().getLoadedInt() + " maps.");
                playerm.loadPlayers();
                kitm.loadKits();
                log("Loaded " + kitm.getKitCount() + " kits.");

                commandManager = new CommandManager(this);
                commandManager.hideTabComplete(true);
                //command suggestions
                commandManager.getCompletionHandler().register("#createsuggestions", input -> {
                    ArrayList<String> sugests = new ArrayList<>();
                    for (int i = 0; i < 200; i++) {
                        sugests.add("Room" + i);
                    }

                    return sugests;
                });

                commandManager.getCompletionHandler().register("#maps", input -> RealSkywars.getGameManager().getRoomNames());
                commandManager.getCompletionHandler().register("#boolean", input -> Arrays.asList("false", "true"));
                commandManager.getCompletionHandler().register("#kits", input -> kitm.getKitNames());

                commandManager.getParameterHandler().register(ChestManager.TierType.class, argument -> {
                    ChestManager.TierType tt = ChestManager.TierType.valueOf(argument.toString().toUpperCase());
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
                    } catch (IllegalArgumentException e)
                    {
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

                long elapsedTimeMillis = System.currentTimeMillis() - start;

                float elapsedTimeSec = elapsedTimeMillis / 1000F;
                log("Finished loading in " + elapsedTimeSec + " seconds.");
                log(star);
            }
        } else {
            getLogger().severe("Your server version " + getServerVersion() + " is not supported by RealSkywars.");
            getLogger().severe("If you think this is a bug, contact the plugin developer.");
            log(star);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private boolean setupNMS() {
        String version = getServerVersion();
        getLogger().info("Your server is running version " + version);

        switch (version) {
            case "v1_17_R1":
                nms = new NMS117R1();
                break;
            case "v1_16_R3":
                nms = new NMS116R3();
                break;
            case "v1_16_R2":
                nms = new NMS116R2();
                break;
            case "v1_16_R1":
                nms = new NMS116R1();
                break;
            case "v1_15_R1":
                nms = new NMS115R1();
                break;
            case "v1_14_R1":
                nms = new NMS114R1();
                break;
        }
        return nms != null;
    }

    public void onDisable() {
        RealSkywars.getGameManager().endGames();
        RealSkywars.getGameManager().getGames().forEach(SWGameMode::clear);
    }

    public void reload() {
        gamem.endGames();

        Config.reload();
        Maps.reload();
        Players.reload();
        Languages.reload();

        //chests
        BasicChest.reload();
        BasicChestMiddle.reload();
        NormalChest.reload();
        NormalChestMiddle.reload();
        OPChest.reload();
        OPChestMiddle.reload();
        CAOSchest.reload();
        CAOSchestMiddle.reload();

        Debugger.debug = Config.file().getBoolean("Debug-Mode");

        lm.loadLanguages();
        playerm.stopScoreboards();
        playerm.loadPlayers();
        Shops.reload();
        Kits.reload();
        kitm.loadKits();

        mapm.loadMaps();
        gamem.loadLobby();
    }
}