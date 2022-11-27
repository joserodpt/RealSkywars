package josegamerpt.realskywars.game.modes;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.teams.Team;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWGameLog;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.sign.SWSign;
import josegamerpt.realskywars.utils.ArenaCuboid;
import josegamerpt.realskywars.utils.Demolition;
import josegamerpt.realskywars.utils.MathUtils;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.world.SWWorld;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class SWGameMode {

    private final SWWorld world;
    private final ArenaCuboid arenaCuboid;
    private final ArrayList<SWChest> chests;

    private final String name;
    private final int maxPlayers;
    private final WorldBorder border;
    private final int borderSize;
    private final ArrayList<RSWPlayer> inRoom = new ArrayList<>();
    private final HashMap<UUID, Integer> chestVotes = new HashMap<>();
    private final HashMap<UUID, Integer> projectileVotes = new HashMap<>();
    private final HashMap<UUID, Integer> timeVotes = new HashMap<>();
    private final Location spectatorLocation;
    private final String schematicName;
    private Boolean ranked;
    private SWGameMode.GameState state;
    private BossBar bossBar;
    private SWChest.Tier chestTier = SWChest.Tier.NORMAL;
    private int timePassed = 0;
    private Boolean specEnabled;
    private Boolean instantEnding;
    private Countdown startTimer;
    private Countdown startRoomTimer;
    private Countdown winTimer;
    private BukkitTask timeCounterTask;
    private ProjectileType projectileType = ProjectileType.NORMAL;
    private TimeType timeType = TimeType.DAY;
    private ArrayList<SWEvent> events;
    private final ArrayList<SWSign> signs = new ArrayList<>();

    public SWGameMode(String nome, World w, String schematicName, SWWorld.WorldType wt, SWGameMode.GameState estado, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2, ArrayList<SWChest> chests, Boolean rankd) {
        this.name = nome;
        this.schematicName = schematicName;

        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.border.setSize(this.borderSize);
        this.world = new SWWorld(this, w, wt);

        this.state = estado;
        this.maxPlayers = maxPlayers;
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;

        this.chests = chests;
        this.ranked = rankd;

        this.chestVotes.put(UUID.randomUUID(), 2);
        this.projectileVotes.put(UUID.randomUUID(), 1);
        this.timeVotes.put(UUID.randomUUID(), 1);

        this.events = RealSkywars.getGameManager().parseEvents(this);

        this.bossBar = Bukkit.createBossBar(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)), BarColor.WHITE, BarStyle.SOLID);
    }

    public void startTimers() {
        this.startTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), this.getMaxTime(), () -> {
            //
        }, () -> {
            //
        }, (t) -> {
            this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_RUNTIME).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + "")));
            double div = (double) t.getSecondsLeft() / (double) this.getMaxTime();
            this.bossBar.setProgress(div);
        });
        this.startTimer.scheduleTimer();

        this.timeCounterTask = new BukkitRunnable() {
            public void run() {
                ++timePassed;
                tickEvents();
            }
        }.runTaskTimer(RealSkywars.getPlugin(), 0, 20);
    }

    private void tickEvents() {
        ArrayList<SWEvent> tmp = new ArrayList<>(this.events);
        tmp.forEach(SWEvent::tick);
    }

    public BukkitTask getTimeCounterTask() {
        return this.timeCounterTask;
    }

    public Countdown getStartTimer() {
        return this.startTimer;
    }

    public Countdown getStartRoomTimer() {
        return this.startRoomTimer;
    }

    public Location getPOS1() {
        return this.arenaCuboid.getPOS1();
    }
    public Location getPOS2() {
        return this.arenaCuboid.getPOS2();
    }

    public ProjectileType getProjectileTier() {
        return this.projectileType;
    }

    public TimeType getTimeType() {
        return this.timeType;
    }

    public void setRanked(Boolean ranked) {
        this.ranked = ranked;
    }

    public Boolean isRanked() {
        return this.ranked;
    }

    public boolean isFull() {
        return this.getPlayerCount() == this.getMaxPlayers();
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

    public BossBar getBossBar() {
        return this.bossBar;
    }

    public WorldBorder getBorder() {
        return this.border;
    }

    public int getPlayerCount() {
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

    public ArrayList<RSWPlayer> getInRoom() {
        return this.inRoom;
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

    public SWWorld getSWWorld() {
        return this.world;
    }

    public void kickPlayers(String msg) {
        ArrayList<RSWPlayer> tmp = new ArrayList<>(this.inRoom);

        for (RSWPlayer p : tmp) {
            if (msg != null) {
                p.sendMessage(Text.color(msg));
            }
            this.removePlayer(p);
        }
    }

    public SWGameMode.GameState getState() {
        return this.state;
    }

    public void setState(SWGameMode.GameState w) {
        this.state = w;
    }

    abstract public boolean isPlaceHolder();

    abstract public String forceStart(RSWPlayer p);
    abstract public boolean canStartGame();

    abstract public void removePlayer(RSWPlayer p);

    public Location getSpectatorLocation() {
        return new Location(this.getSWWorld().getWorld(), this.spectatorLocation.getBlockX(), this.spectatorLocation.getBlockY(), this.spectatorLocation.getBlockZ());
    }

    public void setTierType(SWChest.Tier b, Boolean updateChests) {
        this.chestTier = b;
        if (updateChests) {
            this.chests.forEach(swChest -> swChest.setLoot(RealSkywars.getChestManager().getChest(this.chestTier, swChest.isMiddle()), RealSkywars.getChestManager().getMaxItems(this.chestTier)));
        }
    }

    public void setTime(TimeType tt) {
        this.timeType = tt;
    }

    public void setProjectiles(ProjectileType pt) {
        this.projectileType = pt;
    }

    abstract public void addPlayer(RSWPlayer gp);

    public boolean isSpectatorEnabled() {
        return this.specEnabled;
    }

    public boolean isInstantEndEnabled() {
        return this.instantEnding;
    }

    public SWChest.Tier getChestTier() {
        return this.chestTier;
    }

    public int getTimePassed() {
        return this.timePassed;
    }

    abstract public void resetArena(OperationReason rr);

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
                    t.removeMember(p);
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

                this.sendLog(p, false);

                RealSkywars.getPlayerManager().sendClick(p, this.getGameMode());

                this.checkWin();
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

    abstract public void checkWin();

    abstract public Mode getGameMode();

    abstract public ArrayList<Cage> getCages();

    abstract public ArrayList<Team> getTeams();

    abstract public int maxMembersTeam();

    public void clear() {
        this.world.deleteWorld(OperationReason.RESET);
    }

    public void reset() {
        this.setState(GameState.RESETTING);

        this.kickPlayers(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.ARENA_RESET, true));
        this.resetArena(OperationReason.RESET);
    }

    public ArenaCuboid getArena() {
        return this.arenaCuboid;
    }

    public int getBorderSize() {
        return this.borderSize;
    }

    public void addVote(UUID u, VoteType vt, int i) {
        switch (vt) {
            case CHESTS:
                this.chestVotes.put(u, i);
                break;
            case PROJECTILES:
                this.projectileVotes.put(u, i);
                break;
            case TIME:
                this.timeVotes.put(u, i);
                break;
        }
    }

    public HashMap<UUID, Integer> getChestVotes() {
        return this.chestVotes;
    }

    public HashMap<UUID, Integer> getProjectileVotes() {
        return this.projectileVotes;
    }

    public HashMap<UUID, Integer> getTimeVotes() {
        return this.timeVotes;
    }

    public boolean hasVotedFor(VoteType vt, UUID uuid) {
        switch (vt) {
            case CHESTS:
                return this.chestVotes.containsKey(uuid);
            case TIME:
                return this.timeVotes.containsKey(uuid);
            case PROJECTILES:
                return this.projectileVotes.containsKey(uuid);
        }
        return false;
    }

    public ArrayList<SWChest> getChests() {
        return this.chests;
    }

    public ArrayList<SWEvent> getEvents() {
        return this.events;
    }

    abstract public int getMaxTime();

    public SWChest getChest(Location location) {
        for (SWChest chest : this.chests) {
            if (location.equals(chest.getLocation())) {
                return chest;
            }
        }
        return null;
    }

    public String getShematicName() {
        return this.schematicName;
    }

    public void addSign(SWSign swSign) {
        this.signs.add(swSign);
        RealSkywars.getSignManager().saveSigns();
    }

    public void updateSigns() {
        this.signs.forEach(SWSign::update);
    }

    public void removeSign(Block b) {
        SWSign tmp = null;
        for (SWSign sign : this.signs) {
            if (sign.getBlock().equals(b)) {
                tmp = sign;
                sign.delete();
            }
        }
        if (tmp != null) {
            this.signs.remove(tmp);
        }
        RealSkywars.getSignManager().saveSigns();
    }

    public ArrayList<SWSign> getSigns() {
        return this.signs;
    }

    public void sendLog(RSWPlayer p, boolean winner) {
        if (p.getPlayer() != null) {
            for (String s : Text.color(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.ARENA_END))) {
                p.sendCenterMessage(s.replace("%recvcoins%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_BALANCE, this.isRanked()) + "").replace("%totalcoins%", p.getGameBalance() + "").replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS, this.isRanked()) + "").replace("%time%", Text.formatSeconds(this.startTimer.getPassedSeconds())));
            }

            p.addGameLog(new RSWGameLog(this.getName(), this.getGameMode(), this.isRanked(), this.getMaxPlayers(), winner, this.getTimePassed(), Text.getDayAndTime()));

            p.saveData();
        }
    }

    abstract public void startGameFunction();

        protected void calculateVotes() {
        //chest calculate
        int bigger = MathUtils.mostFrequentElement(getChestVotes().values());
        switch (bigger) {
            case 1:
                this.setTierType(SWChest.Tier.BASIC, true);
                break;
            case 3:
                this.setTierType(SWChest.Tier.EPIC, true);
                break;
            default:
                this.setTierType(SWChest.Tier.NORMAL, true);
                break;
        }

        //projectile calculate
        bigger = MathUtils.mostFrequentElement(getProjectileVotes().values());
        if (bigger == 2) {
            this.setProjectiles(ProjectileType.BREAK_BLOCKS);
        } else {
            this.setProjectiles(ProjectileType.NORMAL);
        }

        //time calculate
        bigger = MathUtils.mostFrequentElement(getTimeVotes().values());
        switch (bigger) {
            case 2:
                this.setTime(TimeType.SUNSET);
                break;
            case 3:
                this.setTime(TimeType.NIGHT);
                break;
            default:
                this.setTime(TimeType.DAY);
                break;
        }
    }

    protected void cancelGameStart() {
        getStartRoomTimer().killTask();
        for (RSWPlayer p : getInRoom()) {
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, true));
            p.sendActionbar(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, false));
            p.setBarNumber(0);
        }
        getBossBar().setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)));
        getBossBar().setProgress(0D);
        this.setState(GameState.WAITING);
    }

    protected void commonRemovePlayer(RSWPlayer p) {
        if (this.bossBar != null && !p.isBot()) {
            this.bossBar.removePlayer(p.getPlayer());
        }

        p.setBarNumber(0);
        p.setInvincible(false);
        p.sendMessage(Text.color(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MATCH_LEAVE, true)));

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

        if (this.getState() == SWGameMode.GameState.PLAYING || this.getState() == SWGameMode.GameState.FINISHING) {
            checkWin();
        }

        //call api
        RealSkywars.getEventsAPI().callRoomStateChange(this);
    }

    abstract public int minimumPlayersToStartGame();

    protected void startRoom() {
        this.startRoomTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"), () -> {
            //
        }, this::startGameFunction, (t) -> {
            if (getPlayerCount() < minimumPlayersToStartGame()) {
                t.killTask();
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, true));
                    p.sendActionbar(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, false));
                    p.setBarNumber(0);
                }
                this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)));
                this.bossBar.setProgress(0D);
                this.setState(GameState.WAITING);
            } else {
                this.setState(GameState.STARTING);
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_START_COUNTDOWN, true).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + ""));
                    p.sendActionbar(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_START_COUNTDOWN, false).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + ""));
                    p.setBarNumber(t.getSecondsLeft(), Config.file().getInt("Config.Time-To-Start"));
                }
                this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_STARTING).replace("%time%", Text.formatSeconds(t.getSecondsLeft()) + "")));
                double div = (double) t.getSecondsLeft() / (double) Config.file().getInt("Config.Time-To-Start");
                this.bossBar.setProgress(div);
            }
        });

        this.startRoomTimer.scheduleTimer();
    }

    protected void commonResetArena(OperationReason rr) {
        this.setState(GameState.RESETTING);

        if (this.timeCounterTask != null) {
            this.timeCounterTask.cancel();
        }
        if (this.startTimer != null) {
            this.startTimer.killTask();
        }
        if (this.winTimer != null) {
            this.winTimer.killTask();
        }
        if (this.startRoomTimer != null) {
            this.startRoomTimer.killTask();
            this.startRoomTimer = null;
        }

        this.chests.forEach(SWChest::clear);
        this.world.resetWorld(rr);

        this.inRoom.clear();

        this.chestVotes.clear();
        this.projectileVotes.clear();
        this.timeVotes.clear();

        this.chestVotes.put(UUID.randomUUID(), 2);
        this.projectileVotes.put(UUID.randomUUID(), 1);
        this.timeVotes.put(UUID.randomUUID(), 1);

        this.bossBar = Bukkit.createBossBar(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)), BarColor.WHITE, BarStyle.SOLID);

        this.events = RealSkywars.getGameManager().parseEvents(this);

        this.timePassed = 0;

        if (rr != OperationReason.SHUTDOWN) {
            this.chests.forEach(SWChest::setChest);
        }
    }

    public void setWinTimer(Countdown winTimer) {
        this.winTimer = winTimer;
    }

    public Countdown getWinTimer() {
        return this.winTimer;
    }

    //enums

    public enum GameState {
        AVAILABLE, STARTING, WAITING, PLAYING, FINISHING, RESETTING
    }

    public enum OperationReason {SHUTDOWN, RESET, LOAD}

    public enum Mode {
        SOLO, TEAMS
    }

    public enum VoteType {
        CHESTS, PROJECTILES, TIME
    }

    public enum ProjectileType {
        NORMAL, BREAK_BLOCKS
    }

    public enum TimeType {
        DAY, NIGHT, SUNSET
    }

    public enum SpectateType {GAME, EXTERNAL}

}
