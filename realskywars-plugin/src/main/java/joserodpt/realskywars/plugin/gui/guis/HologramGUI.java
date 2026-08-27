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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.holograms.HologramType;
import joserodpt.realskywars.api.managers.holograms.RSWLobbyHologram;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.api.managers.LobbyHologramManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Places and removes the lobby holograms. The top row creates a hologram of
 * each type where the player is standing; the rest lists what is already placed.
 */
public class HologramGUI {

    private static final Map<UUID, HologramGUI> inventories = new HashMap<>();

    private final Inventory inv;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");

    private final UUID uuid;
    private final LobbyHologramManagerAPI manager;
    private final Map<Integer, HologramType> createSlots = new HashMap<>();
    private final Map<Integer, String> hologramSlots = new HashMap<>();

    public HologramGUI(RSWPlayer p, LobbyHologramManagerAPI manager) {
        this.uuid = p.getUUID();
        this.manager = manager;

        this.inv = Bukkit.getServer().createInventory(null, 54, Text.color("&9Hologram Manager"));

        loadInv();
    }

    private void loadInv() {
        inv.clear();
        createSlots.clear();
        hologramSlots.clear();

        for (int slot : new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17}) {
            inv.setItem(slot, placeholder);
        }

        //top row: one creation button per type
        int slot = 0;
        for (HologramType type : HologramType.values()) {
            if (slot > 8) {
                break;
            }
            inv.setItem(slot, Itens.createItem(type.getIcon(), 1, type.getDisplayName(),
                    Arrays.asList("&7Click to place this hologram", "&7at your current location.")));
            createSlots.put(slot, type);
            slot++;
        }

        //everything already placed
        int listSlot = 18;
        for (RSWLobbyHologram holo : manager.getHolograms()) {
            if (listSlot > 53) {
                break;
            }

            List<String> lore = new ArrayList<>();
            lore.add("&7Type: &b" + holo.getType().name());
            if (holo.getLocation() != null) {
                lore.add("&7World: &b" + holo.getLocation().getWorld().getName());
                lore.add("&7At: &b" + holo.getLocation().getBlockX() + ", " + holo.getLocation().getBlockY() + ", " + holo.getLocation().getBlockZ());
            }
            if (!holo.isActive()) {
                lore.add("&cNot rendered: no hologram plugin installed.");
            }
            lore.add("");
            lore.add("&a&nLeft-Click&r&f to teleport to it.");
            lore.add("&c&nQ (Drop)&r&f to remove it.");

            inv.setItem(listSlot, Itens.createItem(holo.getType().getIcon(), 1, "&b" + holo.getId(), Text.color(lore)));
            hologramSlots.put(listSlot, holo.getId());
            listSlot++;
        }
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (!(clicker instanceof Player) || e.getCurrentItem() == null) {
                    return;
                }

                Player p = (Player) clicker;
                UUID uuid = p.getUniqueId();
                if (!inventories.containsKey(uuid)) {
                    return;
                }

                HologramGUI current = inventories.get(uuid);
                if (e.getInventory().getHolder() != current.inv.getHolder()) {
                    return;
                }

                e.setCancelled(true);

                int slot = e.getRawSlot();

                if (current.createSlots.containsKey(slot)) {
                    RSWLobbyHologram created = current.manager.createHologram(current.createSlots.get(slot), p.getLocation());
                    if (created == null) {
                        Text.send(p, "&cCould not create the hologram. See the console for details.");
                        return;
                    }

                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                    Text.send(p, "&fPlaced hologram &b" + created.getId() + "&f.");
                    if (!created.isActive()) {
                        Text.send(p, "&cNo hologram plugin is installed, so it will not be visible. Install HolographicDisplays or DecentHolograms.");
                    }
                    current.loadInv();
                    return;
                }

                if (current.hologramSlots.containsKey(slot)) {
                    String id = current.hologramSlots.get(slot);

                    if (e.getClick() == ClickType.DROP) {
                        current.manager.removeHologram(id);
                        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 50, 50);
                        Text.send(p, "&fRemoved hologram &b" + id + "&f.");
                        current.loadInv();
                        return;
                    }

                    RSWLobbyHologram holo = current.manager.getHologram(id);
                    if (holo != null && holo.getLocation() != null) {
                        p.closeInventory();
                        p.teleport(holo.getLocation());
                    }
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    UUID uuid = e.getPlayer().getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unregister();
                    }
                }
            }
        };
    }

    public void openInventory(RSWPlayer player) {
        openInventory(player.getPlayer());
    }

    public void openInventory(Player player) {
        InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(this.inv.getType().name())) {
                openTop.setContents(this.inv.getContents());
            } else {
                player.openInventory(this.inv);
            }
            register();
        }
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
