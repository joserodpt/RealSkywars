package josegamerpt.realskywars.kits;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitSettings {

    public Kit kt;
    private static final Map<UUID, KitSettings> inventories = new HashMap<>();
    private Inventory inv;
    private ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private ItemStack confirm = Itens.createItemLore(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to save your settings."));
    private ItemStack ender_pearl = Itens.createItemLore(Material.ENDER_PEARL, 1, "&9EnderPearl every x Seconds", Collections.singletonList("&aON"));
    private ItemStack ender_pearl_off = Itens.createItemLore(Material.ENDER_PEARL, 1, "&9EnderPearl every x Seconds", Collections.singletonList("&cOFF"));
    private final UUID uuid;

    public KitSettings(Kit k, UUID id) {
        this.uuid = id;
        this.kt = k;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(k.getDisplayName() + " Settings"));

        load();
    }

    private void load() {
        inv.clear();

        for (int i = 0; i < 9; ++i) {
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

        inv.setItem(13, this.kt.hasPerk(Kit.Perks.ENDER) ? ender_pearl : ender_pearl_off);

        inv.setItem(22, confirm);
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

                            ItemStack clickedItem = e.getCurrentItem();

                            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

                            if (e.getRawSlot() == 22) {
                                RealSkywars.getPlugin().getKitManager().getKits().add(current.kt);
                                RealSkywars.getPlugin().getKitManager().registerKit(current.kt);

                                p.closeInventory();
                            }

                            // settings
                            if (e.getRawSlot() == 13) {
                                if (current.kt.hasPerk(Kit.Perks.ENDER)) {
                                    current.kt.removePerk(Kit.Perks.ENDER);
                                } else {
                                    current.kt.addPerk(Kit.Perks.ENDER);
                                }
                                current.load();
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
