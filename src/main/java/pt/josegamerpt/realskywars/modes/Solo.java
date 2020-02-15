package pt.josegamerpt.realskywars.modes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.cages.Cage;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.classes.Team;
import pt.josegamerpt.realskywars.configuration.Config;
import pt.josegamerpt.realskywars.game.Countdown;
import pt.josegamerpt.realskywars.managers.GameManager;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.ArenaCuboid;
import pt.josegamerpt.realskywars.utils.Calhau;
import pt.josegamerpt.realskywars.utils.MathUtils;
import pt.josegamerpt.realskywars.utils.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solo implements GameRoom {

    public int id;
    public String name;
    public int maxPlayers;
    public Enum.GameState state;
    public World world;
    public WorldBorder border;
    public BossBar bossBar;
    public ArenaCuboid arenaCuboid;
    public Location POS1;
    public Location POS2;
    public int borderSize;

    public ArrayList<Cage> cages = new ArrayList<Cage>();
    public ArrayList<GamePlayer> onThisRoom = new ArrayList<GamePlayer>();
    public ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
    public ArrayList<GamePlayer> spectators = new ArrayList<GamePlayer>();
    public ArrayList<Location> chests = new ArrayList<Location>();
    public ArrayList<Location> openedChests = new ArrayList<Location>();
    public ArrayList<Entity> spawnedEntities = new ArrayList<Entity>();
    public ArrayList<Calhau> blockplace = new ArrayList<Calhau>();
    public ArrayList<Calhau> blockbreak = new ArrayList<Calhau>();
    public ArrayList<Integer> votes = new ArrayList<Integer>();
    public ArrayList<GamePlayer> voters = new ArrayList<GamePlayer>();
    public Enum.TierType tierType = Enum.TierType.NORMAL;

    public Countdown timer;
    public int timePassed = 0;
    public Location spectatorLocation;
    public Boolean specEnabled;
    public Boolean instantEnding;
    private HashMap<String, Integer> tasks = new HashMap<String, Integer>();
    // b-1, n-2, o-3, c-4

    public Solo(int i, String nome, World w, Enum.GameState estado, ArrayList<Cage> cages, int maxPlayers,
                Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2) {
        this.id = i;
        this.name = nome;
        this.world = w;
        this.state = estado;
        this.cages = cages;
        this.maxPlayers = maxPlayers;
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;
        this.POS1 = pos1;
        this.POS2 = pos2;
        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border.setSize(this.borderSize);
        Debugger.print("[ARENA - " + this.name + " ID: " + this.id + "] - SETTING BORDER SIZE TO " + this.borderSize);

        votes.add(2);

        bossBar = Bukkit.createBossBar(Text.addColor(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)),
                BarColor.WHITE, BarStyle.SOLID);
    }

    public void saveRoom() {
        GameManager.rooms.add(this);
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getPlayersCount() {
        return this.players.size();
    }

    public ArrayList<GamePlayer> getPlayers() {
        return this.players;
    }

    public int getSpectatorsCount() {
        return this.spectators.size();
    }

    public List<GamePlayer> getSpectators() {
        return this.spectators;
    }

    public int getPlayersInCount() {
        return this.onThisRoom.size();
    }

    public List<GamePlayer> getPlayersIn() {
        return this.onThisRoom;
    }

    public World getWorld() {
        return this.world;
    }

    public void broadcastMessage(String s, Boolean prefix) {
        for (GamePlayer p : this.onThisRoom) {
            if (p.p != null) {
                if (prefix) {
                    p.sendMessage(LanguageManager.getPrefix() + s);
                } else {
                    p.sendMessage(s);
                }
            }
        }
    }

    public void kickPlayers() {
        this.bossBar.removeAll();
        for (GamePlayer p : onThisRoom) {
            for (GamePlayer s : spectators) {
                if (p.p != null) {
                    p.p.showPlayer(RealSkywars.pl, s.p);
                }
            }
            if (p.p != null) {
                p.p.removeMetadata("invencivel", RealSkywars.pl);

                p.sendMessage(LanguageManager.getString(p, Enum.TS.MATCH_LEAVE, true));
                PlayerManager.tpLobby(p);

                p.room = null;
                PlayerManager.giveItems(p.p, PlayerManager.PlayerItems.LOBBY);

                p.state = Enum.PlayerState.LOBBY_OR_NOGAME;
                p.setFlying(false);

                p.saveData();
            }
        }
    }

    public void kickPlayers(String mes) {
        for (GamePlayer p : onThisRoom) {
            p.sendMessage(mes);
            for (GamePlayer s : spectators) {
                if (p.p != null) {
                    p.p.showPlayer(RealSkywars.pl, s.p);
                }
            }
            if (p.p != null) {
                p.p.removeMetadata("invencivel", RealSkywars.pl);

                p.sendMessage(LanguageManager.getString(p, Enum.TS.MATCH_LEAVE, true));
                PlayerManager.tpLobby(p);

                p.room = null;
                PlayerManager.giveItems(p.p, PlayerManager.PlayerItems.LOBBY);

                p.state = Enum.PlayerState.LOBBY_OR_NOGAME;
                p.setFlying(false);

                p.saveData();
            }
        }
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void cancelAllTasks() {

    }

    public Enum.GameState getState() {
        return this.state;
    }

    public void setState(Enum.GameState w) {
        this.state = w;
    }

    public boolean isPlaceHolder() {
        return false;
    }

    public void forceStart() {
        startGameFunction();
    }

    public void cancelTask(String task) {
        if (this.tasks.containsKey(task)) {
            Bukkit.getScheduler().cancelTask(this.tasks.get(task));
        }
    }

    @Override
    public ArrayList<Cage> getCages() {
        return this.cages;
    }

    @Override
    public ArrayList<Team> getTeams() {
        return null;
    }

    @Override
    public int maxMembersTeam() {
        return 999999;
    }

    private void startGameFunction() {
        this.state = Enum.GameState.PLAYING;

        cancelTask("startingcount");

        int timeleft = Config.file().getInt("Config.Maximum-Game-Time");

        int bigger = MathUtils.bigger(votes.stream().mapToInt(i -> i).toArray());
        switch (bigger) {
            case 1:
                this.tierType = Enum.TierType.BASIC;
                break;
            case 3:
                this.tierType = Enum.TierType.OP;
                break;
            case 4:
                this.tierType = Enum.TierType.CAOS;
                break;
            default:
                this.tierType = Enum.TierType.NORMAL;
                break;
        }

        for (GamePlayer p : this.players) {
            if (p.p != null) {
                p.p.getInventory().clear();

                bossBar.addPlayer(p.p);

                for (String s : LanguageManager.getList(p, Enum.TL.ARENA_START)) {
                    if (p.kit != null) {
                        p.sendMessage(variables(s).replace("%kit%", p.kit.name));
                        p.p.getInventory().setContents(p.kit.contents);
                    } else {
                        p.sendMessage(variables(s).replace("%kit%", "None"));
                    }
                }

                p.p.getWorld().getBlockAt(p.p.getLocation().add(0, -1, 0)).setType(Material.AIR);

                p.state = Enum.PlayerState.PLAYING;
            }
        }

        this.timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), timeleft, () -> {
            //
        }, () -> {
            bossBar.setTitle(Text.addColor(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_DEATHMATCH)));
            bossBar.setProgress(0);
            bossBar.setColor(BarColor.RED);

            for (GamePlayer p : this.players) {
                if (p.p != null) {
                    p.p.sendTitle("", Text.addColor(LanguageManager.getString(p, Enum.TS.TITLE_DEATHMATCH, false)), 10, 20,
                            5);
                }
            }

            border.setSize(this.borderSize / 2, 30L);
            border.setCenter(this.arenaCuboid.getCenter());

        }, (t) -> {
            bossBar.setTitle(Text.addColor(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_RUNTIME).replace("%time%",
                    t.getSecondsLeft() + "")));
            Double div = (double) t.getSecondsLeft() / (double) timeleft;
            bossBar.setProgress(div);

            //future events
        });
        timer.scheduleTimer();

        startCountingTime();
    }

    private void startCountingTime() {
        this.tasks.put("timeCounter", Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, new Runnable() {
            @Override
            public void run() {
                timePassed += 1;
            }
        }, 0L, 20L));
    }

    private String variables(String string) {
        return string.replace("%players%", getPlayersCount() + "").replace("%maxplayers%", maxPlayers + "")
                .replace("%chests%", tierType.name().replace("%time%", Config.file().getInt("Config.Time-EndGame") + ""));
    }

    public void removePlayer(GamePlayer p) {
        String lv = variables(Text.addColor(LanguageManager.getString(p, Enum.TS.MATCH_LEAVE, true)));

        switch (p.state) {
            case CAGE:
                p.leaveCage();
                this.players.remove(p);
                break;
            case PLAYING:
                this.players.remove(p);
                checkWin();
                break;
            default:
                for (GamePlayer ws : this.players) {
                    if (ws.p != null) {
                        ws.p.showPlayer(RealSkywars.pl, p.p);
                    }
                }

                p.p.setFlying(false);
                this.spectators.remove(p);
                break;
        }

        for (GamePlayer ws : this.onThisRoom) {
            if (ws.p != null) {
                ws.p.sendMessage(variables(LanguageManager.getString(ws, Enum.TS.PLAYER_LEAVE, true).replace("%player%",
                        p.p.getDisplayName())));
            }
        }

        if (this.state != Enum.GameState.AVAILABLE || this.state != Enum.GameState.STARTING) {
            if (bossBar != null) {
                bossBar.removePlayer(p.p);
            }
        }

        this.onThisRoom.remove(p);

        PlayerManager.tpLobby(p);
        PlayerManager.giveItems(p.p, PlayerManager.PlayerItems.LOBBY);
        p.sendMessage(lv);

        for (PotionEffect effect : p.p.getActivePotionEffects())
            p.p.removePotionEffect(effect.getType());

        p.state = Enum.PlayerState.LOBBY_OR_NOGAME;
        p.room = null;
    }

    @Override
    public Location getSpectatorLocation() {
        return this.spectatorLocation;
    }

    @Override
    public Location getPOS1() {
        return this.POS1;
    }

    @Override
    public Location getPOS2() {
        return this.POS2;
    }

    public ArrayList<GamePlayer> getVoters() {
        return this.voters;
    }

    public ArrayList<Integer> getVoteList() {
        return this.votes;
    }

    public void addPlayer(GamePlayer gp) {
        if (getPlayersCount() == maxPlayers) {
            gp.sendMessage(LanguageManager.getPrefix() + "&cThis room is full");
            return;
        }

        if (state == Enum.GameState.RESETTING) {
            gp.sendMessage(LanguageManager.getPrefix() + "&cYou cant join this room.");
            return;
        }
        if (state == Enum.GameState.FINISHING || state == Enum.GameState.PLAYING
                || state == Enum.GameState.STARTING) {
            if (this.specEnabled) {
                spectateFromExternal(gp);
            } else {
                gp.sendMessage(LanguageManager.getPrefix() + "&cSpectating is not enabled in this room.");
            }
            return;
        }

        gp.room = this;
        gp.state = Enum.PlayerState.CAGE;

        this.onThisRoom.add(gp);
        this.players.add(gp);

        if (gp.p != null) {
            bossBar.addPlayer(gp.p);
            gp.p.setHealth(20);
            ArrayList<String> up = LanguageManager.getList(gp, Enum.TL.TITLE_ROOMJOIN);
            gp.p.sendTitle(up.get(0), up.get(1), 10, 120, 10);
        }

        //cage

        for (Cage c : this.cages) {
            if (c.isEmpty()) {
                c.addPlayer(gp);
                break;
            }
        }

        for (GamePlayer ws : this.onThisRoom) {
            if (gp.p != null) {
                ws.sendMessage(variables(LanguageManager.getString(ws, Enum.TS.PLAYER_JOIN_ARENA, true).replace("%player%",
                        gp.p.getDisplayName())));
            }
        }

        PlayerManager.giveItems(gp.p, PlayerManager.PlayerItems.CAGE);

        if (getPlayersCount() == maxPlayers) {
            this.state = Enum.GameState.STARTING;
            startRoom();
        }
    }

    private void startRoom() {
        Countdown timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"),
                () -> {
                    //
                }, () -> {
            startGameFunction();
            this.tasks.remove("startingcount");
        }, (t) -> {
            if (getPlayersCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
                Bukkit.getScheduler().cancelTask(t.getTaskId());
                for (GamePlayer p : this.players) {
                    p.sendMessage(variables(LanguageManager.getString(p, Enum.TS.ARENA_CANCEL, true)));
                }
                bossBar.setTitle(Text.addColor(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)));
                bossBar.setProgress(0D);
                this.state = Enum.GameState.WAITING;
            } else {
                for (GamePlayer p : this.players) {
                    p.sendMessage(variables(LanguageManager.getString(p, Enum.TS.ARENA_START_COUNTDOWN, true)
                            .replace("%time%", t.getSecondsLeft() + "")));
                }
                bossBar.setTitle(Text.addColor(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_STARTING)
                        .replace("%time%", t.getSecondsLeft() + "")));
                Double div = (double) t.getSecondsLeft()
                        / (double) Config.file().getInt("Config.Time-To-Start");
                bossBar.setProgress(div);
            }
        });

        timer.scheduleTimer();
        this.tasks.put("startingcount", timer.getTaskId());
    }

    private void spectateFromExternal(GamePlayer p) {
        this.onThisRoom.add(p);
        this.spectators.add(p);
        p.room = this;

        for (GamePlayer ws : this.players) {
            if (ws.p != null) {
                ws.p.hidePlayer(RealSkywars.pl, p.p);
            }
        }

        p.state = Enum.PlayerState.EXTERNAL_SPECTATOR;

        p.p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 3));
        p.p.setGameMode(GameMode.SURVIVAL);
        p.p.setHealth(20.0);
        p.p.setFoodLevel(20);

        p.p.teleport(this.spectatorLocation);
        PlayerManager.giveItems(p.p, PlayerManager.PlayerItems.SPECTATOR);

        p.p.setFlying(true);

        p.sendMessage(LanguageManager.getString(p, Enum.TS.MATCH_SPECTATE, true));
    }

    public boolean isSpectatorEnabled() {
        return this.specEnabled;
    }

    public boolean isInstantEndEnabled() {
        return this.instantEnding;
    }

    public Enum.TierType getTierType() {
        return this.tierType;
    }

    public void setTierType(Enum.TierType b) {
        this.tierType = b;
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

    public void resetArena() {
        this.state = Enum.GameState.RESETTING;

        this.players.clear();
        this.spectators.clear();
        this.onThisRoom.clear();
        this.openedChests.clear();
        this.voters.clear();
        this.votes.clear();
        votes.add(2);

        cancelTask("timeCounter");

        this.tasks.clear();
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

        List<Entity> entList = this.world.getEntities();
        for (Entity current : entList) {
            if (current instanceof Item) {
                current.remove();
            }
        }

        border.setSize(this.borderSize);

        this.state = Enum.GameState.AVAILABLE;

        Debugger.print("[ROOM " + this.name + " ID: " + this.id + "] resetted.");
    }

    public void setSpectator(boolean b) {
        this.specEnabled = b;
    }

    public void setInstantEnd(boolean b) {
        this.instantEnding = b;
    }

    public void spectate(GamePlayer p, Location killLoc) {
        p.leaveCage();
        this.players.remove(p);
        this.spectators.add(p);

        p.state = Enum.PlayerState.SPECTATOR;

        p.p.setFoodLevel(20);
        p.p.setHealth(20);

        p.p.teleport(killLoc.add(0, 1, 0));
        PlayerManager.giveItems(p.p, PlayerManager.PlayerItems.SPECTATOR);

        p.setFlying(true);
        sendLog(p);

        checkWin();
    }

    private void sendLog(GamePlayer p) {
        for (String s : Text.addColor(LanguageManager.getList(p, Enum.TL.END_LOG))) {
            p.sendMessage(s.replace("%recvcoins%", p.balanceGame + "").replace("%totalcoins%", p.getSumBalTotal() + "")
                    .replace("%kills%", p.gamekills + ""));
        }
        p.saveData();
    }

    public ArrayList<Location> getOpenedChests() {
        return this.openedChests;
    }

    public void checkWin() {
        if (getPlayersCount() == 1) {
            this.state = Enum.GameState.FINISHING;
            GamePlayer p = getPlayers().get(0);

            bossBar.setTitle(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_END));
            bossBar.setProgress(0);

            if (getPlayers().get(0).p != null) {
                PlayerManager.players.forEach(gamePlayer -> gamePlayer.sendMessage(LanguageManager.getString(gamePlayer, Enum.TS.WINNER_BROADCAST, true).replace("%winner%", p.getName()).replace("%map%", this.name)));
            }

            Countdown timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"),
                    () -> {
                        this.timer.killTask();
                        this.cancelTask("countTime");

                        if (p.p != null) {
                            p.addStatistic(Enum.Statistic.SOLO_WIN, 1);
                            p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                        }

                        sendLog(p);

                        for (GamePlayer g : this.onThisRoom) {
                            if (g.p != null) {
                                g.leaveCage();
                                g.sendMessage(variables(LanguageManager.getString(p, Enum.TS.MATCH_END, true)).replace("%time%", "" + Config.file().getInt("Config.Time-EndGame")));
                                g.p.setMetadata("invencivel", new FixedMetadataValue(RealSkywars.pl, 0));
                                if (p.p != null) {
                                    g.p.sendTitle("",
                                            Text.addColor(LanguageManager.getString(g, Enum.TS.TITLE_WIN, true)
                                                    .replace("%player%", p.p.getDisplayName())),
                                            10, 40, 10);
                                }
                            }
                        }
                    }, () -> {
                kickPlayers();
                resetArena();
            }, (t) -> {
                // if (Players.get(0).p != null) {
                //     firework(Players.get(0));
                // }
            });

            timer.scheduleTimer();
        }
    }

    public Enum.GameType getMode() {
        return Enum.GameType.SOLO;
    }
}
