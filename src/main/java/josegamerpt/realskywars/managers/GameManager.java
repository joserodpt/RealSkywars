package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.Placeholder;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.SWGameMode.GameState;
import josegamerpt.realskywars.misc.Selections;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameManager {

    public Boolean endingGames = false;
    private ArrayList<SWGameMode> games = new ArrayList<>();
    private Boolean lobbyScoreboard = true;
    private Location lobbyLOC;
    private Boolean loginTP = true;

    public SWGameMode getGame(String name) {
        for (SWGameMode g : this.games) {
            if (g.getName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }

    public int getLoadedInt() {
        return games.size();
    }

    public void endGames() {
        this.endingGames = true;
        for (SWGameMode g : this.games) {
            g.setState(GameState.RESETTING);
            g.kickPlayers(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.ADMIN_SHUTDOWN));
            g.resetArena();
        }
    }

    public List<SWGameMode> getRoomsWithSelection(Selections.Values t) {
        List<SWGameMode> f = new ArrayList<>();
        switch (t) {
            case MAPV_ALL:
                f.addAll(this.games);
                break;
            case MAPV_WAITING:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(GameState.WAITING)).collect(Collectors.toList()));
                break;
            case MAPV_STARTING:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(GameState.STARTING)).collect(Collectors.toList()));
                break;
            case MAPV_AVAILABLE:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(GameState.AVAILABLE)).collect(Collectors.toList()));
                break;
            case MAPV_SPECTATE:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(GameState.PLAYING) || r.getState().equals(GameState.FINISHING)).collect(Collectors.toList()));
                break;
            case SOLO:
                f.addAll(this.getGames(PlayerManager.Modes.SOLO));
                break;
            case TEAMS:
                f.addAll(this.getGames(PlayerManager.Modes.TEAMS));
                break;
            case SOLO_RANKED:
                f.addAll(this.getGames(PlayerManager.Modes.SOLO_RANKED));
                break;
            case TEAMS_RANKED:
                f.addAll(this.getGames(PlayerManager.Modes.TEAMS_RANKED));
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

    public String getStateString(RSWPlayer gp, GameState t) {
        switch (t) {
            case WAITING:
                return RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAP_WAITING, false);
            case AVAILABLE:
                return RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAP_AVAILABLE, false);
            case STARTING:
                return RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAP_STARTING, false);
            case PLAYING:
                return RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAP_PLAYING, false);
            case FINISHING:
                return RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAP_FINISHING, false);
            case RESETTING:
                return RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAP_RESETTING, false);
            default:
                return "NaN";
        }
    }

    public void loadLobby() {
        this.loginTP = Config.file().getBoolean("Config.Auto-Teleport-To-Lobby");
        if (Config.file().isConfigurationSection("Config.Lobby")) {

            double x = Config.file().getDouble("Config.Lobby.X");
            double y = Config.file().getDouble("Config.Lobby.Y");
            double z = Config.file().getDouble("Config.Lobby.Z");
            float yaw = (float) Config.file().getDouble("Config.Lobby.Yaw");
            float pitch = (float) Config.file().getDouble("Config.Lobby.Pitch");
            World world = Bukkit.getServer().getWorld(Config.file().getString("Config.Lobby.World"));
            this.lobbyLOC = new Location(world, x, y, z, yaw, pitch);
        }
    }

    public void tpToLobby(RSWPlayer p) {
        if (this.lobbyLOC != null) {
            p.teleport(this.lobbyLOC);
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.LOBBY_TELEPORT, true));
        } else {
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.LOBBYLOC_NOT_SET, true));
        }
    }

    public Location getLobbyLocation() {
        return lobbyLOC;
    }

    public boolean scoreboardInLobby() {
        return lobbyScoreboard;
    }

    public void removeRoom(SWGameMode gr) {
        games.remove(gr);
    }

    public void clearRooms() {
        games.clear();
    }

    public List<SWGameMode> getGames(PlayerManager.Modes pt) {
        switch (pt)
        {
            case ALL:
                return this.games;
            case SOLO:
                return this.games.stream().filter(r -> r.getGameMode().equals(SWGameMode.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS:
                return this.games.stream().filter(r -> r.getGameMode().equals(SWGameMode.Mode.TEAMS)).collect(Collectors.toList());
            case RANKED:
                return this.games.stream().filter(SWGameMode::isRanked).collect(Collectors.toList());
            case SOLO_RANKED:
                return this.games.stream().filter(r -> r.isRanked() && r.getGameMode().equals(SWGameMode.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS_RANKED:
                return this.games.stream().filter(r -> r.isRanked() && r.getGameMode().equals(SWGameMode.Mode.TEAMS)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void addRoom(SWGameMode s) {
        games.add(s);
    }

    public void setLobbyLoc(Location location) {
        this.lobbyLOC = location;
    }

    public List<String> getRoomNames() {
        List<String> sugests = new ArrayList<>();
        this.games.forEach(gameRoom -> sugests.add(ChatColor.stripColor(gameRoom.getName())));
        return sugests;
    }

    public boolean tpLobbyOnJoin() {
        return loginTP;
    }

    public boolean isInLobby(World w) {
        return this.lobbyLOC != null && this.lobbyLOC.getWorld().equals(w);
    }

    public ArrayList<SWEvent> parseEvents(SWGameMode sgm) {
        ArrayList<SWEvent> ret = new ArrayList<>();
        String search = "Teams";
        switch (sgm.getGameMode()) {
            case SOLO:
                search = "Solo";
                break;
            case TEAMS:
                search = "Teams";
                break;
        }
        for (String s1 : Config.file().getStringList("Config.Events." + search)) {
            String[] parse = s1.split("&");
            SWEvent.EventType et = SWEvent.EventType.valueOf(parse[0]);
            int time = Integer.parseInt(parse[1]);
            ret.add(new SWEvent(sgm, et, time));
        }
        ret.add(new SWEvent(sgm, SWEvent.EventType.BORDERSHRINK, Config.file().getInt("Config.Maximum-Game-Time." + search)));
        return ret;
    }

    public void findGame(RSWPlayer p, SWGameMode.Mode type) {
        if (!PlayerManager.teleporting.contains(p.getUUID()))
        {
            PlayerManager.teleporting.add(p.getUUID());
            Optional<SWGameMode> o = this.games.stream().filter(c -> c.getGameMode().equals(type) && c.getState().equals(GameState.AVAILABLE) || c.getState().equals(GameState.STARTING) && !c.isFull()).findFirst();
            if (o.isPresent() && !o.get().isPlaceHolder()) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.GAME_FOUND, true));
                if (p.isInMatch()) {
                    p.getMatch().removePlayer(p);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                    o.get().addPlayer(p);
                    PlayerManager.teleporting.remove(p.getUUID());
                }, 5);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
                PlayerManager.teleporting.remove(p.getUUID());

                if (this.getLobbyLocation().getWorld().equals(p.getWorld()))
                {
                    this.tpToLobby(p);
                }
            }
        }
    }
}
