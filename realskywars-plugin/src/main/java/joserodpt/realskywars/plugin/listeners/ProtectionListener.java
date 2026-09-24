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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.List;

/**
 * Keeps the lobby, the waiting lobby and everything outside a schematic map's arena from being
 * changed. A schematic map's reset only clears and re-pastes POS1-POS2, so anything changed outside
 * it would survive into the next match; default maps get their whole world recreated instead.
 */
public class ProtectionListener implements Listener {
    private final RealSkywarsAPI rs;

    public ProtectionListener(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    /**
     * Whether a block may not be changed by anything, players or the environment.
     */
    private boolean isProtected(Block block) {
        World w = block.getWorld();
        if (rs.getLobbyManagerAPI().isInLobby(w) || rs.getLobbyManagerAPI().isInWaitingLobby(w)) {
            return true;
        }

        RSWMap map = rs.getMapManagerAPI().getMap(w);
        //an unregistered map is being edited, so it's the admin's to change
        if (map == null || map.isUnregistered() || map.getRSWWorld().getType() != RSWWorld.WorldType.SCHEMATIC) {
            return false;
        }
        return map.getMapCuboid() != null && !map.getMapCuboid().contains(block);
    }

    private boolean isProtected(Player p, Block block) {
        if (!this.isProtected(block)) {
            return false;
        }
        //ops keep building the lobby, but not while they are playing a match
        RSWPlayer rp = rs.getPlayerManagerAPI().getPlayer(p);
        return !p.isOp() || (rp != null && rp.isInMatch());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (this.isProtected(e.getPlayer(), e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (this.isProtected(e.getPlayer(), e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (this.isProtected(e.getPlayer(), e.getBlockClicked().getRelative(e.getBlockFace()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (this.isProtected(e.getPlayer(), e.getBlockClicked())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) //for creeper and TNT explosions
    public void onEntityExplode(EntityExplodeEvent e) {
        this.handleExplosion(e.blockList());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) //for beds and respawn anchors, which explode as blocks
    public void onBlockExplode(BlockExplodeEvent e) {
        this.handleExplosion(e.blockList());
    }

    private void handleExplosion(List<Block> blocks) {
        //dropped from the list rather than cancelling, so a TNT rain inside the arena still breaks the arena
        blocks.removeIf(this::isProtected);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if (this.isProtected(e.getBlock()) || this.isProtected(e.getBlock().getRelative(e.getDirection()))) {
            e.setCancelled(true);
            return;
        }
        for (Block block : e.getBlocks()) {
            //pushing a protected block, or pushing an arena block out of the arena
            if (this.isProtected(block) || this.isProtected(block.getRelative(e.getDirection()))) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        this.cancelIf(e, this.isProtected(e.getBlock()) || e.getBlocks().stream().anyMatch(this::isProtected));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFlow(BlockFromToEvent e) {
        //water or lava spreading out of the arena, or through the lobby
        this.cancelIf(e, this.isProtected(e.getToBlock()));
    }

    private void cancelIf(Cancellable e, boolean cancel) {
        if (cancel) {
            e.setCancelled(true);
        }
    }
}
