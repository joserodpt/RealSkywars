package josegamerpt.realskywars.effects;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockWinTrail implements Trail {

    private RSWPlayer p;
    private int executionTime;
    private BukkitTask task;
    private BlockWinType bwp;
    private Material single;
    private List<Material> list;
    private List<Material> randBlocks = new ArrayList<>();

    public BlockWinTrail(RSWPlayer gp, int seconds) {
        this.executionTime = seconds;
        this.p = gp;
        this.bwp = BlockWinType.RANDOM;
        for (Material m : Material.values()) {
            if (!m.equals(Material.AIR) && m.isSolid() && m.isBlock() && m.isItem()) {
                this.randBlocks.add(m);
            }
        }
        startTask();
    }

    public BlockWinTrail(RSWPlayer gp, int seconds, Material b) {
        this.executionTime = seconds;
        this.p = gp;
        this.bwp = BlockWinType.SINGLE;
        this.single = b;
        startTask();
    }

    private void stop() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), this::cancelTask, executionTime * 20L);
    }

    @Override
    public void startTask() {
        Random rand = new Random();
        this.task = Bukkit.getServer().getScheduler().runTaskTimer(RealSkywars.getPlugin(), () -> {
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
                    fbs.setMetadata("trailBlock", new FixedMetadataValue(RealSkywars.getPlugin(), 1));
                    break;
                case RANDOM:
                    Material mr = randBlocks.get(rand.nextInt(randBlocks.size()));
                    FallingBlock fbr = p.getWorld().spawnFallingBlock(p.getLocation().add(0, 3, 0), mr.createBlockData());
                    fbr.setDropItem(false);
                    fbr.setVelocity(v);
                    fbr.setHurtEntities(false);
                    fbr.setMetadata("trailBlock", new FixedMetadataValue(RealSkywars.getPlugin(), 1));
                    break;
                default:
                    break;
            }
            p.getPlayer().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 50, 50);
        }, 5, 1);
        stop();
    }

    @Override
    public void cancelTask() {
        this.task.cancel();
        this.p.removeTrail(this);
    }

    @Override
    public Enum.TrailType getType() {
        return Enum.TrailType.WINBLOCK;
    }

    public enum BlockWinType {
        RANDOM, SINGLE
    }
}
