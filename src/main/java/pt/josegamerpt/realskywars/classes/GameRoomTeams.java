package pt.josegamerpt.realskywars.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.GameType;
import pt.josegamerpt.realskywars.classes.Enum.PlayerState;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.Enum.TSsingle;
import pt.josegamerpt.realskywars.classes.Enum.TierType;
import pt.josegamerpt.realskywars.classes.Enum.TL;
import pt.josegamerpt.realskywars.configuration.Config;
import pt.josegamerpt.realskywars.game.Countdown;
import pt.josegamerpt.realskywars.managers.GameManager;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.MapManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Calhau;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.utils.MathUtils;
import pt.josegamerpt.realskywars.utils.Text;

public class GameRoomTeams implements GameRoom {

	public String Name;
	public GameState State;
	private ArrayList<Team> teams;
	public ArrayList<GamePlayer> GamePlayers = new ArrayList<GamePlayer>();
	public ArrayList<GamePlayer> Spectators = new ArrayList<GamePlayer>();
	public int maxPlayers;
	public ArrayList<Location> openedChests = new ArrayList<Location>();
	public World worldMap;
	public Location spectator;
	public Boolean specEnabled;
	public WorldBorder border;
	public Double bordSize;
	public BossBar gameTimer;
	public Boolean dragonEnabled;
	public Countdown untilEnd;
	public GameType gameType = GameType.TEAMS;
	public List<Entity> spawnedEntities = new ArrayList<Entity>();
	private HashMap<String, Integer> counters = new HashMap<String, Integer>();
	public int timePassed = 0;
	public Boolean placeholder = false;

	public ArrayList<Calhau> blockplace = new ArrayList<Calhau>();
	public ArrayList<Calhau> blockbreak = new ArrayList<Calhau>();
	public TierType tierType = TierType.NORMAL;
	public ArrayList<Integer> votes = new ArrayList<Integer>();
	public ArrayList<GamePlayer> voters = new ArrayList<GamePlayer>();
	// b-1, n-2, o-3, c-4

	public GameRoomTeams(String nome, GameState estado, int m, World w, Location sloc, Boolean e, Boolean drEn,
			Double borderSize, ArrayList<Team> arrayList) {
		this.Name = nome;
		this.State = estado;
		this.maxPlayers = m;
		this.worldMap = w;
		this.spectator = sloc;
		this.specEnabled = e;
		this.dragonEnabled = drEn;
		this.teams = arrayList;
		border = w.getWorldBorder();
		border.setCenter(w.getSpawnLocation());
		border.setSize(borderSize);
		bordSize = borderSize;
		Debugger.print("[ARENA]" + Name + " - SETTING BORDER SIZE TO " + borderSize);

		votes.add(2);

		gameTimer = Bukkit.createBossBar(Text.addColor(LanguageManager.getString(TSsingle.BOSSBAR_ARENA_WAIT)),
				BarColor.WHITE, BarStyle.SOLID);

	}

	public void broadcastMessage(String s) {
		for (Team t : teams) {
			for (GamePlayer p : t.members) {
				p.sendMessage(s);
			}
		}
		for (GamePlayer p : Spectators) {
			if (p.p != null) {
				p.sendMessage(s);
			}
		}
	}

	public void saveRoom() {
		GameManager.rooms.add(this);
	}

