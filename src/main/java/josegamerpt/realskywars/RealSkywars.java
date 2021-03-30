package josegamerpt.realskywars;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.configuration.*;
import josegamerpt.realskywars.configuration.chests.*;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.player.EntityEvents;
import josegamerpt.realskywars.player.PlayerEvents;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.utils.GUIBuilder;
import josegamerpt.realskywars.utils.PlayerInput;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.worlds.WorldManager;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class RealSkywars extends JavaPlugin implements Listener {

    private static Plugin pl;
    private static WorldManager wm = new WorldManager();
    private static Random rand = new Random();
    PluginManager pm = Bukkit.getPluginManager();
    CommandManager commandManager;

    private static ChestManager chestManager;

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

    public static Random getRandom() { return rand; }

    public void onEnable() {
        long start = System.currentTimeMillis();
        pl = this;

        String star = "<------------- RealSkywars PT ------------->".replace("PT", "| " +
                this.getDescription().getVersion());
        log(star);

        Debugger.print(RealSkywars.class, "DEBUG MODE ENABLED");
        Debugger.execute();

        Languages.setup(this);

        log("Loading languages.");
        LanguageManager.loadLanguages();
        if (!LanguageManager.checkSelect()) {
            log("[FATAL] No Language Detected. Stopped loading.");
            pm.disablePlugin(this);
            return;
        }

        log("Setting up configuration.");
        saveDefaultConfig();
        Config.setup(this);
        Debugger.debug = Config.file().getBoolean("Debug-Mode");
        Debugger.execute();
        GameManager.loadLobby();

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
        pm.registerEvents(PlayerInput.getListener(), this);

        log("Loading maps.");
        MapManager.loadMaps();
        log("Loaded " + GameManager.getLoadedInt() + " maps.");
        PlayerManager.loadPlayers();
        KitManager.loadKits();
        log("Loaded " + KitManager.getKitCount() + " kits.");

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

        commandManager.getCompletionHandler().register("#maps", input -> GameManager.getRoomNames());
        commandManager.getCompletionHandler().register("#boolean", input -> Arrays.asList("false", "true"));
        commandManager.getCompletionHandler().register("#kits", input -> KitManager.getKitNames());

        commandManager.getParameterHandler().register(Enum.TierType.class, argument -> {
            Enum.TierType tt = Enum.TierType.valueOf(argument.toString().toUpperCase());
            if (tt == null) return new TypeResult(argument);
            return new TypeResult(tt, argument);
        });
        commandManager.getParameterHandler().register(CurrencyManager.Operations.class, argument -> {
            CurrencyManager.Operations tt = CurrencyManager.Operations.valueOf(argument.toString().toLowerCase());
            if (tt == null) return new TypeResult(argument);
            return new TypeResult(tt, argument);
        });

        //command messages
        commandManager.getMessageHandler().register("cmd.no.exists", sender -> sender.sendMessage(LanguageManager.getPrefix() + Text.color("&cThe command you're trying to run doesn't exist!")));
        commandManager.getMessageHandler().register("cmd.no.permission", sender -> sender.sendMessage(LanguageManager.getPrefix() + Text.color("&fYou &cdon't &fhave permission to execute this command!")));
        commandManager.getMessageHandler().register("cmd.wrong.usage", sender -> sender.sendMessage(LanguageManager.getPrefix() + Text.color("&cWrong usage for the command!")));

        //registo de comandos #portugal
        commandManager.register(new Commands(this));

        long elapsedTimeMillis = System.currentTimeMillis() - start;

        float elapsedTimeSec = elapsedTimeMillis / 1000F;
        log("Finished loading in " + elapsedTimeSec + " seconds.");
        log(star);
    }

    public static ChestManager getChestManager() {
        return chestManager;
    }

    public void onDisable() {
        GameManager.endGames();
        GameManager.getRooms().forEach(SWGameMode::clear);
    }

    public void reload() {
        GameManager.endGames();

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

        LanguageManager.loadLanguages();
        PlayerManager.stopScoreboards();
        PlayerManager.loadPlayers();
        Shops.reload();
        Kits.reload();
        KitManager.loadKits();

        MapManager.loadMaps();
        GameManager.loadLobby();
    }
}