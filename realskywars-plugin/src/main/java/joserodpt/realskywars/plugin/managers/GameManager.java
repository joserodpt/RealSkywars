package joserodpt.realskywars.plugin.managers;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.game.modes.Placeholder;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.game.modes.RSWSign;
import joserodpt.realskywars.api.managers.GameManagerAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameManager extends GameManagerAPI {
    private final RealSkywarsAPI rs;
    public GameManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }
    private final ArrayList<RSWGame> games = new ArrayList<>();
    private Location lobbyLOC;
    private Boolean loginTP = true;

    @Override
    public RSWGame getMatch(World world) {
        return this.games.stream()
                .filter(sw -> sw.getRSWWorld().getWorld().equals(world))
                .findFirst()
                .orElse(null);
    }

    @Override
    public RSWGame getGame(String name) {
        return this.games.stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void endGames() {
        this.endingGames = true;

        this.games.parallelStream().forEach(g -> {
            g.kickPlayers(rs.getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.ADMIN_SHUTDOWN));
            g.resetArena(RSWGame.OperationReason.SHUTDOWN);
        });
    }

    @Override
    public List<RSWGame> getRoomsWithSelection(RSWPlayer.MapViewerPref t) {
        List<RSWGame> f = new ArrayList<>();
        switch (t) {
            case MAPV_ALL:
                f.addAll(this.games);
                break;
            case MAPV_WAITING:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(RSWGame.GameState.WAITING)).collect(Collectors.toList()));
                break;
            case MAPV_STARTING:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(RSWGame.GameState.STARTING)).collect(Collectors.toList()));
                break;
            case MAPV_AVAILABLE:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(RSWGame.GameState.AVAILABLE)).collect(Collectors.toList()));
                break;
            case MAPV_SPECTATE:
                f.addAll(this.games.stream().filter(r -> r.getState().equals(RSWGame.GameState.PLAYING) || r.getState().equals(RSWGame.GameState.FINISHING)).collect(Collectors.toList()));
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

    @Override
    public String getStateString(RSWPlayer gp, RSWGame.GameState t) {
        switch (t) {
            case WAITING:
                return rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.MAP_WAITING, false);
            case AVAILABLE:
                return rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.MAP_AVAILABLE, false);
            case STARTING:
                return rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.MAP_STARTING, false);
            case PLAYING:
                return rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.MAP_PLAYING, false);
            case FINISHING:
                return rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.MAP_FINISHING, false);
            case RESETTING:
                return rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.MAP_RESETTING, false);
            default:
                return "NaN";
        }
    }

    @Override
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

    @Override
    public void tpToLobby(RSWPlayer p) {
        if (this.lobbyLOC != null) {
            p.teleport(this.lobbyLOC);
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.LOBBY_TELEPORT, true));
            rs.getPlayerManagerAPI().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);
        } else {
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.LOBBYLOC_NOT_SET, true));
        }
    }

    @Override
    public Location getLobbyLocation() {
        return this.lobbyLOC;
    }

    @Override
    public boolean scoreboardInLobby() {
        return RSWConfig.file().getBoolean("Config.Scoreboard-In-Lobby");
    }

    @Override
    public void removeRoom(RSWGame gr) {
        gr.getSigns().forEach(RSWSign::delete);
        this.games.remove(gr);
    }

    @Override
    public void clearRooms() {
        this.games.clear();
    }

    @Override
    public List<RSWGame> getGames(GameModes pt) {
        switch (pt) {
            case ALL:
                return this.games;
            case SOLO:
                return this.games.stream().filter(r -> r.getGameMode().equals(RSWGame.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS:
                return this.games.stream().filter(r -> r.getGameMode().equals(RSWGame.Mode.TEAMS)).collect(Collectors.toList());
            case RANKED:
                return this.games.stream().filter(RSWGame::isRanked).collect(Collectors.toList());
            case SOLO_RANKED:
                return this.games.stream().filter(r -> r.isRanked() && r.getGameMode().equals(RSWGame.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS_RANKED:
                return this.games.stream().filter(r -> r.isRanked() && r.getGameMode().equals(RSWGame.Mode.TEAMS)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void addRoom(RSWGame s) {
        this.games.add(s);
    }

    @Override
    public void setLobbyLoc(Location location) {
        this.lobbyLOC = location;
    }

    @Override
    public List<String> getRoomNames() {
        return this.games.stream()
                .map(gameRoom -> Text.strip(gameRoom.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean tpLobbyOnJoin() {
        return loginTP;
    }

    @Override
    public boolean isInLobby(World w) {
        return this.lobbyLOC != null && this.lobbyLOC.getWorld().equals(w);
    }

    @Override
    public void findGame(RSWPlayer player, RSWGame.Mode type) {
        UUID playerUUID = player.getUUID();
        if (!rs.getPlayerManagerAPI().getTeleporting().contains(playerUUID)) {
            rs.getPlayerManagerAPI().getTeleporting().add(playerUUID);

            Optional<RSWGame> suitableGame = findSuitableGame(type);
            if (suitableGame.isPresent()) {
                joinSuitableGame(player, suitableGame.get());
            } else {
                handleNoGameFound(player);
            }
        }
    }

    @Override
    protected Optional<RSWGame> findSuitableGame(RSWGame.Mode type) {
        return this.games.stream()
                .filter(game -> game.getGameMode().equals(type) &&
                        (game.getState().equals(RSWGame.GameState.AVAILABLE) ||
                                game.getState().equals(RSWGame.GameState.STARTING) ||
                                game.getState().equals(RSWGame.GameState.WAITING)) &&
                        !game.isFull())
                .findFirst();
    }

    @Override
    protected void joinSuitableGame(RSWPlayer player, RSWGame gameMode) {
        player.sendMessage(rs.getLanguageManagerAPI().getString(player, LanguageManagerAPI.TS.GAME_FOUND, true));
        if (player.isInMatch()) {
            player.getMatch().removePlayer(player);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywarsAPI.getInstance().getPlugin(), () -> {
            gameMode.addPlayer(player);
            rs.getPlayerManagerAPI().getTeleporting().remove(player.getUUID());
        }, 5);
    }

    @Override
    protected void handleNoGameFound(RSWPlayer player) {
        player.sendMessage(rs.getLanguageManagerAPI().getString(player, LanguageManagerAPI.TS.NO_GAME_FOUND, true));
        rs.getPlayerManagerAPI().getTeleporting().remove(player.getUUID());

        if (this.getLobbyLocation().getWorld().equals(player.getWorld())) {
            this.tpToLobby(player);
        }
    }

}
