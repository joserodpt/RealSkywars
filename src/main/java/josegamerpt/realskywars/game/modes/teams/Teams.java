package josegamerpt.realskywars.game.modes.teams;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWGameLog;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import josegamerpt.realskywars.utils.Demolition;
import josegamerpt.realskywars.utils.MathUtils;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.world.SWWorld;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
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

public class Teams implements SWGameMode {

    private final SWWorld world;
    private final ArenaCuboid arenaCuboid;
    private final ArrayList<SWChest> chests;

    private final String name;
    private final int maxPlayers;
    private final int maxMembersTeam;
    private final WorldBorder border;
    private final int borderSize;
    private final ArrayList<Team> teams;
    private final ArrayList<RSWPlayer> inRoom = new ArrayList<>();
    private final HashMap<UUID, Integer> chestVotes = new HashMap<>();
    private final HashMap<UUID, Integer> projectileVotes = new HashMap<>();
    private final HashMap<UUID, Integer> timeVotes = new HashMap<>();
    private final Location spectatorLocation;
    private Boolean ranked;
    private SWGameMode.GameState state;
    private BossBar bossBar;
    private ChestManager.ChestTier chestTier = ChestManager.ChestTier.NORMAL;
    private int timePassed = 0;
    private Boolean specEnabled;
    private Boolean instantEnding;

    private Countdown startTimer;
    private Countdown startRoomTimer;
    private Countdown winTimer;
    private BukkitTask timeCouterTask;
    private ProjectileType projectileType = ProjectileType.NORMAL;
    private TimeType timeType = TimeType.DAY;
    private ArrayList<SWEvent> events;

