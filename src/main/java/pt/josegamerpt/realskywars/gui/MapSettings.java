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

import pt.josegamerpt.realskywars.classes.SetupRoom;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.managers.MapManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

public class MapSettings {

	SetupRoom gr;
	private UUID uuid;
	private static Map<UUID, MapSettings> inventories = new HashMap<>();
	static Inventory inv;

	static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
	static ItemStack confirm = Itens.createItemLore(Material.CHEST, 1, "&9Save Settings",
			Arrays.asList("&7Click here to confirm your settings."));
	static ItemStack saved = Itens.createItemLore(Material.ENDER_CHEST, 1, "&9Save Settings",
			Arrays.asList("&7Settings saved. You can now exit from this menu."));

	// settings
	static ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
			Arrays.asList("&7Spectator is turned &aON &7for dead players."));
	static ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
			Arrays.asList("&7Spectator is turned &cOFF &7for dead players."));
	static ItemStack ieon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending",
			Arrays.asList("&7Instant Ending is turned &aON&7."));
	static ItemStack ieoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending",
			Arrays.asList("&7Instant Ending is turned &cOFF&7."));

	public MapSettings(SetupRoom g, UUID id) {
		this.uuid = id;
		gr = g;

		inv = Bukkit.getServer().createInventory(null, 27, g.Name + " Settings");

		for (int i = 0; i < 9; i++) {
			inv.setItem(i, placeholder);
		}

		inv.setItem(18, placeholder);
		inv.setItem(19, placeholder);
		inv.setItem(20, placeholder);
		inv.setItem(21, placeholder);
		inv.setItem(23, placeholder);
		inv.setItem(24, placeholder);
		inv.setItem(25, placeholder);
		inv.setItem(26, placeholder);

		inv.setItem(9, placeholder);
		inv.setItem(17, placeholder);

		// ITEMS
		if (g.spec == true) {
			inv.setItem(10, specon);
		} else {
			inv.setItem(10, specoff);
		}
		if (g.instantEnding == true) {
			inv.setItem(16, ieon);
		} else {
			inv.setItem(16, ieoff);
		}

		inv.setItem(22, confirm);

		this.register();
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
							MapSettings current = inventories.get(uuid);
							if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
								return;
							}

							e.setCancelled(true);

							GamePlayer gp = PlayerManager.getPlayer(p);

							if (gp.setup.guiConfirm == true) {
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

									if (e.getRawSlot() == 22) {
										gp.setup.guiConfirm = true;
										inv.setItem(22, saved);
									}

									// settings
									if (e.getRawSlot() == 10) {
										if (gp.setup.spec == true) {
											gp.setup.spec = false;
											inv.setItem(10, specoff);
										} else {
											gp.setup.spec = true;
											inv.setItem(10, specon);
										}
									}
									if (e.getRawSlot() == 16) {
										if (gp.setup.instantEnding) {
											gp.setup.instantEnding = false;
											inv.setItem(16, ieoff);
										} else {
											gp.setup.instantEnding = true;
											inv.setItem(16, ieon);
										}
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

						GamePlayer gp = PlayerManager.getPlayer(p);
						if (gp.setup.guiConfirm == false) {
							Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
								@Override
								public void run() {
									MapSettings m = new MapSettings(gp.setup, p.getUniqueId());
									m.openInventory(gp);
								}
							}, 3);
						} else {
							Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
								@Override
								public void run() {
									MapManager.continueSetup(gp);
								}
							}, 10);
						}

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
