package josegamerpt.realskywars.utils;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.cages.Cage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class Demolition {

    private final Location laserloc1;
    private final Cage cage;
    private final int laserTime;
    private final int delayExplosion;

    public Demolition(Location laserloc1, Cage cage, int laserTime, int delayExplosion) {
        this.laserloc1 = laserloc1;
        this.cage = cage;
        this.laserTime = laserTime;
        this.delayExplosion = delayExplosion;
    }


    public void start(Plugin plugin) {
        try {
            Location sploc = laserloc1;
            sploc.setY(255);
            Location cage = this.cage.getLoc();

            cage.add(0.5, 0, 0.5);
            Laser laser = new Laser.GuardianLaser(sploc, cage, this.laserTime, (int) sploc.distance(cage));
            laser.start(plugin);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> this.cage.clearCage(), this.delayExplosion * 20L);
        } catch (Exception e) {
            Debugger.print(Demolition.class, "Could not show win laser for " + this.cage.getLoc().toString() + "\n" + e.getMessage());
        }
    }
}
