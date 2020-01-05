package pt.josegamerpt.realskywars.cages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.classes.Cage;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.player.GamePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SoloCage implements Cage {

    public int id;
    public GamePlayer p;
    public Location location;

    public SoloCage(int i, Location l) {
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
        return p == null;
    }

    public void setCage() {
        Location p = this.location;
        int x = p.getBlockX();
        int y = p.getBlockY();
        int z = p.getBlockZ();

        Material m = this.p.cageBlock;

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
    }

    public void addPlayer(GamePlayer pl) {
        this.p = pl;
        pl.cage = this;
        this.setCage();
        this.tpPlayer(pl);
        Debugger.print("[SOLOCAGE " + this.id + "] added " + pl.getName());
    }

    public void removePlayer(GamePlayer p) {
        p.cage = null;
        this.p = null;
        Debugger.print("[SOLOCAGE " + this.id + "] removed " + p.getName());
    }

    public void tpPlayer(GamePlayer p) {
        p.teleport(getLocation());
    }

    public int getMaxPlayers() {
        return 1;
    }

    public int getPlayerCount() {
        if (isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public List<GamePlayer> getPlayers() {
        return Collections.singletonList(this.p);
    }

    public Enum.CageType getType() {
        return Enum.CageType.SOLO;
    }
}
