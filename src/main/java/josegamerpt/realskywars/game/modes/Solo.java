package josegamerpt.realskywars.game.modes;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.ChestManager;
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
import josegamerpt.realskywars.utils.*;
import josegamerpt.realskywars.world.SWWorld;
import org.apache.commons.lang.WordUtils;
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

public class Solo implements SWGameMode {

    private final ArenaCuboid arenaCuboid;
    private final String name;
    private final int maxPlayers;
    private final int borderSize;
    private final ArrayList<RSWPlayer> inRoom = new ArrayList<>();
    //votes
    private final HashMap<UUID, Integer> chestVotes = new HashMap<>();
    private final HashMap<UUID, Integer> projectileVotes = new HashMap<>();
    private final HashMap<UUID, Integer> timeVotes = new HashMap<>();
    private final Location spectatorLocation;
    private final ArrayList<Cage> cages;
    private final SWWorld world;
    private final ArrayList<SWChest> chests;
    private final WorldBorder border;
    private final String schematicName;
    private Boolean ranked;
    private BossBar bossBar;
    private SWGameMode.GameState state;
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
    private final ArrayList<SWSign> signs = new ArrayList<>();

    public Solo(String nome, World w, String schematicName, SWWorld.WorldType wt, SWGameMode.GameState estado, ArrayList<Cage> cages, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2, ArrayList<SWChest> chests, Boolean rankd) {
        this.name = nome;
        this.schematicName = schematicName;

        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.border.setSize(this.borderSize);
        this.world = new SWWorld(this, w, wt);

        this.cages = cages;
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

    @Override
    public BossBar getBossBar() {
        return this.bossBar;
    }

    @Override
    public WorldBorder getBorder() {
        return this.border;
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

    public Mode getGameMode() {
        return Mode.SOLO;
    }

    public SWGameMode.GameState getState() {
        return this.state;
    }

    public void setState(SWGameMode.GameState w) {
        this.state = w;
        RealSkywars.getEventsAPI().callRoomStateChange(this);
    }

    public boolean isPlaceHolder() {
        return false;
    }

    public String forceStart(RSWPlayer p) {
        if (this.getPlayerCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
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
        this.world.deleteWorld(OperationReason.RESET);
    }

    @Override
    public void reset() {
        this.setState(GameState.RESETTING);

        this.kickPlayers(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.ARENA_RESET, true));
        this.resetArena(OperationReason.RESET);
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
    public int getMaxTime() {
        return Config.file().getInt("Config.Maximum-Game-Time.Solo");
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
    public String getShematicName() {
        return this.schematicName;
    }

    @Override
    public void addSign(SWSign swSign) {
        this.signs.add(swSign);
        RealSkywars.getSignManager().saveSigns();
    }

    @Override
    public void updateSigns() {
        this.signs.forEach(SWSign::update);
    }

    @Override
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

    @Override
    public ArrayList<SWSign> getSigns() {
        return this.signs;
    }

    private void startGameFunction() {
        if (getPlayerCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
            Bukkit.getScheduler().cancelTask(this.startRoomTimer.getTaskId());
            for (RSWPlayer p : this.inRoom) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, true));
                p.sendActionbar(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_CANCEL, false));
                p.setBarNumber(0);
            }
            this.bossBar.setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_WAIT)));
            this.bossBar.setProgress(0D);
            this.setState(GameState.WAITING);
        } else {
            this.setState(GameState.PLAYING);

            this.startRoomTimer.killTask();

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

            for (RSWPlayer p : this.getPlayers()) {
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
                    p.getCage().open();
                }
            }

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

            this.timeCouterTask = new BukkitRunnable() {
                public void run() {
                    ++timePassed;
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

        RealSkywars.getGameManager().tpToLobby(p);


        p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.LOBBY_OR_NOGAME);
        p.setFlying(false);
        p.heal();

        RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);

        if (p.hasKit()) {
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

        if (this.getState() == SWGameMode.GameState.PLAYING || this.getState() == SWGameMode.GameState.FINISHING) {
            checkWin();
        }

        //call api
        RealSkywars.getEventsAPI().callRoomStateChange(this);
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

                    for (Cage c : this.cages) {
                        if (c.isEmpty() && p.getPlayer() != null) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    p.setRoom(this);
                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

                    this.inRoom.add(p);

                    if (p.getPlayer() != null) {
                        this.bossBar.addPlayer(p.getPlayer());
                        p.heal();
                        ArrayList<String> up = RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.TITLE_ROOMJOIN);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
                    }

                    for (RSWPlayer ws : this.inRoom) {
                        ws.sendMessage(RealSkywars.getLanguageManager().getString(ws, LanguageManager.TS.PLAYER_JOIN_ARENA, true).replace("%player%", p.getDisplayName()).replace("%players%", getPlayerCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
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

                    if (getPlayerCount() == Config.file().getInt("Config.Min-Players-ToStart")) {
                        startRoom();
                    }
                    break;
            }

            //call api
            RealSkywars.getEventsAPI().callRoomStateChange(this);

            //signal that is ranked
            if (this.ranked) p.sendActionbar("&b&lRANKED");
        }
    }

    private void startRoom() {
        this.startRoomTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"), () -> {
            //
        }, this::startGameFunction, (t) -> {
            if (getPlayerCount() < Config.file().getInt("Config.Min-Players-ToStart")) {
                Bukkit.getScheduler().cancelTask(t.getTaskId());
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

    public void resetArena(OperationReason rr) {
        this.setState(GameState.RESETTING);

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

        if (rr != OperationReason.SHUTDOWN) {
            this.chests.forEach(SWChest::setChest);
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

                if (p.hasKit()) {
                    p.getKit().cancelTasks();
                }

                //laser to cage
                new Demolition(this.getSpectatorLocation(), p.getCage(), 5, 3).start(RealSkywars.getPlugin());

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

    //LISTENER

    public void checkWin() {
        if (this.getPlayerCount() == 1 && this.state != SWGameMode.GameState.FINISHING) {
            this.setState(GameState.FINISHING);

            RSWPlayer p = getPlayers().get(0);
            p.setInvincible(true);

            this.startTimer.killTask();
            this.timeCouterTask.cancel();

            this.bossBar.setTitle(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_END));
            this.bossBar.setProgress(0);
            this.bossBar.setColor(BarColor.BLUE);

            RealSkywars.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(RealSkywars.getLanguageManager().getString(gamePlayer, LanguageManager.TS.WINNER_BROADCAST, true).replace("%winner%", p.getDisplayName()).replace("%map%", this.name)));

            if (this.isInstantEndEnabled()) {
                this.bossBar.removeAll();
                this.sendLog(p, true);
                this.kickPlayers(null);
                this.resetArena(OperationReason.RESET);
            } else {
                this.winTimer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"), () -> {
                    if (p.getPlayer() != null) {
                        p.setInvincible(true);
                        p.addStatistic(RSWPlayer.Statistic.SOLO_WIN, 1, this.isRanked());
                        p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                    }

                    for (RSWPlayer g : this.inRoom) {
                        g.delCage();
                        g.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MATCH_END, true).replace("%time%", "" + Text.formatSeconds(Config.file().getInt("Config.Time-EndGame"))));
                    }
                }, () -> {
                    this.bossBar.removeAll();
                    this.chests.forEach(SWChest::clearHologram);
                    this.sendLog(p, true);
                    this.kickPlayers(null);
                    this.resetArena(OperationReason.RESET);
                }, (t) -> {
                    double div = (double) t.getSecondsLeft() / (double) Config.file().getInt("Config.Time-EndGame");
                    if (div <= 1 && div >= 0) {
                        bossBar.setProgress(div);
                    }

                    this.inRoom.forEach(rswPlayer -> rswPlayer.setBarNumber(t.getSecondsLeft(), Config.file().getInt("Config.Time-EndGame")));

                    if (p.getPlayer() != null) {
                        FireworkUtils.spawnRandomFirework(p.getLocation());
                    }
                });

                this.winTimer.scheduleTimer();
            }
        }
    }
}
