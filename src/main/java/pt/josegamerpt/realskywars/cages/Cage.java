package pt.josegamerpt.realskywars.cages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.player.GamePlayer;

import java.util.List;

public interface Cage {

    int getID();

    Location getLocation();

    boolean isEmpty();

    void setCage();

    void addPlayer(GamePlayer p);

    void removePlayer(GamePlayer p);

    void tpPlayer(GamePlayer p);

    int getMaxPlayers();

    int getPlayerCount();

    List<GamePlayer> getPlayers();

    Enum.CageType getType();

    void resetCage();

}
