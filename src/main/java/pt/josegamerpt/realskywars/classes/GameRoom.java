package pt.josegamerpt.realskywars.classes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

import pt.josegamerpt.realskywars.utils.Calhau;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.classes.Enum.GameType;
import pt.josegamerpt.realskywars.classes.Enum.TierType;

public interface GameRoom {

	String getName();

	int getCurrentPlayers();

	int getMaxPlayers();

	World getWorld();

	void broadcastMessage(String s);

	void kickPlayers();

	int getCurrentSpectators();

	GameState getState();

	boolean isPlaceHolder();

	void forceStart();

	void removePlayer(GamePlayer p);

	void setTierType(TierType b);

	ArrayList<GamePlayer> getVoters();

	ArrayList<Integer> getVoteList();

	void addPlayer(GamePlayer gp);

	boolean isSpectatorEnabled();

	boolean isDragonEnabled();

	TierType getTierType();

	ArrayList<Calhau> getBlocksPlaced();

	ArrayList<Calhau> getBlocksDestroyed();

	int getTimePassed();

	void resetArena();

	void setState(GameState w);

	void setSpectator(boolean b);

	void setDragon(boolean b);

	void spectate(GamePlayer p);

	ArrayList<Location> getOpenChests();

	ArrayList<GamePlayer> getGamePlayers();

	void checkWin();

	GameType getMode();
}
