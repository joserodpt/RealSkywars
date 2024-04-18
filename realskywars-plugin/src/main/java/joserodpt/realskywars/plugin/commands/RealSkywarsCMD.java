package joserodpt.realskywars.plugin.commands;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.chests.TierViewer;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.kits.KitInventory;
import joserodpt.realskywars.plugin.gui.guis.KitSettingsGUI;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.CurrencyManager;
import joserodpt.realskywars.api.managers.GameManagerAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.api.utils.WorldEditUtils;
import joserodpt.realskywars.plugin.gui.GUIManager;
import joserodpt.realskywars.plugin.gui.guis.MapSettingsGUI;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerProfileContentsGUI;
import joserodpt.realskywars.plugin.gui.guis.SettingsGUI;
import joserodpt.realskywars.plugin.managers.LanguageManager;
import joserodpt.realskywars.plugin.managers.ShopManager;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Command("realskywars")
@Alias({"sw", "rsw"})
public class RealSkywarsCMD extends CommandBase {

    private final String onlyPlayer = "[RealSkywars] Only players can run this command.";
    public RealSkywarsAPI rs;

    public RealSkywarsCMD(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.getPlayer().isOp() || p.getPlayer().hasPermission("rsw.admin")) {
                GUIManager.openPluginMenu(p, rs);
            } else {
                MapsListGUI v = new MapsListGUI(p, p.getMapViewerPref(), rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MAPS_NAME, false));
                v.openInventory(p);
            }
        } else {
            Text.send(commandSender, "&f&lReal&B&LSkywars &r&aVersion &e" + rs.getPlugin().getDescription().getVersion());
        }
    }

    @SubCommand("reload")
    @Alias("rl")
    @Permission("rsw.admin")
    public void reloadcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            this.rs.reload();
            commandSender.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CONFIG_RELOAD, true));
        } else {
            this.rs.reload();
            commandSender.sendMessage("Reloaded RealSkywars!");
        }
    }

    @SubCommand("list")
    @Permission("rsw.list")
    public void listcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_MAPS, true).replace("%rooms%", "" + rs.getGameManagerAPI().getGames(GameManagerAPI.GameModes.ALL).size()));
            for (RSWGame s : rs.getGameManagerAPI().getGames(GameManagerAPI.GameModes.ALL)) {
                TextComponent a = new TextComponent(Text.color("&7- &f" + s.getDisplayName()));
                a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rsw map " + s.getMapName()));
                a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Text.color("&fClick to open &b" + s.getDisplayName() + "&f settings!")).create()));
                p.getPlayer().spigot().sendMessage(a);
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kits")
    @Permission("rsw.kits")
    public void kitscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            PlayerProfileContentsGUI ds = new PlayerProfileContentsGUI(p, ShopManager.Categories.KITS);
            ds.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kit")
    @Completion({"#enum", "#kits", "#range:100"})
    @Permission("rsw.admin")
    public void kitcmd(final CommandSender commandSender, KIT_OPERATION action, String name, @Optional Double cost) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);

            if (action == null) {
                p.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "Unknown kit command action.");
                return;
            }
            switch (action) {
                case CREATE:
                    if (cost == null) {
                        p.sendMessage(rs.getLanguageManagerAPI().getPrefix() + "Cost value not accepted.");
                        return;
                    }

                    if (rs.getKitManagerAPI().getKit(name) == null) {
                        RSWKit k = new RSWKit(Text.strip(Text.color(name)), name, cost, new KitInventory(p.getPlayer().getInventory().getContents()));
                        KitSettingsGUI m = new KitSettingsGUI(k, p.getUUID());
                        m.openInventory(p);
                    } else {
                        commandSender.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.KIT_EXISTS, true));
                    }
                    break;
                case DELETE:
                    RSWKit k2 = rs.getKitManagerAPI().getKit(name);
                    if (k2 != null) {
                        rs.getKitManagerAPI().unregisterKit(k2);
                        commandSender.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.DELETEKIT_DONE, true));
                    } else {
                        commandSender.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_KIT_FOUND, true));
                    }
                    break;
                case GIVE:
                    RSWKit k3 = rs.getKitManagerAPI().getKit(name);
                    if (k3 != null) {
                        k3.give(p);
                        p.playSound(Sound.ENTITY_VILLAGER_YES, 50, 50);
                    } else {
                        commandSender.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_KIT_FOUND, true));
                    }
                    break;
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("shop")
    @Permission("rsw.shop")
    public void shopcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            GUIManager.openShopMenu(rs.getPlayerManagerAPI().getPlayer((Player) commandSender));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("play")
    @Completion("#enum")
    public void playcmd(final CommandSender commandSender, RSWGame.Mode type) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (type != null && p != null && p.getPlayer() != null) {
                if (!(p.getState() == RSWPlayer.PlayerState.CAGE)) {
                    rs.getGameManagerAPI().findGame(p, type);
                } else {
                    Text.send(commandSender, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ALREADY_IN_MATCH, true));
                }
            } else {
                Text.send(commandSender, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_GAME_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("balance")
    @Alias("bal")
    @Permission("rsw.coins")
    @WrongUsage("&c/rsw bal")
    public void balancecmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);

            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_COINS, true).replace("%coins%", rs.getCurrencyAdapter().getCoins(p) + ""));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("coins")
    @Permission("rsw.coins")
    @Completion({"#enum", "#players", "#range:100"})
    @WrongUsage("&c/rsw coins <send;add;set;remove> <name> <coins>")
    public void coinscmd(final CommandSender commandSender, CurrencyManager.Operations o, Player target, Double coins) {
        if (o == null) {
            Text.send(commandSender, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Invalid operation type.");
            return;
        }

        RSWPlayer search = rs.getPlayerManagerAPI().getPlayer(target);

        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);

            if (search == null) {
                Text.send(commandSender, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_PLAYER_FOUND, true));
                return;
            }

            new CurrencyManager(rs.getCurrencyAdapter(), search, p, coins, o, true);
        } else {
            if (search == null) {
                Text.send(commandSender, rs.getLanguageManagerAPI().getString(LanguageManagerAPI.TS.NO_PLAYER_FOUND, true));
                return;
            }

            new CurrencyManager(rs.getCurrencyAdapter(), search, coins, o, true);
        }
    }

    @SubCommand("verifylang")
    @Permission("rsw.admin")
    public void verifylangcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            String sep = "&9&m&l--------------------------------";
            Text.sendList(p.getPlayer(), Arrays.asList(sep, "&fLanguage Verification Started.", "&6"));

            HashMap<String, HashMap<LanguageManager.TS, String>> flag = rs.getLanguageManagerAPI().verifyLanguages();
            if (flag.isEmpty()) {
                p.sendMessage(rs.getLanguageManagerAPI().getPrefix() + Text.color("&aNo errors encountered."));
                p.sendMessage(Text.color(sep));
            } else {
                for (Map.Entry<String, HashMap<LanguageManager.TS, String>> entry : flag.entrySet()) {
                    String key = entry.getKey();
                    HashMap<LanguageManager.TS, String> value = entry.getValue();

                    p.sendMessage(Text.color("&6Found translation errors in Language: &b" + key));

                    for (Map.Entry<LanguageManager.TS, String> e : value.entrySet()) {
                        LanguageManager.TS t = e.getKey();
                        Object s = e.getValue();

                        p.sendMessage(Text.color("&7<&4!&7> &b" + t.name() + " &4returned: &7" + s));
                    }

                    Text.sendList(p.getPlayer(), Arrays.asList("", "&fFound &b" + value.size() + " &ferrors for the Language &b" + key + ".", sep));
                }
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("setspectator")
    @Permission("rsw.admin")
    public void setspectator(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.getSetupRoom() != null) {
                p.getSetupRoom().setSpectatorLoc(p.getLocation());
                p.getSetupRoom().setSpectatorConfirm(true);
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_FINISHSETUP, true));
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("settings")
    @Alias("s")
    @Permission("rsw.admin")
    public void settings(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            SettingsGUI v = new SettingsGUI(p, rs);
            v.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("lobby")
    @Permission("rsw.lobby")
    public void lobbycmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.getMatch() == null) {
                rs.getGameManagerAPI().tpToLobby(p);
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_MATCH_CANCEL, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("forcestart")
    @Permission("rsw.forcestart")
    public void forcestartcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(Bukkit.getPlayer(commandSender.getName()));
            if (p.isInMatch()) {
                p.sendMessage(p.getMatch().forceStart(p));
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("addsharik")
    @Permission("rsw.admin")
    public void addsharik(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().addPlayer(new RSWPlayer(true));
                p.sendMessage(Text.color("&4&lEXPERIMENTAL FEATURE. CAN RESULT IN SERVER & CLIENT CRASHES. &c&lAdded Null Player"));
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("leave")
    @Permission("rsw.leave")
    public void leave(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().removePlayer(p);
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("setlobby")
    @Permission("rsw.admin")
    public void setlobby(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            RSWConfig.file().set("Config.Lobby.World", p.getLocation().getWorld().getName());
            RSWConfig.file().set("Config.Lobby.X", p.getLocation().getX());
            RSWConfig.file().set("Config.Lobby.Y", p.getLocation().getY());
            RSWConfig.file().set("Config.Lobby.Z", p.getLocation().getZ());
            RSWConfig.file().set("Config.Lobby.Yaw", p.getLocation().getYaw());
            RSWConfig.file().set("Config.Lobby.Pitch", p.getLocation().getPitch());
            RSWConfig.save();
            rs.getGameManagerAPI().setLobbyLoc(p.getLocation());
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.LOBBY_SET, true));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("cancelsetup")
    @Permission("rsw.admin")
    public void cancelsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.getSetupRoom() != null) {
                rs.getMapManagerAPI().cancelSetup(p);
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("finishsetup")
    @Permission("rsw.admin")
    public void finishsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.getSetupRoom() != null) {
                if (p.getSetupRoom().areCagesConfirmed() & p.getSetupRoom().isSpectatorLocConfirmed()) {
                    rs.getMapManagerAPI().finishSetup(p);
                } else {
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SETUP_NOT_FINISHED, true));
                }
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("maps")
    @Permission("rsw.admin")
    public void mapscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p.getMatch() == null) {
                MapsListGUI v = new MapsListGUI(p, p.getMapViewerPref(), rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MAPS_NAME, false));
                v.openInventory(p);
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ALREADY_IN_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("players")
    @Permission("rsw.admin")
    public void players(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_PLAYERS, true).replace("%players%", rs.getPlayerManagerAPI().getPlayers().size() + ""));
            for (RSWPlayer pair : rs.getPlayerManagerAPI().getPlayers()) {
                if (pair.getPlayer() != null) {
                    TextComponent a = new TextComponent(Text.color("&7- &f" + pair.getName()));
                    a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rsw player " + pair.getName()));
                    a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Text.color("&fClick here to inspect &b" + pair.getName())).create()));
                    p.getPlayer().spigot().sendMessage(a);
                }
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("map")
    @Completion("#maps")
    @Permission("rsw.admin")
    public void map(final CommandSender commandSender, String name) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            RSWGame sw = rs.getGameManagerAPI().getGame(name);
            if (sw != null) {
                MapSettingsGUI r = new MapSettingsGUI(sw, p.getUUID());
                r.openInventory(p);
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_GAME_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("tp")
    @Completion("#maps")
    @Permission("rsw.admin")
    public void tpcmd(final CommandSender commandSender, String name) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            if (p != null) {
                p.getPlayer().setGameMode(GameMode.CREATIVE);
                p.teleport(rs.getGameManagerAPI().getGame(name).getRSWWorld().getWorld().getSpawnLocation());
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("set2chest")
    @Completion({"#enum", "#enum"})
    @Permission("rsw.admin")
    public void setchest(final CommandSender commandSender, RSWChest.Tier tt, RSWChest.Type t) {
        if (commandSender instanceof Player) {
            final Player p = (Player) commandSender;
            rs.getChestManagerAPI().set2Chest(tt, t, Arrays.asList(IntStream.range(9, 35).boxed().map(p.getInventory()::getItem).filter(Objects::nonNull).toArray(ItemStack[]::new)));
            Text.send(commandSender, "Itens set for " + tt.name() + " (middle: " + t.name() + ")");
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("seetier")
    @Completion({"#enum", "#enum"})
    @Permission("rsw.admin")
    public void seetier(final CommandSender commandSender, RSWChest.Tier tt, RSWChest.Type t) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            TierViewer tv = new TierViewer(p, tt, t);
            tv.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("player")
    @Completion("#players")
    @Permission("rsw.admin")
    public void player(final CommandSender commandSender, Player get) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
            RSWPlayer search = rs.getPlayerManagerAPI().getPlayer(get);
            if (search != null) {
                PlayerGUI playg = new PlayerGUI(rs.getPlayerManagerAPI().getPlayer(get), p.getUUID(), search);
                playg.openInventory(p);
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_PLAYER_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("create")
    @Permission("rsw.admin")
    @Completion({"#createsuggestions", "#worldtype", "#range:20", "#range:20"})
    @WrongUsage("&c/rsw create <name> <type> <players> or /rsw create <name> <type> <number of teams> <players per team>")
    public void createcmd(final CommandSender commandSender, String mapname, RSWWorld.WorldType wt, Integer maxPlayersandTeams, @Optional Integer teamPlayers) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);

            if (wt == null) {
                p.sendMessage("&cInvalid game generation type.");
                return;
            }

            if (wt.equals(RSWWorld.WorldType.SCHEMATIC) && !WorldEditUtils.schemFileExists(mapname)) {
                p.sendMessage("&cNo " + mapname + "&c found in RealSkywars/maps. Did you forget to add .schem?");
                return;
            }

            if (RSWConfig.file().isSection("Config.Lobby")) {
                RSWGame map = rs.getMapManagerAPI().getMap(mapname);
                if (map == null) {
                    if (p.getSetupRoom() != null) {
                        p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SETUP_NOT_FINISHED, true));
                    } else {
                        if (teamPlayers == null) {
                            rs.getMapManagerAPI().setupSolo(p, Text.strip(mapname), mapname, wt, maxPlayersandTeams);
                        } else {
                            rs.getMapManagerAPI().setupTeams(p, Text.strip(mapname), mapname, wt, maxPlayersandTeams, teamPlayers);
                        }
                    }
                } else {
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MAP_EXISTS, true));
                }
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.LOBBYLOC_NOT_SET, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("unregister")
    @Completion("#maps")
    @Alias("del")
    @Permission("rsw.admin")
    @WrongUsage("&c/rsw unregister <map>")
    public void delete(final CommandSender commandSender, String mapName) {
        RSWGame map = rs.getMapManagerAPI().getMap(mapName);
        if (map != null) {
            rs.getMapManagerAPI().unregisterMap(map);
            commandSender.sendMessage(rs.getLanguageManagerAPI().getString(LanguageManagerAPI.TS.MAP_UNREGISTERED, true));
        } else {
            commandSender.sendMessage(rs.getLanguageManagerAPI().getString(LanguageManagerAPI.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("rename")
    @Completion("#maps")
    @Alias("ren")
    @Permission("rsw.admin")
    @WrongUsage("&c/rsw rename <map> ")
    public void renamecmd(final CommandSender commandSender, final String mapName, final String displayName) {
        RSWGame map = rs.getMapManagerAPI().getMap(mapName);
        if (map != null) {
            map.setDisplayName(displayName);
            map.save(RSWGame.Data.SETTINGS, true);
            Text.send(commandSender, "&aMap renamed to &f" + displayName);
        } else {
            commandSender.sendMessage(rs.getLanguageManagerAPI().getString(LanguageManagerAPI.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("reset")
    @Completion("#maps")
    @Permission("rsw.admin")
    @WrongUsage("&c/rsw reset <map>")
    public void reset(final CommandSender commandSender, String mapSTR) {
        RSWPlayer p = rs.getPlayerManagerAPI().getPlayer((Player) commandSender);
        RSWGame map = rs.getMapManagerAPI().getMap(mapSTR);
        if (map != null) {
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_RESET, true));
            map.reset();
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MAP_RESET_DONE, true));
        } else {
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("refreshLeaderboards")
    @Permission("rsw.admin")
    public void reset(final CommandSender commandSender) {
        rs.getLeaderboardManagerAPI().refreshLeaderboards();
        Text.send(commandSender, "Leaderboards Refreshed.");
    }

    public enum KIT_OPERATION {CREATE, DELETE, GIVE}
}