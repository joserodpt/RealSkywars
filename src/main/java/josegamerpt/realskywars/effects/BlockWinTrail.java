package josegamerpt.realskywars.effects;

import josegamerpt.realskywars.RealSkywars;
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

public class BlockWinTrail implements Trail {

    private final RSWPlayer p;
    private final int executionTime;
    private BukkitTask task;
    private final BlockWinType bwp;
    private Material single;
    private final List<Material> randBlocks = new ArrayList<>();

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
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), this::cancelTask, this.executionTime * 20L);
    }

    @Override
    public void startTask() {
        this.task = Bukkit.getServer().getScheduler().runTaskTimer(RealSkywars.getPlugin(), () -> {
            float x = (float) (Math.random() * 2) - 1;
            float y = (float) (Math.random());
            float z = (float) (Math.random() * 2) - 1;
            Vector v = new Vector(x, y, z);
            v.normalize().multiply(.5);

            switch (bwp) {
                case SINGLE:
                    FallingBlock fbs = this.p.getWorld().spawnFallingBlock(p.getLocation().add(0, 3, 0), this.single.createBlockData());
                    fbs.setDropItem(false);
                    fbs.setVelocity(v);
                    fbs.setHurtEntities(false);
                    fbs.setMetadata("trailBlock", new FixedMetadataValue(RealSkywars.getPlugin(), 1));
                    break;
                case RANDOM:
                    Material mr = this.randBlocks.get(RealSkywars.getRandom().nextInt(this.randBlocks.size()));
                    FallingBlock fbr = this.p.getWorld().spawnFallingBlock(p.getLocation().add(0, 3, 0), mr.createBlockData());
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
    public TrailType getType() {
        return TrailType.WINBLOCK;
    }

    public enum BlockWinType {
        RANDOM, SINGLE
    }
}