    public Teams(String nome, World w, SWWorld.WorldType wt, SWGameMode.GameState estado, ArrayList<Team> teams, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2, ArrayList<SWChest> chests, Boolean rankd) {
        this.name = nome;

        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.world = new SWWorld(this, w, wt);

        this.state = estado;
        this.teams = teams;
        this.maxPlayers = maxPlayers;
        this.maxMembersTeam = teams.get(0).getMaxMembers();
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border.setSize(this.borderSize);
        this.chests = chests;
        this.ranked = rankd;

        this.chestVotes.put(UUID.randomUUID(), 2);
        this.projectileVotes.put(UUID.randomUUID(), 1);
        this.timeVotes.put(UUID.randomUUID(), 1);

        this.events = RealSkywars.getGameManager().parseEvents(this);

        this.bossBar = Bukkit.createBossBar(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)), BarColor.WHITE, BarStyle.SOLID);
    }

    @Override
    public Countdown getStartRoomTimer() {
        return this.startRoomTimer;
    }

    @Override
    public ProjectileType getProjectile() {
        return this.projectileType;
    }

    @Override
    public void setRanked(Boolean ranked) {
        this.ranked = ranked;
    }

    @Override
    public Boolean isRanked() {
        return this.ranked;
    }

    @Override
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

    @Override
    public BossBar getBossBar() {
        return this.bossBar;
    }

    @Override
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

    public boolean isPlaceHolder() {
        return false;
    }

    public String forceStart(RSWPlayer p) {
        if (this.getPlayerCount() < (this.maxMembersTeam() + 1)) {
            return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_CANT_FORCESTART, true);
        } else {
            switch (this.state) {
                case PLAYING:
                case FINISHING:
                    return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_STARTED, true);
                default:
                    this.startGameFunction();
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
        this.world.deleteWorld();
    }

    @Override
    public void reset() {
        this.state = SWGameMode.GameState.RESETTING;

        this.kickPlayers(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.ARENA_RESET, true));
        this.resetArena(ResetReason.NORMAL);
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

    @Override
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
        if (getPlayerCount() < this.maxMembersTeam + 1) {
            Bukkit.getScheduler().cancelTask(this.startRoomTimer.getTaskId());
            for (RSWPlayer p : this.inRoom) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, true));
                p.sendActionbar(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, false));
                p.setBarNumber(0);
            }
            this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)));
            this.bossBar.setProgress(0D);
            this.state = SWGameMode.GameState.WAITING;
        } else {
            this.state = SWGameMode.GameState.PLAYING;

            this.startRoomTimer.killTask();

            int timeleft = Config.file().getInt("Config.Maximum-Game-Time.Teams");

            //chest calculate
            int bigger = MathUtils.mostFrequentElement(this.chestVotes.values());
            switch (bigger) {
                case 1:
                    this.setTierType(ChestManager.ChestTier.BASIC, true);
                    break;
                case 3:
                    this.setTierType(ChestManager.ChestTier.EPIC, true);
                    break;
                default:
                    this.setTierType(ChestManager.ChestTier.NORMAL, true);
                    break;
            }

            //projectile calculate
            bigger = MathUtils.mostFrequentElement(this.projectileVotes.values());
            if (bigger == 2) {
                this.setProjectiles(ProjectileType.BREAK_BLOCKS);
            } else {
                this.setProjectiles(ProjectileType.NORMAL);
            }

            //time calculate
            bigger = MathUtils.mostFrequentElement(this.timeVotes.values());
            switch (bigger) {
                case 3:
                    this.setTime(TimeType.NIGHT);
                    break;
                case 2:
                    this.setTime(TimeType.SUNSET);
                    break;
                default:
                    this.setTime(TimeType.DAY);
                    break;
            }

            for (Team t : this.teams) {
                for (RSWPlayer p : t.getMembers()) {
                    if (p.getPlayer() != null) {
                        p.setBarNumber(0);
                        p.getInventory().clear();

                        this.bossBar.addPlayer(p.getPlayer());

                        //start msg
                        for (String s : Text.color(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.ARENA_START))) {
                            p.sendCenterMessage(s.replace("%chests%", WordUtils.capitalizeFully(this.chestTier.name())).replace("%kit%", p.getKit().getName()).replace("%project%", WordUtils.capitalizeFully(this.projectileType.name().replace("_", " "))).replace("%time%", WordUtils.capitalizeFully(this.timeType.name())));
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
    }

    private void tickEvents() {
        ArrayList<SWEvent> tmp = new ArrayList<>(this.events);
        tmp.forEach(SWEvent::tick);
    }

    public void removePlayer(RSWPlayer p) {
        if (this.bossBar != null && !p.isBot()) {
            this.bossBar.removePlayer(p.getPlayer());
        }

        p.setBarNumber(0);
        p.setInvincible(false);
        p.sendMessage(Text.color(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MATCH_LEAVE, true)));

        if (p.hasTeam()) {
            p.getTeam().removeMember(p);
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
            this.checkWin();
        }
    }

    @Override
    public Location getSpectatorLocation() {
        return new Location(this.getSWWorld().getWorld(), this.spectatorLocation.getBlockX(), this.spectatorLocation.getBlockY(), this.spectatorLocation.getBlockZ());
    }

    @Override
    public Location getPOS1() {
        return this.arenaCuboid.getPOS1();
    }

    @Override
    public Location getPOS2() {
        return this.arenaCuboid.getPOS2();
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
                    if (this.getPlayerCount() == this.maxPlayers) {
                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ROOM_FULL, true));
                        return;
                    }

                    //cage

                    for (Team c : this.teams) {
                        if (!c.isTeamFull()) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    p.setRoom(this);
                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

                    for (RSWPlayer ws : this.inRoom) {
                        if (p.getPlayer() != null) {
                            ws.sendMessage(RealSkywars.getLanguageManager().getString(ws, LanguageManager.TS.PLAYER_JOIN_ARENA, true).replace("%player%", p.getDisplayName()).replace("%players%", this.getPlayerCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
                        }
                    }

                    this.inRoom.add(p);
                    p.heal();

                    if (p.getPlayer() != null) {
                        this.bossBar.addPlayer(p.getPlayer());
                        ArrayList<String> up = RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.TITLE_ROOMJOIN);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
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

                    if (this.getPlayerCount() == this.maxMembersTeam + 1) {
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
            if (getPlayerCount() < this.maxMembersTeam + 1) {
                Bukkit.getScheduler().cancelTask(t.getTaskId());
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, true));
                    p.sendActionbar(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, false));
                    p.setBarNumber(0);
                }
                this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)));
                this.bossBar.setProgress(0D);
                this.state = SWGameMode.GameState.WAITING;
            } else {
                for (RSWPlayer p : this.getPlayers()) {
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

    public boolean isSpectatorEnabled() {
        return this.specEnabled;
    }

    public boolean isInstantEndEnabled() {
        return this.instantEnding;
    }

    public ChestManager.ChestTier getChestTier() {
        return this.chestTier;
    }

    public void setTierType(ChestManager.ChestTier b, Boolean updateChests) {
        this.chestTier = b;
        if (updateChests) {
            this.chests.forEach(swChest -> swChest.setLoot(RealSkywars.getChestManager().getChest(this.chestTier, swChest.isMiddle()), RealSkywars.getChestManager().getMaxItems(this.chestTier)));
        }
    }

    @Override
    public void setTime(TimeType tt) {
        this.timeType = tt;
        switch (this.timeType) {
            case DAY:
                this.world.setTime(1000);
                break;
            case NIGHT:
                this.world.setTime(13000);
                break;
            case SUNSET:
                this.world.setTime(12000);
                break;
        }
    }

    @Override
    public void setProjectiles(ProjectileType pt) {
        this.projectileType = pt;
    }

    public int getTimePassed() {
        return this.timePassed;
    }

    public void resetArena(ResetReason rr) {
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
            this.startRoomTimer = null;
        }

        this.world.resetWorld(rr);

        this.inRoom.clear();
        this.teams.forEach(Team::reset);

        this.chestVotes.clear();
        this.projectileVotes.clear();
        this.timeVotes.clear();

        this.chestVotes.put(UUID.randomUUID(), 2);
        this.projectileVotes.put(UUID.randomUUID(), 1);
        this.timeVotes.put(UUID.randomUUID(), 1);

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

    private void sendLog(RSWPlayer p, boolean winner) {
        if (p.getPlayer() != null) {
            for (String s : Text.color(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.ARENA_END))) {
                p.sendCenterMessage(s.replace("%recvcoins%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_BALANCE, this.isRanked()) + "").replace("%totalcoins%", p.getGameBalance() + "").replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS, this.isRanked()) + "").replace("%time%", Text.formatSeconds(this.startTimer.getPassedSeconds())));
            }

            p.addGameLog(new RSWGameLog(this.getName(), this.getGameMode(), this.isRanked(), this.getMaxPlayers(), winner, this.getTimePassed(), Text.getDayAndTime()));

            p.saveData();
        }
    }

    public void checkWin() {
        if (this.getAliveTeams() == 1 && this.state != SWGameMode.GameState.FINISHING) {
            this.state = SWGameMode.GameState.FINISHING;
            Team winTeam = getPlayers().get(0).getTeam();

            this.startTimer.killTask();
            this.timeCouterTask.cancel();

            this.bossBar.setTitle(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_END));
            this.bossBar.setProgress(0);
            this.bossBar.setColor(BarColor.BLUE);

            RealSkywars.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(RealSkywars.getLanguageManager().getString(gamePlayer, LanguageManager.TS.WINNER_BROADCAST, true).replace("%winner%", winTeam.getNames()).replace("%map%", this.name)));

            if (this.isInstantEndEnabled()) {
                this.bossBar.removeAll();
                winTeam.getMembers().forEach(rswPlayer -> this.sendLog(rswPlayer, true));
                this.kickPlayers(null);
                this.resetArena(ResetReason.NORMAL);
            } else {
                this.winTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"), () -> {
                    for (RSWPlayer p : winTeam.getMembers()) {
                        if (p.getPlayer() != null) {
                            p.setInvincible(true);
                            p.addStatistic(RSWPlayer.Statistic.TEAM_WIN, 1, this.isRanked());
                            p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                        }
                        this.sendLog(p, true);
                    }

                    for (RSWPlayer g : this.inRoom) {
                        if (g.getPlayer() != null) {
                            g.sendMessage(RealSkywars.getLanguageManager().getString(g, LanguageManager.TS.MATCH_END, true).replace("%time%", "" + Text.formatSeconds(Config.file().getInt("Config.Time-EndGame"))));
                            g.getPlayer().sendTitle("", Text.color(RealSkywars.getLanguageManager().getString(g, LanguageManager.TS.TITLE_WIN, true).replace("%player%", winTeam.getNames())), 10, 40, 10);
                        }
                    }
                }, () -> {
                    this.bossBar.removeAll();
                    winTeam.getMembers().forEach(rswPlayer -> this.sendLog(rswPlayer, true));
                    this.kickPlayers(null);
                    this.resetArena(ResetReason.NORMAL);
                }, (t) -> {
                    // if (Players.get(0).p != null) {
                    //     firework(Players.get(0));
                    // }
                    double div = (double) t.getSecondsLeft() / (double) Config.file().getInt("Config.Time-EndGame");
                    if (div <= 1 && div >= 0) {
                        bossBar.setProgress(div);
                    }

                    this.inRoom.forEach(rswPlayer -> rswPlayer.setBarNumber(t.getSecondsLeft(), Config.file().getInt("Config.Time-EndGame")));
                });

                this.winTimer.scheduleTimer();
            }
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
