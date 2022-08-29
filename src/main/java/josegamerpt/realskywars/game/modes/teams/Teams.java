package josegamerpt.realskywars.game.modes.teams;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.SWWorld;
import josegamerpt.realskywars.game.modes.SWGameMode;
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

public class Teams implements SWGameMode {

    private final SWWorld world;
    private final ArenaCuboid arenaCuboid;
    private final ArrayList<SWChest> chests;

    private final String name;
    private final int maxPlayers;
    private final int maxMembersTeam;
    private final Boolean ranked;
    private SWGameMode.GameState state;
    private final WorldBorder border;
    private BossBar bossBar;
    private final int borderSize;
    private final ArrayList<Team> teams;
    private final ArrayList<RSWPlayer> inRoom = new ArrayList<>();
    private final ArrayList<Integer> votes = new ArrayList<>();
    private final ArrayList<UUID> voters = new ArrayList<>();
    private ChestManager.TierType tierType = ChestManager.TierType.NORMAL;
    private int timePassed = 0;
    private final Location spectatorLocation;
    private Boolean specEnabled;
    private Boolean instantEnding;

    private Countdown startTimer;
    private Countdown startRoomTimer;
    private Countdown winTimer;
    private BukkitTask timeCouterTask;

    private ArrayList<SWEvent> events;

