package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Optional;

public abstract class GameManagerAPI {
    public Boolean endingGames = false;

    public abstract RSWGame getMatch(World world);

    public abstract RSWGame getGame(String name);

    public abstract void endGames();

    public abstract List<RSWGame> getRoomsWithSelection(RSWPlayer rswPlayer);

    public abstract String getStateString(RSWPlayer gp, RSWGame.GameState t);

    public abstract void loadLobby();

    public abstract void tpToLobby(RSWPlayer p);

    public abstract Location getLobbyLocation();

    public abstract boolean scoreboardInLobby();

    public abstract void removeRoom(RSWGame gr);

    public abstract void clearRooms();

    public abstract List<RSWGame> getGames(GameModes pt);

    public abstract void addRoom(RSWGame s);

    public abstract void setLobbyLoc(Location location);

    public abstract List<String> getRoomNames();

    public abstract boolean tpLobbyOnJoin();

    public abstract boolean isInLobby(World w);

    public abstract void findGame(RSWPlayer player, RSWGame.Mode type);

    protected abstract Optional<RSWGame> findSuitableGame(RSWGame.Mode type);

    protected abstract void joinSuitableGame(RSWPlayer player, RSWGame gameMode);

    protected abstract void handleNoGameFound(RSWPlayer player);

    public enum GameModes {SOLO, SOLO_RANKED, TEAMS, TEAMS_RANKED, RANKED, ALL}
}
