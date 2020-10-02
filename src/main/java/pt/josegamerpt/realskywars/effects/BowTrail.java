package pt.josegamerpt.realskywars.effects;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitTask;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Trail;
import pt.josegamerpt.realskywars.player.GamePlayer;

public class BowTrail implements Trail {
  public Particle pa;
  public Arrow a;
  public GamePlayer p;
  public BukkitTask task;

  public BowTrail(Particle bowParticle, Projectile entity, GamePlayer gp) {
    pa = bowParticle;
    a = (Arrow) entity;
    p = gp;
    startTask();
  }

  @Override
  public void startTask() {
    task = Bukkit.getServer().getScheduler().runTaskTimer(RealSkywars.pl, () -> {
      if (a.isOnGround() || a.isDead() || a == null) {
        cancelTask();
        return;
      }
      a.getLocation().getWorld().spawnParticle(pa, a.getLocation(), 1);
    }, 1, 1);
  }

  @Override
  public void cancelTask() {
    task.cancel();
    p.removeTrail(this);
  }

  @Override
  public Enum.TrailType getType() {
    return Enum.TrailType.BOW;
  }
}
