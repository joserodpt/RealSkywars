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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.map.RSWMap;
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

public class MapSettingsGUI {

    private static final Map<UUID, MapSettingsGUI> inventories = new HashMap<>();
    private Inventory inv;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack confirm = Itens.createItem(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to confirm your settings."));
    // settings
    private final ItemStack specon = Itens.createItem(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    private final ItemStack specoff = Itens.createItem(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    private final ItemStack ieon = Itens.createItem(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &aON&7."));
    private final ItemStack ieoff = Itens.createItem(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    private final ItemStack rankedon = Itens.createItem(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked Mode is turned &aON&7."));
    private final ItemStack rankedoff = Itens.createItem(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked Mode is turned &cOFF&7."));
    private final ItemStack borderon = Itens.createItem(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &aON&7."));
    private final ItemStack borderoff = Itens.createItem(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &cOFF&7."));

    private final UUID uuid;
    private RSWMap map;

    public MapSettingsGUI(RSWPlayer p, RSWMap map) {
        this.uuid = p.getUUID();
        this.map = map;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(map.getMapName() + " settings"));

        loadInv();
    }

    private void loadInv() {
        inv.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 19, 20, 21, 23, 24, 25, 26, 17}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(10, map.isSpectatorEnabled() ? specon : specoff);
        inv.setItem(12, map.isRanked() ? rankedon : rankedoff);
        inv.setItem(14, map.isInstantEndEnabled() ? ieon : ieoff);
        inv.setItem(16, map.isBorderEnabled() ? borderon : borderoff);

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
                            MapSettingsGUI current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

                            //Settings
                            switch (e.getRawSlot()) {
                                case 10:
                                    current.map.setSpectating(!current.map.isSpectatorEnabled());
                                    break;
                                case 12:
                                    current.map.setRanked(!current.map.isRanked());
                                    break;
                                case 14:
                                    current.map.setInstantEnding(!current.map.isInstantEndEnabled());
                                    break;
                                case 16:
                                    current.map.setBorderEnabled(!current.map.isBorderEnabled());
                                    break;
                                case 22:
                                    current.map.save(RSWMap.Data.SETTINGS, true);
                                    p.closeInventory();
                                    break;

                            }
                            current.loadInv();
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
            register();
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