	public void resetArena() {
		this.State = GameState.RESETTING;

		this.teams.clear();
		this.Spectators.clear();
		this.GamePlayers.clear();
		this.openedChests.clear();
		this.voters.clear();
		this.votes.clear();
		votes.add(2);

		if (State == GameState.PLAYING || State == GameState.FINISHING) {
			Bukkit.getScheduler().cancelTask(counters.get("countTime"));
		}

		counters.clear();
		timePassed = 0;

		border.reset();

		for (Entity e : spawnedEntities) {
			if (e != null) {
				e.remove();
			}
		}

		for (Calhau blockData : blockplace) {
			Block block = blockData.getLocation().getWorld().getBlockAt(blockData.getLocation());
			block.setType(Material.AIR);
		}

		for (Calhau broke : blockbreak) {
			Block block = broke.getLocation().getWorld().getBlockAt(broke.getLocation());
			block.setType(broke.getMaterial());
			block.setBlockData(broke.getLocation().getBlock().getBlockData());
		}

		blockplace.clear();
		blockbreak.clear();

		List<Entity> entList = worldMap.getEntities();
		for (Entity current : entList) {
			if (current instanceof Item) {
				current.remove();
			}
		}

		teams.addAll(MapManager.getTeams(Name));

		// Debugger.printValue("[GAMEROOM-RESETVALUES] CAGES" + Cages.toString());

		border.setCenter(worldMap.getSpawnLocation());
		border.setSize(bordSize);

		this.State = GameState.AVAILABLE;
	}

	public void addPlayer(GamePlayer gp) {
		if (State == GameState.RESETTING) {
			gp.sendMessage("&cYou cant join this map.");
			return;
		}
		if (State == GameState.FINISHING || State == GameState.PLAYING || State == GameState.STARTING) {
			spectateFromExternal(gp);
			return;
		}

		gp.room = GameManager.getGame(Name);
		gp.state = PlayerState.CAGE;
		gp.save();

		GamePlayers.add(gp);
		addPlayerToTeam(gp);

		if (gp.p != null) {
			gameTimer.addPlayer(gp.p);
			gp.p.setHealth(20);
		}

		for (Team t : teams) {
			for (GamePlayer ws : t.members) {
				if (ws.p != null) {
					if (gp.p != null)
						ws.p.sendMessage(variables(LanguageManager.getString(ws, TS.PLAYER_JOIN_ARENA, true)
								.replace("%player%", gp.p.getDisplayName())));
				}
			}
		}

		if (getCurrentPlayers() < maxPlayers) {
			this.State = GameState.WAITING;
		} else {
			this.State = GameState.STARTING;
			startRoom();
		}

		if (gp.p != null) {
			PlayerManager.giveItems(gp.p, 2);
		}
	}

	private void addPlayerToTeam(GamePlayer gp) {
		for (Team t : teams) {
			if (t.isTeamFull() == false) {
				t.addPlayer(gp);
				return;
			}
		}
	}

	private String variables(String string) {
		return string.replace("%players%", getCurrentPlayers() + "").replace("%maxplayers%", maxPlayers + "")
				.replace("%chests%", tierType.name());
	}

	private void startGameFunction() {
		this.State = GameState.PLAYING;

		if (counters.containsKey("startingcount")) {
			Bukkit.getScheduler().cancelTask(counters.get("startingcount"));
		}

		int timeleft = Config.file().getInt("Config.Maximum-Game-Time");

		int bigger = MathUtils.bigger(votes.stream().mapToInt(i -> i).toArray());
		if (bigger == 1) {
			tierType = TierType.BASIC;
		}
		if (bigger == 2) {
			tierType = TierType.NORMAL;
		}
		if (bigger == 3) {
			tierType = TierType.OP;
		}
		if (bigger == 4) {
			tierType = TierType.CAOS;
		}

		for (Team t : teams) {
			t.openCage();
			for (GamePlayer p : t.members) {
				if (p.p != null) {
					p.p.getInventory().clear();
					gameTimer.addPlayer(p.p);

					for (String s : LanguageManager.getList(p, TL.ARENA_START)) {
						if (p.selectedKit != null) {
							p.sendMessage(variables(s).replace("%kit%", p.selectedKit.name));
							p.p.getInventory().setContents(p.selectedKit.contents);
						} else {
							p.sendMessage(variables(s).replace("%kit%", "None"));
						}
					}

					p.state = PlayerState.PLAYING;
					p.save();
				}
			}
		}

		untilEnd = new Countdown(RealSkywars.getPlugin(RealSkywars.class), timeleft, () -> {
			//
		}, () -> {
			gameTimer.setTitle(Text.addColor(LanguageManager.getString(TSsingle.BOSSBAR_ARENA_DEATHMATCH)));
			gameTimer.setProgress(0);
			gameTimer.setColor(BarColor.RED);

			for (Team t : teams) {
				for (GamePlayer p : t.members) {
					if (p.p != null) {
						p.p.sendTitle("", Text.addColor(LanguageManager.getString(p, TS.TITLE_DEATHMATCH, false)), 10,
								20, 30);
					}
				}
			}

			border.setSize(bordSize / 2, 30L);
			border.setCenter(worldMap.getSpawnLocation());

		}, (t) -> {
			gameTimer.setTitle(Text.addColor(LanguageManager.getString(TSsingle.BOSSBAR_ARENA_RUNTIME).replace("%time%",
					t.getSecondsLeft() + "")));
			Double div = (double) t.getSecondsLeft() / (double) timeleft;
			gameTimer.setProgress(div);

			for (Team te : teams) {
				Debugger.print(te.getName() + " |NAMES " + te.getNames() + " |PLAYING " + te.playing + " |ELIMINATED "
						+ te.eliminated);
			}
		});
		untilEnd.scheduleTimer();

		startTimer();

	}

