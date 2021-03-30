package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.Enum.GameState;
import josegamerpt.realskywars.classes.Enum.Selections;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.modes.Placeholder;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private static ArrayList<SWGameMode> rooms = new ArrayList<>();
    private static Boolean lobbyScoreboard = true;
    private static Location lobbyLOC;
    private static Boolean loginTP = true;
    public static Boolean endingGames = false;

    public static SWGameMode getGame(String name) {
        for (SWGameMode g : rooms) {
            if (g.getName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }

    public static int getLoadedInt() {
        return rooms.size();
    }

    public static void endGames() {
        endingGames = true;
        for (SWGameMode g : rooms) {
            g.setState(GameState.RESETTING);
            g.kickPlayers(Text.color("&cAn ADMIN ordered all games to shut down."));
            g.resetArena();
        }
    }

    public static List<SWGameMode> getRoomsWithSelection(Selections t) {
        List<SWGameMode> f = new ArrayList<>();
        switch (t) {
            case MAPV_ALL:
                f.addAll(rooms);
                break;
            case MAPV_WAITING:
                for (SWGameMode g : rooms) {
                    if (g.getState() == GameState.WAITING) {
                        f.add(g);
                    }
                }
                break;
            case MAPV_STARTING:
                for (SWGameMode g : rooms) {
                    if (g.getState() == GameState.STARTING) {
                        f.add(g);
                    }
                }
                break;
            case MAPV_AVAILABLE:
                for (SWGameMode g : rooms) {
                    if (g.getState() == GameState.AVAILABLE) {
                        f.add(g);
                    }
                }
                break;
            case MAPV_SPECTATE:
                for (SWGameMode g : rooms) {
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

    public static String getStateString(RSWPlayer gp, GameState t) {
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

    public static void loadLobby() {
        GameManager.loginTP = Config.file().getBoolean("Config.Auto-Teleport-To-Lobby");
        if (Config.file().isConfigurationSection("Config.Lobby")) {

            double x = Config.file().getDouble("Config.Lobby.X");
            double y = Config.file().getDouble("Config.Lobby.Y");
            double z = Config.file().getDouble("Config.Lobby.Z");
            float yaw = (float) Config.file().getDouble("Config.Lobby.Yaw");
            float pitch = (float) Config.file().getDouble("Config.Lobby.Pitch");
            World world = Bukkit.getServer().getWorld(Config.file().getString("Config.Lobby.World"));
            GameManager.lobbyLOC = new Location(world, x, y, z, yaw, pitch);
        }
    }

    public static void tpToLobby(RSWPlayer p) {
        if (lobbyLOC != null) {
            p.teleport(lobbyLOC);
            p.sendMessage(LanguageManager.getString(p, Enum.TS.LOBBY_TELEPORT, true));
        } else {
            p.sendMessage(LanguageManager.getString(p, Enum.TS.LOBBYLOC_NOT_SET, true));
        }
    }

    public static void tpToLobby(Player p) {
        if (lobbyLOC != null) {
            p.teleport(lobbyLOC);
            p.sendMessage(LanguageManager.getString(new RSWPlayer(false), Enum.TS.LOBBY_TELEPORT, true));
        } else {
            p.sendMessage(LanguageManager.getString(new RSWPlayer(false), Enum.TS.LOBBYLOC_NOT_SET, true));
        }
    }

    public static Location getLobbyLocation() {
        return lobbyLOC;
    }

    public static boolean scoreboardInLobby() {
        return lobbyScoreboard;
    }

    public static void removeRoom(SWGameMode gr) {
        rooms.remove(gr);
    }

    public static void clearRooms() {
        rooms.clear();
    }

    public static ArrayList<SWGameMode> getRooms() {
        return rooms;
    }

    public static void addRoom(SWGameMode s) {
        rooms.add(s);
    }

    public static void setLobbyLoc(Location location) {
        lobbyLOC = location;
    }

    public static List<String> getRoomNames() {
        List<String> sugests = new ArrayList<>();
        rooms.forEach(gameRoom -> sugests.add(ChatColor.stripColor(gameRoom.getName())));
        return sugests;
    }

    public static boolean tpLobbyOnJoin() {
        return loginTP;
    }

    public static boolean isInLobby(Location location) {
        return lobbyLOC != null && lobbyLOC.getWorld().equals(location.getWorld());
    }

    public static List<World> getRoomWorlds() {
        List<World> sugests = new ArrayList<>();
        rooms.forEach(gameRoom -> sugests.add(gameRoom.getWorld()));
        return sugests;
    }
}
