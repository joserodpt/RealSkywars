package josegamerpt.realskywars.cages;

import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Location;

import org.bukkit.Material;

import java.util.List;

public interface Cage {

    int getID();

    Location getLoc();

    boolean isEmpty();

    void setCage();

    void addPlayer(RSWPlayer p);

    void removePlayer(RSWPlayer p);

    void tpPlayer(RSWPlayer p);

    int getMaxPlayers();

    List<RSWPlayer> getPlayers();

    void clearCage();

    void setCage(Material m);

    void open();

}
