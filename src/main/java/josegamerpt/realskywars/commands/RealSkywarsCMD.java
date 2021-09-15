package josegamerpt.realskywars.commands;

import com.j256.ormlite.stmt.QueryBuilder;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.database.PlayerData;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.managers.CurrencyManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.misc.Selections;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Command("realskywars")
@Alias({"sw", "rsw"})
public class RealSkywarsCMD extends CommandBase {

    public RealSkywars rs;
    private String onlyPlayer = "[RealSkywars] Only players can run this command.";
    public enum KIT { create, delete, give }

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
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            this.rs.reload();
            commandSender.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CONFIG_RELOAD, true));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("list")
    public void listcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getMatch() == null) {
                MapsViewer v = new MapsViewer(p, p.getMapViewerPref(),
                        RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MAPS_NAME, false));
                v.openInventory(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_IN_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kits")
    public void kitscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            ProfileContent ds = new ProfileContent(p, ShopManager.Categories.KITS);
            ds.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kit")
    @Completion({"#enum", "#kits", "#range:100"})
    @Permission("RealSkywars.Admin")
    public void kitcmd(final CommandSender commandSender, RealSkywarsCMD.KIT action, String name, @Optional Double cost) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);

            if (action == null)
            {
                p.sendMessage(RealSkywars.getLanguageManager().getPrefix() + "Unknown kit command action.");
                return;
            }
            switch (action)
            {
                case create:
                    if (cost == null)
                    {
                        p.sendMessage(RealSkywars.getLanguageManager().getPrefix() + "Cost value not accepted.");
                        return;
                    }
                    Kit k = new Kit(RealSkywars.getKitManager().getNewID(), name, cost,
                            p.getInventory().getContents());
                    KitSettings m = new KitSettings(k, p.getUUID());
                    m.openInventory(p);
                    break;
                case delete:
                    Kit k2 = RealSkywars.getKitManager().getKit(name);
                    if (k2 != null) {
                        k2.deleteKit();
                        commandSender.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.DELETEKIT_DONE, true));
                    } else {
                        commandSender.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_KIT_FOUND, true));
                    }
                    break;
                case give:
                    Kit k3 = RealSkywars.getKitManager().getKit(name);
                    if (k3 != null) {
                        k3.give(p);
                        p.playSound(Sound.ENTITY_VILLAGER_YES, 50, 50);
                    } else {
                        commandSender.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_KIT_FOUND, true));
                    }
                    break;
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("shop")
    public void shopcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            GUIManager.openShopMenu(RealSkywars.getPlayerManager().getPlayer((Player) commandSender));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("play")
    @Completion("#enum")
    public void playcmd(final CommandSender commandSender, SWGameMode.Mode type) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (type != null && p != null && p.getPlayer() != null) {
                if (!(p.getState() == RSWPlayer.PlayerState.CAGE)) {
                    RealSkywars.getGameManager().findGame(p, type);
                } else {
                    Text.send(commandSender, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_IN_MATCH, true));
                }
            } else {
                Text.send(commandSender, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("coins")
    @Permission("RealSkywars.Coins")
    @Completion({"#enum", "#players", "#range:100"})
    @WrongUsage("&c/rsw coins <send;add;set> <name> <coins>")
    public void coinscmd(final CommandSender commandSender, CurrencyManager.Operations o, Player target, Double coins) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (o == null) {
                p.sendMessage(
                        RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_COINS, true).replace("%coins%", p.getCoins() + ""));
            } else {
                RSWPlayer search = RealSkywars.getPlayerManager().getPlayer(target);
                if (search != null) {
                    switch (o) {
                        case send:
                            if (coins == null) {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.INSUFICIENT_COINS, true)
                                        .replace("%coins%", p.getCoins() + ""));
                                return;
                            }
                            CurrencyManager c = new CurrencyManager(search, p, coins, false);
                            if (c.canMakeOperation()) {
                                c.transferCoins();
                            } else {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.INSUFICIENT_COINS, true)
                                        .replace("%coins%", p.getCoins() + ""));
                            }
                            break;
                        case set:
                            if (!p.getPlayer().hasPermission("RealSkywars.Admin")) {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                                return;
                            }
                            if (search != null) {
                                CurrencyManager c2 = new CurrencyManager(search, p, coins, true);
                                c2.setCoins();
                            } else {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_PLAYER_FOUND, true));
                            }
                            break;
                        case add:
                            if (!p.getPlayer().hasPermission("RealSkywars.Admin")) {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                                return;
                            }
                            if (search != null) {
                                CurrencyManager c3 = new CurrencyManager(search, coins);
                                c3.addCoins();
                            } else {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_PLAYER_FOUND, true));
                            }
                            break;
                    }
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_PLAYER_FOUND, true));
                }
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("verifylang")
    @Permission("RealSkywars.Admin")
    public void verifylangcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            String sep = "&9&m&l--------------------------------";
            Text.sendList(p.getPlayer(), Arrays.asList(sep, "&fLanguage Verification Started.", "&6"));

            HashMap<String, HashMap<LanguageManager.TS, String>> flag = RealSkywars.getLanguageManager().verifyLanguages();
            if (flag.size() == 0) {
                p.sendMessage(RealSkywars.getLanguageManager().getPrefix() + Text.color("&aNo errors encountered."));
                p.sendMessage(Text.color(sep));
            } else {
                for (Map.Entry<String, HashMap<LanguageManager.TS, String>> entry : flag.entrySet()) {
                    String key = entry.getKey();
                    HashMap<LanguageManager.TS, String> value = entry.getValue();

                    p.sendMessage(Text.color("&6Found translation errors in Language: &b" + key));

                    for (Map.Entry<LanguageManager.TS, String> e : value.entrySet()) {
                        LanguageManager.TS t = e.getKey();
                        Object s = e.getValue();

                        p.sendMessage(Text
                                .color("&7<&4!&7> &b" + t.name() + " &4returned: &7" + s));
                    }

                    Text.sendList(p.getPlayer(), Arrays.asList("",
                            "&fFound &b" + value.size() + " &ferrors for the Language &b" + key + ".",
                            sep));
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
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getSetup() != null) {
                p.getSetup().setSpectatorLoc(p.getLocation());
                p.getSetup().setSpectatorConfirm(true);
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_FINISHSETUP, true));
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("lobby")
    public void lobbycmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getMatch() == null) {
                RealSkywars.getGameManager().tpToLobby(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_MATCH_CANCEL, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("forcestart")
    @Permission("RealSkywars.ForceStart")
    public void forcestartcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer(Bukkit.getPlayer(commandSender.getName()));
            if (p.isInMatch()) {
                p.sendMessage(p.getMatch().forceStart(p));
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("addsharik")
    @Permission("RealSkywars.Admin")
    public void addsharik(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().addPlayer(new RSWPlayer(true));
                p.sendMessage(Text.color(
                        "&4EXPERIMENTAL FEATURE. CAN RESULT IN SERVER & CLIENT CRASHES. &cAdded Null Player"));
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("leave")
    public void leave(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().removePlayer(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("setlobby")
    @Permission("RealSkywars.Admin")
    public void setlobby(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            Config.file().set("Config.Lobby.World", p.getLocation().getWorld().getName());
            Config.file().set("Config.Lobby.X", p.getLocation().getX());
            Config.file().set("Config.Lobby.Y", p.getLocation().getY());
            Config.file().set("Config.Lobby.Z", p.getLocation().getZ());
            Config.file().set("Config.Lobby.Yaw", p.getLocation().getYaw());
            Config.file().set("Config.Lobby.Pitch", p.getLocation().getPitch());
            Config.save();
            RealSkywars.getGameManager().setLobbyLoc(p.getLocation());
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.LOBBY_SET, true));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("cancelsetup")
    @Permission("RealSkywars.Admin")
    public void cancelsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getSetup() != null) {
                RealSkywars.getMapManager().cancelSetup(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("finishsetup")
    @Permission("RealSkywars.Admin")
    public void finishsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.getSetup() != null) {
                if (p.getSetup().isGUIConfirmed() && p.getSetup().areCagesConfirmed() & p.getSetup().isSpectatorLocConfirmed()) {
                    RealSkywars.getMapManager().finishSetup(p);
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SETUP_NOT_FINISHED, true));
                }
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("maps")
    @Permission("RealSkywars.Admin")
    public void maps(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_MAPS, true).replace("%rooms%",
                    "" + RealSkywars.getGameManager().getGames(PlayerManager.Modes.ALL).size()));
            for (SWGameMode s : RealSkywars.getGameManager().getGames(PlayerManager.Modes.ALL)) {
                TextComponent a = new TextComponent(Text.color("&7- &f" + s.getName()));
                a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/rsw map " + s.getName()));
                a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                Text.color("&fClick to open &b" + s.getName() + "&f settings!"))
                                .create()));
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
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_PLAYERS, true).replace("%players%",
                    RealSkywars.getPlayerManager().getPlayers().size() + ""));
            for (RSWPlayer pair : RealSkywars.getPlayerManager().getPlayers()) {
                if (pair.getPlayer() != null) {
                    TextComponent a = new TextComponent(Text.color("&7- &f" + pair.getName()));
                    a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/rsw player " + pair.getName()));
                    a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(
                                    Text.color("&fClick here to inspect &b" + pair.getName()))
                                    .create()));
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
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            SWGameMode sw = RealSkywars.getGameManager().getGame(name);
            if (sw != null) {
                RoomSettings r = new RoomSettings(sw, p.getUUID());
                r.openInventory(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("maptp")
    @Completion("#maps")
    @Permission("RealSkywars.Admin")
    public void maptp(final CommandSender commandSender, String name) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            p.teleport(RealSkywars.getGameManager().getGame(name).getWorld().getSpawnLocation());
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("settier")
    @Completion("#enum")
    @Permission("RealSkywars.Admin")
    public void settier(final CommandSender commandSender, ChestManager.TierType tt) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().setTierType(tt, true);
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIER_SET, true).replace("%chest%",
                        tt.name()));
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }


    @SubCommand("set2chest")
    @Completion({"#enum", "#boolean"})
    @Permission("RealSkywars.Admin")
    public void setchest(final CommandSender commandSender, ChestManager.TierType tt, Boolean middle) {
        if (commandSender instanceof Player) {
            RealSkywars.getChestManager().set2Chest(tt, middle, Itens.getInventory(((Player) commandSender)));
            Text.send(commandSender, "Itens set for " + tt.name() + " (middle: " + middle + ")");
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("gettier")
    @Completion({"#enum", "#boolean"})
    @Permission("RealSkywars.Admin")
    public void gettier(final CommandSender commandSender, ChestManager.TierType tt, Boolean middle) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            RealSkywars.getChestManager().getChest(tt, middle).forEach(swChestItem -> p.getInventory().addItem(swChestItem.getItemStack()));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("add2chest")
    @Completion({"#enum", "#boolean"})
    @Permission("RealSkywars.Admin")
    public void addchest(final CommandSender commandSender, ChestManager.TierType tt, Boolean middle) {
        if (commandSender instanceof Player) {
            RealSkywars.getChestManager().add2Chest(tt, middle, Itens.getInventory(((Player) commandSender)));
            Text.send(commandSender, "Itens set for " + tt.name() + " (middle: " + middle + ")");
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("player")
    @Completion("#players")
    @Permission("RealSkywars.Admin")
    public void player(final CommandSender commandSender, Player get) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            RSWPlayer search = RealSkywars.getPlayerManager().getPlayer(get);
            if (search != null) {
                PlayerGUI playg = new PlayerGUI(RealSkywars.getPlayerManager().getPlayer(get), p.getUUID(), search);
                playg.openInventory(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_PLAYER_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("create")
    @Permission("RealSkywars.Admin")
    @Completion({"#range:50", "#range:20", "#range"})
    @WrongUsage("&c/rsw create <name> <players> or /rsw create <name> <number of teams> <players per team>")
    public void create(final CommandSender commandSender, String mapname, Integer
            maxPlayersandTeams, @Optional Integer teamPlayers) {
        if (commandSender instanceof Player) {

            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);

            if (Config.file().isConfigurationSection("Config.Lobby")) {
                if (teamPlayers == null) {
                    //solo
                    if (RealSkywars.getMapManager().getRegisteredMaps().contains(mapname)) {
                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MAP_EXISTS, true));
                    } else {
                        if (p.getSetup() != null) {
                            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SETUP_NOT_FINISHED, true));
                        } else {
                            RealSkywars.getMapManager().setupSolo(p, mapname, maxPlayersandTeams);
                        }
                    }
                } else {
                    //teams
                    if (!RealSkywars.getMapManager().getRegisteredMaps().contains(mapname)) {
                        if (p.getSetup() != null) {
                            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SETUP_NOT_FINISHED, true));
                        } else {
                            RealSkywars.getMapManager().setupTeams(p, mapname, maxPlayersandTeams, teamPlayers);
                        }
                    } else {
                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MAP_EXISTS, true));
                    }
                }
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.LOBBYLOC_NOT_SET, true));
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
        if (RealSkywars.getMapManager().getRegisteredMaps().contains(map)) {
            RealSkywars.getMapManager().unregisterMap(RealSkywars.getMapManager().getMap(map));
            commandSender.sendMessage(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.MAP_UNREGISTERED, true));
        } else {
            commandSender.sendMessage(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("reset")
    @Completion("#maps")
    @Permission("RealSkywars.Admin")
    @WrongUsage("&c/rsw reset <map>")
    public void reset(final CommandSender commandSender, String map) {
        RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
        if (RealSkywars.getMapManager().getRegisteredMaps().contains(map)) {
            RealSkywars.getMapManager().getMap(map).reset();
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_RESET, true));
        } else {
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
        }
    }

    @SubCommand("refreshLeaderboards")
    @Permission("RealSkywars.Admin")
    public void reset(final CommandSender commandSender) {
        RealSkywars.getLeaderboardManager().refreshLeaderboards();
        Text.send(commandSender, "Ok.");
    }
}