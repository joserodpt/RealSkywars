package pt.josegamerpt.realskywars.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import pt.josegamerpt.realskywars.utils.Calhau;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.classes.Enum.GameType;
import pt.josegamerpt.realskywars.classes.Enum.TierType;

public interface GameRoom {

	void saveRoom();

	String getName();

	int getMaxPlayers();

	int getPlayersCount();

	ArrayList<GamePlayer> getPlayers();

	int getSpectatorsCount();

	List<GamePlayer> getSpectators();

	int getPlayersInCount();

	List<GamePlayer> getPlayersIn();


	World getWorld();

	void broadcastMessage(String s, Boolean prefix);

	void kickPlayers();

	void kickPlayers(String s);

	GameState getState();

	boolean isPlaceHolder();

	void forceStart();

	void removePlayer(GamePlayer p);

	Location getSpectatorLocation();

	Location getPOS1();

	Location getPOS2();

	void setTierType(TierType b);

	ArrayList<GamePlayer> getVoters();

	ArrayList<Integer> getVoteList();

	void addPlayer(GamePlayer gp);

	boolean isSpectatorEnabled();

	boolean isInstantEndEnabled();

	TierType getTierType();

	ArrayList<Calhau> getBlocksPlaced();

	ArrayList<Calhau> getBlocksDestroyed();

	int getTimePassed();

	void resetArena();

	void setState(GameState w);

	void setSpectator(boolean b);

	void setInstantEnd(boolean b);

	void spectate(GamePlayer p, Location killLoc);

	ArrayList<Location> getOpenedChests();

	void checkWin();

	GameType getMode();

	void cancelAllTasks();

	void cancelTask(String s);

	ArrayList<Cage> getCages();
}
