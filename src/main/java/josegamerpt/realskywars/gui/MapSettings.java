package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.misc.SetupRoom;
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

public class MapSettings {

    static Inventory inv;
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack confirm = Itens.createItemLore(Material.CHEST, 1, "&9Save Settings",
            Collections.singletonList("&7Click here to confirm your settings."));
    static ItemStack saved = Itens.createItemLore(Material.ENDER_CHEST, 1, "&9Save Settings",
            Collections.singletonList("&7Settings saved. You can now exit from this menu."));
    // settings
    static ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
            Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    static ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
            Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    static ItemStack ieon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending",
            Collections.singletonList("&7Instant Ending is turned &aON&7."));
    static ItemStack ieoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending",
            Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    private static Map<UUID, MapSettings> inventories = new HashMap<>();
    SetupRoom gr;
    private UUID uuid;

    public MapSettings(SetupRoom g, UUID id) {
        this.uuid = id;
        gr = g;

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

        // ITEMS
        if (g.isSpectatingON()) {
            inv.setItem(10, specon);
        } else {
            inv.setItem(10, specoff);
        }
        if (g.isInstantEnding()) {
            inv.setItem(16, ieon);
        } else {
            inv.setItem(16, ieoff);
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
                            MapSettings current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

                            RSWPlayer gp = RealSkywars.getPlayerManager().getPlayer(p);

                            if (gp.getSetup().isGUIConfirmed()) {
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

                                    switch (e.getRawSlot()) {
                                        case 22:
                                            gp.getSetup().setGUIConfirm(true);
                                            inv.setItem(22, saved);
                                            break;

                                        //Settings
                                        case 10:
                                            if (gp.getSetup().isSpectatingON()) {
                                                inv.setItem(10, specoff);
                                            } else {
                                                inv.setItem(10, specon);
                                            }
                                            gp.getSetup().setSpectating(!gp.getSetup().isSpectatingON());
                                            break;
                                        case 16:

                                            if (gp.getSetup().isInstantEnding()) {
                                                inv.setItem(16, ieoff);
                                            } else {
                                                inv.setItem(16, ieon);
                                            }
                                            gp.getSetup().setInstantEnding(!gp.getSetup().isInstantEnding());
                                            break;
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

                        RSWPlayer gp = RealSkywars.getPlayerManager().getPlayer(p);
                        if (!gp.getSetup().isGUIConfirmed()) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
                                MapSettings m = new MapSettings(gp.getSetup(), p.getUniqueId());
                                m.openInventory(gp);
                            }, 3);
                        } else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> RealSkywars.getMapManager().continueSetup(gp), 10);
                        }

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
