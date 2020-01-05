package pt.josegamerpt.realskywars.cages;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pt.josegamerpt.realskywars.classes.Cage;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.player.GamePlayer;

import java.util.List;

public class TeamsCage implements Cage {

    public int id;
    public int maxPlayers;
    public List<GamePlayer> p;
    public Location location;
    public Boolean cageSet = false;

    public int getID() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return this.getLocation();
    }

    public boolean isEmpty() {
        return p.size() > 0;
    }

    public void setCage() {
        if (cageSet == false) {
            Location p = this.location;
            int x = p.getBlockX();
            int y = p.getBlockY();
            int z = p.getBlockZ();

            Material m = this.p.get(0).cageBlock;

            p.getWorld().getBlockAt(x, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x, y, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 3, z).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
            p.getWorld().getBlockAt(x - 1, y, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);
            this.cageSet = true;
        }
    }

    public void addPlayer(GamePlayer p) {

    }

    public void removePlayer(GamePlayer p) {

    }

    public void tpPlayer(GamePlayer p) {
        p.teleport(getLocation());
    }

    public void tp(Player pt) {
        pt.teleport(this.location);
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getPlayerCount() {
        return p.size();
    }

    public List<GamePlayer> getPlayers() {
        return this.p;
    }

    public Enum.CageType getType() {
        return Enum.CageType.TEAMS;
    }
}
