package josegamerpt.realskywars;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.Kit;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.modes.SWGameMode;
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
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Command("realskywars")
@Alias({"sw", "rsw"})
public class Commands extends CommandBase {

    public RealSkywars rs;
    private String onlyPlayer = "[RealSkywars] Only players can run this command.";

    public Commands(RealSkywars rs) {
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            this.rs.reload();
            commandSender.sendMessage(LanguageManager.getString(p, Enum.TS.CONFIG_RELOAD, true));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("join")
    public void joincmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getRoom() == null) {
                MapsViewer v = new MapsViewer(p, p.getSelection(Enum.Selection.MAPVIEWER),
                        "Maps");
                v.openInventory(p);
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.ALREADY_IN_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kits")
    public void kitscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            ProfileContent ds = new ProfileContent(p.getPlayer(), Enum.Categories.KITS, "&9Kits");
            ds.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("shop")
    public void shopcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            GUIManager.openShopMenu(PlayerManager.getPlayer((Player) commandSender));
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (o == null) {
                p.sendMessage(
                        LanguageManager.getString(p, Enum.TS.CMD_COINS, true).replace("%coins%", p.getCoins() + ""));
            } else {
                RSWPlayer search = PlayerManager.getPlayer(target);
                if (search != null) {
                    switch (o) {
                        case send:
                            if (coins == null) {
                                p.sendMessage(LanguageManager.getString(p, Enum.TS.INSUFICIENT_COINS, true)
                                        .replace("%coins%", p.getCoins() + ""));
                                return;
                            }
                            CurrencyManager c = new CurrencyManager(search, p, coins, false);
                            if (c.canMakeOperation()) {
                                c.transferCoins();
                            } else {
                                p.sendMessage(LanguageManager.getString(p, Enum.TS.INSUFICIENT_COINS, true)
                                        .replace("%coins%", p.getCoins() + ""));
                            }
                            break;
                        case set:
                            if (!p.getPlayer().hasPermission("RealSkywars.Admin"))
                            {
                                p.sendMessage(LanguageManager.getString(p, Enum.TS.CMD_NOPERM, true));
                                return;
                            }
                            if (search != null) {
                                CurrencyManager c2 = new CurrencyManager(search, p, coins, true);
                                c2.setCoins();
                            } else {
                                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_PLAYER_FOUND, true));
                            }
                            break;
                        case add:
                            if (!p.getPlayer().hasPermission("RealSkywars.Admin"))
                            {
                                p.sendMessage(LanguageManager.getString(p, Enum.TS.CMD_NOPERM, true));
                                return;
                            }
                            if (search != null) {
                                CurrencyManager c3 = new CurrencyManager(search, p, coins, true);
                                c3.addCoins();
                            } else {
                                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_PLAYER_FOUND, true));
                            }
                            break;
                    }
                } else {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_PLAYER_FOUND, true));
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            String sep = "&9&m&l--------------------------------";
            Text.sendList(p.getPlayer(), Arrays.asList(sep, "&fLanguage Verification Started.", "&6"));

            HashMap<String, HashMap<Enum.TS, String>> flag = LanguageManager.verifyLanguages();
            if (flag.size() == 0) {
                p.sendMessage(LanguageManager.getPrefix() + Text.color("&aNo errors encountered."));
                p.sendMessage(Text.color(sep));
            } else {
                for (Map.Entry<String, HashMap<Enum.TS, String>> entry : flag.entrySet()) {
                    String key = entry.getKey();
                    HashMap<Enum.TS, String> value = entry.getValue();

                    p.sendMessage(Text.color("&6Found translation errors in Language: &b" + key));

                    for (Map.Entry<Enum.TS, String> e : value.entrySet()) {
                        Enum.TS t = e.getKey();
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getSetup() != null) {
                p.getSetup().setSpectatorLoc(p.getLocation());
                p.getSetup().setSpectatorConfirm(true);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CMD_FINISHSETUP, true));
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("lobby")
    public void lobbycmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getRoom() == null) {
                GameManager.tpToLobby(p);
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CMD_MATCH_CANCEL, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("forcestart")
    @Permission("RealSkywars.ForceStart")
    public void forcestartcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer(Bukkit.getPlayer(commandSender.getName()));
            if (p.getRoom() != null) {
                p.sendMessage(p.getRoom().forceStart(p));
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("addsharik")
    @Permission("RealSkywars.Admin")
    public void addsharik(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getRoom() != null) {
                p.getRoom().addPlayer(new RSWPlayer(true));
                p.sendMessage(Text.color(
                        "&4EXPERIMENTAL FEATURE. CAN RESULT IN SERVER & CLIENT CRASHES. &cAdded Null Player"));
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("leave")
    public void leave(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getRoom() != null) {
                p.getRoom().removePlayer(p);
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("setlobby")
    @Permission("RealSkywars.Admin")
    public void setlobby(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            Config.file().set("Config.Lobby.World", p.getLocation().getWorld().getName());
            Config.file().set("Config.Lobby.X", p.getLocation().getX());
            Config.file().set("Config.Lobby.Y", p.getLocation().getY());
            Config.file().set("Config.Lobby.Z", p.getLocation().getZ());
            Config.file().set("Config.Lobby.Yaw", p.getLocation().getYaw());
            Config.file().set("Config.Lobby.Pitch", p.getLocation().getPitch());
            Config.save();
            GameManager.setLobbyLoc(p.getLocation());
            p.sendMessage(LanguageManager.getString(p, Enum.TS.LOBBY_SET, true));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("cancelsetup")
    @Permission("RealSkywars.Admin")
    public void cancelsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getSetup() != null) {
                MapManager.cancelSetup(p);
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("finishsetup")
    @Permission("RealSkywars.Admin")
    public void finishsetup(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getSetup() != null) {
                if (p.getSetup().isGUIConfirmed() && p.getSetup().areCagesConfirmed() & p.getSetup().isSpectatorLocConfirmed()) {
                    MapManager.finishSetup(p);
                } else {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.SETUP_NOT_FINISHED, true));
                }
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_SETUPMODE, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("maps")
    @Permission("RealSkywars.Admin")
    public void maps(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            p.sendMessage(LanguageManager.getString(p, Enum.TS.CMD_MAPS, true).replace("%rooms%",
                    "" + GameManager.getRooms().size()));
            for (SWGameMode s : GameManager.getRooms()) {
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            p.sendMessage(LanguageManager.getString(p, Enum.TS.CMD_PLAYERS, true).replace("%players%",
                    PlayerManager.getPlayers().size() + ""));
            for (RSWPlayer pair : PlayerManager.getPlayers()) {
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (GameManager.getGame(name) != null) {
                RoomSettings r = new RoomSettings(GameManager.getGame(name), p.getUniqueId());
                r.openInventory(p);
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NOMAP_FOUND, true));
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            p.teleport(GameManager.getGame(name).getWorld().getSpawnLocation());
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("delkit")
    @Completion("#kits")
    @Permission("RealSkywars.Admin")
    public void delkit(final CommandSender commandSender, String name) {
        Kit k = KitManager.getKit(name);
        if (k != null) {
            k.deleteKit();
            commandSender.sendMessage(LanguageManager.getString(new RSWPlayer(false), Enum.TS.DELETEKIT_DONE, true));
        } else {
            commandSender.sendMessage(LanguageManager.getString(new RSWPlayer(false), Enum.TS.NO_KIT_FOUND, true));
        }
    }

    @SubCommand("settier")
    @Completion("#enum")
    @Permission("RealSkywars.Admin")
    public void settier(final CommandSender commandSender, Enum.TierType tt) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getRoom() != null) {
                p.getRoom().setTierType(tt, true);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.TIER_SET, true).replace("%chest%",
                        tt.name()));
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }


    @SubCommand("set2chest")
    @Completion({"#enum", "#boolean"})
    @Permission("RealSkywars.Admin")
    public void setchest(final CommandSender commandSender, Enum.TierType tt, Boolean middle) {
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
    public void gettier(final CommandSender commandSender, Enum.TierType tt, Boolean middle) {
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
    public void addchest(final CommandSender commandSender, Enum.TierType tt, Boolean middle) {
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
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            RSWPlayer search = PlayerManager.getPlayer(get);
            if (search != null) {
                PlayerGUI playg = new PlayerGUI(PlayerManager.getPlayer(get), p.getUniqueId(), search);
                playg.openInventory(p);
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_PLAYER_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("createkit")
    @Permission("RealSkywars.Admin")
    @Completion({"#range:30", "#range:20"})
    @WrongUsage("&c/rsw createkit <name> <price>")
    public void createkit(final CommandSender commandSender, String kitname, Double cost) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            Kit k = new Kit(KitManager.getNewID(), kitname, cost, Material.LEATHER_CHESTPLATE,
                    p.getInventory().getContents(), "RealSkywars.Kit");
            KitSettings m = new KitSettings(k, p.getUniqueId());
            m.openInventory(p);
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

            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);

            if (Config.file().isConfigurationSection("Config.Lobby")) {
                if (teamPlayers == null) {
                    //solo
                    if (MapManager.getRegisteredMaps().contains(mapname)) {
                        p.sendMessage(LanguageManager.getString(p, Enum.TS.MAP_EXISTS, true));
                    } else {
                        if (p.getSetup() != null) {
                            p.sendMessage(LanguageManager.getString(p, Enum.TS.SETUP_NOT_FINISHED, true));
                        } else {
                            MapManager.setupSolo(p, mapname, maxPlayersandTeams);
                        }
                    }
                } else {
                    //teams
                    if (!MapManager.getRegisteredMaps().contains(mapname)) {
                        if (p.getSetup() != null) {
                            p.sendMessage(LanguageManager.getString(p, Enum.TS.SETUP_NOT_FINISHED, true));
                        } else {
                            MapManager.setupTeams(p, mapname, maxPlayersandTeams, teamPlayers);
                        }
                    } else {
                        p.sendMessage(LanguageManager.getString(p, Enum.TS.MAP_EXISTS, true));
                    }
                }
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.LOBBYLOC_NOT_SET, true));
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
        if (MapManager.getRegisteredMaps().contains(map)) {
            MapManager.unregisterMap(MapManager.getMap(map));
            commandSender.sendMessage(LanguageManager.getString(new RSWPlayer(false), Enum.TS.MAP_UNREGISTERED, true));
        } else {
            commandSender.sendMessage(LanguageManager.getString(new RSWPlayer(false), Enum.TS.NOMAP_FOUND, true));
        }
    }

    @SubCommand("reset")
    @Completion("#maps")
    @Permission("RealSkywars.Admin")
    @WrongUsage("&c/rsw reset <map>")
    public void reset(final CommandSender commandSender, String map) {
        RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
        if (MapManager.getRegisteredMaps().contains(map)) {
            MapManager.getMap(map).reset();
            p.sendMessage(LanguageManager.getString(p, Enum.TS.ARENA_RESET, true));
        } else {
            p.sendMessage(LanguageManager.getString(p, Enum.TS.NOMAP_FOUND, true));
        }
    }

    @SubCommand("invincible")
    @Permission("RealSkywars.Admin")
    @Completion("#players")
    @WrongUsage("&c/rsw invincible <name>")
    public void inv(final CommandSender commandSender, Player p) {
        RSWPlayer rp = PlayerManager.getPlayer(p);
        rp.setInvincible(!rp.isInvencible());
        commandSender.sendMessage(rp.getDisplayName() + " invincibility: " + rp.isInvencible());
    }
}