package josegamerpt.realskywars;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.configuration.*;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.player.*;
import josegamerpt.realskywars.utils.*;
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
import java.util.logging.Level;

public class RealSkywars extends JavaPlugin implements Listener {

    private static Plugin pl;
    PluginManager pm = Bukkit.getPluginManager();
    CommandManager commandManager;

    public static Plugin getPlugin()
    {
        return pl;
    }
    private static WorldManager wm = new WorldManager();

    public static void log(String s) {
        Bukkit.getLogger().log(Level.INFO, "[RealSkywars] " + s);
    }
    public static void log(Level l, String s) {
        Bukkit.getLogger().log(l, "[RealSkywars] " + s);
    }

    public static WorldManager getWorldManager() {
        return wm;
    }

    public void onEnable() {
        long start = System.currentTimeMillis();
        pl = this;

        String star = "<------------- RealSkywars PT ------------->".replace("PT", "| " +
                this.getDescription().getVersion());
        log(star);

        Debugger.print( RealSkywars.class,"DEBUG MODE ENABLED");
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

        Maps.setup(this);
        Players.setup(this);
        //Chests.setup(this);
        Shops.setup(this);
        Kits.setup(this);

        log("Setting up events.");
        pm.registerEvents(new PlayerEvents(), this);
        pm.registerEvents(new EntityEvents(), this);
        pm.registerEvents(GUIBuilder.getListener(), this);
        pm.registerEvents(MapSettings.getListener(), this);
        pm.registerEvents(RoomSettings.getListener(), this);
        pm.registerEvents(PlayerGUI.getListener(), this);
        pm.registerEvents(ShopViewer.getListener(), this);
        pm.registerEvents(TrailEditor.getListener(), this);
        pm.registerEvents(ProfileContent.getListener(), this);
        pm.registerEvents(KitSettings.getListener(), this);
        pm.registerEvents(MapsViewer.getListener(), this);
        pm.registerEvents(PlayerInput.getListener(), this);
        pm.registerEvents(MaterialPicker.getListener(), this);

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
            List<String> sugests = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                sugests.add("Room" + i);
            }

            return sugests;
        });

        commandManager.getCompletionHandler().register("#maps", input -> GameManager.getRoomNames());
        commandManager.getCompletionHandler().register("#kits", input -> KitManager.getKitNames());
        commandManager.getCompletionHandler().register("#chesttiers", input -> Arrays.asList("Basic", "Normal", "OP", "Caos"));
        commandManager.getParameterHandler().register(Enum.TierType.class, argument -> {
            // Gets the entity from the UUID
            Enum.TierType tt = Enum.TierType.valueOf(argument.toString().toUpperCase());
            // Checks if the entity is null or not and returns only the argument used
            if (tt == null) return new TypeResult(argument);
            // Returns the entity found and the argument
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

    public void onDisable() {
        GameManager.endGames();
        GameManager.getRooms().forEach(SWGameMode::clear);
    }
}