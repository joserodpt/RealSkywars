package josegamerpt.realskywars.cages;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;

public class SoloCage implements Cage {

    private final int id;
    private final int x;
    private final int y;
    private final int z;
    private final int locx;
    private final int locy;
    private final int locz;
    private final String worldName;
    private RSWPlayer p;

    public SoloCage(int i, int x, int y, int z, String worldName, int locx, int locy, int locz) {
        this.id = i;
        this.x = x;
        this.y = y;
        this.z = z;
        this.locx = locx;
        this.locy = locy;
        this.locz = locz;
        this.worldName = worldName;
    }

    //CREDIT open source spigot
    public static Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

    public int getID() {
        return this.id;
    }

    public Location getLoc() {
        return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z).add(0.5, 0, 0.5);
    }

    public boolean isEmpty() {
        return p == null;
    }

    public void setCage(Material m) {
        World w = Bukkit.getWorld(this.worldName);

        w.getBlockAt(x, y - 1, z).setType(m);
        w.getBlockAt(x, y, z + 1).setType(m);
        w.getBlockAt(x, y, z - 1).setType(m);
        w.getBlockAt(x, y + 3, z).setType(m);
        w.getBlockAt(x, y + 1, z + 1).setType(m);
        w.getBlockAt(x, y + 2, z + 1).setType(m);
        w.getBlockAt(x, y + 2, z + 1).setType(m);
        w.getBlockAt(x, y + 1, z - 1).setType(m);
        w.getBlockAt(x, y + 2, z - 1).setType(m);
        w.getBlockAt(x, y + 2, z - 1).setType(m);
        w.getBlockAt(x - 1, y, z).setType(m);
        w.getBlockAt(x - 1, y + 1, z).setType(m);
        w.getBlockAt(x - 1, y + 2, z).setType(m);
        w.getBlockAt(x - 1, y + 2, z).setType(m);
        w.getBlockAt(x + 1, y, z).setType(m);
        w.getBlockAt(x + 1, y + 1, z).setType(m);
        w.getBlockAt(x + 1, y + 2, z).setType(m);
        w.getBlockAt(x + 1, y + 2, z).setType(m);
    }

    public void setCage() {
        setCage((Material) this.p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK));
    }

    public void clearCage() {
        setCage(Material.AIR);
    }

    public void addPlayer(RSWPlayer pl) {
        this.p = pl;
        pl.setCage(this);
        this.setCage();
        this.tpPlayer(pl);
    }

    public void removePlayer(RSWPlayer p) {
        p.setCage(null);
        this.p = null;
    }

    public void tpPlayer(RSWPlayer p) {
        Location lookat = new Location(Bukkit.getWorld(this.worldName), this.locx, this.locy, this.locz);
        p.teleport(lookAt(getLoc(), lookat));
    }

    public int getMaxPlayers() {
        return 1;
    }

    public int getPlayerCount() {
        return isEmpty() ? 1 : 0;
    }

    public List<RSWPlayer> getPlayers() {
        return Collections.singletonList(this.p);
    }

    public void open() {
        Material m = Material.AIR;

        World w = Bukkit.getWorld(this.worldName);
        w.getBlockAt(x, y - 1, z).setType(m);

        this.p.setInvincible(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
            if (this.p != null) this.p.setInvincible(false);
        }, 200);
    }

}
