package josegamerpt.realskywars.commands;

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

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.chests.TierViewer;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.GUIManager;
import josegamerpt.realskywars.gui.guis.*;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.kits.KitInventory;
import josegamerpt.realskywars.kits.KitSettings;
import josegamerpt.realskywars.managers.CurrencyManager;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.shop.ShopManager;
import josegamerpt.realskywars.player.PlayerGUI;
import josegamerpt.realskywars.player.ProfileContent;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.utils.WorldEditUtils;
import josegamerpt.realskywars.world.SWWorld;
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
    public RealSkywars rs;

    public RealSkywarsCMD(RealSkywars rs) {
        this.rs = rs;
    }

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        Text.send(commandSender, "&f&lReal&B&LSkywars &r&aVersion &e" + rs.getDescription().getVersion());
    }

    @SubCommand("reload")
    @Alias("rl")
    @Permission("RealSkywars.Admin")
    public void reloadcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            this.rs.reload();
            commandSender.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.CONFIG_RELOAD, true));
        } else {
            this.rs.reload();
            commandSender.sendMessage("Reloaded RealSkywars!");
        }
    }

    @SubCommand("list")
    @Permission("rs.list")
    public void listcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getMatch() == null) {
                MapsViewer v = new MapsViewer(p, p.getMapViewerPref(), rs.getLanguageManager().getString(p, LanguageManager.TS.MAPS_NAME, false));
                v.openInventory(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_IN_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kits")
    @Permission("rs.kits")
    public void kitscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            ProfileContent ds = new ProfileContent(p, ShopManager.Categories.KITS);
            ds.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kit")
    @Completion({"#enum", "#kits", "#range:100"})
    @Permission("RealSkywars.Admin")
    public void kitcmd(final CommandSender commandSender, KIT_OPERATION action, String name, @Optional Double cost) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);

            if (action == null) {
                p.sendMessage(rs.getLanguageManager().getPrefix() + "Unknown kit command action.");
                return;
            }
            switch (action) {
                case CREATE:
                    if (cost == null) {
                        p.sendMessage(rs.getLanguageManager().getPrefix() + "Cost value not accepted.");
                        return;
                    }

                    if (rs.getKitManager().getKit(name) == null) {
                        Kit k = new Kit(Text.strip(Text.color(name)), name, cost, new KitInventory(p.getPlayer().getInventory().getContents()));
                        KitSettings m = new KitSettings(k, p.getUUID());
                        m.openInventory(p);
                    } else {
                        commandSender.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.KIT_EXISTS, true));
                    }
                    break;
                case DELETE:
                    Kit k2 = rs.getKitManager().getKit(name);
                    if (k2 != null) {
                        rs.getKitManager().unregisterKit(k2);
                        commandSender.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.DELETEKIT_DONE, true));
                    } else {
                        commandSender.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_KIT_FOUND, true));
                    }
                    break;
                case GIVE:
                    Kit k3 = rs.getKitManager().getKit(name);
                    if (k3 != null) {
                        k3.give(p);
                        p.playSound(Sound.ENTITY_VILLAGER_YES, 50, 50);
                    } else {
                        commandSender.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_KIT_FOUND, true));
                    }
                    break;
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("shop")
    @Permission("rs.shop")
    public void shopcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            GUIManager.openShopMenu(rs.getPlayerManager().getPlayer((Player) commandSender));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("play")
    @Completion("#enum")
    public void playcmd(final CommandSender commandSender, SWGameMode.Mode type) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (type != null && p != null && p.getPlayer() != null) {
                if (!(p.getState() == RSWPlayer.PlayerState.CAGE)) {
                    rs.getGameManager().findGame(p, type);
                } else {
                    Text.send(commandSender, rs.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_IN_MATCH, true));
                }
            } else {
                Text.send(commandSender, rs.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("balance")
    @Alias("bal")
    @Permission("rs.coins")
    @WrongUsage("&c/rsw bal")
    public void balancecmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);

            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.CMD_COINS, true).replace("%coins%", p.getCoins() + ""));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("coins")
    @Permission("rs.coins")
    @Completion({"#enum", "#players", "#range:100"})
    @WrongUsage("&c/rsw coins <send;add;set;remove> <name> <coins>")
    public void coinscmd(final CommandSender commandSender, CurrencyManager.Operations o, Player target, Double coins) {
        if (o == null) {
            Text.send(commandSender, RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Invalid operation type.");
            return;
        }

        RSWPlayer search = rs.getPlayerManager().getPlayer(target);

        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);

            if (search == null) {
                Text.send(commandSender, rs.getLanguageManager().getString(p, LanguageManager.TS.NO_PLAYER_FOUND, true));
                return;
            }

            new CurrencyManager(search, p, coins, o, true);
        } else {
            if (search == null) {
                Text.send(commandSender, rs.getLanguageManager().getString(LanguageManager.TS.NO_PLAYER_FOUND, true));
                return;
            }

            new CurrencyManager(search, coins, o, true);
        }
    }

    @SubCommand("verifylang")
    @Permission("RealSkywars.Admin")
    public void verifylangcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            String sep = "&9&m&l--------------------------------";
            Text.sendList(p.getPlayer(), Arrays.asList(sep, "&fLanguage Verification Started.", "&6"));

            HashMap<String, HashMap<LanguageManager.TS, String>> flag = rs.getLanguageManager().verifyLanguages();
            if (flag.isEmpty()) {
                p.sendMessage(rs.getLanguageManager().getPrefix() + Text.color("&aNo errors encountered."));
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
    @Permission("RealSkywars.Admin")
    public void setspectator(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getSetupRoom() != null) {
                p.getSetupRoom().setSpectatorLoc(p.getLocation());
                p.getSetupRoom().setSpectatorConfirm(true);
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.CMD_FINISHSETUP, true));
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("lobby")
    @Permission("rs.lobby")
    public void lobbycmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getMatch() == null) {
                rs.getGameManager().tpToLobby(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.CMD_MATCH_CANCEL, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("forcestart")
    @Permission("rs.forcestart")
    public void forcestartcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer(Bukkit.getPlayer(commandSender.getName()));
            if (p.isInMatch()) {
                p.sendMessage(p.getMatch().forceStart(p));
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("addsharik")
    @Permission("RealSkywars.Admin")
    public void addsharik(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().addPlayer(new RSWPlayer(true));
                p.sendMessage(Text.color("&4&lEXPERIMENTAL FEATURE. CAN RESULT IN SERVER & CLIENT CRASHES. &c&lAdded Null Player"));
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("leave")
    @Permission("rs.leave")
    public void leave(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().removePlayer(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("setlobby")
    @Permission("RealSkywars.Admin")
    public void setlobby(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            Config.file().set("Config.Lobby.World", p.getLocation().getWorld().getName());
            Config.file().set("Config.Lobby.X", p.getLocation().getX());
            Config.file().set("Config.Lobby.Y", p.getLocation().getY());
            Config.file().set("Config.Lobby.Z", p.getLocation().getZ());
            Config.file().set("Config.Lobby.Yaw", p.getLocation().getYaw());
            Config.file().set("Config.Lobby.Pitch", p.getLocation().getPitch());
            Config.save();
            rs.getGameManager().setLobbyLoc(p.getLocation());
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.LOBBY_SET, true));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("cancelsetup")
    @Permission("RealSkywars.Admin")
    public void cancelsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getSetupRoom() != null) {
                rs.getMapManager().cancelSetup(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("finishsetup")
    @Permission("RealSkywars.Admin")
    public void finishsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getSetupRoom() != null) {
                if (p.getSetupRoom().areCagesConfirmed() & p.getSetupRoom().isSpectatorLocConfirmed()) {
                    rs.getMapManager().finishSetup(p);
                } else {
                    p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.SETUP_NOT_FINISHED, true));
                }
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("maps")
    @Permission("RealSkywars.Admin")
    public void maps(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.CMD_MAPS, true).replace("%rooms%", "" + rs.getGameManager().getGames(GameManager.GameModes.ALL).size()));
            for (SWGameMode s : rs.getGameManager().getGames(GameManager.GameModes.ALL)) {
                TextComponent a = new TextComponent(Text.color("&7- &f" + s.getName()));
                a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rsw map " + s.getName()));
                a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Text.color("&fClick to open &b" + s.getName() + "&f settings!")).create()));
                p.getPlayer().spigot().sendMessage(a);
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("players")
    @Permission("RealSkywars.Admin")
    public void players(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.CMD_PLAYERS, true).replace("%players%", rs.getPlayerManager().getPlayers().size() + ""));
            for (RSWPlayer pair : rs.getPlayerManager().getPlayers()) {
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
    @Permission("RealSkywars.Admin")
    public void map(final CommandSender commandSender, String name) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            SWGameMode sw = rs.getGameManager().getGame(name);
            if (sw != null) {
                MapSettings r = new MapSettings(sw, p.getUUID());
                r.openInventory(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("tp")
    @Completion("#maps")
    @Permission("RealSkywars.Admin")
    public void tpcmd(final CommandSender commandSender, String name) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p != null) {
                p.getPlayer().setGameMode(GameMode.CREATIVE);
                p.teleport(rs.getGameManager().getGame(name).getSWWorld().getWorld().getSpawnLocation());
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("set2chest")
    @Completion({"#enum", "#enum"})
    @Permission("RealSkywars.Admin")
    public void setchest(final CommandSender commandSender, SWChest.Tier tt, SWChest.Type t) {
        if (commandSender instanceof Player) {
            final Player p = (Player) commandSender;
            rs.getChestManager().set2Chest(tt, t, Arrays.asList(IntStream.range(9, 35).boxed().map(p.getInventory()::getItem).filter(Objects::nonNull).toArray(ItemStack[]::new)));
            Text.send(commandSender, "Itens set for " + tt.name() + " (middle: " + t.name() + ")");
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("seetier")
    @Completion({"#enum", "#enum"})
    @Permission("RealSkywars.Admin")
    public void seetier(final CommandSender commandSender, SWChest.Tier tt, SWChest.Type t) {
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
    @Permission("RealSkywars.Admin")
    public void player(final CommandSender commandSender, Player get) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            RSWPlayer search = rs.getPlayerManager().getPlayer(get);
            if (search != null) {
                PlayerGUI playg = new PlayerGUI(rs.getPlayerManager().getPlayer(get), p.getUUID(), search);
                playg.openInventory(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_PLAYER_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("create")
    @Permission("RealSkywars.Admin")
    @Completion({"#createsuggestions", "#worldtype", "#range:20", "#range:20"})
    @WrongUsage("&c/rsw create <name> <type> <players> or /rsw create <name> <type> <number of teams> <players per team>")
    public void createcmd(final CommandSender commandSender, String mapname, SWWorld.WorldType wt, Integer maxPlayersandTeams, @Optional Integer teamPlayers) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);

            if (wt == null) {
                p.sendMessage("&cInvalid game generation type.");
                return;
            }

            if (wt.equals(SWWorld.WorldType.SCHEMATIC) && !WorldEditUtils.schemFileExists(mapname)) {
                p.sendMessage("&cNo " + mapname + "&c found in RealSkywars/maps. Did you forget to add .schem?");
                return;
            }

            if (Config.file().isSection("Config.Lobby")) {
                if (teamPlayers == null) {
                    //solo
                    if (rs.getMapManager().getRegisteredMaps().contains(mapname)) {
                        p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.MAP_EXISTS, true));
                    } else {
                        if (p.getSetupRoom() != null) {
                            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.SETUP_NOT_FINISHED, true));
                        } else {
                            rs.getMapManager().setupSolo(p, mapname, wt, maxPlayersandTeams);
                        }
                    }
                } else {
                    //teams
                    if (!rs.getMapManager().getRegisteredMaps().contains(mapname)) {
                        if (p.getSetupRoom() != null) {
                            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.SETUP_NOT_FINISHED, true));
                        } else {
                            rs.getMapManager().setupTeams(p, mapname, wt, maxPlayersandTeams, teamPlayers);
                        }
                    } else {
                        p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.MAP_EXISTS, true));
                    }
                }
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.LOBBYLOC_NOT_SET, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("unregister")
    @Completion("#maps")
    @Alias("del")
    @Permission("RealSkywars.Admin")
    @WrongUsage("&c/rsw unregister <map>")
    public void delete(final CommandSender commandSender, String map) {
        if (rs.getMapManager().getRegisteredMaps().contains(map)) {
            rs.getMapManager().unregisterMap(rs.getMapManager().getMap(map));
            commandSender.sendMessage(rs.getLanguageManager().getString(LanguageManager.TS.MAP_UNREGISTERED, true));
        } else {
            commandSender.sendMessage(rs.getLanguageManager().getString(LanguageManager.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("reset")
    @Completion("#maps")
    @Permission("RealSkywars.Admin")
    @WrongUsage("&c/rsw reset <map>")
    public void reset(final CommandSender commandSender, String map) {
        RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
        if (rs.getMapManager().getRegisteredMaps().contains(map)) {
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.ARENA_RESET, true));
            rs.getMapManager().getMap(map).reset();
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.MAP_RESET_DONE, true));
        } else {
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("refreshLeaderboards")
    @Permission("RealSkywars.Admin")
    public void reset(final CommandSender commandSender) {
        rs.getLeaderboardManager().refreshLeaderboards();
        Text.send(commandSender, "Leaderboards Refreshed.");
    }

    public enum KIT_OPERATION {CREATE, DELETE, GIVE}
}