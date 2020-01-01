package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.Selections;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.classes.GameRoomSolo;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

public class GameManager {

	public static ArrayList<GameRoom> rooms = new ArrayList<GameRoom>();
	public static Location lobby;
	public static Boolean lobbyscoreboard;

	public static GameRoom getGame(String name) {
		for (GameRoom g : rooms) {
			if (g.getName().equalsIgnoreCase(name)) {
				return g;
			}
		}
		return null;
	}

	public static int getCurrentPlayers(GameRoom g) {
		return g.getCurrentPlayers();
	}

	public static int getMaxPlayers(GameRoom g) {
		return g.getMaxPlayers();
	}

	public static ArrayList<World> getRoomWorlds() {
		ArrayList<World> words = new ArrayList<World>();
		for (GameRoom g : rooms) {
			words.add(g.getWorld());
		}
		return words;
	}

	public static void endGames() {
		for (GameRoom g : rooms) {
			g.broadcastMessage(Text.addColor("&cAn ADMIN ordered all games to shut down."));
			g.kickPlayers();
		}
	}

	public static List<GameRoom> getRooms(Selections t) {
		List<GameRoom> f = new ArrayList<GameRoom>();
		switch (t) {
		case MAPV_ALL:
			f.addAll(rooms);
			break;
		case MAPV_WAITING:
			for (GameRoom g : rooms) {
				if (g.getState() == GameState.WAITING) {
					f.add(g);
				}
			}
			break;
		case MAPV_STARTING:
			for (GameRoom g : rooms) {
				if (g.getState() == GameState.STARTING) {
					f.add(g);
				}
			}
			break;
		case MAPV_AVAILABLE:
			for (GameRoom g : rooms) {
				if (g.getState() == GameState.AVAILABLE) {
					f.add(g);
				}
			}
			break;
		case MAPV_SPECTATE:
			for (GameRoom g : rooms) {
				if (g.getState() == GameState.PLAYING || g.getState() == GameState.FINISHING) {
					f.add(g);
				}
			}
			break;
		default:
			break;
		}
		if (f.size() == 0) {
			GameRoom g = new GameRoomSolo("No Maps Found");
			f.add(g);
		}
		return f;
	}

    public static String getStateString(GamePlayer gp, GameState state) {
		return "Aguardando";
    }
}
