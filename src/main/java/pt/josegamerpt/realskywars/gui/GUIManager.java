package pt.josegamerpt.realskywars.gui;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.event.inventory.InventoryClickEvent;

import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.GUIBuilder;
import pt.josegamerpt.realskywars.utils.GUIBuilder.ClickRunnable;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.classes.Enum.Categories;

public class GUIManager {

	public static void openShopMenu(GamePlayer p) {
		GUIBuilder inventory = new GUIBuilder("&fShop &9Categories", 9, p.p.getUniqueId(),
				Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ShopViewer v = new ShopViewer(p.p.getUniqueId(), Categories.CAGEBLOCK);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.SPAWNER, 1, "&9Cage Blocks",
				Arrays.asList("&fClick here to open this category.")), 0);

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ShopViewer v = new ShopViewer(p.p.getUniqueId(), Categories.KITS);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.CHEST, 1, "&9Kits", Collections.singletonList("&fClick here to open this category.")), 1);

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ShopViewer v = new ShopViewer(p.p.getUniqueId(), Categories.WINBLOCKS);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, "&9Win Blocks",
				Arrays.asList("&fClick here to open this category.")), 2);

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ShopViewer v = new ShopViewer(p.p.getUniqueId(), Categories.WINPARTICLE);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.GLOWSTONE_DUST, 1, "&9Win Particles",
				Collections.singletonList("&fClick here to open this category.")), 3);

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ShopViewer v = new ShopViewer(p.p.getUniqueId(), Categories.BOWPARTICLE);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.BOW, 1, "&9Bow Particles",
				Collections.singletonList("&fClick here to open this category.")), 4);

		inventory.openInventory(p.p);
	}

	public static void openSpectate(GamePlayer p) {
		GUIBuilder inventory = new GUIBuilder("&9Players", 54, p.p.getUniqueId(),
				Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

		int i = 0;
		for (GamePlayer s : p.room.getGamePlayers()) {
			if (s.p != null) {
				inventory.addItem(new ClickRunnable() {
					public void run(InventoryClickEvent e) {
						p.teleport(s.p.getLocation());
						p.sendMessage(LanguageManager.getString(p, Enum.TS.COMPASS_TELEPORT, true).replace("%name%", s.p.getDisplayName()));
					}
				}, Itens.addLore(Itens.getHead(s.p, 1, "&b" + s.p.getDisplayName()),
						Arrays.asList("&fLife: &c" + s.p.getHealth(), "&fClick to teleport to this player.", "")), i);
				i++;
			}
		}
		inventory.openInventory(p.p);
	}

	public static void openLanguage(GamePlayer p) {
		GUIBuilder inventory = new GUIBuilder("&9Languages", 54, p.p.getUniqueId(),
				Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

		int i = 0;
		for (String s : LanguageManager.getLanguages()) {
			inventory.addItem(new ClickRunnable() {
				public void run(InventoryClickEvent e) {
					PlayerManager.setLanguage(p, s);
				}
			}, Itens.createItemLore(Material.JUNGLE_SIGN, 1, "&b" + s,
					Arrays.asList("&fClick here to select this language.")), i);
			i++;

		}
		inventory.openInventory(p.p);
	}

	public static void openPlayerMenu(GamePlayer p) {
		GUIBuilder inventory = new GUIBuilder("&9Your &3Profile", 9, p.p.getUniqueId(),
				Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ProfileContent v = new ProfileContent(p.p, Categories.KITS);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.CHEST, 1, "&bYour &9Kits",
				Arrays.asList("&fClick here to view this item.")), 0);
		
		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ProfileContent v = new ProfileContent(p.p, Categories.CAGEBLOCK);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.SPAWNER, 1, "&bYour &9Cage Blocks",
				Arrays.asList("&fClick here to view this item.")), 1);
		
		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ProfileContent v = new ProfileContent(p.p, Categories.WINBLOCKS);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, "&bYour &9Win Blocks",
				Arrays.asList("&fClick here to view this item.")), 2);
		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ProfileContent v = new ProfileContent(p.p, Categories.WINPARTICLE);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.GLOWSTONE_DUST, 1, "&bYour &9Win Particles",
				Arrays.asList("&fClick here to view this item.")), 3);

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				ProfileContent v = new ProfileContent(p.p, Categories.BOWPARTICLE);
				v.openInventory(p);
			}
		}, Itens.createItemLore(Material.BOW, 1, "&bYour &9Bow Particles",
				Collections.singletonList("&fClick here to open this category.")), 4);

		//settings

		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.p.closeInventory();
				GUIManager.openLanguage(p);
			}
		}, Itens.createItemLore(Material.JUNGLE_SIGN, 1, "&9Language",
				Arrays.asList("&fCurrently set: " + p.Language)), 7);
		
		inventory.addItem(new ClickRunnable() {
			public void run(InventoryClickEvent e) {
				p.resetPurchases();
			}
		}, Itens.createItemLore(Material.BARRIER, 1, "&4Delete Your Purchases",
				Arrays.asList("&cProceed with caution. This action cannot be rolled back.")), 8);

		inventory.openInventory(p.p);
	}
}
