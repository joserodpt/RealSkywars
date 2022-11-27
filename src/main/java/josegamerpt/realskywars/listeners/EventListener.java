package josegamerpt.realskywars.listeners;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EventListener implements Listener {

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
            if (match.getProjectileTier() == SWGameMode.ProjectileType.BREAK_BLOCKS) {
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

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).contains("[RSW]") || event.getLine(0).contains("[rsw]")) {
            event.setLine(0, RealSkywars.getLanguageManager().getPrefix());
            String name = event.getLine(1);

            SWGameMode m = RealSkywars.getGameManager().getGame(name);
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer(event.getPlayer());

            if (m != null && event.getPlayer().isOp()) {
                RealSkywars.getSignManager().addSign(m, event.getBlock());
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_GAME_FOUND, true));
            }
        }
    }
}