package joserodpt.realskywars.api.map;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWMapsConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.modes.RSWSign;
import joserodpt.realskywars.api.map.modes.teams.Team;
import joserodpt.realskywars.api.player.RSWGameLog;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerTab;
import joserodpt.realskywars.api.utils.BungeecordUtils;
import joserodpt.realskywars.api.utils.CountdownTimer;
import joserodpt.realskywars.api.utils.Demolition;
import joserodpt.realskywars.api.utils.MapCuboid;
import joserodpt.realskywars.api.utils.MathUtils;
import joserodpt.realskywars.api.utils.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class RSWMap {

    private final RSWWorld world;
    private final MapCuboid mapCuboid;
    private final List<RSWChest> chests;
    private final List<RSWSign> signs;
    private final String name;
    private String displayName;
    private final int maxPlayers, borderSize;
    private int timePassed;
    private final WorldBorder border;
    private final Location spectatorLocation;
    private final String schematicName;
    private final List<RSWPlayer> inRoom = new ArrayList<>();
    private final HashMap<UUID, Integer> chestVotes = new HashMap<>();
    private final HashMap<UUID, Integer> projectileVotes = new HashMap<>();
    private final HashMap<UUID, Integer> timeVotes = new HashMap<>();
    private MapState state;
    private RSWBossbar bossbar;
    private RSWChest.Tier chestTier = RSWChest.Tier.NORMAL;
    private Boolean specEnabled, instantEnding, ranked, borderEnabled;
    private CountdownTimer mapTimer, startMapTimer, finishingTimer;
    private BukkitTask timeCounterTask;
    private ProjectileType projectileType = ProjectileType.NORMAL;
    private TimeType timeType = TimeType.DAY;
    private List<RSWEvent> events;
    private RealSkywarsAPI rs;
    private boolean registered = true;

    public RSWMap(String nome, String displayName, World w, String schematicName, RSWWorld.WorldType wt, MapState estado, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Boolean borderEnabled, Location pos1, Location pos2, List<RSWChest> chests, Boolean rankd, RealSkywarsAPI rs) {
        this.rs = rs;

        this.name = nome;
        this.displayName = displayName;
        this.schematicName = schematicName;

        this.mapCuboid = new MapCuboid(pos1, pos2);
        this.borderSize = this.mapCuboid.getSizeX();
        this.border = w.getWorldBorder();
        this.border.setCenter(this.mapCuboid.getCenter());
        this.border.setSize(this.borderSize);
        this.world = new RSWWorld(this, w, wt);

        this.state = estado;
        this.maxPlayers = maxPlayers;
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;
        this.borderEnabled = borderEnabled;

        this.chests = chests;
        this.ranked = rankd;

        this.chestVotes.put(UUID.randomUUID(), 2);
        this.projectileVotes.put(UUID.randomUUID(), 1);
        this.timeVotes.put(UUID.randomUUID(), 1);

        this.bossbar = new RSWBossbar(this);

        //load events
        this.events = parseEvents();

        //load signs
        this.signs = loadSigns();
    }

    public RSWMap(String nome) {
        this.name = nome;
        this.displayName = nome;
        this.world = null;
        this.mapCuboid = null;
        this.chests = null;
        this.signs = null;
        this.maxPlayers = -1;
        this.border = null;
        this.borderSize = -1;
        this.spectatorLocation = null;
        this.schematicName = "";
    }

    public String forceStart(RSWPlayer p) {
        if (canStartMap()) {
            return this.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_CANT_FORCESTART, true);
        } else {
            switch (this.getState()) {
                case PLAYING:
                case FINISHING:
                    return this.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ALREADY_STARTED, true);
                default:
                    this.forceStartMap();
                    return this.getRealSkywarsAPI().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CMD_MATCH_FORCESTART, true);
            }
        }
    }

    public void startTimers() {
        this.mapTimer = new CountdownTimer(rs.getPlugin(), this.getMaxTime(), () -> {
        }, () -> {
        }, (t) -> this.bossbar.tick());
        this.mapTimer.scheduleTimer();

        this.timeCounterTask = new BukkitRunnable() {
            public void run() {
                ++timePassed;
                tickEvents();
            }
        }.runTaskTimer(rs.getPlugin(), 0, 20);
    }

    private void tickEvents() {
        List<RSWEvent> tmp = new ArrayList<>(this.events);
        tmp.forEach(RSWEvent::tick);
    }

    public BukkitTask getTimeCounterTask() {
        return this.timeCounterTask;
    }

    public CountdownTimer getMapTimer() {
        return this.mapTimer;
    }

    public CountdownTimer getStartMapTimer() {
        return this.startMapTimer;
    }

    public Location getPOS1() {
        return this.mapCuboid.getPOS1();
    }

    public Location getPOS2() {
        return this.mapCuboid.getPOS2();
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

    public Boolean isBorderEnabled() {
        return this.borderEnabled;
    }

    public boolean isFull() {
        return this.getPlayerCount() == this.getMaxPlayers();
    }

    public String getMapName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public RSWBossbar getBossBar() {
        return this.bossbar;
    }

    public WorldBorder getBorder() {
        return this.border;
    }

    public int getPlayerCount() {
        return this.getPlayers().size();
    }

    public List<RSWPlayer> getPlayers() {
        List<RSWPlayer> players = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.PLAYING || rswPlayer.getState() == RSWPlayer.PlayerState.CAGE)
                players.add(rswPlayer);
        }

        return players;
    }

    public List<RSWPlayer> getAllPlayers() {
        return this.inRoom;
    }

    public int getSpectatorsCount() {
        return this.getSpectators().size();
    }

    public List<RSWPlayer> getSpectators() {
        List<RSWPlayer> players = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.SPECTATOR || rswPlayer.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR)
                players.add(rswPlayer);
        }
        return players;
    }

    public RSWWorld getRSWWorld() {
        return this.world;
    }

    public void kickPlayers(String msg) {
        List<RSWPlayer> tmp = new ArrayList<>(this.inRoom);

        for (RSWPlayer p : tmp) {
            if (msg != null) {
                p.sendMessage(Text.color(msg));
            }
            this.removePlayer(p);
        }
    }

    public MapState getState() {
        return this.state;
    }

    public void setState(MapState w) {
        this.state = w;
        this.getRealSkywarsAPI().getEventsAPI().callRoomStateChange(this);
        if (this.bossbar != null)
            this.bossbar.setState(w);
    }

    abstract public boolean isPlaceHolder();

    abstract public boolean canStartMap();

    abstract public void removePlayer(RSWPlayer p);

    public Location getSpectatorLocation() {
        return new Location(this.getRSWWorld().getWorld(), this.spectatorLocation.getBlockX(), this.spectatorLocation.getBlockY(), this.spectatorLocation.getBlockZ());
    }

    public void setTierType(RSWChest.Tier b) {
        this.chestTier = b;
        this.chests.forEach(swChest -> swChest.setLoot(rs.getChestManagerAPI().getChest(this.chestTier, swChest.getType()), rs.getChestManagerAPI().getMaxItems(this.chestTier)));
    }

    public void setTime(TimeType tt) {
        this.timeType = tt;
        switch (this.timeType) {
            case DAY:
                this.getRSWWorld().getWorld().setTime(0);
                break;
            case NIGHT:
                this.getRSWWorld().getWorld().setTime(13000);
                break;
            case SUNSET:
                this.getRSWWorld().getWorld().setTime(11999);
                break;
            case RAIN:
                this.getRSWWorld().getWorld().setStorm(true);
                break;
        }
    }

    public void setProjectiles(ProjectileType pt) {
        this.projectileType = pt;
    }

    abstract public AddResult addPlayer(RSWPlayer gp);

    public boolean isSpectatorEnabled() {
        return this.specEnabled;
    }

    public boolean isInstantEndEnabled() {
        return this.instantEnding;
    }

    public RSWChest.Tier getChestTier() {
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
        p.setGameMode(GameMode.CREATIVE);

        switch (st) {
            case INSIDE_GAME:
                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.SPECTATOR);
                p.heal();
                p.getPlayer().teleport(killLoc.add(0, 1, 0));

                if (p.hasTeam()) {
                    Team t = p.getTeam();
                    t.removeMember(p);
                    if (t.isEliminated()) {
                        new Demolition(this.getSpectatorLocation(), p.getCage(), 5, 3).start(rs.getPlugin());
                    }
                }
                //update tab
                if (!p.isBot()) {
                    for (RSWPlayer rswPlayer : this.inRoom) {
                        if (!rswPlayer.isBot()) {
                            RSWPlayerTab rt = rswPlayer.getTab();
                            rt.remove(p.getPlayer());
                            rt.updateRoomTAB();
                        }
                    }
                }

                if (p.hasKit()) {
                    p.getKit().cancelTasks();
                }

                this.sendLog(p, false);

                //click to play again
                TextComponent component = new TextComponent(TextComponent.fromLegacyText(" > " + rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PLAY_AGAIN, false)));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rsw play " + this.getGameMode().name().toLowerCase()));
                p.getPlayer().spigot().sendMessage(component);

                this.checkWin();
                break;
            case EXTERNAL:
                this.inRoom.add(p);
                p.setRoom(this);
                this.getBossBar().addPlayer(p.getPlayer());

                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.EXTERNAL_SPECTATOR);
                p.teleport(this.getSpectatorLocation());
                p.heal();

                //update tab
                if (!p.isBot()) {
                    for (RSWPlayer rswPlayer : this.inRoom) {
                        if (!rswPlayer.isBot()) {
                            RSWPlayerTab rt = rswPlayer.getTab();
                            List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                            rt.clear();
                            rt.add(players);
                            rt.updateRoomTAB();
                        }
                    }
                }

                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MATCH_SPECTATE, true));
                break;
        }

        rs.getPlayerManagerAPI().giveItems(p.getPlayer(), PlayerManagerAPI.Items.SPECTATOR);
    }

    abstract public void checkWin();

    abstract public Mode getGameMode();

    abstract public List<RSWCage> getCages();

    abstract public List<Team> getTeams();

    abstract public int maxMembersTeam();

    public void clear() {
        this.world.deleteWorld(OperationReason.RESET);
    }

    public void reset() {
        this.setState(MapState.RESETTING);

        this.kickPlayers(rs.getLanguageManagerAPI().getString(LanguageManagerAPI.TS.ARENA_RESET, true));
        this.resetArena(OperationReason.RESET);
    }

    public MapCuboid getArena() {
        return this.mapCuboid;
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

    public List<RSWChest> getChests() {
        return this.chests;
    }

    public List<RSWEvent> getEvents() {
        return this.events;
    }

    abstract public int getMaxTime();

    public RSWChest getChest(Location location) {
        for (RSWChest chest : this.chests) {
            if (location.equals(chest.getLocation())) {
                return chest;
            }
        }
        return null;
    }

    public String getShematicName() {
        return this.schematicName;
    }

    private List<RSWSign> loadSigns() {
        List<RSWSign> list = new ArrayList<>();

        if (RSWMapsConfig.file().isList(this.getMapName() + ".Signs")) {
            for (String i : RSWMapsConfig.file().getStringList(this.getMapName() + ".Signs")) {

                String[] signData = i.split("<");
                World w = Bukkit.getWorld(signData[0]);
                int x = Integer.parseInt(signData[1]);
                int y = Integer.parseInt(signData[2]);
                int z = Integer.parseInt(signData[3]);

                list.add(new RSWSign(this, w.getBlockAt(x, y, z)));
            }
        }
        return list;
    }

    public void addSign(Block b) {
        this.signs.add(new RSWSign(this, b));
        this.saveSigns();
    }

    public void updateSigns() {
        this.signs.forEach(RSWSign::update);
    }

    public void removeSign(Block b) {
        RSWSign tmp = null;
        for (RSWSign sign : this.signs) {
            if (sign.getBlock().equals(b)) {
                tmp = sign;
                sign.getBehindBlock().setType(Material.BLACK_CONCRETE);
                sign.delete();
            }
        }
        if (tmp != null) {
            this.signs.remove(tmp);
        }

        this.saveSigns();
    }

    private void saveSigns() {
        RSWMapsConfig.file().set(this.getMapName() + ".Signs", this.getSigns().stream().map(RSWSign::getLocationSerialized)
                .collect(Collectors.toCollection(ArrayList::new)));
        RSWMapsConfig.save();
    }

    public List<RSWSign> getSigns() {
        return this.signs;
    }

    public void sendLog(RSWPlayer p, boolean winner) {
        if (p.getPlayer() != null) {
            for (String s : Text.color(rs.getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.ARENA_END))) {
                p.sendCenterMessage(s.replace("%recvcoins%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_BALANCE, this.isRanked()) + "").replace("%totalcoins%", p.getGameBalance() + "").replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS, this.isRanked()) + "").replace("%time%", Text.formatSeconds(this.mapTimer.getPassedSeconds())));
            }

            p.addGameLog(new RSWGameLog(this.getMapName(), this.getGameMode(), this.isRanked(), this.getMaxPlayers(), winner, this.getTimePassed(), Text.getDayAndTime()));

            p.saveData(RSWPlayer.PlayerData.GAME);
        }
    }

    abstract public void forceStartMap();

    protected void calculateVotes() {
        //chest calculate
        int bigger = MathUtils.mostFrequentElement(getChestVotes().values());
        switch (bigger) {
            case 1:
                this.setTierType(RSWChest.Tier.BASIC);
                break;
            case 3:
                this.setTierType(RSWChest.Tier.EPIC);
                break;
            default:
                this.setTierType(RSWChest.Tier.NORMAL);
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
            case 4:
                this.setTime(TimeType.RAIN);
                break;
            default:
                this.setTime(TimeType.DAY);
                break;
        }
    }

    protected void cancelMapStart() {
        getStartMapTimer().killTask();
        for (RSWPlayer p : getAllPlayers()) {
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_CANCEL, true));
            p.sendActionbar(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_CANCEL, false));
            p.setBarNumber(0);
        }

        this.setState(MapState.WAITING);
    }

    protected void commonRemovePlayer(RSWPlayer p) {
        this.getBossBar().removePlayer(p.getPlayer());

        p.setBarNumber(0);
        p.setInvincible(false);
        p.sendMessage(Text.color(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MATCH_LEAVE, true)));

        p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.LOBBY_OR_NOGAME);
        p.setFlying(false);
        p.setGameMode(GameMode.SURVIVAL);
        p.heal();

        if (p.hasKit()) {
            p.getKit().cancelTasks();
        }

        this.inRoom.remove(p);
        p.setRoom(null);

        //update tab
        if (!p.isBot()) {
            RSWPlayerTab rt = p.getTab();
            rt.reset();
            rt.updateRoomTAB();
        }
        for (RSWPlayer player : this.getPlayers()) {
            if (!player.isBot()) {
                RSWPlayerTab rt = player.getTab();
                rt.clear();
                List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                rt.add(players);
                rt.updateRoomTAB();
            }
        }

        boolean isBungeeEnabled = RSWConfig.file().getBoolean("Config.Bungeecord.Enabled");
        boolean shouldKickPlayer = RSWConfig.file().getBoolean("Config.Bungeecord.Kick-Player");

        if (isBungeeEnabled) {
            if (shouldKickPlayer) {
                if (p.getPlayer() != null)
                    p.getPlayer().kickPlayer(TranslatableLine.BUNGEECORD_KICK_MESSAGE.get());
            } else {
                TranslatableLine.BUNGEECORD_KICK_MESSAGE.send(p);
                BungeecordUtils.connect(RSWConfig.file().getString("Config.Bungeecord.Lobby-Server"), p.getPlayer(), this.getRealSkywarsAPI().getPlugin());
            }
        }

        rs.getGameManagerAPI().tpToLobby(p);
        rs.getPlayerManagerAPI().giveItems(p.getPlayer(), PlayerManagerAPI.Items.LOBBY);


        if (this.getState() == MapState.PLAYING || this.getState() == MapState.FINISHING) {
            checkWin();
        }

        //call api
        rs.getEventsAPI().callRoomStateChange(this);
    }

    abstract public int minimumPlayersToStartMap();

    protected void startRoom() {
        this.startMapTimer = new CountdownTimer(rs.getPlugin(), RSWConfig.file().getInt("Config.Time-To-Start"), () -> {
            //
        }, this::forceStartMap, (t) -> {
            if (getPlayerCount() < minimumPlayersToStartMap()) {
                t.killTask();
                for (RSWPlayer p : this.inRoom) {
                    if (p.getWorld() != this.getRSWWorld().getWorld() && p.hasCage()) {
                        p.getCage().tpPlayer(p);
                    }

                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_CANCEL, true));
                    p.sendActionbar(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_CANCEL, false));
                    p.setBarNumber(0);
                }
                this.setState(MapState.WAITING);
            } else {
                this.setState(MapState.STARTING);
                for (RSWPlayer p : this.inRoom) {
                    if (p.getWorld() != this.getRSWWorld().getWorld() && p.hasCage()) {
                        p.getCage().tpPlayer(p);
                    }

                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_START_COUNTDOWN, true).replace("%time%", Text.formatSeconds(t.getSecondsLeft())));
                    p.sendActionbar(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_START_COUNTDOWN, false).replace("%time%", Text.formatSeconds(t.getSecondsLeft())));
                    p.setBarNumber(t.getSecondsLeft(), RSWConfig.file().getInt("Config.Time-To-Start"));
                }
            }
        });

        this.startMapTimer.scheduleTimer();
    }

    protected void commonResetArena(OperationReason rr) {
        this.setState(MapState.RESETTING);

        if (this.timeCounterTask != null) {
            this.timeCounterTask.cancel();
        }
        if (this.mapTimer != null) {
            this.mapTimer.killTask();
        }
        if (this.finishingTimer != null) {
            this.finishingTimer.killTask();
        }
        if (this.startMapTimer != null) {
            this.startMapTimer.killTask();
            this.startMapTimer = null;
        }

        this.chests.forEach(RSWChest::clear);
        this.world.resetWorld(rr);

        this.inRoom.clear();

        this.chestVotes.clear();
        this.projectileVotes.clear();
        this.timeVotes.clear();

        this.chestVotes.put(UUID.randomUUID(), 2);
        this.projectileVotes.put(UUID.randomUUID(), 1);
        this.timeVotes.put(UUID.randomUUID(), 1);

        if (this.bossbar != null)
            this.bossbar.reset();

        this.events = parseEvents();

        this.timePassed = 0;

        if (rr != OperationReason.SHUTDOWN) {
            this.chests.forEach(RSWChest::setChest);
        }

        this.setState(MapState.AVAILABLE);
    }

    public void setFinishingTimer(CountdownTimer finishingTimer) {
        this.finishingTimer = finishingTimer;
    }

    public CountdownTimer getFinishingTimer() {
        return this.finishingTimer;
    }

    public List<RSWEvent> parseEvents() {
        List<RSWEvent> ret = new ArrayList<>();
        String search = "Teams";
        switch (this.getGameMode()) {
            case SOLO:
                search = "Solo";
                break;
            case TEAMS:
                search = "Teams";
                break;
        }
        for (String s1 : RSWConfig.file().getStringList("Config.Events." + search)) {
            String[] parse = s1.split("&");
            RSWEvent.EventType et = RSWEvent.EventType.valueOf(parse[0]);
            int time = Integer.parseInt(parse[1]);
            ret.add(new RSWEvent(this, et, time));
        }
        ret.add(new RSWEvent(this, RSWEvent.EventType.BORDERSHRINK, RSWConfig.file().getInt("Config.Maximum-Game-Time." + search)));
        return ret;
    }

    protected RealSkywarsAPI getRealSkywarsAPI() {
        return rs;
    }

    public void setBorderEnabled(boolean b) {
        this.borderEnabled = b;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRegistered(boolean b) {
        this.registered = b;
    }

    public boolean isRegistered() {
        return registered;
    }

    public enum Data {
        ALL, SETTINGS, WORLD, NAME, TYPE, NUM_PLAYERS, CAGES, CHESTS, SPECT_LOC, BORDER
    }

    public void save(Data d, boolean save) {
        switch (d) {
            case ALL:
                this.save(Data.SETTINGS, false);
                this.save(Data.WORLD, false);
                this.save(Data.NAME, false);
                this.save(Data.TYPE, false);
                this.save(Data.NUM_PLAYERS, false);
                this.save(Data.CAGES, false);
                this.save(Data.CHESTS, false);
                this.save(Data.SPECT_LOC, false);
                this.save(Data.BORDER, false);
                break;
            case WORLD:
                // World
                RSWMapsConfig.file().set(this.getMapName() + ".world", this.getRSWWorld().getName());
                break;
            case NAME:
                RSWMapsConfig.file().set(this.getMapName() + ".name", this.name);
                break;
            case NUM_PLAYERS:
                RSWMapsConfig.file().set(this.getMapName() + ".number-of-players", this.getMaxPlayers());
                break;
            case TYPE:
                RSWMapsConfig.file().set(this.getMapName() + ".type", this.getRSWWorld().getType().name());
                if (this.getRSWWorld().getType() == RSWWorld.WorldType.SCHEMATIC) {
                    RSWMapsConfig.file().set(this.getMapName() + ".schematic", this.getShematicName());
                }
                break;
            case CAGES:
                switch (this.getGameMode()) {
                    case SOLO:
                        for (RSWCage c : this.getCages()) {
                            Location loc = c.getLoc();
                            RSWMapsConfig.file().set(this.getMapName() + ".Locations.Cages." + c.getID() + ".X", loc.getBlockX());
                            RSWMapsConfig.file().set(this.getMapName() + ".Locations.Cages." + c.getID() + ".Y", loc.getBlockY());
                            RSWMapsConfig.file().set(this.getMapName() + ".Locations.Cages." + c.getID() + ".Z", loc.getBlockZ());
                        }
                        break;
                    case TEAMS:
                        for (Team c : this.getTeams()) {
                            Location loc = c.getTeamCage().getLoc();
                            RSWMapsConfig.file().set(this.getMapName() + ".Locations.Cages." + c.getTeamCage().getID() + ".X", loc.getBlockX());
                            RSWMapsConfig.file().set(this.getMapName() + ".Locations.Cages." + c.getTeamCage().getID() + ".Y", loc.getBlockY());
                            RSWMapsConfig.file().set(this.getMapName() + ".Locations.Cages." + c.getTeamCage().getID() + ".Z", loc.getBlockZ());
                        }
                        break;
                }
                break;
            case CHESTS:
                int chestID = 1;
                for (RSWChest chest : this.getChests()) {
                    RSWMapsConfig.file().set(this.getMapName() + ".Chests." + chestID + ".LocationX", chest.getLocation().getBlockX());
                    RSWMapsConfig.file().set(this.getMapName() + ".Chests." + chestID + ".LocationY", chest.getLocation().getBlockY());
                    RSWMapsConfig.file().set(this.getMapName() + ".Chests." + chestID + ".LocationZ", chest.getLocation().getBlockZ());
                    String face;
                    try {
                        BlockFace f = ((Directional) chest.getChestBlock().getBlockData()).getFacing();
                        face = f.name();
                    } catch (Exception ignored) {
                        face = "NORTH";
                    }
                    RSWMapsConfig.file().set(this.getMapName() + ".Chests." + chestID + ".Face", face);
                    RSWMapsConfig.file().set(this.getMapName() + ".Chests." + chestID + ".Type", chest.getType().name());
                    ++chestID;
                }
                break;
            case SPECT_LOC:
                RSWMapsConfig.file().set(this.getMapName() + ".Locations.Spectator.X", this.getSpectatorLocation().getX());
                RSWMapsConfig.file().set(this.getMapName() + ".Locations.Spectator.Y", this.getSpectatorLocation().getY());
                RSWMapsConfig.file().set(this.getMapName() + ".Locations.Spectator.Z", this.getSpectatorLocation().getZ());
                RSWMapsConfig.file().set(this.getMapName() + ".Locations.Spectator.Yaw", this.getSpectatorLocation().getYaw());
                RSWMapsConfig.file().set(this.getMapName() + ".Locations.Spectator.Pitch", this.getSpectatorLocation().getPitch());

                break;
            case SETTINGS:
                RSWMapsConfig.file().set(this.getMapName() + ".Settings.DisplayName", this.getDisplayName());
                RSWMapsConfig.file().set(this.getMapName() + ".Settings.GameType", this.getGameMode().name());
                RSWMapsConfig.file().set(this.getMapName() + ".Settings.Spectator", this.isSpectatorEnabled());
                RSWMapsConfig.file().set(this.getMapName() + ".Settings.Instant-End", this.isInstantEndEnabled());
                RSWMapsConfig.file().set(this.getMapName() + ".Settings.Ranked", this.isRanked());
                RSWMapsConfig.file().set(this.getMapName() + ".Settings.Border", this.isBorderEnabled());
                break;
            case BORDER:
                RSWMapsConfig.file().set(this.getMapName() + ".World.Border.POS1-X", this.getPOS1().getX());
                RSWMapsConfig.file().set(this.getMapName() + ".World.Border.POS1-Y", this.getPOS1().getY());
                RSWMapsConfig.file().set(this.getMapName() + ".World.Border.POS1-Z", this.getPOS1().getZ());
                RSWMapsConfig.file().set(this.getMapName() + ".World.Border.POS2-X", this.getPOS2().getX());
                RSWMapsConfig.file().set(this.getMapName() + ".World.Border.POS2-Y", this.getPOS2().getY());
                RSWMapsConfig.file().set(this.getMapName() + ".World.Border.POS2-Z", this.getPOS2().getZ());
                break;
        }
        if (save) {
            RSWMapsConfig.save();
        }
    }

    //enums
    public enum MapState {
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
        DAY, NIGHT, RAIN, SUNSET
    }

    public enum SpectateType {INSIDE_GAME, EXTERNAL}

    public enum AddResult {
        ADDED, FULL, RESETTING, SPECTATING_DISABLED, SPECTATING
    }
}