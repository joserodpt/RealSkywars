package josegamerpt.realskywars.modes;

import java.util.*;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.SWChest;
import josegamerpt.realskywars.classes.Team;
import josegamerpt.realskywars.utils.ArenaCuboid;
import org.bukkit.Location;
import org.bukkit.World;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.player.RSWPlayer;
public interface SWGameMode {

	void saveRoom();

	String getName();

	int getMaxPlayers();

	int getPlayersCount();

	ArrayList<RSWPlayer> getPlayers();

	int getSpectatorsCount();

	List<RSWPlayer> getSpectators();

	World getWorld();

	void kickPlayers(String msg);

	Enum.GameState getState();

	boolean isPlaceHolder();

	String forceStart(RSWPlayer p);

	void removePlayer(RSWPlayer p);

	Location getSpectatorLocation();

	Location getPOS1();

	Location getPOS2();

	void setTierType(Enum.TierType b, Boolean updateChests);

	ArrayList<UUID> getVoters();

	ArrayList<Integer> getVoteList();

	void addPlayer(RSWPlayer gp);

	boolean isSpectatorEnabled();

	boolean isInstantEndEnabled();

	Enum.TierType getTierType();

	int getTimePassed();

	void resetArena();

	void setState(Enum.GameState w);

	void setSpectator(boolean b);

	void setInstantEnd(boolean b);

	void spectate(RSWPlayer p, SpectateType st, Location killLoc);

	void checkWin();

	Enum.GameType getMode();

	ArrayList<Cage> getCages();

	ArrayList<Team> getTeams();

	int maxMembersTeam();

    void clear();

    void reset();

	ArenaCuboid getArena();

	int getID();

	int getBorderSize();

	void addVote(UUID u, int i);

    ArrayList<SWChest> getChests();

    SWChest getChest(Location location);

    enum SpectateType { GAME, EXTERNAL }

}
