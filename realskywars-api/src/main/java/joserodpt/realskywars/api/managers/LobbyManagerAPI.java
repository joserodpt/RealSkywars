package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class LobbyManagerAPI {
    public abstract void loadLobby();

    public abstract void tpToLobby(RSWPlayer p);

    public abstract Location getLobbyLocation();

    public abstract boolean scoreboardInLobby();

    public abstract void setLobbyLoc(Location location);

    public abstract boolean tpLobbyOnJoin();

    public abstract boolean isInLobby(World w);
}
