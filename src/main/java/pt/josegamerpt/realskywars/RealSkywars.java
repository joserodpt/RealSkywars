package pt.josegamerpt.realskywars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import pt.josegamerpt.realskywars.configuration.Chests;
import pt.josegamerpt.realskywars.configuration.Config;
import pt.josegamerpt.realskywars.configuration.Kits;
import pt.josegamerpt.realskywars.configuration.Languages;
import pt.josegamerpt.realskywars.configuration.Maps;
import pt.josegamerpt.realskywars.configuration.Players;
import pt.josegamerpt.realskywars.configuration.Shops;
import pt.josegamerpt.realskywars.gui.*;
import pt.josegamerpt.realskywars.managers.*;
import pt.josegamerpt.realskywars.player.BlockBreak;
import pt.josegamerpt.realskywars.player.BlockPlace;
import pt.josegamerpt.realskywars.player.EntityEvents;
import pt.josegamerpt.realskywars.player.PlayerEvents;
import pt.josegamerpt.realskywars.player.PlayerInteractions;
import pt.josegamerpt.realskywars.utils.GUIBuilder;
import pt.josegamerpt.realskywars.utils.Holograms;
import pt.josegamerpt.realskywars.utils.MaterialPicker;
import pt.josegamerpt.realskywars.utils.PlayerInput;

import java.util.logging.Level;

public class RealSkywars extends JavaPlugin implements Listener {

    public static Plugin pl;
    PluginManager pm = Bukkit.getPluginManager();

	public static String getPrefix() {
	    return "[" + RealSkywars.pl.getDescription().getName() + "] ";
	}

	public void onEnable() {
        long start = System.currentTimeMillis();

        String star = "<------------- RealSkywars PT ------------->".replace("PT", "| " +
                this.getDescription().getVersion());
        System.out.print(star);
        pl = this;
        Languages.setup(this);

        print("Loading languages.");
        LanguageManager.loadLanguages();
        if (!LanguageManager.checkSelect()) {
            print("[FATAL] No Language Detected. Stopped loading.");
            pm.disablePlugin(this);
            return;
        }

        print("Setting up configuration.");
        saveDefaultConfig();
        Config.setup(this);
        Debugger.debug = Config.file().getBoolean("Debug-Mode");
        GameManager.loginTP = Config.file().getBoolean("Config.Auto-Teleport-To-Lobby");
        Maps.setup(this);
        Players.setup(this);
        Chests.setup(this);
        Shops.setup(this);
        Kits.setup(this);

        print("Setting up events.");
        pm.registerEvents(GUIBuilder.getListener(), this);
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new PlayerInteractions(), this);
        pm.registerEvents(new PlayerEvents(), this);
        pm.registerEvents(new EntityEvents(), this);
        pm.registerEvents(MapSettings.getListener(), this);
        pm.registerEvents(RoomSettings.getListener(), this);
        pm.registerEvents(PlayerGUI.getListener(), this);
        pm.registerEvents(ChestTierMenu.getListener(), this);
        pm.registerEvents(ChestTierViewer.getListener(), this);
        pm.registerEvents(ChestTierVote.getListener(), this);
        pm.registerEvents(ShopViewer.getListener(), this);
        pm.registerEvents(TrailEditor.getListener(), this);
        pm.registerEvents(ProfileContent.getListener(), this);
        pm.registerEvents(KitSettings.getListener(), this);
        pm.registerEvents(MapsViewer.getListener(), this);
        pm.registerEvents(PlayerInput.getListener(), this);
        pm.registerEvents(MaterialPicker.getListener(), this);

        print("Setting up commands.");
        getCommand("realskywars").setExecutor(new RSWcmd());

        if (Config.file().isConfigurationSection("Config.Lobby")) {
            double x = Config.file().getDouble("Config.Lobby.X");
            double y = Config.file().getDouble("Config.Lobby.Y");
            double z = Config.file().getDouble("Config.Lobby.Z");
            float yaw = (float) Config.file().getDouble("Config.Lobby.Yaw");
            float pitch = (float) Config.file().getDouble("Config.Lobby.Pitch");
            World world = Bukkit.getServer().getWorld(Config.file().getString("Config.Lobby.World"));
            Location loc = new Location(world, x, y, z, yaw, pitch);
            GameManager.lobbyLOC = loc;
        }

        GameManager.lobbyScoreboard = Config.file().getBoolean("Config.Lobby-Scoreboard");

        print("Loading arenas.");
        MapManager.loadMaps();
        print("Loaded " + GameManager.getLoadedInt() + " arenas.");
        print("Loading players.");
        PlayerManager.loadPlayers();
        print("Loading kits.");
        KitManager.loadKits();

        Debugger.print(getPrefix() + "!!> DEBUG MODE ENABLED <!!");
        Debugger.execute();

        long elapsedTimeMillis = System.currentTimeMillis() - start;

        float elapsedTimeSec = elapsedTimeMillis / 1000F;
        print("Finished loading in " + elapsedTimeSec + " seconds.");
        System.out.print(star);
    }

    public void onDisable() {
        GameManager.endGames();
        Holograms.removeAll();
    }

    public static void print(String s)
	{
	    Bukkit.getLogger().log(Level.INFO, getPrefix() + s);
	}
}