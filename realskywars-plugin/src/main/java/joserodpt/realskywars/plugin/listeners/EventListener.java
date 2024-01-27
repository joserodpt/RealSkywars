package joserodpt.realskywars.plugin.listeners;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
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
    private final RealSkywarsAPI rs;
    public EventListener(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @EventHandler
    public void blockChangeEvent(EntityChangeBlockEvent e) {
        Entity ent = e.getEntity();

        if (ent.hasMetadata("trailBlock")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void projectileHitEvent(ProjectileHitEvent e) {
        RSWGame match = rs.getGameManagerAPI().getMatch(e.getEntity().getWorld());
        if (match != null && match.getProjectileTier() == RSWGame.ProjectileType.BREAK_BLOCKS) {
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

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).contains("[RSW]") || event.getLine(0).contains("[rsw]")) {
            event.setLine(0, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix());
            String name = event.getLine(1);

            RSWGame m = rs.getGameManagerAPI().getGame(name);
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());

            if (m != null && (event.getPlayer().isOp() || p.getPlayer().hasPermission("rs.admin"))) {
                m.addSign(event.getBlock());
            } else {
                p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_GAME_FOUND, true));
            }
        }
    }
}