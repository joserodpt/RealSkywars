package pt.josegamerpt.realskywars.managers;

import org.bukkit.Location;
import org.bukkit.World;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.Selections;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.modes.Placeholder;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    public static ArrayList<GameRoom> rooms = new ArrayList<>();
    public static Location lobbyLOC;
    public static Boolean lobbyScoreboard;
    public static Boolean loginTP;

    public static GameRoom getGame(String name) {
        for (GameRoom g : rooms) {
            if (g.getName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }

    public static int getLoadedInt() {
        return rooms.size();
    }

    public static ArrayList<World> getRoomWorlds() {
        ArrayList<World> words = new ArrayList<>();
        for (GameRoom g : rooms) {
            words.add(g.getWorld());
        }
        return words;
    }

    public static void endGames() {
        for (GameRoom g : rooms) {
            g.kickPlayers(Text.addColor("&cAn ADMIN ordered all games to shut down."));
        }
    }

    public static List<GameRoom> getRooms(Selections t) {
        List<GameRoom> f = new ArrayList<>();
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
            Placeholder g = new Placeholder("No Maps Found");
            f.add(g);
        }
        return f;
    }

    public static String getStateString(GamePlayer gp, GameState t) {
        switch (t) {
            case WAITING:
                return LanguageManager.getString(gp, Enum.TS.MAP_WAITING, false);
            case AVAILABLE:
                return LanguageManager.getString(gp, Enum.TS.MAP_AVAILABLE, false);
            case STARTING:
                return LanguageManager.getString(gp, Enum.TS.MAP_STARTING, false);
            case PLAYING:
                return LanguageManager.getString(gp, Enum.TS.MAP_PLAYING, false);
            case FINISHING:
                return LanguageManager.getString(gp, Enum.TS.MAP_FINISHING, false);
            case RESETTING:
                return LanguageManager.getString(gp, Enum.TS.MAP_RESETTING, false);
            default:
                return "NaN";
        }
    }
}
