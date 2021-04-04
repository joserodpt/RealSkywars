package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.KitManager;
import josegamerpt.realskywars.misc.Kit;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitSettings {

    public static Kit kt;
    static Inventory inv;
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack confirm = Itens.createItemLore(Material.CHEST, 1, "&9Save Settings",
            Collections.singletonList("&7Click here to confirm your settings."));
    static ItemStack saved = Itens.createItemLore(Material.ENDER_CHEST, 1, "&9Save Settings",
            Collections.singletonList("&7Settings saved. You can now exit from this menu."));
    // settings
    static ItemStack dragon = Itens.createItemLore(Material.ENDER_PEARL, 1, "&9EnderPearl every x Seconds",
            Collections.singletonList("&aON"));
    static ItemStack dragoff = Itens.createItemLore(Material.ENDER_PEARL, 1, "&9EnderPearl every x Seconds",
            Collections.singletonList("&cOFF"));
    private static Map<UUID, KitSettings> inventories = new HashMap<>();
    private UUID uuid;

    public KitSettings(Kit g, UUID id) {
        this.uuid = id;
        kt = g;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(g.getName() + " Settings"));

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

        if (g.getPerk(KitManager.KitPerks.ENDER_PEARl)) {
            inv.setItem(16, dragon);
        } else {
            inv.setItem(16, dragoff);
        }

        inv.setItem(22, confirm);

        this.register();
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
                            KitSettings current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

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
                                        inv.setItem(22, saved);
                                    }

                                    // settings
                                    if (e.getRawSlot() == 16) {
                                        if (kt.getPerk(KitManager.KitPerks.ENDER_PEARl)) {
                                            kt.setPerk(KitManager.KitPerks.ENDER_PEARl, false);
                                            inv.setItem(16, dragoff);
                                        } else {
                                            kt.setPerk(KitManager.KitPerks.ENDER_PEARl, true);
                                            inv.setItem(16, dragon);
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

                        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> kt.saveKit(), 10);
                    }
                }
            }
        };
    }

    public void openInventory(RSWPlayer player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getPlayer().getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getPlayer().getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.getPlayer().openInventory(inv);
            }
        }
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
