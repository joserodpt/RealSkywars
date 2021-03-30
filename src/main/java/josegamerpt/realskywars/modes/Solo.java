package josegamerpt.realskywars.modes;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.SWChest;
import josegamerpt.realskywars.classes.SWWorld;
import josegamerpt.realskywars.classes.Team;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import josegamerpt.realskywars.utils.Demolition;
import josegamerpt.realskywars.utils.MathUtils;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Solo implements SWGameMode {

    private SWWorld world;
    private final ArenaCuboid arenaCuboid;
    private ArrayList<SWChest> chests;

    private final int id;
    private final String name;
    private final int maxPlayers;
    private final int borderSize;
    private final ArrayList<RSWPlayer> inRoom = new ArrayList<>();
    private final ArrayList<Integer> votes = new ArrayList<>();
    private final ArrayList<UUID> voters = new ArrayList<>();
    private final Location spectatorLocation;
    private final ArrayList<Cage> cages;
    private WorldBorder border;
    private BossBar bossBar;
    private Enum.GameState state;
    private Enum.TierType tierType = Enum.TierType.NORMAL;
    private int timePassed = 0;
    private Boolean specEnabled;
    private Boolean instantEnding;

    private Countdown startTimer;
    private Countdown startRoomTimer;
    private Countdown winTimer;
    private BukkitTask timeCouterTask;

    // b-1, n-2, o-3, c-4

    public Solo(int i, String nome, World w, Enum.GameState estado, ArrayList<Cage> cages, int maxPlayers,
                Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2, ArrayList<SWChest> chests) {
        this.id = i;
        this.name = nome;
        this.world = new SWWorld(this, w);

        this.cages = cages;
        this.state = estado;
        this.maxPlayers = maxPlayers;
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;
        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.border.setSize(this.borderSize);
        this.chests = chests;

        this.votes.add(2);

        this.bossBar = Bukkit.createBossBar(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)),
                BarColor.WHITE, BarStyle.SOLID);
    }

    public void saveRoom() {
        GameManager.addRoom(this);
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getPlayersCount() {
        return getPlayers().size();
    }

    public ArrayList<RSWPlayer> getPlayers() {
        ArrayList<RSWPlayer> players = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.PLAYING || rswPlayer.getState() == RSWPlayer.PlayerState.CAGE)
                players.add(rswPlayer);
        }
        return players;
    }

    public int getSpectatorsCount() {
        return getSpectators().size();
    }

    public List<RSWPlayer> getSpectators() {
        ArrayList<RSWPlayer> players = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.SPECTATOR || rswPlayer.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR)
                players.add(rswPlayer);
        }
        return players;
    }

    @Override
    public World getWorld() {
        return this.world.getWorld();
    }

    public void kickPlayers(String msg) {
        ArrayList<RSWPlayer> tmp = new ArrayList<>(this.inRoom);

        for (RSWPlayer p : tmp) {
            if (msg != null) {
                p.sendMessage(Text.color(msg));
            }
            removePlayer(p);
        }
    }

    public Enum.GameType getMode() {
        return Enum.GameType.SOLO;
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

    public String forceStart(RSWPlayer p) {
        if (this.getPlayersCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
            return LanguageManager.getString(p, Enum.TS.CMD_CANT_FORCESTART, true);
        } else {
            switch (this.state) {
                case PLAYING:
                case FINISHING:
                    return LanguageManager.getString(p, Enum.TS.ALREADY_STARTED, true);
                default:
                    startGameFunction();
                    return LanguageManager.getString(p, Enum.TS.CMD_MATCH_FORCESTART, true);
            }
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
        return -17;
    }

    @Override
    public void clear() {
        this.world.clear();
    }

    @Override
    public void reset() {
        this.state = Enum.GameState.RESETTING;

        this.kickPlayers(LanguageManager.getString(new RSWPlayer(false), Enum.TS.ARENA_RESET, true));
        this.resetArena();
    }

    @Override
    public ArenaCuboid getArena() {
        return this.arenaCuboid;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getBorderSize() {
        return this.borderSize;
    }

    @Override
    public void addVote(UUID u, int i) {
        this.voters.add(u);
        this.votes.add(i);
    }

    @Override
    public ArrayList<SWChest> getChests() {
        return this.chests;
    }

    @Override
    public SWChest getChest(Location location) {
        for (SWChest chest : this.chests) {
            if (location.equals(chest.getLocation()))
            {
                return chest;
            }
        }
        return null;
    }

    private void startGameFunction() {
        this.state = Enum.GameState.PLAYING;

        this.startRoomTimer.killTask();

        int timeleft = Config.file().getInt("Config.Maximum-Game-Time");

        int bigger = MathUtils.bigger(votes.stream().mapToInt(i -> i).toArray());
        switch (bigger) {
            case 1:
                this.setTierType(Enum.TierType.BASIC, true);
                break;
            case 3:
                this.setTierType(Enum.TierType.OP, true);
                break;
            case 4:
                this.setTierType(Enum.TierType.CAOS, true);
                break;
            default:
                this.setTierType(Enum.TierType.NORMAL, true);
                break;
        }

        for (RSWPlayer p : this.getPlayers()) {
            if (p.getPlayer() != null) {
                p.getInventory().clear();

                this.bossBar.addPlayer(p.getPlayer());

                //start msg
                for (String s : Text.color(LanguageManager.getList(p, Enum.TL.ARENA_START))) {
                    p.sendCenterMessage(s.replace("%chests%", this.tierType.name())
                            .replace("%kit%", p.getKit().getName()));
                }

                if (p.hasKit()) {
                    p.getKit().give(p);
                }

                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.PLAYING);
                p.getCage().open();
            }
        }

        this.startTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), timeleft, () -> {
            //
        }, () -> {
            this.bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_DEATHMATCH)));
            this.bossBar.setProgress(0);
            this.bossBar.setColor(BarColor.RED);

            this.getPlayers().forEach(rswPlayer -> rswPlayer.sendTitle("", Text.color(LanguageManager.getString(rswPlayer, Enum.TS.TITLE_DEATHMATCH, false)), 10, 20,
                    5));

            this.border.setSize(this.borderSize / 2, 30L);
            this.border.setCenter(this.arenaCuboid.getCenter());

        }, (t) -> {
            this.bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_RUNTIME).replace("%time%",
                    Text.formatSeconds(t.getSecondsLeft()) + "")));
            double div = (double) t.getSecondsLeft() / (double) timeleft;
            this.bossBar.setProgress(div);

            //future events
        });
        this.startTimer.scheduleTimer();

        this.timeCouterTask = new BukkitRunnable() {
            public void run() {
                timePassed += 1;
            }
        }.runTaskTimerAsynchronously(RealSkywars.getPlugin(), 0, 20); // Spelled Async wrong and I know it, deal with it haha
    }

    public void removePlayer(RSWPlayer p) {
        if (this.bossBar != null && !p.isBot()) {
            this.bossBar.removePlayer(p.getPlayer());
        }

        p.setInvincible(false);
        p.sendMessage(Text.color(LanguageManager.getString(p, Enum.TS.MATCH_LEAVE, true)));

        switch (p.getState()) {
            case PLAYING:
            case CAGE:
            case EXTERNAL_SPECTATOR:
            case SPECTATOR:
                GameManager.tpToLobby(p);
                break;
        }
        PlayerManager.giveItems(p.getPlayer(), PlayerManager.PlayerItems.LOBBY);

        p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.LOBBY_OR_NOGAME);
        p.setFlying(false);
        p.heal();

        if (p.hasKit())
        {
            p.getKit().cancelTasks();
        }

        this.inRoom.remove(p);
        if (p.hasCage()) {
            p.getCage().removePlayer(p);
        }
        p.setRoom(null);

        //update tab
        if (!p.isBot()) {
            RSWPlayer.RoomTAB rt = p.getTab();
            rt.reset();
            rt.updateRoomTAB();
        }
        for (RSWPlayer player : this.getPlayers()) {
            if (!player.isBot()) {
                RSWPlayer.RoomTAB rt = player.getTab();
                rt.clear();
                List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                rt.add(players);
                rt.updateRoomTAB();
            }
        }

        if (this.state == Enum.GameState.PLAYING || this.state == Enum.GameState.FINISHING) {
            checkWin();
        }

    }

    @Override
    public Location getSpectatorLocation() {
        return new Location(this.world.getWorld(), this.spectatorLocation.getBlockX(), this.spectatorLocation.getBlockY(), this.spectatorLocation.getBlockZ());
    }

    @Override
    public Location getPOS1() {
        return this.arenaCuboid.getPOS1();
    }

    @Override
    public Location getPOS2() {
        return this.arenaCuboid.getPOS2();
    }

    @Override
    public ArrayList<UUID> getVoters() {
        return this.voters;
    }

    public ArrayList<Integer> getVoteList() {
        return this.votes;
    }

    public void addPlayer(RSWPlayer p) {
        if (this.state == Enum.GameState.RESETTING) {
            p.sendMessage(LanguageManager.getPrefix() + "&cYou cant join this room.");
            return;
        }
        if (this.state == Enum.GameState.FINISHING || this.state == Enum.GameState.PLAYING
                || this.state == Enum.GameState.STARTING) {
            if (this.specEnabled) {
                spectate(p, SpectateType.EXTERNAL, null);
            } else {
                p.sendMessage(LanguageManager.getPrefix() + "&cSpectating is not enabled in this room.");
            }
            return;
        }
        if (getPlayersCount() == this.maxPlayers) {
            p.sendMessage(LanguageManager.getPrefix() + "&cThis room is full");
            return;
        }

        p.setRoom(this);
        p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

        this.inRoom.add(p);

        if (p.getPlayer() != null) {
            this.bossBar.addPlayer(p.getPlayer());
            p.heal();
            ArrayList<String> up = LanguageManager.getList(p, Enum.TL.TITLE_ROOMJOIN);
            p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
        }

        //cage

        for (Cage c : this.cages) {
            if (c.isEmpty() && p.getPlayer() != null) {
                c.addPlayer(p);
                break;
            }
        }

        for (RSWPlayer ws : this.inRoom) {
            ws.sendMessage(LanguageManager.getString(ws, Enum.TS.PLAYER_JOIN_ARENA, true).replace("%player%",
                    p.getDisplayName()).replace("%players%", getPlayersCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
        }

        PlayerManager.giveItems(p.getPlayer(), PlayerManager.PlayerItems.CAGE);

        //update tab
        if (!p.isBot()) {
            for (RSWPlayer player : this.getPlayers()) {
                if (!player.isBot()) {
                    RSWPlayer.RoomTAB rt = player.getTab();
                    List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                    rt.clear();
                    rt.add(players);
                    rt.updateRoomTAB();
                }
            }
        }

        if (getPlayersCount() == Config.file().getInt("Config.Min-Players-ToStart")) {
            startRoom();
        }

    }

    private void startRoom() {
        this.startRoomTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"),
                () -> {
                    //
                }, this::startGameFunction, (t) -> {
            if (getPlayersCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
                Bukkit.getScheduler().cancelTask(t.getTaskId());
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.ARENA_CANCEL, true));
                }
                this.bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)));
                this.bossBar.setProgress(0D);
                this.state = Enum.GameState.WAITING;
            } else {
                this.state = Enum.GameState.STARTING;
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.ARENA_START_COUNTDOWN, true)
                            .replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + ""));
                }
                this.bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_STARTING)
                        .replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + "")));
                double div = (double) t.getSecondsLeft()
                        / (double) Config.file().getInt("Config.Time-To-Start");
                this.bossBar.setProgress(div);
            }
        });

        this.startRoomTimer.scheduleTimer();
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

    public void setTierType(Enum.TierType b, Boolean updateChests) {
        this.tierType = b;
        if (updateChests) {
            this.chests.forEach(swChest -> swChest.setLoot(RealSkywars.getChestManager().getChest(this.tierType, swChest.isMiddle()), RealSkywars.getChestManager().getMaxItems(this.tierType, swChest.isMiddle())));
        }
    }

    public int getTimePassed() {
        return this.timePassed;
    }

    public void resetArena() {
        this.state = Enum.GameState.RESETTING;
        this.world.resetWorld();

        this.inRoom.clear();
        this.voters.clear();
        this.votes.clear();
        this.votes.add(2);
        this.bossBar = Bukkit.createBossBar(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)),
                BarColor.WHITE, BarStyle.SOLID);

        this.chests.forEach(SWChest::clear);

        if (this.timeCouterTask != null) {
            this.timeCouterTask.cancel();
        }
        if (this.startTimer != null) {
            this.startTimer.killTask();
        }
        if (this.winTimer != null) {
            this.winTimer.killTask();
        }
        if (this.startRoomTimer != null) {
            this.startRoomTimer.killTask();
        }
        this.timePassed = 0;
    }

    public void setSpectator(boolean b) {
        this.specEnabled = b;
    }

    public void setInstantEnd(boolean b) {
        this.instantEnding = b;
    }

    public void spectate(RSWPlayer p, SpectateType st, Location killLoc) {
        p.setInvincible(true);
        p.setFlying(true);
        PlayerManager.giveItems(p.getPlayer(), PlayerManager.PlayerItems.SPECTATOR);

        switch (st) {
            case GAME:
                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.SPECTATOR);
                p.heal();
                p.teleport(killLoc.add(0, 1, 0));

                //update tab
                if (!p.isBot()) {
                    for (RSWPlayer rswPlayer : this.inRoom) {
                        if (!rswPlayer.isBot()) {
                            RSWPlayer.RoomTAB rt = rswPlayer.getTab();
                            rt.remove(p.getPlayer());
                            rt.updateRoomTAB();
                        }
                    }
                }

                if (p.hasKit())
                {
                    p.getKit().cancelTasks();
                }

                //laser to cage
                new Demolition(this.getSpectatorLocation(), p.getCage(), 5, 3).start(RealSkywars.getPlugin());

                sendLog(p);
                checkWin();
                break;
            case EXTERNAL:
                this.inRoom.add(p);
                p.setRoom(this);

                if (p.getPlayer() != null) {
                    this.bossBar.addPlayer(p.getPlayer());
                }
                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.EXTERNAL_SPECTATOR);
                p.teleport(this.getSpectatorLocation());
                p.getPlayer().setGameMode(GameMode.SURVIVAL);
                p.heal();

                //update tab
                if (!p.isBot()) {
                    for (RSWPlayer rswPlayer : this.inRoom) {
                        if (!rswPlayer.isBot()) {
                            RSWPlayer.RoomTAB rt = rswPlayer.getTab();
                            List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                            rt.clear();
                            rt.add(players);
                            rt.updateRoomTAB();
                        }
                    }
                }

                p.sendMessage(LanguageManager.getString(p, Enum.TS.MATCH_SPECTATE, true));
                break;
        }
    }

    private void sendLog(RSWPlayer p) {
        if (p.getPlayer() != null) {
            for (String s : Text.color(LanguageManager.getList(p, Enum.TL.END_LOG))) {
                p.sendCenterMessage(s.replace("%recvcoins%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_BALANCE) + "")
                        .replace("%totalcoins%", p.getGameBalance() + "")
                        .replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS) + ""));
            }
            p.saveData();
        }
    }

    //LISTENER

    public void checkWin() {
        if (getPlayersCount() == 1 && this.state != Enum.GameState.FINISHING) {
            this.state = Enum.GameState.FINISHING;
            RSWPlayer p = getPlayers().get(0);
            p.setInvincible(true);

            this.startTimer.killTask();
            this.timeCouterTask.cancel();

            this.bossBar.setTitle(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_END));
            this.bossBar.setProgress(0);
            this.bossBar.setColor(BarColor.BLUE);

            PlayerManager.getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(LanguageManager.getString(gamePlayer, Enum.TS.WINNER_BROADCAST, true).replace("%winner%", p.getDisplayName()).replace("%map%", this.name)));

            this.winTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"),
                    () -> {
                        if (p.getPlayer() != null) {
                            p.addStatistic(Enum.Statistic.SOLO_WIN, 1);
                            p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                        }

                        for (RSWPlayer g : this.inRoom) {
                            g.delCage();
                            g.sendMessage(LanguageManager.getString(p, Enum.TS.MATCH_END, true).replace("%time%", "" + Text.formatSeconds(Config.file().getInt("Config.Time-EndGame"))));
                        }
                    }, () -> {
                this.bossBar.removeAll();
                this.sendLog(p);
                this.kickPlayers(null);
                this.resetArena();
            }, (t) -> {
                // if (Players.get(0).p != null) {
                //     firework(Players.get(0));
                // }
                double div = (double) t.getSecondsLeft() / (double) Config.file().getInt("Config.Time-EndGame");
                if (div <= 1 && div >= 0) {
                    bossBar.setProgress(div);
                }
            });

            this.winTimer.scheduleTimer();
        }
    }
}