	private void startTimer() {
		int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, new Runnable() {
			@Override
			public void run() {
				timePassed += 1;
			}
		}, 0L, 20L);
		counters.put("countTime", i);
	}

	public void startRoom() {
		Countdown timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"),
				() -> {
					//
				}, () -> {
					startGameFunction();
					counters.remove("startingcount");
				}, (t) -> {
					if (getCurrentPlayers() < Config.file().getInt("Config.Min-Players-ToStart")) {
						Bukkit.getScheduler().cancelTask(t.getTaskId());
						for (Team te : teams) {
							for (GamePlayer p : te.members) {
								p.sendMessage(variables(LanguageManager.getString(p, TS.ARENA_CANCEL, true)));
							}
						}
						gameTimer.setTitle(Text.addColor(LanguageManager.getString(TSsingle.BOSSBAR_ARENA_WAIT)));
						gameTimer.setProgress(0D);
						this.State = GameState.WAITING;
					} else {
						for (Team te : teams) {
							for (GamePlayer p : te.members) {
								p.sendMessage(variables(LanguageManager.getString(p, TS.ARENA_START_COUNTDOWN, true)
										.replace("%time%", t.getSecondsLeft() + "")));
							}
						}
						gameTimer.setTitle(Text.addColor(LanguageManager.getString(TSsingle.BOSSBAR_ARENA_STARTING)
								.replace("%time%", t.getSecondsLeft() + "")));
						Double div = (double) t.getSecondsLeft()
								/ (double) Config.file().getInt("Config.Time-To-Start");
						gameTimer.setProgress(div);
					}
				});

		timer.scheduleTimer();
		counters.put("startingcount", timer.getTaskId());
	}

	public void removePlayer(GamePlayer p) {
		if (p.state == PlayerState.EXTERNAL_SPECTATOR) {

			Spectators.remove(p);
			PlayerManager.giveItems(p.p, 0);
			p.p.setAllowFlight(false);
			p.p.setFlying(false);

			p.sendMessage(LanguageManager.getString(p, TS.MATCH_LEAVE, true));
			PlayerManager.tpLobby(p);

			for (Team t : teams) {
				for (GamePlayer ws : t.members) {
					if (ws.p != null) {
						ws.p.showPlayer(RealSkywars.pl, p.p);
					}
				}
			}

			p.state = PlayerState.LOBBY_OR_NOGAME;

			return;
		}
		if (p.state == PlayerState.CAGE) {
			p.team.removePlayer(p);
			p.sendMessage(LanguageManager.getString(p, TS.MATCH_LEAVE, true));
			PlayerManager.tpLobby(p);
			PlayerManager.giveItems(p.p, 0);
		} else {
			p.sendMessage(LanguageManager.getString(p, TS.MATCH_LEAVE, true));
			PlayerManager.tpLobby(p);
			Spectators.remove(p);
			PlayerManager.giveItems(p.p, 0);

			gameTimer.removePlayer(p.p);
			this.GamePlayers.remove(p);

			p.cageLoc = null;
			p.room = null;
			p.p.setFlying(false);
		}

		for (Team t : teams) {
			for (GamePlayer ws : t.members) {
				if (ws.p != null) {
					ws.p.sendMessage(variables(LanguageManager.getString(ws, TS.PLAYER_LEAVE, true).replace("%player%",
							p.p.getDisplayName())));
				}
			}
		}

		if (this.State != GameState.AVAILABLE || this.State != GameState.STARTING) {
			if (gameTimer != null) {
				gameTimer.removePlayer(p.p);
			}
		}
	}

	public void kickPlayers() {
		for (GamePlayer p : GamePlayers) {
			if (p.p != null) {

				p.p.removeMetadata("invencivel", RealSkywars.pl);

				p.sendMessage(LanguageManager.getString(p, TS.MATCH_LEAVE, true));
				PlayerManager.tpLobby(p);
				p.room = null;
				p.cageLoc = null;
				PlayerManager.giveItems(p.p, 0);

				p.state = PlayerState.LOBBY_OR_NOGAME;
				p.p.setAllowFlight(false);
				p.p.setFlying(false);

				p.saveData();

			}
		}

		if (gameTimer != null) {
			gameTimer.removeAll();
		}
	}

	public void checkWin() {
		if (aliveTeams() == 1) {
			this.State = GameState.FINISHING;

			gameTimer.setTitle(LanguageManager.getString(TSsingle.BOSSBAR_ARENA_END));
			gameTimer.setProgress(1);

			Team winner = getWinnerTeam();

			Countdown timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"),
					() -> {
						untilEnd.killTask();

						for (GamePlayer p : winner.members) {
							if (p.p != null) {
								if (dragonEnabled == true) {
									rideDragon(p.p);
								}
								p.addWin(1);
							}
						}

						for (GamePlayer p : GamePlayers) {
							if (p.p != null) {
								p.sendMessage(variables(LanguageManager.getString(p, TS.MATCH_END, true)));
								p.p.setMetadata("invencivel", new FixedMetadataValue(RealSkywars.pl, 0));
								p.p.sendTitle("", Text.addColor(LanguageManager.getString(p, TS.TITLE_WIN, true)
										.replace("%player%", winner.getName())), 10, 100, 10);
								sendLog(p);
							}
						}
					}, () -> {
						kickPlayers();
						resetArena();
					}, (t) -> {
						for (Team ts : teams) {
							for (GamePlayer p : ts.members) {
								firework(p);
							}
						}
					});

			timer.scheduleTimer();
			
			Bukkit.broadcastMessage(
					winner.getName() + " (" + winner.getNames() + ")" + " won on the map " + Name);
		}
	}

	private Team getWinnerTeam() {
		Team ret = null;
		for (Team t : teams) {
			if (t.eliminated == false) {
				ret = t;
			}
		}
		return ret;
	}

	private int aliveTeams() {
		int alive = 0;
		for (Team t : teams) {
			if (t.eliminated == false) {
				alive++;
			}
		}
		return alive;
	}

	private void rideDragon(Player p) {
		if (p != null) {
			EnderDragon d = worldMap.spawn(worldMap.getSpawnLocation(), EnderDragon.class);
			d.setCustomName("Fofinho");
			d.setPhase(Phase.CIRCLING);
			d.addPassenger(p);
			spawnedEntities.add(d);
		}
	}

	private void firework(GamePlayer gamePlayer) {
		if (gamePlayer.p != null) {
			final Location location = gamePlayer.p.getLocation();
			final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
			final FireworkMeta fireworkMeta = firework.getFireworkMeta();
			fireworkMeta.addEffect(FireworkEffect.builder().flicker(new Random().nextBoolean()).withColor(Color.ORANGE)
					.withColor(Color.GREEN).withFade(Color.RED).with(FireworkEffect.Type.BALL_LARGE)
					.trail(new Random().nextBoolean()).build());
			fireworkMeta.setPower(0);
			firework.setFireworkMeta(fireworkMeta);
		}
	}

	private void spectateFromExternal(GamePlayer p) {
		if (specEnabled == true) {

			GamePlayers.add(p);

			p.room = GameManager.getGame(Name);

			for (Team t : teams) {
				for (GamePlayer ws : t.members) {
					if (ws.p != null) {
						ws.p.hidePlayer(RealSkywars.pl, p.p);
					}
				}
			}

			Spectators.add(p);

			p.state = PlayerState.EXTERNAL_SPECTATOR;
			p.save();

			p.p.setGameMode(GameMode.SURVIVAL);
			p.p.setHealth(20.0);
			p.p.setFoodLevel(20);

			p.p.teleport(spectator);
			PlayerManager.giveItems(p.p, 1);

			p.p.setAllowFlight(true);
			p.p.setFlying(true);

			p.sendMessage(LanguageManager.getString(p, TS.MATCH_SPECTATE, true));
		} else {
			p.sendMessage("&6Spectate isnt allowed on this arena.");
		}
	}

	public void spectate(GamePlayer p) {
		p.team.removePlayer(p);
		if (specEnabled == true) {
			Spectators.add(p);

			p.state = PlayerState.SPECTATOR;
			p.save();

			p.p.setFoodLevel(20);

			p.p.teleport(spectator);
			PlayerManager.giveItems(p.p, 1);

			p.p.setAllowFlight(true);
			p.p.setFlying(true);
		} else {
			p.cageLoc = null;
			p.room = null;
			p.team = null;
			p.state = PlayerState.SPECTATOR;
			p.save();

			p.p.setFoodLevel(20);

			p.sendMessage(LanguageManager.getString(p, TS.MATCH_LEAVE, true));
			PlayerManager.tpLobby(p);
			PlayerManager.giveItems(p.p, 0);

		}
	}

	private void sendLog(GamePlayer p) {
		for (String s : Text.addColor(LanguageManager.getList(p, TL.END_LOG))) {
			p.sendMessage(s.replace("%recvcoins%", p.balanceGame + "").replace("%totalcoins%", p.getSumBalTotal() + "")
					.replace("%kills%", p.GameKills + ""));
		}
		p.saveData();
	}

	public void forceStart() {
		startGameFunction();
	}

	public int getCurrentPlayers() {
		int alive = 0;
		for (Team t : teams) {
			alive = alive + t.members.size();
		}
		return alive;
	}

	//methods

	public String getName() {
		return this.Name;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public World getWorld() {
		return this.worldMap;
	}

	public int getCurrentSpectators() {
		return this.Spectators.size();
	}


	public Enum.GameState getState() {
		return this.State;
	}


	public boolean isPlaceHolder() {
		return this.placeholder;
	}

	public ArrayList<GamePlayer> getPlayerList() {
		return this.GamePlayers;
	}

	public void setTierType(Enum.TierType b) {
		this.tierType = b;
	}


	public ArrayList<GamePlayer> getVoters() {
		return this.voters;
	}


	public ArrayList<Integer> getVoteList() {
		return this.votes;
	}

	public boolean isSpectatorEnabled() {
		return this.specEnabled;
	}


	public boolean isDragonEnabled() {
		return this.dragonEnabled;
	}


	public Enum.TierType getTierType() {
		return this.tierType;
	}


	public ArrayList<Calhau> getBlocksPlaced() {
		return this.blockplace;
	}


	public ArrayList<Calhau> getBlocksDestroyed() {
		return this.blockbreak;
	}


	public int getTimePassed() {
		return this.timePassed;
	}

	public void setState(Enum.GameState w) {
		this.State = w;
	}


	public void setSpectator(boolean b) {
		this.specEnabled = b;
	}


	public void setDragon(boolean b) {
		this.dragonEnabled = b;
	}

	public ArrayList<Location> getOpenChests() {
		return this.openedChests;
	}


	public ArrayList<GamePlayer> getGamePlayers() {
		return this.getGamePlayers();
	}

	public Enum.GameType getMode() {
		return this.gameType;
	}
}
