package pt.josegamerpt.realskywars.effects;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitTask;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Trail;

public class BowTrail implements Trail {
  public Particle p;
  public Arrow a;
  public BukkitTask task;

  public BowTrail(Particle bowParticle, Projectile entity) {
    p = bowParticle;
    a = (Arrow) entity;
    startTask();
  }

  @Override
  public void startTask() {
    task = Bukkit.getServer().getScheduler().runTaskTimer(RealSkywars.pl, new Runnable() {
      public void run() {
        if (a.isOnGround() || a.isDead() || a == null) {
          cancelTask();
          return;
        }
        a.getLocation().getWorld().spawnParticle(p, a.getLocation(), 1);
      }
    }, 1, 1);
  }

  @Override
  public void cancelTask() {
    task.cancel();
  }

  @Override
  public Enum.TrailType getType() {
    return Enum.TrailType.BOW;
  }
}
