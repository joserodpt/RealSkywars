package josegamerpt.realskywars.effects;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitTask;

public class BowTrail implements Trail {

    private Particle pa;
    private Arrow a;
    private RSWPlayer p;
    private BukkitTask task;

    public BowTrail(Particle bowParticle, Projectile entity, RSWPlayer gp) {
        this.pa = bowParticle;
        this.a = (Arrow) entity;
        this.p = gp;
        startTask();
    }

    @Override
    public void startTask() {
        this.task = Bukkit.getServer().getScheduler().runTaskTimer(RealSkywars.getPlugin(), () -> {
            if (this.a.isOnGround() || this.a.isDead() || this.a == null) {
                cancelTask();
                return;
            }
            this.a.getLocation().getWorld().spawnParticle(this.pa, this.a.getLocation(), 1);
        }, 1, 1);
    }

    @Override
    public void cancelTask() {
        this.task.cancel();
        this.p.removeTrail(this);
    }

    @Override
    public Enum.TrailType getType() {
        return Enum.TrailType.BOW;
    }
}
