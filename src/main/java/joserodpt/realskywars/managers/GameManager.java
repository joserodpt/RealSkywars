package joserodpt.realskywars.managers;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.config.RSWConfig;
import joserodpt.realskywars.game.modes.Placeholder;
import joserodpt.realskywars.game.modes.SWGame;
import joserodpt.realskywars.game.modes.SWGame.GameState;
import joserodpt.realskywars.game.modes.SWSign;
import joserodpt.realskywars.player.PlayerManager;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameManager {
    private final RealSkywars rs;
    public GameManager(RealSkywars rs) {
        this.rs = rs;
    }

    private final ArrayList<SWGame> games = new ArrayList<>();
    public Boolean endingGames = false;
    private Location lobbyLOC;
    private Boolean loginTP = true;

    public SWGame getMatch(World world) {
        return this.games.stream()
                .filter(sw -> sw.getSWWorld().getWorld().equals(world))
                .findFirst()
                .orElse(null);
    }

    public SWGame getGame(String name) {
        return this.games.stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void endGames() {
        this.endingGames = true;

        this.games.parallelStream().forEach(g -> {
            g.kickPlayers(rs.getLanguageManager().getString(LanguageManager.TSsingle.ADMIN_SHUTDOWN));
            g.resetArena(SWGame.OperationReason.SHUTDOWN);
        });
    }

    public List<SWGame> getRoomsWithSelection(RSWPlayer.MapViewerPref t) {
        List<SWGame> f = new ArrayList<>();
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
                f.addAll(this.getGames(GameModes.SOLO));
                break;
            case TEAMS:
                f.addAll(this.getGames(GameModes.TEAMS));
                break;
            case SOLO_RANKED:
                f.addAll(this.getGames(GameModes.SOLO_RANKED));
                break;
            case TEAMS_RANKED:
                f.addAll(this.getGames(GameModes.TEAMS_RANKED));
                break;
            default:
                break;
        }
        if (f.isEmpty()) {
            Placeholder g = new Placeholder("No Maps Found");
            f.add(g);
        }
        return f;
    }

    public String getStateString(RSWPlayer gp, GameState t) {
        switch (t) {
            case WAITING:
                return rs.getLanguageManager().getString(gp, LanguageManager.TS.MAP_WAITING, false);
            case AVAILABLE:
                return rs.getLanguageManager().getString(gp, LanguageManager.TS.MAP_AVAILABLE, false);
            case STARTING:
                return rs.getLanguageManager().getString(gp, LanguageManager.TS.MAP_STARTING, false);
            case PLAYING:
                return rs.getLanguageManager().getString(gp, LanguageManager.TS.MAP_PLAYING, false);
            case FINISHING:
                return rs.getLanguageManager().getString(gp, LanguageManager.TS.MAP_FINISHING, false);
            case RESETTING:
                return rs.getLanguageManager().getString(gp, LanguageManager.TS.MAP_RESETTING, false);
            default:
                return "NaN";
        }
    }

    public void loadLobby() {
        this.loginTP = RSWConfig.file().getBoolean("Config.Auto-Teleport-To-Lobby");
        if (RSWConfig.file().isSection("Config.Lobby")) {
            double x = RSWConfig.file().getDouble("Config.Lobby.X");
            double y = RSWConfig.file().getDouble("Config.Lobby.Y");
            double z = RSWConfig.file().getDouble("Config.Lobby.Z");
            float yaw = RSWConfig.file().getFloat("Config.Lobby.Yaw");
            float pitch = RSWConfig.file().getFloat("Config.Lobby.Pitch");
            World world = Bukkit.getServer().getWorld(RSWConfig.file().getString("Config.Lobby.World"));
            this.lobbyLOC = new Location(world, x, y, z, yaw, pitch);
        }
    }

    public void tpToLobby(RSWPlayer p) {
        if (this.lobbyLOC != null) {
            p.teleport(this.lobbyLOC);
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.LOBBY_TELEPORT, true));
            rs.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);
        } else {
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.LOBBYLOC_NOT_SET, true));
        }
    }

    public Location getLobbyLocation() {
        return this.lobbyLOC;
    }

    public boolean scoreboardInLobby() {
        return RSWConfig.file().getBoolean("Config.Scoreboard-In-Lobby");
    }

    public void removeRoom(SWGame gr) {
        gr.getSigns().forEach(SWSign::delete);
        this.games.remove(gr);
    }

    public void clearRooms() {
        this.games.clear();
    }

    public List<SWGame> getGames(GameModes pt) {
        switch (pt) {
            case ALL:
                return this.games;
            case SOLO:
                return this.games.stream().filter(r -> r.getGameMode().equals(SWGame.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS:
                return this.games.stream().filter(r -> r.getGameMode().equals(SWGame.Mode.TEAMS)).collect(Collectors.toList());
            case RANKED:
                return this.games.stream().filter(SWGame::isRanked).collect(Collectors.toList());
            case SOLO_RANKED:
                return this.games.stream().filter(r -> r.isRanked() && r.getGameMode().equals(SWGame.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS_RANKED:
                return this.games.stream().filter(r -> r.isRanked() && r.getGameMode().equals(SWGame.Mode.TEAMS)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void addRoom(SWGame s) {
        this.games.add(s);
    }

    public void setLobbyLoc(Location location) {
        this.lobbyLOC = location;
    }

    public List<String> getRoomNames() {
        return this.games.stream()
                .map(gameRoom -> Text.strip(gameRoom.getName()))
                .collect(Collectors.toList());
    }

    public boolean tpLobbyOnJoin() {
        return loginTP;
    }

    public boolean isInLobby(World w) {
        return this.lobbyLOC != null && this.lobbyLOC.getWorld().equals(w);
    }

    public void findGame(RSWPlayer player, SWGame.Mode type) {
        UUID playerUUID = player.getUUID();
        if (!rs.getPlayerManager().getTeleporting().contains(playerUUID)) {
            rs.getPlayerManager().getTeleporting().add(playerUUID);

            Optional<SWGame> suitableGame = findSuitableGame(type);
            if (suitableGame.isPresent()) {
                joinSuitableGame(player, suitableGame.get());
            } else {
                handleNoGameFound(player);
            }
        }
    }

    private Optional<SWGame> findSuitableGame(SWGame.Mode type) {
        return this.games.stream()
                .filter(game -> game.getGameMode().equals(type) &&
                        (game.getState().equals(GameState.AVAILABLE) ||
                                game.getState().equals(GameState.STARTING) ||
                                game.getState().equals(GameState.WAITING)) &&
                        !game.isFull())
                .findFirst();
    }

    private void joinSuitableGame(RSWPlayer player, SWGame gameMode) {
        player.sendMessage(rs.getLanguageManager().getString(player, LanguageManager.TS.GAME_FOUND, true));
        if (player.isInMatch()) {
            player.getMatch().removePlayer(player);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
            gameMode.addPlayer(player);
            rs.getPlayerManager().getTeleporting().remove(player.getUUID());
        }, 5);
    }

    private void handleNoGameFound(RSWPlayer player) {
        player.sendMessage(rs.getLanguageManager().getString(player, LanguageManager.TS.NO_GAME_FOUND, true));
        rs.getPlayerManager().getTeleporting().remove(player.getUUID());

        if (this.getLobbyLocation().getWorld().equals(player.getWorld())) {
            this.tpToLobby(player);
        }
    }

    public enum GameModes {SOLO, SOLO_RANKED, TEAMS, TEAMS_RANKED, RANKED, ALL}
}
