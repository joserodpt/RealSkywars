package josegamerpt.realskywars;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.Kit;
import josegamerpt.realskywars.configuration.*;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.managers.*;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.player.RSWPlayer;
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
        Text.send(commandSender, "&f&lReal&B&LSkywars &r&aVersion &9" + rs.getDescription().getVersion());
    }

    @SubCommand("reload")
    @Alias("rl")
    @Permission("RealSkywars.Admin")
    public void reloadcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            GameManager.endGames();

            Config.reload();
            Maps.reload();
            Players.reload();
            //Chests.reload();
            Languages.reload();

            Debugger.debug = Config.file().getBoolean("Debug-Mode");

            LanguageManager.loadLanguages();
            PlayerManager.stopScoreboards();
            PlayerManager.loadPlayers();
            Shops.reload();
            Kits.reload();
            KitManager.loadKits();

            MapManager.loadMaps();
            GameManager.loadLobby();

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
    public void coinscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            p.sendMessage(
                    LanguageManager.getString(p, Enum.TS.CMD_COINS, true).replace("%coins%", p.getCoins() + ""));
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

    @SubCommand("edittrails")
    @Permission("RealSkywars.Admin")
    public void edittrailscmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            GUIManager.openTrailEditor(PlayerManager.getPlayer((Player) commandSender));
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
                    TextComponent a = new TextComponent(Text.color("&7- &f" + pair.getPlayer().getName()));
                    a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/rsw player " + pair.getPlayer().getName()));
                    a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(
                                    Text.color("&fClick here to inspect &b" + pair.getPlayer().getName()))
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
    @Completion("#chesttiers")
    @Permission("RealSkywars.Admin")
    public void settier(final CommandSender commandSender, Enum.TierType tt) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (p.getRoom() != null) {
                p.getRoom().setTierType(tt);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.TIER_SET, true).replace("%chest%",
                        tt.name()));
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("player")
    @Completion("#players")
    @Permission("RealSkywars.Admin")
    public void player(final CommandSender commandSender, String name) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            if (PlayerManager.getPlayer(PlayerManager.searchPlayer(name)) != null) {
                PlayerGUI playg = new PlayerGUI(
                        PlayerManager.getPlayer(PlayerManager.searchPlayer(name)),
                        p.getUniqueId());
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
    @WrongUsage("&c/rsw createkit <name> <price>")
    public void createkit(final CommandSender commandSender, String kitname, Double cost) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            Kit k = new Kit(KitManager.getNewID(), kitname, cost, Material.LEATHER_CHESTPLATE,
                    p.getPlayer().getInventory().getContents(), "RealSkywars.Kit");
            KitSettings m = new KitSettings(k, p.getUniqueId());
            m.openInventory(p);
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("sendcoins")
    @Permission("RealSkywars.Coins")
    @Completion("#players")
    @WrongUsage("&c/rsw sendcoins <name> <coins>")
    public void sendcoins(final CommandSender commandSender, Player pla, Double coins) {
        if (commandSender instanceof Player) {
            RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
            RSWPlayer search = PlayerManager.getPlayer(pla);
            if (search != null) {
                CurrencyManager c = new CurrencyManager(search, p, coins, false);
                if (c.canMakeOperation()) {
                    c.transferCoins();
                } else {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.INSUFICIENT_COINS, true)
                            .replace("%coins%", p.getCoins() + ""));
                }
            } else {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_PLAYER_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("setcoins")
    @Permission("RealSkywars.Admin")
    @Completion("#players")
    @WrongUsage("&c/rsw setcoins <name> <coins>")
    public void setcoins(final CommandSender commandSender, Player pla, Double coins) {
        RSWPlayer p = PlayerManager.getPlayer((Player) commandSender);
        RSWPlayer search = PlayerManager.getPlayer(pla);
        if (search != null) {
            CurrencyManager c = new CurrencyManager(search, p, coins, true);
            c.setCoins();
        } else {
            p.sendMessage(LanguageManager.getString(p, Enum.TS.NO_PLAYER_FOUND, true));
        }
    }

    @SubCommand("create")
    @Permission("RealSkywars.Admin")
    @WrongUsage("&c/rsw create <name> <players> or /rsw create <name> <number of teams> <players per team>")
    public void create(final CommandSender commandSender, String mapname, Integer maxPlayersandTeams, @Optional Integer teamPlayers) {
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

    @SubCommand("delete")
    @Completion("#maps")
    @Alias("del")
    @Permission("RealSkywars.Admin")
    @WrongUsage("&c/rsw delete <map>")
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