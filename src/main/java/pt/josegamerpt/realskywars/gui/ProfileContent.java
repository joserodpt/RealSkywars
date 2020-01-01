package pt.josegamerpt.realskywars.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.classes.DisplayItem;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.Kit;
import pt.josegamerpt.realskywars.managers.KitManager;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.utils.Pagination;
import pt.josegamerpt.realskywars.utils.Text;

public class ProfileContent {

	private static Map<UUID, ProfileContent> inventories = new HashMap<>();
	private Inventory inv;

	static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
	static ItemStack menu = Itens.createItemLore(Material.CHEST, 1, "&9Menu",
			Arrays.asList("&fClick here to go back to the main menu."));
	static ItemStack next = Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, "&aNext",
			Arrays.asList("&fClick here to go to the next page."));
	static ItemStack back = Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, "&6Back",
			Arrays.asList("&fClick here to go back to the next page."));

	private UUID uuid;
	private List<DisplayItem> items;
	private HashMap<Integer, DisplayItem> display = new HashMap<Integer, DisplayItem>();
	private Boolean disableMenu = false;

	int pageNumber = 0;
	Pagination<DisplayItem> p;
	private Enum.Categories cat;

	public ProfileContent(Player as, Enum.Categories t) {
		this.uuid = as.getUniqueId();
		this.cat = t;
		inv = Bukkit.getServer().createInventory(null, 54, Text.addColor("&bSeeing " + t.name()));

		items = PlayerManager.getBoughtItems(PlayerManager.getPlayer(as), t);

		p = new Pagination<DisplayItem>(28, items);
		fillChest(p.getPage(pageNumber), false);

		this.register();
	}

	public ProfileContent(Player as, Enum.Categories t, String invName) {
		this.uuid = as.getUniqueId();
		this.cat = t;
		inv = Bukkit.getServer().createInventory(null, 54, Text.addColor(invName));

		items = PlayerManager.getBoughtItems(PlayerManager.getPlayer(as), t);

		p = new Pagination<DisplayItem>(28, items);
		fillChest(p.getPage(pageNumber), true);

		disableMenu = true;

		this.register();
	}

	public void fillChest(List<DisplayItem> items, Boolean b) {

		inv.clear();

		for (int i = 0; i < 9; i++) {
			inv.setItem(i, placeholder);
		}

		display.clear();

		inv.setItem(45, placeholder);
		inv.setItem(46, placeholder);
		inv.setItem(47, placeholder);
		inv.setItem(48, placeholder);
		inv.setItem(49, placeholder);
		inv.setItem(50, placeholder);
		inv.setItem(51, placeholder);
		inv.setItem(52, placeholder);
		inv.setItem(53, placeholder);
		inv.setItem(36, placeholder);
		inv.setItem(44, placeholder);
		inv.setItem(9, placeholder);
		inv.setItem(17, placeholder);

		inv.setItem(18, back);
		inv.setItem(27, back);
		inv.setItem(26, next);
		inv.setItem(35, next);

		if (b == false) {
			inv.setItem(49, menu);
		} else {
			inv.setItem(49, placeholder);
		}

		int slot = 0;
		for (ItemStack i : inv.getContents()) {
			if (i == null) {
				if (items.size() != 0) {
					DisplayItem s = items.get(0);
					inv.setItem(slot, s.i);
					display.put(slot, s);
					items.remove(0);
				}
			}
			slot++;
		}
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
					UUID uuid = clicker.getUniqueId();
					if (inventories.containsKey(uuid)) {
						ProfileContent current = inventories.get(uuid);
						if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
							return;
						}

						e.setCancelled(true);
						GamePlayer gp = PlayerManager.getPlayer((Player) clicker);

						if (e.getRawSlot() == 49) {
							if (e.getCurrentItem().equals(menu)) {
								clicker.closeInventory();
								if (inventories.containsKey(uuid)) {
									inventories.get(uuid).unregister();
								}
								GUIManager.openPlayerMenu(gp);
							}
						}

						if (e.getRawSlot() == 26 || e.getRawSlot() == 35) {
							nextPage(current);
							gp.p.playSound(gp.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
						}
						if (e.getRawSlot() == 18 || e.getRawSlot() == 27) {
							backPage(current);
							gp.p.playSound(gp.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
						}

						if (current.display.containsKey(e.getRawSlot())) {
							DisplayItem a = current.display.get(e.getRawSlot());

							if (a.interactable == false)
							{
								gp.sendMessage(LanguageManager.getString(gp, TS.NOT_BUYABLE, true));
								return;
							}

							if (current.cat == Enum.Categories.CAGEBLOCK) {
								gp.cageBlock = a.i.getType();
								gp.sendMessage(LanguageManager.getString(gp, TS.PROFILE_SELECTED, true).replace("%name%", a.name).replace("%type%", LanguageManager.getString(gp, TS.CAGEBLOCK, false)));
							}
							if (current.cat == Enum.Categories.KITS) {
								Kit k = KitManager.getKit(a.id);
								gp.selectedKit = k;
								gp.sendMessage(LanguageManager.getString(gp, TS.PROFILE_SELECTED, true).replace("%name%", a.name).replace("%type%", LanguageManager.getString(gp, TS.KITS, false)));
							}
						}
					}
				}
			}

			private void backPage(ProfileContent asd) {
				if (asd.p.exists(asd.pageNumber - 1)) {
					asd.pageNumber--;
				}

				asd.fillChest(asd.p.getPage(asd.pageNumber), asd.disableMenu);
			}

			private void nextPage(ProfileContent asd) {
				if (asd.p.exists(asd.pageNumber + 1)) {
					asd.pageNumber++;
				}

				asd.fillChest(asd.p.getPage(asd.pageNumber), asd.disableMenu);
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
				}
			}
		};
	}

	public Inventory getInventory() {
		return inv;
	}

	private void register() {
		inventories.put(this.uuid, this);
	}

	private void unregister() {
		inventories.remove(this.uuid);
	}
}
