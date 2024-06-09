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
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.GamesManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.PlaceholderMode;
import joserodpt.realskywars.api.map.modes.RSWSign;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GamesManager extends GamesManagerAPI {
    private final RealSkywarsAPI rs;

    public GamesManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    private final Map<String, RSWMap> games = new HashMap<>();
    private Location lobbyLOC;
    private Boolean loginTP = true;

    @Override
    public RSWMap getMap(World world) {
        return this.games.values().stream()
                .filter(sw -> sw.getRSWWorld().getWorld().equals(world))
                .findFirst()
                .orElse(null);
    }

    @Override
    public RSWMap getMap(String name) {
        return this.games.get(name);
    }

    @Override
    public void endGames() {
        this.endingGames = true;

        this.games.values().parallelStream().forEach(g -> {
            g.kickPlayers(TranslatableLine.ADMIN_SHUTDOWN.getSingle());
            g.resetArena(RSWMap.OperationReason.SHUTDOWN);
        });
    }

    @Override
    public List<RSWMap> getRoomsWithSelection(RSWPlayer rswPlayer) {
        List<RSWMap> f = new ArrayList<>();
        switch (rswPlayer.getMapViewerPref()) {
            case MAPV_ALL:
                f.addAll(rswPlayer.getPlayer().hasPermission("rsw.admin") || rswPlayer.getPlayer().isOp() ? this.games.values() : this.games.values().stream().filter(RSWMap::isUnregistered).collect(Collectors.toList()));
                break;
            case MAPV_WAITING:
                f.addAll(this.games.values().stream().filter(r -> r.getState().equals(RSWMap.MapState.WAITING) && r.isUnregistered()).collect(Collectors.toList()));
                break;
            case MAPV_STARTING:
                f.addAll(this.games.values().stream().filter(r -> r.getState().equals(RSWMap.MapState.STARTING) && r.isUnregistered()).collect(Collectors.toList()));
                break;
            case MAPV_AVAILABLE:
                f.addAll(this.games.values().stream().filter(r -> r.getState().equals(RSWMap.MapState.AVAILABLE) && r.isUnregistered()).collect(Collectors.toList()));
                break;
            case MAPV_SPECTATE:
                f.addAll(this.games.values().stream().filter(r -> (r.getState().equals(RSWMap.MapState.PLAYING) || r.getState().equals(RSWMap.MapState.FINISHING) && r.isUnregistered())).collect(Collectors.toList()));
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
        return f.isEmpty() ? Collections.singletonList(new PlaceholderMode("No Maps Found")) : f;
    }

    @Override
    public List<RSWMap> getGames(GameModes pt) {
        switch (pt) {
            case ALL:
                return new ArrayList<>(this.games.values());
            case SOLO:
                return this.games.values().stream().filter(r -> r.getGameMode().equals(RSWMap.Mode.SOLO) && r.isUnregistered()).collect(Collectors.toList());
            case TEAMS:
                return this.games.values().stream().filter(r -> r.getGameMode().equals(RSWMap.Mode.TEAMS) && r.isUnregistered()).collect(Collectors.toList());
            case RANKED:
                return this.games.values().stream().filter(rswGame -> rswGame.isRanked() && rswGame.isUnregistered()).collect(Collectors.toList());
            case SOLO_RANKED:
                return this.games.values().stream().filter(r -> r.isRanked() && r.isUnregistered() && r.getGameMode().equals(RSWMap.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS_RANKED:
                return this.games.values().stream().filter(r -> r.isRanked() && r.isUnregistered() && r.getGameMode().equals(RSWMap.Mode.TEAMS)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String getStateString(RSWPlayer p, RSWMap.MapState t) {
        switch (t) {
            case WAITING:
                return TranslatableLine.MAP_WAITING.get(p);
            case AVAILABLE:
                return TranslatableLine.MAP_AVAILABLE.get(p);
            case STARTING:
                return TranslatableLine.MAP_STARTING.get(p);
            case PLAYING:
                return TranslatableLine.MAP_PLAYING.get(p);
            case FINISHING:
                return TranslatableLine.MAP_FINISHING.get(p);
            case RESETTING:
                return TranslatableLine.MAP_RESETTING.get(p);
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
            TranslatableLine.LOBBY_TELEPORT.send(p, true);
            RSWPlayerItems.LOBBY.giveSet(p);
        } else {
            TranslatableLine.LOBBYLOC_NOT_SET.send(p, true);
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
    public void removeRoom(RSWMap gr) {
        gr.getSigns().forEach(RSWSign::delete);
        this.games.remove(gr);
    }

    @Override
    public void clearRooms() {
        this.games.clear();
    }

    @Override
    public void addMap(RSWMap s) {
        this.games.put(s.getMapName(), s);
    }

    @Override
    public void setLobbyLoc(Location location) {
        this.lobbyLOC = location;
    }

    @Override
    public List<String> getRoomNames() {
        return new ArrayList<>(this.games.keySet());
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
    public void findGame(RSWPlayer player, RSWMap.Mode type) {
        UUID playerUUID = player.getUUID();
        if (!rs.getPlayerManagerAPI().getTeleporting().contains(playerUUID)) {
            rs.getPlayerManagerAPI().getTeleporting().add(playerUUID);

            Optional<RSWMap> suitableGame = findSuitableGame(type);
            if (suitableGame.isPresent()) {
                if (suitableGame.get().isFull()) {
                    TranslatableLine.ROOM_FULL.send(player, true);
                    rs.getPlayerManagerAPI().getTeleporting().remove(playerUUID);
                    return;
                }

                TranslatableLine.MAP_FOUND.send(player, true);
                if (player.isInMatch()) {
                    player.getMatch().removePlayer(player);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywarsAPI.getInstance().getPlugin(), () -> {
                    suitableGame.get().addPlayer(player);
                    rs.getPlayerManagerAPI().getTeleporting().remove(player.getUUID());
                }, 5);
            } else {
                TranslatableLine.NO_MAP_FOUND.send(player, true);
                rs.getPlayerManagerAPI().getTeleporting().remove(player.getUUID());

                if (this.getLobbyLocation() != null && this.getLobbyLocation().getWorld() != null && Objects.equals(this.getLobbyLocation().getWorld(), player.getWorld())) {
                    this.tpToLobby(player);
                }
            }
        }
    }

    @Override
    public Optional<RSWMap> findSuitableGame(RSWMap.Mode type) {
        return type == null ? this.games.values().stream().findFirst() : this.games.values().stream()
                .filter(game -> game.getGameMode().equals(type) &&
                        (game.getState().equals(RSWMap.MapState.AVAILABLE) ||
                                game.getState().equals(RSWMap.MapState.STARTING) ||
                                game.getState().equals(RSWMap.MapState.WAITING)))
                .findFirst();

    }

}
