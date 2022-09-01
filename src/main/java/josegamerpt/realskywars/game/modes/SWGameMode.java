package josegamerpt.realskywars.game.modes;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.teams.Team;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import josegamerpt.realskywars.world.SWWorld;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface SWGameMode {

    Countdown getStartRoomTimer();

    ProjectileType getProjectile();

    void setRanked(Boolean ranked);

    Boolean isRanked();

    boolean isFull();

    void saveRoom();

    String getName();

    int getMaxPlayers();

    BossBar getBossBar();

    WorldBorder getBorder();

    int getPlayerCount();

    ArrayList<RSWPlayer> getPlayers();

    ArrayList<RSWPlayer> getInRoom();

    int getSpectatorsCount();

    List<RSWPlayer> getSpectators();

    SWWorld getSWWorld();

    void kickPlayers(String msg);

    SWGameMode.GameState getState();

    void setState(SWGameMode.GameState w);

    boolean isPlaceHolder();

    String forceStart(RSWPlayer p);

    void removePlayer(RSWPlayer p);

    Location getSpectatorLocation();

    Location getPOS1();

    Location getPOS2();

    void setTierType(ChestManager.ChestTier b, Boolean updateChests);

    void setTime(TimeType tt);

    void setProjectiles(ProjectileType pt);

    void addPlayer(RSWPlayer gp);

    boolean isSpectatorEnabled();

    boolean isInstantEndEnabled();

    ChestManager.ChestTier getChestTier();

    int getTimePassed();

    void resetArena(ResetReason rr);

    void setSpectator(boolean b);

    void setInstantEnd(boolean b);

    void spectate(RSWPlayer p, SpectateType st, Location killLoc);

    void checkWin();

    Mode getGameMode();

    ArrayList<Cage> getCages();

    ArrayList<Team> getTeams();

    int maxMembersTeam();

    void clear();

    void reset();

    ArenaCuboid getArena();

    int getBorderSize();

    void addVote(UUID u, VoteType vt, int i);

    boolean hasVotedFor(VoteType vt, UUID uuid);

    ArrayList<SWChest> getChests();

    ArrayList<SWEvent> getEvents();

    int getMaxTime();

    SWChest getChest(Location location);

    enum GameState {
        AVAILABLE, STARTING, WAITING, PLAYING, FINISHING, RESETTING
    }

    enum ResetReason {SHUTDOWN, NORMAL, ADMIN}

    enum Mode {
        SOLO, TEAMS
    }

    enum VoteType {
        CHESTS, PROJECTILES, TIME
    }

    enum ProjectileType {
        NORMAL, BREAK_BLOCKS
    }

    enum TimeType {
        DAY, NIGHT, SUNSET
    }

    enum SpectateType {GAME, EXTERNAL}

}
