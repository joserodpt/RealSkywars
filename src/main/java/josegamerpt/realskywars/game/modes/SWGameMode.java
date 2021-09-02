package josegamerpt.realskywars.game.modes;

import java.util.*;

import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.teams.Team;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.utils.ArenaCuboid;
import org.bukkit.Location;
import org.bukkit.World;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BossBar;

public interface SWGameMode {

    enum GameState {
		AVAILABLE, STARTING, WAITING, PLAYING, FINISHING, RESETTING
	}

	enum Mode {
		SOLO, TEAMS
	}

	Boolean isRanked();

	boolean isFull();

	void saveRoom();

	String getName();

	int getMaxPlayers();

	BossBar getBossBar();

	WorldBorder getBorder();

	int getPlayersCount();

	ArrayList<RSWPlayer> getPlayers();

	ArrayList<RSWPlayer> getInRoom();

	int getSpectatorsCount();

	List<RSWPlayer> getSpectators();

	World getWorld();

	void kickPlayers(String msg);

	SWGameMode.GameState getState();

	boolean isPlaceHolder();

	String forceStart(RSWPlayer p);

	void removePlayer(RSWPlayer p);

	Location getSpectatorLocation();

	Location getPOS1();

	Location getPOS2();

	void setTierType(ChestManager.TierType b, Boolean updateChests);

	ArrayList<UUID> getVoters();

	ArrayList<Integer> getVoteList();

	void addPlayer(RSWPlayer gp);

	boolean isSpectatorEnabled();

	boolean isInstantEndEnabled();

	ChestManager.TierType getTierType();

	int getTimePassed();

	void resetArena();

	void setState(SWGameMode.GameState w);

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

	void addVote(UUID u, int i);

    ArrayList<SWChest> getChests();

	ArrayList<SWEvent> getEvents();
	int getMaxTime();

	SWChest getChest(Location location);

    enum SpectateType { GAME, EXTERNAL }

}
