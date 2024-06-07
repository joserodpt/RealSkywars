package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Optional;

public abstract class GamesManagerAPI {
    public Boolean endingGames = false;

    public abstract RSWMap getMatch(World world);

    public abstract RSWMap getGame(String name);

    public abstract void endGames();

    public abstract List<RSWMap> getRoomsWithSelection(RSWPlayer rswPlayer);

    public abstract String getStateString(RSWPlayer gp, RSWMap.MapState t);

    public abstract void loadLobby();

    public abstract void tpToLobby(RSWPlayer p);

    public abstract Location getLobbyLocation();

    public abstract boolean scoreboardInLobby();

    public abstract void removeRoom(RSWMap gr);

    public abstract void clearRooms();

    public abstract List<RSWMap> getGames(GameModes pt);

    public abstract void addRoom(RSWMap s);

    public abstract void setLobbyLoc(Location location);

    public abstract List<String> getRoomNames();

    public abstract boolean tpLobbyOnJoin();

    public abstract boolean isInLobby(World w);

    public abstract void findGame(RSWPlayer player, RSWMap.Mode type);

    public abstract Optional<RSWMap> findSuitableGame(RSWMap.Mode type);

    public enum GameModes {SOLO, SOLO_RANKED, TEAMS, TEAMS_RANKED, RANKED, ALL}
}
