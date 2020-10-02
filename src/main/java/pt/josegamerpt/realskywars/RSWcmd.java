package pt.josegamerpt.realskywars;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Enum.InteractionState;
import pt.josegamerpt.realskywars.classes.Enum.Selection;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.Enum.TierType;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.classes.Kit;
import pt.josegamerpt.realskywars.configuration.*;
import pt.josegamerpt.realskywars.gui.*;
import pt.josegamerpt.realskywars.managers.*;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

public class RSWcmd implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player gp = (Player) sender;
            GamePlayer p = PlayerManager.getPlayer(gp);
            assert p != null;
            if ((cmd.getName().equalsIgnoreCase("realskywars"))
                    || (cmd.getName().equalsIgnoreCase("rsw")) && (p.p.hasPermission("RealSkywars.maincmd"))) {
				switch (args.length) {
					case 1:
						switch (args[0].toLowerCase()) {
							case "reload":
								if (p.p.hasPermission("RealSkywars.Admin")) {

									GameManager.endGames();

									Config.reload();
									Maps.reload();
									Players.reload();
									Chests.reload();
									Languages.reload();

									Debugger.debug = Config.file().getBoolean("Debug-Mode");
									GameManager.loginTP = Config.file().getBoolean("Config.Auto-Teleport-To-Lobby");

									LanguageManager.loadLanguages();
									PlayerManager.players.forEach(gamePlayer -> gamePlayer.ps.stop());
									PlayerManager.loadPlayers();
									Shops.reload();
									Kits.reload();
									KitManager.loadKits();

									MapManager.loadMaps();

									double x = Config.file().getDouble("Config.Lobby.X");
									double y = Config.file().getDouble("Config.Lobby.Y");
									double z = Config.file().getDouble("Config.Lobby.Z");
									float yaw = (float) Config.file().getDouble("Config.Lobby.Yaw");
									float pitch = (float) Config.file().getDouble("Config.Lobby.Pitch");
									World world = Bukkit.getServer().getWorld(Objects.requireNonNull(Config.file().getString("Config.Lobby.World")));
									GameManager.lobbyLOC = new Location(world, x, y, z, yaw, pitch);
									p.sendMessage(LanguageManager.getString(p, TS.CONFIG_RELOAD, true));
									return false;
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								break;
							case "join":
								if (gp.hasPermission("RealSkywars.join")) {
									if (p.room == null) {
										MapsViewer v = new MapsViewer(p, PlayerManager.getSelection(p, Selection.MAPVIEWER),
												"Maps");
										v.openInventory(p);
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.ALREADY_IN_MATCH, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "kits":
								ProfileContent ds = new ProfileContent(gp, Enum.Categories.KITS, "&9Kits");
								ds.openInventory(p);
								break;
							case "shop":
								GUIManager.openShopMenu(p);
								break;
							case "coins":
								if (gp.hasPermission("RealSkywars.coins")) {
									p.sendMessage(
											LanguageManager.getString(p, TS.CMD_COINS, true).replace("%coins%", p.coins + ""));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "verifylang":
								if (gp.hasPermission("RealSkywars.Admin")) {
									String sep = "&9&m&l--------------------------------";
									Text.sendList(p.p, Arrays.asList(sep, "&fLanguage Verification Started.", "&6"));

									HashMap<String, HashMap<TS, String>> flag = LanguageManager.verifyLanguages();
									if (flag.size() == 0) {
										p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&aNo errors encountered."));
										p.sendMessage(Text.addColor(sep));
									} else {
										for (Map.Entry<String, HashMap<TS, String>> entry : flag.entrySet()) {
											String key = entry.getKey();
											HashMap<TS, String> value = entry.getValue();

											p.sendMessage(Text.addColor("&6Found translation errors in Language: &b" + key));

											for (Map.Entry<TS, String> e : value.entrySet()) {
												TS t = e.getKey();
												Object s = e.getValue();

												p.sendMessage(Text
														.addColor("&7<&4!&7> &b" + t.name() + " &4returned: &7" + s));
											}

											Text.sendList(p.p, Arrays.asList("",
													"&fFound &b" + value.size() + " &ferrors for the Language &b" + key + ".",
													sep));
										}
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "sendcoins":
								if (gp.hasPermission("RealSkywars.Coins")) {
									p.sendMessage(Text.addColor("&c/rsw sendcoins <Players> <Coins>"));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "pos1":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.setup != null) {
										BigDecimal xCert = BigDecimal.valueOf(p.p.getLocation().getX());
										BigDecimal yCert = BigDecimal.valueOf(p.p.getLocation().getY());
										BigDecimal zCert = BigDecimal.valueOf(p.p.getLocation().getZ());

										int xfinal = xCert.intValue();
										int yfinal = yCert.intValue();
										int zfinal = zCert.intValue();

										p.setup.POS1 = new Location(p.setup.worldMap, xfinal, yfinal, zfinal);
										p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&aPOS1 > X:" + xfinal + " | Y: " + yfinal + " | Z:" + zfinal));
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_SETUPMODE, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "pos2":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.setup != null) {
										BigDecimal xCert = BigDecimal.valueOf(p.p.getLocation().getX());
										BigDecimal yCert = BigDecimal.valueOf(p.p.getLocation().getY());
										BigDecimal zCert = BigDecimal.valueOf(p.p.getLocation().getZ());

										int xfinal = xCert.intValue();
										int yfinal = yCert.intValue();
										int zfinal = zCert.intValue();

										p.setup.POS2 = new Location(p.setup.worldMap, xfinal, yfinal, zfinal);
										p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&aPOS2 > X:" + xfinal + " | Y: " + yfinal + " | Z:" + zfinal));
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_SETUPMODE, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								break;
							case "chests":
								if (p.p.hasPermission("RealSkywars.Admin")) {
									ChestTierMenu c = new ChestTierMenu(p.p.getUniqueId());
									c.openInventory(p);
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								break;
							case "setspectator":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.setup != null) {
										p.setup.spectator = p.p.getLocation();
										p.setup.speclocConfirm = true;
										p.sendMessage(LanguageManager.getString(p, TS.CMD_FINISHSETUP, true));

									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_SETUPMODE, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "testp":
								if (Debugger.debug) {
									if (gp.hasPermission("RealSkywars.Admin")) {
										p.executeWinBlock(4);
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOT_FOUND, true));
								}
								return false;
							case "edittrails":
								if (gp.hasPermission("RealSkywars.Admin")) {
									GUIManager.openTrailEditor(p);
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "lobby":
								if (p.room == null) {
									PlayerManager.tpLobby(p);
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_MATCH_CANCEL, true));
								}
								return false;

							case "forcestart":
								if (gp.hasPermission("RealSkywars.Forcestart")) {
									if (p.room != null) {
										switch (p.room.getMode()) {
											case SOLO:
												if (p.room.getPlayersCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
													p.sendMessage(LanguageManager.getString(p, TS.CMD_CANT_FORCESTART, true));
												} else {
													p.room.forceStart();
													p.sendMessage(LanguageManager.getString(p, TS.CMD_MATCH_FORCESTART, true));
												}
												break;
											case TEAMS:
												Debugger.print(p.room.getPlayersCount() + "");
												Debugger.print((p.room.maxMembersTeam() + 1) + "");
												if (p.room.getPlayersCount() < (p.room.maxMembersTeam() + 1)) {
													p.sendMessage(LanguageManager.getString(p, TS.CMD_CANT_FORCESTART, true));
												} else {
													p.room.forceStart();
													p.sendMessage(LanguageManager.getString(p, TS.CMD_MATCH_FORCESTART, true));
												}
												break;
										}
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_MATCH, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "addnullplayer":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.room != null) {
										p.room.addPlayer(new GamePlayer());
										p.sendMessage(Text.addColor(
												"&4EXPERIMENTAL FEATURE. CAN RESULT IN SERVER & CLIENT CRASHES. &cAdded Null Player"));
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_MATCH, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "leave":
								if (gp.hasPermission("RealSkywars.Leave")) {
									if (p.room != null) {
										p.room.removePlayer(p);
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_MATCH, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "setlobby":
								if (gp.hasPermission("RealSkywars.setLobby")) {
									Config.file().set("Config.Lobby.World", Objects.requireNonNull(p.p.getLocation().getWorld()).getName());
									Config.file().set("Config.Lobby.X", p.p.getLocation().getX());
									Config.file().set("Config.Lobby.Y", p.p.getLocation().getY());
									Config.file().set("Config.Lobby.Z", p.p.getLocation().getZ());
									Config.file().set("Config.Lobby.Yaw", p.p.getLocation().getYaw());
									Config.file().set("Config.Lobby.Pitch", p.p.getLocation().getPitch());
									Config.save();
									GameManager.lobbyLOC = p.p.getLocation();
									p.sendMessage(LanguageManager.getString(p, TS.LOBBY_SET, true));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "cancelsetup":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.setup != null) {
										MapManager.cancelSetup(p);
										return false;
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_SETUPMODE, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								break;
							case "finishsetup":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.setup != null) {
										if (p.setup.guiConfirm) {
											if (p.setup.confirmCages) {
												if (p.setup.speclocConfirm) {
													MapManager.finishSetup(p);
													return false;
												} else {
													p.sendMessage(LanguageManager.getString(p, TS.SETUP_NOT_FINISHED, true));
												}
											} else {
												p.sendMessage(LanguageManager.getString(p, TS.SETUP_NOT_FINISHED, true));
											}
										} else {
											p.sendMessage(LanguageManager.getString(p, TS.SETUP_NOT_FINISHED, true));
										}
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_SETUPMODE, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								break;
							case "maps":
								if (p.p.hasPermission("RealSkywars.Admin")) {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_MAPS, true).replace("%rooms%",
											"" + GameManager.rooms.size()));
									for (GameRoom s : GameManager.rooms) {
										TextComponent a = new TextComponent(Text.addColor("&7- &f" + s.getName()));
										a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/rsw mapsetting " + s.getName()));
										a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder(
														Text.addColor("&fClick to open &b" + s.getName() + "&f settings!"))
														.create()));
										p.p.spigot().sendMessage(a);
									}
									return false;
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
									return false;
								}
							case "players":
								if (p.p.hasPermission("RealSkywars.Admin")) {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_PLAYERS, true).replace("%players%",
											PlayerManager.players.size() + ""));
									for (GamePlayer pair : PlayerManager.players) {
										if (pair.p != null) {
											TextComponent a = new TextComponent(Text.addColor("&7- &f" + pair.p.getName()));
											a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/rsw player " + pair.p.getName()));
											a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new ComponentBuilder(
															Text.addColor("&fClick here to inspect &b" + pair.p.getName()))
															.create()));
											p.p.spigot().sendMessage(a);
										}
									}
									return false;
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
									return false;
								}
							case "create":
								if (gp.hasPermission("RealSkywars.Admin")) {
									p.sendMessage(Text.addColor("&cCorrect usage. /rsw create <name> <players>"));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "unregister":
								if (gp.hasPermission("RealSkywars.Admin")) {
									p.sendMessage(Text.addColor("&cCorrect usage: /rsw unregister [map]"));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "setchest":
								if (gp.hasPermission("RealSkywars.Admin")) {
									p.sendMessage(Text.addColor("&cCorrect usage: /rsw setchest [BASIC, NORMAL, OP, CAOS]"));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							default:
								p.sendMessage(LanguageManager.getString(p, TS.CMD_NOT_FOUND, true));
								break;
						}
						break;
					case 2:
						switch (args[0].toLowerCase()) {
							case "mapsetting":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (GameManager.getGame(args[1]) != null) {
										RoomSettings r = new RoomSettings(GameManager.getGame(args[1]), p.p.getUniqueId());
										r.openInventory(p);
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NOMAP_FOUND, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "sendcoins":
								if (gp.hasPermission("RealSkywars.coins")) {
									p.sendMessage(Text.addColor("&c/rsw sendcoins <Players> <Coins>"));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "delkit":
								if (gp.hasPermission("RealSkywars.Admin")) {
									Kit k = KitManager.getKit(args[1]);
									if (k != null) {
										k.deleteKit();
										p.sendMessage(LanguageManager.getString(p, TS.DELETEKIT_DONE, true));
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_KIT_FOUND, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "settier":
								if (gp.hasPermission("RealSkywars.Admin")) {
									if (p.room != null) {
										if (args[1].equalsIgnoreCase(TierType.BASIC.name())) {
											p.room.setTierType(TierType.BASIC);
											p.sendMessage(LanguageManager.getString(p, TS.TIER_SET, true).replace("%chest%",
													LanguageManager.getString(p, TS.CHEST_BASIC, false)));
										} else if (args[1].equalsIgnoreCase(TierType.NORMAL.name())) {
											p.room.setTierType(TierType.NORMAL);
											p.sendMessage(LanguageManager.getString(p, TS.TIER_SET, true).replace("%chest%",
													LanguageManager.getString(p, TS.CHEST_NORMAL, false)));
										} else if (args[1].equalsIgnoreCase(TierType.OP.name())) {
											p.room.setTierType(TierType.OP);
											p.sendMessage(LanguageManager.getString(p, TS.TIER_SET, true).replace("%chest%",
													LanguageManager.getString(p, TS.CHEST_OP, false)));
										} else if (args[1].equalsIgnoreCase(TierType.CAOS.name())) {
											p.room.setTierType(TierType.CAOS);
											p.sendMessage(LanguageManager.getString(p, TS.TIER_SET, true).replace("%chest%",
													LanguageManager.getString(p, TS.CHEST_CAOS, false)));
										} else {
											p.sendMessage(LanguageManager.getString(p, TS.NO_TIER_FOUND, true));
										}
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_MATCH, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "setchest":
								if (gp.hasPermission("RealSkywars.Admin")) {
									ItemStack[] items = IntStream.range(0, 35).boxed().map(p.p.getInventory()::getItem)
											.toArray(ItemStack[]::new);
									ArrayList<ItemStack> f = new ArrayList<ItemStack>();
									for (ItemStack i : items) {
										if (i != null) {
											f.add(i);
										}
									}
									ChestManager.setContents(f, args[1]);
									p.sendMessage(LanguageManager.getString(p, TS.SET_TIER, true));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "addchest":
								if (gp.hasPermission("RealSkywars.Admin")) {
									ItemStack[] items = IntStream.range(0, 35).boxed().map(p.p.getInventory()::getItem)
											.toArray(ItemStack[]::new);
									ArrayList<ItemStack> f = new ArrayList<ItemStack>();
									for (ItemStack i : items) {
										if (i != null) {
											f.add(i);
										}
									}
									ChestManager.addContents(f, args[1]);
									p.sendMessage(LanguageManager.getString(p, TS.ADD_TIER, true));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "player":
								if (gp.hasPermission("RealSkywars.Admin")) {
									p.istate = InteractionState.GUI_PLAYER;
									if (PlayerManager.getPlayer(PlayerManager.searchPlayer(args[1])) != null) {
										PlayerGUI playg = new PlayerGUI(
												PlayerManager.getPlayer(PlayerManager.searchPlayer(args[1])),
												p.p.getUniqueId());
										playg.openInventory(p);
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_PLAYER_FOUND, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "create":
								if (gp.hasPermission("RealSkywars.Admin")) {
									p.sendMessage(Text.addColor(
											"&cCorrect usage. /rsw create <name> <players> or /rsw create <name> <teams> <player-per-team>"));
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "unregister":
								if (gp.hasPermission("RealSkywars.Admin")) {
									String map = args[1];
									if (MapManager.getRegisteredMaps().contains(map)) {
										MapManager.unregisterMap(MapManager.getMap(map));
										p.sendMessage(LanguageManager.getString(p, TS.MAP_UNREGISTERED, true));
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NOMAP_FOUND, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							default:
								p.sendMessage(LanguageManager.getString(p, TS.CMD_NOT_FOUND, true));
								break;
						}
						break;
					case 3:
						switch (args[0].toLowerCase()) {
							case "createkit":
								if (gp.hasPermission("RealSkywars.Admin")) {
									String kitname = args[1];
									Double cost = Double.parseDouble(args[2]);
									Kit k = new Kit(KitManager.getNewID(), kitname, cost, Material.LEATHER_CHESTPLATE,
											p.p.getInventory().getContents(), "RealSkywars.Kit");
									KitSettings m = new KitSettings(k, gp.getUniqueId());
									m.openInventory(p);
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "create":
								if (gp.hasPermission("RealSkywars.Admin")) {
									String mapname = args[1];
									int maxP = Integer.valueOf(args[2]);
									if (Config.file().isConfigurationSection("Config.Lobby") == true) {
										if (MapManager.getRegisteredMaps().contains(mapname)) {
											p.sendMessage(LanguageManager.getString(p, TS.MAP_EXISTS, true));
										} else {
											if (p.setup != null) {
												p.sendMessage(LanguageManager.getString(p, TS.SETUP_NOT_FINISHED, true));
											} else {
												MapManager.setupSolo(p, mapname, maxP);
											}
										}
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.LOBBYLOC_NOT_SET, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								break;
							case "sendcoins":
								if (gp.hasPermission("RealSkywars.coins")) {
									GamePlayer search = PlayerManager.getPlayer(PlayerManager.searchPlayer(args[1]));
									if (search != null) {
										Double t = Double.valueOf(args[2]);
										CurrencyManager c = new CurrencyManager(search, p, t, false);
										if (c.canMakeOperation() == true) {
											c.transferCoins();
										} else {
											p.sendMessage(LanguageManager.getString(p, TS.INSUFICIENT_COINS, true)
													.replace("%coins%", p.coins + ""));
										}
									} else {
										p.sendMessage(LanguageManager.getString(p, TS.NO_PLAYER_FOUND, true));
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.CMD_NOPERM, true));
								}
								return false;
							case "setcoins":
								GamePlayer search = PlayerManager.getPlayer(PlayerManager.searchPlayer(args[1]));
								if (search != null) {
									Double t = Double.valueOf(args[2]);
									CurrencyManager c = new CurrencyManager(search, p, t, true);
									c.setCoins();
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.NO_PLAYER_FOUND, true));
								}
								return false;
							default:
								p.sendMessage(LanguageManager.getString(p, TS.CMD_NOT_FOUND, true));
								break;
						}
						break;
					case 4:
						if (args[0].equals("create")) {
							String mapname = args[1];
							int teams = Integer.parseInt(args[2]);
							int pperteam = Integer.parseInt(args[3]);
							if (Config.file().isConfigurationSection("Config.Lobby")) {
								if (!MapManager.getRegisteredMaps().contains(mapname)) {
									if (p.setup != null) {
										p.sendMessage(LanguageManager.getString(p, TS.SETUP_NOT_FINISHED, true));
									} else {
										MapManager.setupTeams(p, mapname, teams, pperteam);
									}
								} else {
									p.sendMessage(LanguageManager.getString(p, TS.MAP_EXISTS, true));
								}
							} else {
								p.sendMessage(LanguageManager.getString(p, TS.LOBBYLOC_NOT_SET, true));
							}
						}
						break;
					case 0:
					default:
						p.sendMessage(LanguageManager.getString(p, TS.CMD_NOT_FOUND, true));
						prnHelp(p.p);
						break;
				}
			}
        } else {
            RealSkywars.print("Only players can execute this command.");
        }
        return false;

    }

    private void prnHelp(Player p) {
        Text.sendList(p, Arrays.asList("&7&l&m---------------", "&fReal&9Sky&bWars", "&7", "&F/rsw reload",
                "&F/rsw lobby", "&F/rsw setlobby", "&f/rsw join", "&f/rsw leave", "&f/rsw forcestart",
                "&f/rsw create [map name]", "&f/rsw finishsetup", "&f/rsw cancelsetup",
                "&7&l&m---------------"));
    }
}
