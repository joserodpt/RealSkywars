package pt.josegamerpt.realskywars.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.InteractionState;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.MapManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

public class RoomSettings {

	static GameRoom game;
	private UUID uuid;
	private static int refreshTask;
	private static Map<UUID, RoomSettings> inventories = new HashMap<>();
	static Inventory inv;

	static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
	static ItemStack confirm = Itens.createItemLore(Material.CHEST, 1, "&9Save Settings",
			Arrays.asList("&7Click here to confirm your settings."));
	static ItemStack saved = Itens.createItemLore(Material.ENDER_CHEST, 1, "&9Save Settings",
			Arrays.asList("&7Settings saved. You can now exit from this menu."));

	static ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
			Arrays.asList("&7Spectator is turned &aON &7for dead players."));
	static ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
			Arrays.asList("&7Spectator is turned &cOFF &7for dead players."));
	static ItemStack dragon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Winner rides Dragon",
			Arrays.asList("&7Riding is turned &aON &7for the winner(s)."));
	static ItemStack dragoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Winner rides Dragon",
			Arrays.asList("&7Riding is turned &cOFF &7for the winner(s)."));

	static ItemStack aAvailable = Itens.createItemLore(Material.GREEN_CONCRETE, 1, "&9Map Status",
			Arrays.asList("&fCick to change the map status.", "", "&aAvailable", "&7Starting", "&7Waiting", "&7Playing",
					"&7Finishing", "&7Resetting"));
	static ItemStack aStarting = Itens.createItemLore(Material.YELLOW_CONCRETE, 1, "&9Map Status",
			Arrays.asList("&fCick to change the map status.", "", "&7Available", "&aStarting", "&7Waiting", "&7Playing",
					"&7Finishing", "&7Resetting"));
	static ItemStack aWaiting = Itens.createItemLore(Material.LIGHT_BLUE_CONCRETE, 1, "&9Map Status",
			Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&aWaiting", "&7Playing",
					"&7Finishing", "&7Resetting"));
	static ItemStack aPlaying = Itens.createItemLore(Material.RED_CONCRETE, 1, "&9Map Status",
			Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&aPlaying",
					"&7Finishing", "&7Resetting"));
	static ItemStack aFinishing = Itens.createItemLore(Material.GRAY_CONCRETE, 1, "&9Map Status",
			Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing",
					"&aFinishing", "&7Resetting"));
	static ItemStack aResetting = Itens.createItemLore(Material.PURPLE_CONCRETE, 1, "&9Map Status",
			Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing",
					"&7Finishing", "&aResetting"));

	static ItemStack resetRoom = Itens.createItemLore(Material.BARRIER, 1, "&9Reset Room",
			Arrays.asList("&cClick here to reset the room.", "&4NOTE: ALL PLAYERS WILL BE KICKED FROM THE GAME."));

	public RoomSettings(GameRoom g, UUID id) {
		this.uuid = id;
		game = g;

		inv = Bukkit.getServer().createInventory(null, 27, g.getName() + " Settings");

		for (int i = 0; i < 9; i++) {
			inv.setItem(i, placeholder);
		}

		inv.setItem(18, placeholder);
		inv.setItem(19, placeholder);
		inv.setItem(20, placeholder);
		inv.setItem(21, placeholder);
		inv.setItem(22, placeholder);
		inv.setItem(23, placeholder);
		inv.setItem(24, placeholder);
		inv.setItem(25, placeholder);
		inv.setItem(26, placeholder);

		inv.setItem(9, placeholder);
		inv.setItem(17, placeholder);

		// ARENASTATE
		if (game.getState() == GameState.AVAILABLE) {
			inv.setItem(10, aAvailable);
		}
		if (game.getState() == GameState.FINISHING) {
			inv.setItem(10, aFinishing);
		}
		if (game.getState() == GameState.PLAYING) {
			inv.setItem(10, aPlaying);
		}
		if (game.getState() == GameState.RESETTING) {
			inv.setItem(10, aResetting);
		}
		if (game.getState() == GameState.STARTING) {
			inv.setItem(10, aStarting);
		}
		if (game.getState() == GameState.WAITING) {
			inv.setItem(10, aWaiting);
		}

		// DETAILS
		if (game.isSpectatorEnabled() == true) {
			inv.setItem(15, specon);
		} else {
			inv.setItem(15, specoff);
		}
		if (game.isDragonEnabled() == true) {
			inv.setItem(16, dragon);
		} else {
			inv.setItem(16, dragoff);
		}

		ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9Info",
				Arrays.asList("&fPlayers: " + game.getCurrentPlayers() + "/" + game.getMaxPlayers(),
						"&fSpectators: " + game.getCurrentSpectators(), "&fChest Tier: &b" + game.getTierType().name(),
						"&fBlocks Placed: " + game.getBlocksPlaced().size(), "&fBlocks Removed: " + game.getBlocksDestroyed().size(), "",
						"&fRunning Time: " + game.getTimePassed()));
		// infoMap
		inv.setItem(4, infoMap);

		// resetbutton
		inv.setItem(22, resetRoom);

		refresher();

		inventories.put(id, this);
	}

	private void refresher() {
		refreshTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, new Runnable() {
			public void run() {

				ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9Info",
						Arrays.asList("&fPlayers: " + game.getCurrentPlayers() + "/" + game.getMaxPlayers(),
								"&fSpectators: " + game.getCurrentSpectators(), "&fChest Tier: &b" + game.getTierType().name(),
								"&fBlocks Placed: " + game.getBlocksPlaced().size(), "&fBlocks Removed: " + game.getBlocksDestroyed().size(), "",
								"&fRunning Time: " + game.getTimePassed()));
				// infoMap
				inv.setItem(4, infoMap);
			}
		}, 0L, 10L);
	}

	public void openInventory(GamePlayer player) {
		Inventory inv = getInventory();
		InventoryView openInv = player.p.getOpenInventory();
		if (openInv != null) {
			Inventory openTop = player.p.getOpenInventory().getTopInventory();
			if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
				openTop.setContents(inv.getContents());
			} else {
				player.p.openInventory(inv);
			}
			register();
			player.istate = InteractionState.GUI_ROOMSET;
		}
	}

	public static Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onClick(InventoryClickEvent e) {
				HumanEntity clicker = e.getWhoClicked();
				if (clicker instanceof Player) {
					if (e.getCurrentItem() == null) {
						return;
					}
					Player p = (Player) clicker;
					if (p != null) {
						UUID uuid = p.getUniqueId();
						if (inventories.containsKey(uuid)) {
							RoomSettings current = inventories.get(uuid);
							if (!e.getInventory().getType().name()
									.equalsIgnoreCase(current.getInventory().getType().name())) {
								return;
							}
							e.setCancelled(true);

							GamePlayer gp = PlayerManager.getPlayer(p);

							if (gp.istate != InteractionState.GUI_ROOMSET) {
								return;
							}

							if (inv != null) {
								if (inv.getHolder() == e.getInventory().getHolder()) {
									if (e.getClick().equals(ClickType.NUMBER_KEY)) {
										e.setCancelled(true);
									}
									e.setCancelled(true);

									ItemStack clickedItem = e.getCurrentItem();

									if (clickedItem == null || clickedItem.getType() == Material.AIR)
										return;

									// reset
									if (e.getRawSlot() == 22) {
										game.broadcastMessage(
												LanguageManager.getString(gp, TS.ARENA_RESET, false));
										game.kickPlayers();
										game.resetArena();
										p.sendMessage(LanguageManager.getString(gp, TS.MAP_RESET_DONE, true));

										if (game.getState() == GameState.AVAILABLE) {
											current.getInventory().setItem(10, aAvailable);
										}
										if (game.getState() == GameState.FINISHING) {
											current.getInventory().setItem(10, aFinishing);
										}
										if (game.getState() == GameState.PLAYING) {
											current.getInventory().setItem(10, aPlaying);
										}
										if (game.getState() == GameState.RESETTING) {
											current.getInventory().setItem(10, aResetting);
										}
										if (game.getState() == GameState.STARTING) {
											current.getInventory().setItem(10, aStarting);
										}
										if (game.getState() == GameState.WAITING) {
											current.getInventory().setItem(10, aWaiting);
										}
									}

									// arstat
									if (e.getRawSlot() == 10) {
										if (game.getState() == GameState.AVAILABLE) {
											game.setState(GameState.STARTING);
											current.getInventory().setItem(10, aStarting);
										} else if (game.getState() == GameState.STARTING) {
											game.setState(GameState.WAITING);
											current.getInventory().setItem(10, aWaiting);
										} else if (game.getState() == GameState.WAITING) {
											game.setState(GameState.PLAYING);
											current.getInventory().setItem(10, aPlaying);
										} else if (game.getState() == GameState.PLAYING) {
											game.setState(GameState.FINISHING);
											current.getInventory().setItem(10, aFinishing);
										} else if (game.getState() == GameState.FINISHING) {
											game.setState(GameState.RESETTING);
											current.getInventory().setItem(10, aResetting);
										} else if (game.getState() == GameState.RESETTING) {
											game.setState(GameState.AVAILABLE);
											current.getInventory().setItem(10, aAvailable);
										}

										p.sendMessage(LanguageManager.getString(gp, TS.GAME_STATUS_SET, true).replace("%status%", game.getState().name()));
									}

									// settings
									if (e.getRawSlot() == 15) {
										if (game.isSpectatorEnabled() == true) {
											game.setSpectator(false);
											current.getInventory().setItem(15, specoff);
										} else {
											game.setSpectator(true);
											current.getInventory().setItem(15, specon);
										}
										MapManager.saveSettings(game);
									}
									if (e.getRawSlot() == 16) {
										if (game.isDragonEnabled() == true) {
											game.setDragon(false);
											current.getInventory().setItem(16, dragoff);
										} else {
											game.setDragon(true);
											current.getInventory().setItem(16, dragon);
										}
										MapManager.saveSettings(game);
									}
								}
							}
						}
					}
				}
			}

			@EventHandler
			public void onClose(InventoryCloseEvent e) {
				if (e.getPlayer() instanceof Player) {
					if (e.getInventory() == null) {
						return;
					}
					Player p = (Player) e.getPlayer();
					UUID uuid = p.getUniqueId();
					if (inventories.containsKey(uuid)) {
						inventories.get(uuid).unregister();

						Bukkit.getScheduler().cancelTask(refreshTask);

						PlayerManager.getPlayer(p).istate = InteractionState.NONE;
					}
				}
			}
		};
	}

	private Inventory getInventory() {
		return inv;
	}

	private void register() {
		inventories.put(this.uuid, this);
	}

	private void unregister() {
		inventories.remove(this.uuid);
	}
}
