package joserodpt.realskywars.plugin.gui.guis;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
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

public class KitSettingsGUI {

    public RSWKit kt;
    private static final Map<UUID, KitSettingsGUI> inventories = new HashMap<>();
    private Inventory inv;
    private ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private ItemStack confirm = Itens.createItem(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to save your settings."));
    private ItemStack ender_pearl = Itens.createItem(Material.ENDER_PEARL, 1, "&9EnderPearl every x Seconds", Collections.singletonList("&aON"));
    private ItemStack ender_pearl_off = Itens.createItem(Material.ENDER_PEARL, 1, "&9EnderPearl every x Seconds", Collections.singletonList("&cOFF"));
    private final UUID uuid;

    public KitSettingsGUI(RSWKit k, UUID id) {
        this.uuid = id;
        this.kt = k;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(k.getDisplayName() + " settings"));

        load();
    }

    private void load() {
        inv.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 23, 24, 25, 26, 9, 17}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(13, this.kt.hasPerk(RSWKit.Perks.ENDER) ? ender_pearl : ender_pearl_off);
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
                            KitSettingsGUI current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

                            ItemStack clickedItem = e.getCurrentItem();

                            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

                            if (e.getRawSlot() == 22) {
                                RealSkywarsAPI.getInstance().getKitManagerAPI().getKits().add(current.kt);
                                RealSkywarsAPI.getInstance().getKitManagerAPI().registerKit(current.kt);

                                p.closeInventory();

                                TranslatableLine.KIT_CREATED.send(RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer(p), true);
                            }

                            // settings
                            if (e.getRawSlot() == 13) {
                                if (current.kt.hasPerk(RSWKit.Perks.ENDER)) {
                                    current.kt.removePerk(RSWKit.Perks.ENDER);
                                } else {
                                    current.kt.addPerk(RSWKit.Perks.ENDER);
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
        register();
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
