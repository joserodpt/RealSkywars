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
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;
import java.util.List;

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
        RSWMap match = rs.getMapManagerAPI().getMap(e.getEntity().getWorld());
        if (match != null && match.getProjectileTier() == RSWMap.ProjectileType.BREAK_BLOCKS) {
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

            RSWMap m = rs.getMapManagerAPI().getMap(name);
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(event.getPlayer());

            if (m != null && (event.getPlayer().isOp() || p.getPlayer().hasPermission("rs.admin"))) {
                m.addSign(event.getBlock());
            } else {
                TranslatableLine.NO_MAP_FOUND.send(p, true);
            }
        }
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (RSWConfig.file().getBoolean("Config.Bungeecord.Enabled")) {
            List<RSWMap> maps = new ArrayList<>(rs.getMapManagerAPI().getMaps(MapManagerAPI.MapGamemodes.ALL));
            RSWMap map = maps.get(0);
            event.setMaxPlayers(maps.size() == 1 ? map.getMaxPlayers() : 1);
            event.setMotd(Text.color("&f&lReal&B&LSkywars &r&6Version &e" + rs.getPlugin().getDescription().getVersion() + "\n&dBungeecord &r&2Map: &a" + (maps.size() == 1 ? map.getMapName() : "?")));
        }
    }
}