package josegamerpt.realskywars.listeners;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityEvents implements Listener {

    @EventHandler
    public void blockChangeEvent(EntityChangeBlockEvent e) {
        Entity ent = e.getEntity();

        if (ent.hasMetadata("trailBlock")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void projectileHitEvent(ProjectileHitEvent e) {
        Entity ent = e.getEntity();

        if (RealSkywars.getGameManager().isInGame(ent.getWorld())) {
            SWGameMode match = RealSkywars.getGameManager().getMatch(ent.getWorld());
            if (match.getProjectile() == SWGameMode.ProjectileType.BREAK_BLOCKS) {
                Projectile projectile = e.getEntity();
                if (projectile instanceof EnderPearl) {
                    return;
                }

                Block block = e.getHitBlock();
                if (block == null)
                    return;
                block.breakNaturally();
            }
        }
    }
}