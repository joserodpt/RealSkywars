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

import joserodpt.realpermissions.api.RealPermissionsAPI;
import joserodpt.realpermissions.api.pluginhookup.ExternalPlugin;
import joserodpt.realpermissions.api.pluginhookup.ExternalPluginPermission;
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.chests.TierViewer;
import joserodpt.realskywars.api.config.RSWAchievementsConfig;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWKitsConfig;
import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.config.RSWMapsConfig;
import joserodpt.realskywars.api.config.RSWSQLConfig;
import joserodpt.realskywars.api.config.RSWShopsConfig;
import joserodpt.realskywars.api.config.chests.BasicChestConfig;
import joserodpt.realskywars.api.config.chests.EPICChestConfig;
import joserodpt.realskywars.api.config.chests.NormalChestConfig;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.kits.KitSettings;
import joserodpt.realskywars.api.managers.CurrencyManager;
import joserodpt.realskywars.api.managers.GameManagerAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.nms.NMS114R1tov116R3;
import joserodpt.realskywars.api.nms.NMS117R1;
import joserodpt.realskywars.api.nms.NMS118R2andUP;
import joserodpt.realskywars.api.utils.GUIBuilder;
import joserodpt.realskywars.api.utils.PlayerInput;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.plugin.commands.PartyCMD;
import joserodpt.realskywars.plugin.commands.RealSkywarsCMD;
import joserodpt.realskywars.plugin.commands.SairCMD;
import joserodpt.realskywars.plugin.currency.LocalCurrencyAdapter;
import joserodpt.realskywars.plugin.currency.VaultCurrencyAdapter;
import joserodpt.realskywars.plugin.gui.guis.AchievementViewerGUI;
import joserodpt.realskywars.plugin.gui.guis.GameHistoryGUI;
import joserodpt.realskywars.plugin.gui.guis.MapSettingsGUI;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerProfileContentsGUI;
import joserodpt.realskywars.plugin.gui.guis.SetupRoomSettingsGUI;
import joserodpt.realskywars.plugin.gui.guis.ShopGUI;
import joserodpt.realskywars.plugin.gui.guis.VoteGUI;
import joserodpt.realskywars.plugin.listeners.EventListener;
import joserodpt.realskywars.plugin.listeners.PlayerListener;
import joserodpt.realskywars.plugin.managers.DatabaseManager;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RealSkywarsPlugin extends JavaPlugin {
    private static RealSkywarsPlugin pl;
    public static RealSkywarsPlugin getPlugin() {
        return pl;
    }
    private RealSkywars realSkywars;

    private boolean newUpdate;
    private final PluginManager pm = Bukkit.getPluginManager();

    public void onEnable() {
        printASCII();

        final long start = System.currentTimeMillis();
        pl = this;
        realSkywars = new RealSkywars(this);
        RealSkywarsAPI.setInstance(realSkywars);
        //setup metrics
        new Metrics(this, 16365);

        //verify nms version
        if (!setupNMS()) {
            getLogger().severe("Your server version is not currently supported by RealSkywars.");
            getLogger().severe("If you think this is a bug, contact JoseGamer_PT.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        RSWLanguagesConfig.setup(this);

        realSkywars.getLanguageManagerAPI().loadLanguages();
        if (realSkywars.getLanguageManagerAPI().areLanguagesEmpty()) {
            getLogger().severe("[ERROR] No Languages have been Detected. Stopped loading.");
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        RSWConfig.setup(this);

        Debugger.debug = RSWConfig.file().getBoolean("Debug-Mode");
        Debugger.print(RealSkywars.class, "DEBUG MODE ENABLED");
        Debugger.execute();
        realSkywars.getGameManagerAPI().loadLobby();
        realSkywars.getGameManagerAPI().loadLobby();

        //config
        RSWAchievementsConfig.setup(this);
        RSWMapsConfig.setup(this);
        RSWSQLConfig.setup(this);
        RSWShopsConfig.setup(this);
        RSWKitsConfig.setup(this);

        //chests
        BasicChestConfig.setup(this);
        NormalChestConfig.setup(this);
        EPICChestConfig.setup(this);

        try {
            realSkywars.setDatabaseManager(new DatabaseManager(realSkywars));
        } catch (SQLException a) {
            getLogger().severe("Error while creating Database Manager for RealSkywars: " + a.getMessage());
        }

        pm.registerEvents(new PlayerListener(realSkywars), this);
        pm.registerEvents(new EventListener(realSkywars), this);
        pm.registerEvents(PlayerInput.getListener(), this);
        pm.registerEvents(GUIBuilder.getListener(), this);
        pm.registerEvents(GameHistoryGUI.getListener(), this);
        pm.registerEvents(SetupRoomSettingsGUI.getListener(), this);
        pm.registerEvents(MapSettingsGUI.getListener(), this);
        pm.registerEvents(PlayerGUI.getListener(), this);
        pm.registerEvents(ShopGUI.getListener(), this);
        pm.registerEvents(PlayerProfileContentsGUI.getListener(), this);
        pm.registerEvents(KitSettings.getListener(), this);
        pm.registerEvents(MapsListGUI.getListener(), this);
        pm.registerEvents(TierViewer.getListener(), this);
        pm.registerEvents(AchievementViewerGUI.getListener(), this);
        pm.registerEvents(GameHistoryGUI.getListener(), this);
        pm.registerEvents(VoteGUI.getListener(), this);

        realSkywars.getKitManagerAPI().loadKits();
        getLogger().info("Loaded " + realSkywars.getKitManagerAPI().getKits().size() + " kits.");

        realSkywars.getMapManagerAPI().loadMaps();
        getLogger().info("Loaded " + realSkywars.getGameManagerAPI().getGames(GameManagerAPI.GameModes.ALL).size() + " maps.");
        realSkywars.getPlayerManagerAPI().loadPlayers();

        realSkywars.getAchievementsManagerAPI().loadAchievements();

        //load leaderboard
        realSkywars.getLeaderboardManagerAPI().refreshLeaderboards();


        CommandManager commandManager = new CommandManager(this);
        commandManager.hideTabComplete(true);
        //command suggestions
        commandManager.getCompletionHandler().register("#createsuggestions", input -> IntStream.range(0, 200)
                .mapToObj(i -> "Map" + i)
                .collect(Collectors.toCollection(ArrayList::new)));

        commandManager.getCompletionHandler().register("#maps", input -> realSkywars.getGameManagerAPI().getRoomNames());
        commandManager.getCompletionHandler().register("#boolean", input -> Arrays.asList("false", "true"));
        commandManager.getCompletionHandler().register("#worldtype", input -> Arrays.asList("DEFAULT", "SCHEMATIC"));
        commandManager.getCompletionHandler().register("#kits", input -> realSkywars.getKitManagerAPI().getKits().stream()
                .map(kit -> Text.strip(kit.getName()))
                .collect(Collectors.toList()));

        commandManager.getParameterHandler().register(RSWChest.Tier.class, argument -> {
            try {
                RSWChest.Tier tt = RSWChest.Tier.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(RSWChest.Type.class, argument -> {
            try {
                RSWChest.Type tt = RSWChest.Type.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(RSWGame.Mode.class, argument -> {
            try {
                RSWGame.Mode tt = RSWGame.Mode.valueOf(argument.toString().toUpperCase());
                return new TypeResult(tt, argument);
            } catch (Exception e) {
                return new TypeResult(null, argument);
            }
        });
        commandManager.getParameterHandler().register(RSWWorld.WorldType.class, argument -> {
            try {
                RSWWorld.WorldType tt = RSWWorld.WorldType.valueOf(argument.toString().toUpperCase());
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
        commandManager.getMessageHandler().register("cmd.no.exists", sender -> sender.sendMessage(realSkywars.getLanguageManagerAPI().getPrefix() + Text.color("&cThe command you're trying to run doesn't exist!")));
        commandManager.getMessageHandler().register("cmd.no.permission", sender -> sender.sendMessage(realSkywars.getLanguageManagerAPI().getPrefix() + Text.color("&fYou &cdon't &fhave permission to execute this command!")));
        commandManager.getMessageHandler().register("cmd.wrong.usage", sender -> sender.sendMessage(realSkywars.getLanguageManagerAPI().getPrefix() + Text.color("&cWrong usage for the command!")));

        //registo de comandos #portugal
        commandManager.register(new RealSkywarsCMD(realSkywars), new SairCMD(realSkywars), new PartyCMD(realSkywars));

        //placeholderAPI support
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hooked on PlaceholderAPI!");
            new RealSkywarsPlaceholderAPI(realSkywars).register();
        }

        //hook into vault
        if (setupEconomy()) {
            getLogger().info("Vault found and Hooked into!");
            if (RSWConfig.file().getBoolean("Config.Use-Vault-As-Currency")) {
                realSkywars.setCurrencyAdapter(new VaultCurrencyAdapter());
                getLogger().info("Currency via Vault has been enabled.");
            } else {
                realSkywars.setCurrencyAdapter(new LocalCurrencyAdapter());
                getLogger().info("Local currency has been enabled, as specified in the config file.");
            }
        } else {
            realSkywars.setCurrencyAdapter(new LocalCurrencyAdapter());
            getLogger().warning("Vault not found. Local currency will be used.");
        }

        //refresh leaderboards
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, realSkywars.getLeaderboardManagerAPI()::refreshLeaderboards, RSWConfig.file().getInt("Config.Refresh-Leaderboards"), RSWConfig.file().getInt("Config.Refresh-Leaderboards"));

        new UpdateChecker(this, 105115).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.getLogger().info("The plugin is updated to the latest version.");
            } else {
                this.newUpdate = true;
                this.getLogger().warning("There is a new update available! Version: " + version + " https://www.spigotmc.org/resources/105115/");
            }
        });

        if (getServer().getPluginManager().getPlugin("RealPermissions") != null) {
            //register RealSkywars permissions onto RealPermissions
            RealPermissionsAPI.getInstance().getHookupAPI().addHookup(new ExternalPlugin(this.getDescription().getName(), "&fReal&bSkywars", this.getDescription().getDescription(), Material.BOW, Arrays.asList(
                    new ExternalPluginPermission("rsw.basic", "Permission for voting on the Basic Chest Tier."),
                    new ExternalPluginPermission("rsw.normal", "Permission for voting on the Normal Chest Tier."),
                    new ExternalPluginPermission("rsw.epic", "Permission for voting on the Epic Chest Tier."),
                    new ExternalPluginPermission("rsw.day", "Permission for voting on the Game Time Day."),
                    new ExternalPluginPermission("rsw.sunset", "Permission for voting on the Game Time Sunset."),
                    new ExternalPluginPermission("rsw.night", "Permission for voting on the Game Time Night."),
                    new ExternalPluginPermission("rsw.normal-projectile", "Permission for voting on the Game Normal Projectiles."),
                    new ExternalPluginPermission("rsw.break-projectile", "Permission for voting the on Game Break Projectiles."),
                    new ExternalPluginPermission("rsw.join", "Allow access to the maps menu.", List.of("rsw list")),
                    new ExternalPluginPermission("rsw.kits", "Allow access to the kits menu.", List.of("rsw kits")),
                    new ExternalPluginPermission("rsw.shop", "Allow access to the shop menu.", List.of("rsw shop")),
                    new ExternalPluginPermission("rsw.coins", "Allow checking the player's current balance.", List.of("rsw coins")),
                    new ExternalPluginPermission("rsw.lobby", "Allow teleportation to the lobby.", List.of("rsw lobby")),
                    new ExternalPluginPermission("rsw.forcestart", "Allow force starting the current match.", List.of("rsw forcestart")),
                    new ExternalPluginPermission("rsw.leave", "Allow leaving the current match.", List.of("rsw leave")),
                    new ExternalPluginPermission("rsw.party.owner", "Allow party owner commands.", Arrays.asList("party create", "party disband", "party kick")),
                    new ExternalPluginPermission("rsw.party.invite", "Allow party invite commands.", List.of("party invite")),
                    new ExternalPluginPermission("rsw.party.accept", "Allow accepting a party invite.", List.of("party accept")),
                    new ExternalPluginPermission("rsw.party.leave", "Allow leaving a party.", List.of("party leave"))
            ), this.getDescription().getVersion()));
        }

        getLogger().info("Finished loading in " + ((System.currentTimeMillis() - start) / 1000F) + " seconds.");
        getLogger().info("<------------- RealSkywars vPT ------------->".replace("PT", this.getDescription().getVersion()));
    }

    private void printASCII() {
        logWithColor("&b   _____            _  _____ _ ");
        logWithColor("&b  |  __ \\          | |/ ____| |");
        logWithColor("&b  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___");
        logWithColor("&b  |  _  // _ \\/ _` | |\\___ \\| |/ / | | \\ \\ /\\ / / _` | '__/ __|");
        logWithColor("&b  | | \\ \\  __/ (_| | |____) |   <| |_| |\\ V  V / (_| | |  \\__ \\");
        logWithColor("&b  |_|  \\_\\___|\\__,_|_|_____/|_|\\_\\\\__, | \\_/\\_/ \\__,_|_|  |___/");
        logWithColor("&b   &8Made by: &9JoseGamer_PT           &b__/ |      &8Version: &9" + this.getDescription().getVersion());
        logWithColor("&b                                  |___/");
    }

    public void logWithColor(String s) {
        getServer().getConsoleSender().sendMessage("[" + this.getDescription().getName() + "] " + Text.color(s));
    }

    public void onDisable() {
        realSkywars.getGameManagerAPI().endGames();
        realSkywars.getGameManagerAPI().getGames(GameManagerAPI.GameModes.ALL).forEach(RSWGame::clear);

        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    private boolean setupNMS() {
        String version = getServerVersion();
        getLogger().info("Server version: " + version);

        switch (version) {
            case "v1_20_R3":
            case "v1_20_R2":
            case "v1_20_R1":
            case "v1_19_R3":
            case "v1_18_R2":
                realSkywars.setNMS(new NMS118R2andUP());
                break;
            case "v1_17_R1":
                realSkywars.setNMS(new NMS117R1());
            case "v1_16_R3":
            case "v1_15_R1":
            case "v1_14_R1":
                realSkywars.setNMS(new NMS114R1tov116R3());
                break;
        }
        return realSkywars.getNMS() != null;
    }

    private static Economy vaultEconomy = null;

    public Economy getEconomy() {
        return vaultEconomy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEconomy = rsp.getProvider();
        return vaultEconomy != null;
    }

    public boolean hasNewUpdate() {
        return this.newUpdate;
    }
}