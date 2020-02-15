package pt.josegamerpt.realskywars.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.classes.ChestItem;
import pt.josegamerpt.realskywars.classes.Enum.InteractionState;
import pt.josegamerpt.realskywars.classes.Enum.TierType;
import pt.josegamerpt.realskywars.managers.ChestManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.utils.Pagination;
import pt.josegamerpt.realskywars.utils.SignGUI;

public class ChestTierViewer {

	private static Map<UUID, ChestTierViewer> inventories = new HashMap<>();
	private Inventory inv;

	static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
	static ItemStack info = Itens.createItemLore(Material.BIRCH_SIGN, 1, "&9Info",
			Arrays.asList("&fUse /rsw setchest <BASIC, NORMAL, OP, CAOS>", "&fto set the chest contents."));
	static ItemStack info2 = Itens.createItemLore(Material.BIRCH_SIGN, 1, "&9Info",
			Arrays.asList("&fYou are now seeing the contents of this tier.",
					"&fThe tier is selected by players when the game is starting."));
	static ItemStack menu = Itens.createItemLore(Material.CHEST, 1, "&9Menu",
			Arrays.asList("&fClick here to go back to the main menu."));
	static ItemStack next = Itens.createItemLore(Material.TRIDENT, 1, "&9Next",
			Arrays.asList("&fClick here to go to the next page."));
	static ItemStack back = Itens.createItemLore(Material.CROSSBOW, 1, "&9Back",
			Arrays.asList("&fClick here to go back to the next page."));

	private UUID uuid;
	private ArrayList<ChestItem> loot;
	private List<ChestItem> showing;
	private TierType tp;

	int pageNumber = 0;
	Pagination<ChestItem> p;

	public ChestTierViewer(UUID id, TierType t) {
		this.uuid = id;
		inv = Bukkit.getServer().createInventory(null, 54, t.name() + " Contents");

		for (int i = 27; i < 9; i++) {
			inv.setItem(i, placeholder);
		}
		tp = t;
		int slot = 0;
		loot = ChestManager.seeTier(t);

		p = new Pagination<ChestItem>(45, loot);

		if (p.totalPages() == 1) {
			for (ChestItem i : loot) {
				inv.setItem(slot, i.i);
				slot++;
			}
		} else {
			for (ChestItem i : p.getPage(pageNumber)) {
				inv.setItem(slot, i.i);
				slot++;
			}
		}
		showing = p.getPage(pageNumber);

		inv.setItem(45, back);
		inv.setItem(47, info);
		inv.setItem(49, menu);
		inv.setItem(51, info2);
		inv.setItem(53, next);

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
			register();
			player.istate = InteractionState.GUI_CHESTCONTENTS;
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
							ChestTierViewer current = inventories.get(uuid);
							if (!e.getInventory().getType().name()
									.equalsIgnoreCase(current.getInventory().getType().name())) {
								return;
							}
							GamePlayer gp = PlayerManager.getPlayer(p);
							if (gp.istate == InteractionState.GUI_CHESTCONTENTS) {
								e.setCancelled(true);

								if (e.getRawSlot() == 49) {
									ChestTierMenu r = new ChestTierMenu(p.getUniqueId());
									p.closeInventory();
									if (inventories.containsKey(uuid)) {
										inventories.get(uuid).unregister();

									}
									gp.istate = InteractionState.GUI_CHESTMENU;
									r.openInventory(gp);
								}
								if (e.getRawSlot() == 53) {
									if (e.getCurrentItem().equals(next)) {
										nextPage(current);
									}
								}
								if (e.getRawSlot() == 45) {
									if (e.getCurrentItem().equals(back)) {
										backPage(current);
									}
								}
								int i = e.getSlot();

								if (containsItem(current.showing, e.getCurrentItem()) == true) {
									p.closeInventory();
									SignGUI.openDialog(p, i + 1 + (current.pageNumber * 45), current.tp);
								}
							}
						}
					}
				}
			}

			public boolean containsItem(final List<ChestItem> list, ItemStack is) {
				return list.stream().filter(o -> o.i.equals(is)).findFirst().isPresent();
			}

			private void backPage(ChestTierViewer asd) {
				if (asd.p.exists(asd.pageNumber - 1)) {
					asd.pageNumber--;
				}

				asd.inv.clear();
				int slot = 0;
				for (ChestItem i : asd.p.getPage(asd.pageNumber)) {
					asd.inv.setItem(slot, i.i);
					slot++;
				}

				asd.inv.setItem(45, back);
				asd.inv.setItem(47, info);
				asd.inv.setItem(49, menu);
				asd.inv.setItem(51, info2);
				asd.inv.setItem(53, next);

				asd.showing = asd.p.getPage(asd.pageNumber);
			}

			private void nextPage(ChestTierViewer asd) {
				if (asd.p.exists(asd.pageNumber + 1)) {
					asd.pageNumber++;
				}

				asd.inv.clear();
				int slot = 0;
				for (ChestItem i : asd.p.getPage(asd.pageNumber)) {
					asd.inv.setItem(slot, i.i);
					slot++;
				}

				asd.inv.setItem(45, back);
				asd.inv.setItem(47, info);
				asd.inv.setItem(49, menu);
				asd.inv.setItem(51, info2);
				asd.inv.setItem(53, next);

				asd.showing = asd.p.getPage(asd.pageNumber);
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

                    }
                    GamePlayer gp = PlayerManager.getPlayer(p);
                    if (gp != null) {
                        gp.istate = InteractionState.NONE;
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
