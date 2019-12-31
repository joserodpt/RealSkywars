package pt.josegamerpt.realskywars.classes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

import pt.josegamerpt.realskywars.player.GameScoreboard;
import pt.josegamerpt.realskywars.utils.Calhau;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.classes.Enum.GameType;
import pt.josegamerpt.realskywars.classes.Enum.TierType;

public class GameRoom {

	public GameRoomSolo gs;
	public GameRoomTeams gt;
	public Enum.GameType mode;
	public GameScoreboard scoreboard;

	int error = -16;

	public GameRoom(GameRoomSolo g) {
		this.gs = g;
		mode = Enum.GameType.SOLO;
		scoreboard = new GameScoreboard(this);
		scoreboard.run();
	}

	public GameRoom(GameRoomTeams g) {
		this.gt = g;
		mode = Enum.GameType.TEAMS;
		scoreboard = new GameScoreboard(this);
		scoreboard.run();
	}

	public String getName() {
		if (mode == Enum.GameType.SOLO) {
			return gs.Name;
		}
		if (mode == GameType.TEAMS) {
			return gt.Name;
		}
		return "No Name";
	}

	public int getCurrentPlayers() {
		if (mode == GameType.SOLO) {
			return gs.Players.size();
		}
		if (mode == GameType.TEAMS) {
			return gt.getCurrentPlayers();
		}
		return error;
	}

	public int getMaxPlayers() {
		if (mode == GameType.SOLO) {
			return gs.maxPlayers;
		}
		if (mode == GameType.TEAMS) {
			return gt.maxPlayers;
		}
		return error;
	}

	public World getWorld() {
		if (mode == GameType.SOLO) {
			return gs.worldMap;
		}
		if (mode == GameType.TEAMS) {
			return gt.worldMap;
		}
		return null;
	}

	public void broadcastMessage(String s) {
		if (mode == GameType.SOLO) {
			for (GamePlayer p : gs.GamePlayers) {
				if (p.p != null) {
					p.sendMessage(s);
				}
			}
		}
		if (mode == GameType.TEAMS) {
			for (GamePlayer p : gt.GamePlayers) {
				if (p.p != null) {
					p.sendMessage(s);
				}
			}
		}
	}

	public void kickPlayers() {
		if (mode == GameType.SOLO) {
			gs.kickPlayers();
		}
		if (mode == GameType.TEAMS) {
			gt.kickPlayers();
		}
	}

	public int getCurrentSpectators() {
		if (mode == GameType.SOLO) {
			return gs.Spectators.size();
		}
		if (mode == GameType.TEAMS) {
			return gt.Spectators.size();
		}
		return error;
	}

	public GameState getState() {
		if (mode == GameType.SOLO) {
			return gs.State;
		}
		if (mode == GameType.TEAMS) {
			return gt.State;
		}
		return null;
	}

	public boolean isPlaceHolder() {
		if (mode == GameType.SOLO) {
			return gs.placeholder;
		}
		if (mode == GameType.TEAMS) {
			return gt.placeholder;
		}
		return true;
	}

	public void forceStart() {
		if (mode == GameType.SOLO) {
			gs.forceStart();
		}
		if (mode == GameType.TEAMS) {
			gt.forceStart();
			;
		}
	}

	public ArrayList<GamePlayer> getPlayerList() {
		if (mode == GameType.SOLO) {
			return gs.Players;
		}
		return null;
	}

	public void removePlayer(GamePlayer p) {
		if (mode == GameType.SOLO) {
			gs.removePlayer(p);
		}
		if (mode == GameType.TEAMS) {
			gt.removePlayer(p);
		}
	}

	public void setTierType(TierType b) {
		if (mode == GameType.SOLO) {
			gs.tierType = b;
		}
		if (mode == GameType.TEAMS) {
			gt.tierType = b;
		}
	}

	public ArrayList<GamePlayer> getVoters() {
		if (mode == GameType.SOLO) {
			return gs.voters;
		}
		if (mode == GameType.TEAMS) {
			return gt.voters;
		}
		return null;
	}

	public ArrayList<Integer> getVoteList() {
		if (mode == GameType.SOLO) {
			return gs.votes;
		}
		if (mode == GameType.TEAMS) {
			return gt.votes;
		}
		return null;
	}

	public void addPlayer(GamePlayer gp) {
		if (mode == GameType.SOLO) {
			gs.addPlayer(gp);
		}
		if (mode == GameType.TEAMS) {
			gt.addPlayer(gp);
		}
	}

	public boolean isSpectatorEnabled() {
		if (mode == GameType.SOLO) {
			return gs.specEnabled;
		}
		if (mode == GameType.TEAMS) {
			return gt.specEnabled;
		}
		return false;
	}

	public boolean isDragonEnabled() {
		if (mode == GameType.SOLO) {
			return gs.dragonEnabled;
		}
		if (mode == GameType.TEAMS) {
			return gt.dragonEnabled;
		}
		return false;
	}

	public TierType getTierType() {
		if (mode == GameType.SOLO) {
			return gs.tierType;
		}
		if (mode == GameType.TEAMS) {
			return gt.tierType;
		}
		return null;
	}

	public ArrayList<Calhau> getBlocksPlaced() {
		if (mode == GameType.SOLO) {
			return gs.blockplace;
		}
		if (mode == GameType.TEAMS) {
			return gt.blockplace;
		}
		return null;
	}
	
	public ArrayList<Calhau> getBlocksDestroyed() {
		if (mode == GameType.SOLO) {
			return gs.blockbreak;
		}
		if (mode == GameType.TEAMS) {
			return gt.blockbreak;
		}
		return null;
	}

	public int getTimePassed() {
		if (mode == GameType.SOLO) {
			return gs.timePassed;
		}
		if (mode == GameType.TEAMS) {
			return gt.timePassed;
		}
		return error;
	}

	public void resetArena() {
		if (mode == GameType.SOLO) {
			gs.resetArena();
		}
		if (mode == GameType.TEAMS) {
			gt.resetArena();
		}
	}

	public void setState(GameState w) {
		if (mode == GameType.SOLO) {
			gs.State = w;
		}
		if (mode == GameType.TEAMS) {
			gt.State = w;
		}
	}

	public void setSpectator(boolean b) {
		if (mode == GameType.SOLO) {
			gs.specEnabled = b;
		}
		if (mode == GameType.TEAMS) {
			gt.specEnabled = b;
		}
	}

	public void setDragon(boolean b) {
		if (mode == GameType.SOLO) {
			gs.dragonEnabled = b;
		}
		if (mode == GameType.TEAMS) {
			gt.dragonEnabled = b;
		}
	}

	public void spectate(GamePlayer p) {
		if (mode == GameType.SOLO) {
			gs.spectate(p);
		}
		if (mode == GameType.TEAMS) {
			gt.spectate(p);
		}
	}

	public ArrayList<Location> getOpenChests() {
		if (mode == GameType.SOLO) {
			return gs.openedChests;
		}
		if (mode == GameType.TEAMS) {
			return gt.openedChests;
		}
		return null;
	}

	public ArrayList<GamePlayer> getGamePlayers() {
		if (mode == GameType.SOLO) {
			return gs.GamePlayers;
		}
		if (mode == GameType.TEAMS) {
			return gt.GamePlayers;
		}
		return null;
	}

	public void checkWin() {
		if (mode == GameType.SOLO) {
			gs.checkWin();
		}
		if (mode == GameType.TEAMS) {
			gt.checkWin();
		}	}

	public boolean getType() {
		// TODO Auto-generated method stub
		return false;
	}
}
