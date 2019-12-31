package pt.josegamerpt.realskywars.player;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.PlayerState;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.managers.MapManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.utils.Calhau;

public class BlockBreak implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		GamePlayer p = PlayerManager.getPlayer(event.getPlayer());
		if (p != null) {
			if (p.state == PlayerState.EXTERNAL_SPECTATOR) {
				event.setCancelled(true);
				return;
			}
			if (p.room != null) {
				if (p.room.getState().equals(GameState.PLAYING)) {
					p.room.getBlocksDestroyed().add(new Calhau(event.getBlock()));
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		for (String s : MapManager.getRegisteredMaps()) {
			if (e.getLocation().getWorld().getName().equalsIgnoreCase(s)) {
				GameRoom g = MapManager.getMap(s);
				for (Block b : e.blockList()) {
					Calhau ss = new Calhau(b);
					if (!g.getBlocksPlaced().contains(ss)) {
						g.getBlocksPlaced().add(ss);
					}
				}
			}
		}
	}
}
