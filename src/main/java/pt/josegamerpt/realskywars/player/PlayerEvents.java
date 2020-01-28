package pt.josegamerpt.realskywars.player;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Trail;
import pt.josegamerpt.realskywars.configuration.Items;
import pt.josegamerpt.realskywars.effects.BowTrail;
import pt.josegamerpt.realskywars.managers.EffectsManager;
import pt.josegamerpt.realskywars.managers.GameManager;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;

public class PlayerEvents implements Listener {

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity().hasMetadata("invencivel"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(Items.SHOP)
				|| event.getItemDrop().getItemStack().equals(Items.CAGESET)
				|| event.getItemDrop().getItemStack().equals(Items.CHESTS)
				|| event.getItemDrop().getItemStack().equals(Items.KITS)
				|| event.getItemDrop().getItemStack().equals(Items.MAPS)
				|| event.getItemDrop().getItemStack().equals(Items.KITS)
				|| event.getItemDrop().getItemStack().equals(Items.LEAVE)
				|| event.getItemDrop().getItemStack().getType().equals(Material.PLAYER_HEAD)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player pkilled = e.getEntity();
		Player pkiller = e.getEntity().getKiller();

		Location deathLoc = null;

		if (pkiller instanceof Player) {
			GamePlayer killer = PlayerManager.getPlayer(pkiller);
			if (killer.room != null) {
				killer.addKill(1);
			}
			deathLoc = pkiller.getLocation();
		}

		GamePlayer killed = PlayerManager.getPlayer(pkilled);
		if (killed.room != null) {

			for (Player players : Bukkit.getServer().getOnlinePlayers()) {
				players.hidePlayer(RealSkywars.pl, killed.p);
			}

			killed.addDeath(1);

			Location finalDeathLoc = deathLoc;
			Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
				@Override
				public void run() {
					killed.p.spigot().respawn();
					;
					if (finalDeathLoc == null) {
						killed.room.spectate(killed, killed.room.getSpectatorLocation());
					} else {
						killed.room.spectate(killed, pkiller.getLocation());
					}
				}
			}, 1);
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player whoWasHit = (Player) e.getEntity();
			Player whoHit = (Player) e.getDamager();
			GamePlayer hitter = PlayerManager.getPlayer(whoHit);
			GamePlayer hurt = PlayerManager.getPlayer(whoWasHit);
			if (hitter.team != null) {
				if (hitter.team.members.contains(hurt)) {
					whoHit.sendMessage(LanguageManager.getString(hitter, Enum.TS.TEAMMATE_DAMAGE_CANCEL, true));
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerManager.giveItems(event.getPlayer(), PlayerManager.PlayerItems.LOBBY);
		PlayerManager.loadPlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		GamePlayer p = PlayerManager.getPlayer(e.getPlayer());
		if (p.room != null) {
			p.room.checkWin();
			p.room.removePlayer(p);
		}

		p.saveData();
		p.stopTrails();

		p.ps.stop();
		PlayerManager.tpLobby(p);
		PlayerManager.players.remove(p);
	}

	@EventHandler
	public void onPlayerShootArrow(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() != null &&
				e.getEntity().getShooter() instanceof Player &&
				e.getEntity() instanceof Arrow) {
			Player p = (Player) e.getEntity().getShooter();
			GamePlayer gp = PlayerManager.getPlayer(p);
			if (gp.bowParticle != null) {
				if (gp.isInGame()) {
					gp.addTrail(new BowTrail(gp.bowParticle, e.getEntity(), gp));
				}
			}
		}
	}
}