    public Teams(String nome, World w, SWGameMode.GameState estado, ArrayList<Team> teams, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2, ArrayList<SWChest> chests, Boolean rankd) {
        this.name = nome;
        this.world = new SWWorld(this, w);

        this.state = estado;
        this.teams = teams;
        this.maxPlayers = maxPlayers;
        this.maxMembersTeam = teams.get(0).getMaxMembers();
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;
        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border.setSize(this.borderSize);
        this.chests = chests;
        this.ranked = rankd;

        this.votes.add(2);

        this.events = RealSkywars.getGameManager().parseEvents(this);

        this.bossBar = Bukkit.createBossBar(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)), BarColor.WHITE, BarStyle.SOLID);
    }

    @Override
    public Boolean isRanked() {
        return this.ranked;
    }

    @Override
    public boolean isFull() {
        return this.getPlayersCount() == this.getMaxPlayers();
    }

    public void saveRoom() {
        RealSkywars.getGameManager().addRoom(this);
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public BossBar getBossBar() {
        return this.bossBar;
    }

    @Override
    public WorldBorder getBorder() {
        return this.border;
    }

    public int getPlayersCount() {
        return this.getPlayers().size();
    }

    public ArrayList<RSWPlayer> getPlayers() {
        ArrayList<RSWPlayer> players = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.PLAYING || rswPlayer.getState() == RSWPlayer.PlayerState.CAGE)
                players.add(rswPlayer);
        }

        return players;
    }

    @Override
    public ArrayList<RSWPlayer> getInRoom() {
        return this.inRoom;
    }

    public ArrayList<RSWPlayer> getSpectators() {
        ArrayList<RSWPlayer> spec = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.SPECTATOR || rswPlayer.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR)
                spec.add(rswPlayer);
        }

        return spec;
    }

    public int getSpectatorsCount() {
        return this.getSpectators().size();
    }

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

    public SWGameMode.GameState getState() {
        return this.state;
    }

    public void setState(SWGameMode.GameState w) {
        this.state = w;
    }

    public boolean isPlaceHolder() {
        return false;
    }

    public String forceStart(RSWPlayer p) {
        if (this.getPlayersCount() < (this.maxMembersTeam() + 1)) {
            return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_CANT_FORCESTART, true);
        } else {
            switch (this.state) {
                case PLAYING:
                case FINISHING:
                    return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_STARTED, true);
                default:
                    startGameFunction();
                    return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_MATCH_FORCESTART, true);
            }
        }
    }

    public ArrayList<Cage> getCages() {
        ArrayList<Cage> c = new ArrayList<>();
        this.teams.forEach(team -> c.add(team.getTeamCage()));
        return c;
    }

    public ArrayList<Team> getTeams() {
        return this.teams;
    }

    @Override
    public int maxMembersTeam() {
        return this.maxMembersTeam;
    }

    @Override
    public void clear() {
        this.world.clear();
    }

    @Override
    public void reset() {
        this.state = SWGameMode.GameState.RESETTING;

        this.kickPlayers(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.ARENA_RESET, true));
        this.resetArena();
    }

    @Override
    public ArenaCuboid getArena() {
        return this.arenaCuboid;
    }

    @Override
    public int getBorderSize() {
        return this.borderSize;
    }

    @Override
    public void addVote(UUID u, int i) {
        this.votes.add(i);
        this.voters.add(u);
    }

    @Override
    public ArrayList<SWChest> getChests() {
        return this.chests;
    }

    @Override
    public ArrayList<SWEvent> getEvents() {
        return this.events;
    }

    @Override
    public SWChest getChest(Location location) {
        for (SWChest chest : this.chests) {
            if (location.equals(chest.getLocation())) {
                return chest;
            }
        }
        return null;
    }

    @Override
    public int getMaxTime() {
        return Config.file().getInt("Config.Maximum-Game-Time.Teams");
    }

    private void startGameFunction() {
        this.state = SWGameMode.GameState.PLAYING;

        this.startRoomTimer.killTask();

        int timeleft = Config.file().getInt("Config.Maximum-Game-Time.Teams");

        int bigger = MathUtils.bigger(votes.stream().mapToInt(i -> i).toArray());
        switch (bigger) {
            case 1:
                this.setTierType(ChestManager.TierType.BASIC, true);
                break;
            case 3:
                this.setTierType(ChestManager.TierType.EPIC, true);
                break;
            default:
                this.setTierType(ChestManager.TierType.NORMAL, true);
                break;
        }

        for (Team t : this.teams) {
            for (RSWPlayer p : t.getMembers()) {
                if (p.getPlayer() != null) {
                    p.getInventory().clear();

                    this.bossBar.addPlayer(p.getPlayer());

                    //start msg
                    for (String s : Text.color(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.ARENA_START))) {
                        p.sendCenterMessage(s.replace("%chests%", this.tierType.name() + "").replace("%kit%", p.getKit().getName() + ""));
                    }

                    if (p.hasKit()) {
                        p.getKit().give(p);
                    }

                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.PLAYING);
                }
            }
            t.openCage();
        }

        this.startTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), timeleft, () -> {
            //
        }, () -> {
            //
        }, (t) -> {
            this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_RUNTIME).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + "")));
            double div = (double) t.getSecondsLeft() / (double) timeleft;
            this.bossBar.setProgress(div);
        });
        this.startTimer.scheduleTimer();

        this.timeCouterTask = new BukkitRunnable() {
            public void run() {
                timePassed += 1;
                tickEvents();
            }
        }.runTaskTimer(RealSkywars.getPlugin(), 0, 20);
    }

    private void tickEvents() {
        ArrayList<SWEvent> tmp = new ArrayList<>(this.events);
        tmp.forEach(SWEvent::tick);
    }

    public void removePlayer(RSWPlayer p) {
        if (this.bossBar != null && !p.isBot()) {
            this.bossBar.removePlayer(p.getPlayer());
        }

        p.setInvincible(false);
        p.sendMessage(Text.color(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MATCH_LEAVE, true)));

        if (p.hasTeam()) {
            p.getTeam().removePlayer(p);
        }

        RealSkywars.getGameManager().tpToLobby(p);

        RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);

        p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.LOBBY_OR_NOGAME);
        p.setFlying(false);
        p.heal();

        if (p.hasKit()) {
            p.getKit().cancelTasks();
        }

        this.inRoom.remove(p);
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

        if (this.state == SWGameMode.GameState.PLAYING || this.state == SWGameMode.GameState.FINISHING) {
            checkWin();
        }
    }

    @Override
    public Location getSpectatorLocation() {
        return new Location(getWorld(), this.spectatorLocation.getBlockX(), this.spectatorLocation.getBlockY(), this.spectatorLocation.getBlockZ());
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
        if (RealSkywars.getPartyManager().checkForParties(p, this)) {
            switch (this.state) {
                case RESETTING:
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CANT_JOIN, true));
                    break;
                case FINISHING:
                case PLAYING:
                    if (this.specEnabled) {
                        spectate(p, SpectateType.EXTERNAL, null);
                    } else {
                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SPECTATING_DISABLED, true));
                    }
                    break;
                default:
                    if (this.getPlayersCount() == this.maxPlayers) {
                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ROOM_FULL, true));
                        return;
                    }

                    p.setRoom(this);
                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

                    for (RSWPlayer ws : this.inRoom) {
                        if (p.getPlayer() != null) {
                            ws.sendMessage(RealSkywars.getLanguageManager().getString(ws, LanguageManager.TS.PLAYER_JOIN_ARENA, true).replace("%player%", p.getDisplayName()).replace("%players%", this.getPlayersCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
                        }
                    }

                    this.inRoom.add(p);
                    p.heal();

                    if (p.getPlayer() != null) {
                        this.bossBar.addPlayer(p.getPlayer());
                        ArrayList<String> up = RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.TITLE_ROOMJOIN);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
                    }

                    //cage

                    for (Team c : this.teams) {
                        if (!c.isTeamFull()) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.CAGE);

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

                    if (this.getPlayersCount() == this.maxMembersTeam + 1) {
                        startRoom();
                    }
                    break;
            }

            //signal that is ranked
            if (this.ranked) p.sendActionbar("&b&lRANKED");
        }
    }

    private void startRoom() {
        this.startRoomTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"), () -> {
            //
        }, this::startGameFunction, (t) -> {
            if (getPlayersCount() < this.maxMembersTeam + 1) {
                Bukkit.getScheduler().cancelTask(t.getTaskId());
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, true));
                }
                this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)));
                this.bossBar.setProgress(0D);
                this.state = SWGameMode.GameState.WAITING;
            } else {
                for (RSWPlayer p : this.getPlayers()) {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_START_COUNTDOWN, true).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + ""));
                }
                this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_STARTING).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + "")));
                double div = (double) t.getSecondsLeft() / (double) Config.file().getInt("Config.Time-To-Start");
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

    public ChestManager.TierType getTierType() {
        return this.tierType;
    }

    public void setTierType(ChestManager.TierType b, Boolean updateChests) {
        this.tierType = b;
        if (updateChests) {
            this.chests.forEach(swChest -> swChest.setLoot(RealSkywars.getChestManager().getChest(this.tierType, swChest.isMiddle()), RealSkywars.getChestManager().getMaxItems(this.tierType, swChest.isMiddle())));
        }
    }

    public int getTimePassed() {
        return this.timePassed;
    }

    public void resetArena() {
        this.state = SWGameMode.GameState.RESETTING;

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

        this.world.resetWorld();

        this.inRoom.clear();
        this.teams.forEach(Team::reset);
        this.voters.clear();
        this.votes.clear();
        this.votes.add(2);
        this.bossBar = Bukkit.createBossBar(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)), BarColor.WHITE, BarStyle.SOLID);

        this.events = RealSkywars.getGameManager().parseEvents(this);

        this.chests.forEach(SWChest::clear);

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

        switch (st) {
            case GAME:
                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.SPECTATOR);
                p.heal();
                p.getPlayer().teleport(killLoc.add(0, 1, 0));

                if (p.hasTeam()) {
                    Team t = p.getTeam();
                    t.removePlayer(p);
                    if (t.isEliminated()) {
                        new Demolition(this.getSpectatorLocation(), p.getCage(), 5, 3).start(RealSkywars.getPlugin());
                    }
                }
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

                if (p.hasKit()) {
                    p.getKit().cancelTasks();
                }

                sendLog(p);

                RealSkywars.getPlayerManager().sendClick(p, this.getGameMode());

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

                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MATCH_SPECTATE, true));
                break;
        }

        RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.SPECTATOR);
    }

    private void sendLog(RSWPlayer p) {
        if (p.getPlayer() != null) {
            for (String s : Text.color(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.END_LOG))) {
                p.sendCenterMessage(s.replace("%recvcoins%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_BALANCE, this.isRanked()) + "").replace("%totalcoins%", p.getGameBalance() + "").replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS, this.isRanked()) + ""));
            }
            p.saveData();
        }
    }

    public void checkWin() {
        if (getAliveTeams() == 1 && this.state != SWGameMode.GameState.FINISHING) {
            this.state = SWGameMode.GameState.FINISHING;
            Team winTeam = getPlayers().get(0).getTeam();

            this.startTimer.killTask();
            this.timeCouterTask.cancel();

            this.bossBar.setTitle(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_END));
            this.bossBar.setProgress(0);
            this.bossBar.setColor(BarColor.BLUE);

            RealSkywars.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(RealSkywars.getLanguageManager().getString(gamePlayer, LanguageManager.TS.WINNER_BROADCAST, true).replace("%winner%", winTeam.getNames()).replace("%map%", this.name)));

            this.winTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"), () -> {
                for (RSWPlayer p : winTeam.getMembers()) {
                    if (p.getPlayer() != null) {
                        p.setInvincible(true);
                        p.addStatistic(RSWPlayer.Statistic.TEAM_WIN, 1, this.isRanked());
                        p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                    }
                    sendLog(p);
                }

                for (RSWPlayer g : this.inRoom) {
                    if (g.getPlayer() != null) {
                        g.sendMessage(RealSkywars.getLanguageManager().getString(g, LanguageManager.TS.MATCH_END, true).replace("%time%", "" + Text.formatSeconds(Config.file().getInt("Config.Time-EndGame"))));
                        g.getPlayer().sendTitle("", Text.color(RealSkywars.getLanguageManager().getString(g, LanguageManager.TS.TITLE_WIN, true).replace("%player%", winTeam.getNames())), 10, 40, 10);
                    }
                }
            }, () -> {
                this.bossBar.removeAll();
                winTeam.getMembers().forEach(this::sendLog);
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

    @Override
    public Mode getGameMode() {
        return Mode.TEAMS;
    }

    public int getAliveTeams() {
        int al = 0;
        for (Team t : this.teams) {
            if (!t.isEliminated() && t.getMemberCount() > 0) {
                al++;
            }
        }
        return al;
    }

}
