package pt.josegamerpt.realskywars.cages;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pt.josegamerpt.realskywars.classes.Cage;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.player.GamePlayer;

import java.util.ArrayList;

public class TeamCage implements Cage {

    public int id;
    public int maxPlayers;
    public ArrayList<GamePlayer> p = new ArrayList<GamePlayer>();
    public Location location;
    public Boolean cageSet = false;

    public TeamCage(int i, Location l) {
        this.id = i;
        this.location = l;
    }

    public int getID() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isEmpty() {
        return p.size() > 0;
    }

    public void setCage() {
        if (this.cageSet == false) {
            Location p = this.location;
            int x = p.getBlockX();
            int y = p.getBlockY();
            int z = p.getBlockZ();

            Material m = this.p.get(0).cageBlock;

            // chao
            p.getWorld().getBlockAt(x + 1, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y - 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y - 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(m);

            y = y + 4;

            // teto
            p.getWorld().getBlockAt(x + 1, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y - 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y - 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y - 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(m);

            // paredes 1 e 3
            y = p.getBlockY();
            x = x + 2;
            p.getWorld().getBlockAt(x, y + 0, z).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x, y + 0, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 0, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);

            x = p.getBlockX();
            x = x - 2;
            p.getWorld().getBlockAt(x, y + 0, z).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x, y + 0, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
            p.getWorld().getBlockAt(x, y + 0, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z + 1).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);

            // paredes 3 e 4
            x = p.getBlockX();
            z = p.getBlockZ();
            z = z - 2;
            p.getWorld().getBlockAt(x, y, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y, z).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);

            z = p.getBlockZ();
            z = z + 2;
            p.getWorld().getBlockAt(x, y, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y, z).setType(m);
            p.getWorld().getBlockAt(x, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 1, z).setType(m);
            p.getWorld().getBlockAt(x, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
            p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);
            this.cageSet = true;
        }
    }

    public void addPlayer(GamePlayer p) {
        this.p.add(p);
        setCage();
    }

    public void removePlayer(GamePlayer p) {
        this.p.remove(p);
    }

    public void tpPlayer(GamePlayer p) {
        p.teleport(getLocation());
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getPlayerCount() {
        return p.size();
    }

    public ArrayList<GamePlayer> getPlayers() {
        return this.p;
    }

    public Enum.CageType getType() {
        return Enum.CageType.TEAMS;
    }

    public void open() {
        Location p = this.location;
        int x = p.getBlockX();
        int y = p.getBlockY();
        int z = p.getBlockZ();

        Material m = Material.AIR;

        p.getWorld().getBlockAt(x + 1, y - 1, z).setType(m);
        p.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(m);
        p.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(m);
        p.getWorld().getBlockAt(x, y - 1, z + 1).setType(m);
        p.getWorld().getBlockAt(x, y - 1, z - 1).setType(m);
        p.getWorld().getBlockAt(x, y - 1, z).setType(m);
        p.getWorld().getBlockAt(x - 1, y - 1, z).setType(m);
        p.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(m);
        p.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(m);
    }
}
