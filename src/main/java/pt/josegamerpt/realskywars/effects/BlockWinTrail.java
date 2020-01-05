package pt.josegamerpt.realskywars.effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Trail;
import pt.josegamerpt.realskywars.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockWinTrail implements Trail {

  public GamePlayer p;
  public int executionTime;
  public BukkitTask task;
  public BlockWinType bwp;
  public Material single;
  public List<Material> list;
  public List<Material> randBlocks = new ArrayList<Material>();

  public BlockWinTrail(GamePlayer gp, int seconds) {
    executionTime = seconds;
    p = gp;
    bwp = BlockWinType.RANDOM;
    for (Material m : Material.values()) {
      if (!m.equals(Material.AIR) && m.isSolid() && m.isBlock() && m.isItem()) {
        randBlocks.add(m);
      }
    }
    startTask();
  }

  public BlockWinTrail(GamePlayer gp, int seconds, Material b) {
    executionTime = seconds;
    p = gp;
    bwp = BlockWinType.SINGLE;
    single = b;
    startTask();
  }

  private void stop() {
    Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
      public void run() {
        cancelTask();
      }
    }, executionTime * 20L);
  }

  @Override
  public void startTask() {
    Random rand = new Random();
    task = Bukkit.getServer().getScheduler().runTaskTimer(RealSkywars.pl, new Runnable() {
      public void run() {
        float x = (float) (Math.random() * 2) - 1;
        float y = (float) (Math.random());
        float z = (float) (Math.random() * 2) - 1;
        Vector v = new Vector(x, y, z);
        v.normalize().multiply(.5);

        switch (bwp) {
          case SINGLE:
            FallingBlock fbs = p.getWorld().spawnFallingBlock(p.getLocation().add(0, 3, 0), single.createBlockData());
            fbs.setDropItem(false);
            fbs.setVelocity(v);
            fbs.setHurtEntities(false);
            fbs.setMetadata("trailBlock", new FixedMetadataValue(RealSkywars.pl, 1));
            break;
          case RANDOM:
            Material mr = randBlocks.get(rand.nextInt(randBlocks.size()));
            FallingBlock fbr = p.getWorld().spawnFallingBlock(p.getLocation().add(0, 3, 0), mr.createBlockData());
            fbr.setDropItem(false);
            fbr.setVelocity(v);
            fbr.setHurtEntities(false);
            fbr.setMetadata("trailBlock", new FixedMetadataValue(RealSkywars.pl, 1));
            break;
          default:
            break;
        }
        p.p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 50, 50);
      }
    }, 5, 1);
    stop();
  }

  @Override
  public void cancelTask() {
    task.cancel();
    p.removeTrail(this);
  }

  @Override
  public Enum.TrailType getType() {
    return Enum.TrailType.WINBLOCK;
  }

  public enum BlockWinType {
    RANDOM, SINGLE
  }
}
