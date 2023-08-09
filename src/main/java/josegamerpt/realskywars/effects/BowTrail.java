package josegamerpt.realskywars.effects;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitTask;

public class BowTrail implements Trail {
    private final Particle pa;
    private final Arrow a;
    private final RSWPlayer p;
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
            if (this.a == null || this.a.isOnGround() || this.a.isDead()) {
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
    public TrailType getType() {
        return TrailType.BOW;
    }
}
