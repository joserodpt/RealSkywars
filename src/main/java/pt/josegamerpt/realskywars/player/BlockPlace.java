package pt.josegamerpt.realskywars.player;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.GameType;
import pt.josegamerpt.realskywars.classes.Enum.PlayerState;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.Team;
import pt.josegamerpt.realskywars.configuration.Items;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.utils.Calhau;
import pt.josegamerpt.realskywars.utils.Holograms;

public class BlockPlace implements Listener {

	@EventHandler
	public void place(BlockPlaceEvent event) {
		GamePlayer pg = PlayerManager.getPlayer(event.getPlayer());
		if (pg.setup != null) {
			if (event.getBlock().getType() == Items.CAGESET.getType()) {
				if (pg.setup.gameType.equals(GameType.SOLO)) {
					if (pg.setup.placedCages < pg.setup.maxPlayers) {
						log(event, pg);
						pg.setup.placedCages++;
					} else {
						log(event, pg);
						pg.setup.confirmCages = true;
						pg.p.sendMessage(LanguageManager.getString(pg, TS.CAGES_SET, false));
					}
				} else {
					if (pg.setup.placedCages < pg.setup.teams) {
						log(event, pg);
						pg.setup.placedCages++;
					} else {
						log(event, pg);
						pg.setup.confirmCages = true;
						pg.p.sendMessage(LanguageManager.getString(pg, TS.CAGES_SET, false));
					}
				}
			}
		}
		if (pg.state == PlayerState.EXTERNAL_SPECTATOR) {
			event.setCancelled(true);
			return;
		}
		if (pg.room != null) {
			if (pg.room.getState().equals(GameState.PLAYING)) {
				pg.room.getBlocksPlaced().add(new Calhau(event.getBlock()));
			} else {
				event.setCancelled(true);
			}
		}
	}

	public void log(BlockPlaceEvent e, GamePlayer p) {
		Location loc = e.getBlock().getLocation().add(0.5, 0, 0.5);
		if (p.setup.gameType.equals(GameType.SOLO)) {
			Holograms.add("&aCage &9" + p.setup.placedCages, loc);
			p.setup.Cages.add(loc);
			e.getPlayer().sendMessage(ChatColor.GREEN + "You placed cage number " + p.setup.placedCages);
		}
		if (p.setup.gameType.equals(GameType.TEAMS)) {
			Holograms.add("&aTeam &9" + p.setup.placedCages + "&a Cage", loc);
			Team t = new Team(p.setup.placedCages, p.setup.playersPerTeam, loc);
			p.setup.teamslist.add(t);
			e.getPlayer().sendMessage(ChatColor.GREEN + "You placed cage number " + p.setup.placedCages);
		}
	}
}
