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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.classes.Enum.InteractionState;
import pt.josegamerpt.realskywars.classes.Enum.PlayerState;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

public class ChestTierVote {

	private static Map<UUID, ChestTierVote> inventories = new HashMap<>();
	static Inventory inv;

	static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
	private UUID uuid;

	static ItemStack b = Itens.createItemLore(Material.COBBLESTONE, 1, "&eBasic &9Chest",
			Arrays.asList("&7Click to vote on this tier."));
	static ItemStack n = Itens.createItemLore(Material.BRICK, 1, "&fNormal &9Chest",
			Arrays.asList("&7Click to vote on this tier."));
	static ItemStack op = Itens.createItemLore(Material.DIAMOND_BLOCK, 1, "&bOver-Powered &9Chest",
			Arrays.asList("&7Click to vote on this tier."));
	static ItemStack c = Itens.createItemLore(Material.END_STONE, 1, "&cCaos &9Chest",
			Arrays.asList("&7Click to vote on this tier."));

	public ChestTierVote(UUID id) {
		inv = Bukkit.getServer().createInventory(null, 27, "Chest Tiers");

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

		inv.setItem(10, b);
		inv.setItem(12, n);
		inv.setItem(14, op);
		inv.setItem(16, c);

		inventories.put(id, this);
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
		}
		player.istate = InteractionState.GUI_CHESTTIER;
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
							ChestTierVote current = inventories.get(uuid);
							if (!e.getInventory().getType().name()
									.equalsIgnoreCase(current.getInventory().getType().name())) {
								return;
							}

							GamePlayer gp = PlayerManager.getPlayer(p);
							if (gp.state == PlayerState.CAGE) {
								e.setCancelled(true);

								if (e.getRawSlot() == 10) {
									if (!gp.room.getVoters().contains(gp)) {
										gp.room.getVoteList().add(1);
										gp.room.getVoters().add(gp);
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(gp, TS.CHEST_BASIC, false)));
									} else {
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_ALREADY_VOTED, true));
									}
								}

								if (e.getRawSlot() == 12) {
									if (!gp.room.getVoters().contains(gp)) {
										gp.room.getVoteList().add(2);
										gp.room.getVoters().add(gp);
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(gp, TS.CHEST_NORMAL, false)));
									} else {
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_ALREADY_VOTED, true));
									}
								}

								if (e.getRawSlot() == 14) {
									if (!gp.room.getVoters().contains(gp)) {
										gp.room.getVoteList().add(3);
										gp.room.getVoters().add(gp);
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(gp, TS.CHEST_OP, false)));
									} else {
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_ALREADY_VOTED, true));
									}
								}

								if (e.getRawSlot() == 16) {
									if (!gp.room.getVoters().contains(gp)) {
										gp.room.getVoteList().add(4);
										gp.room.getVoters().add(gp);
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(gp, TS.CHEST_CAOS, false)));
										p.closeInventory();
										if (inventories.containsKey(uuid)) {
											inventories.get(uuid).unregister();

										}
									} else {
										p.sendMessage(LanguageManager.getString(gp, TS.CHEST_ALREADY_VOTED, true));
									}
								}
								
								p.closeInventory();
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
