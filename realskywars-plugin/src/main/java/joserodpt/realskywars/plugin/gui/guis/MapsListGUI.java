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
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.PlaceholderMode;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MapsListGUI {

    private static final Map<UUID, MapsListGUI> inventories = new HashMap<>();
    int pageNumber = 0;
    private final Pagination<RSWMap> p;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final Inventory inv;
    private final UUID uuid;
    private RSWPlayer gp;
    private final Map<Integer, RSWMap> display = new HashMap<>();

    public MapsListGUI(RSWPlayer p) {
        this.uuid = p.getUUID();
        this.inv = Bukkit.getServer().createInventory(null, 54, TranslatableLine.MENU_MAPS_TITLE.get(p, false) + ": " + p.getPlayerMapViewerPref().getDisplayName(p));

        this.gp = p;
        List<RSWMap> items = RealSkywarsAPI.getInstance().getMapManagerAPI().getMapsForPlayer(p);

        this.p = new Pagination<>(28, items);
        fillChest(this.p.getPage(pageNumber), p);
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
                        MapsListGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                selectNext(p, current);
                                break;
                            case 26:
                            case 35:
                                if (!current.lastPage()) {
                                    nextPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                            case 18:
                            case 27:
                                if (!current.firstPage()) {
                                    backPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            RSWMap a = current.display.get(e.getRawSlot());
                            if (!(a instanceof PlaceholderMode)) {
                                a.addPlayer(p);
                            }
                        }
                    }
                }
            }

            private void selectNext(RSWPlayer gp, MapsListGUI curr) {
                switch (gp.getPlayerMapViewerPref()) {
                    case MAPV_ALL:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.MAPV_AVAILABLE);
                        break;
                    case MAPV_AVAILABLE:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.MAPV_WAITING);
                        break;
                    case MAPV_WAITING:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.MAPV_STARTING);
                        break;
                    case MAPV_STARTING:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.MAPV_SPECTATE);
                        break;
                    case MAPV_SPECTATE:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.SOLO);
                        break;
                    case SOLO:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.SOLO_RANKED);
                        break;
                    case SOLO_RANKED:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.TEAMS);
                        break;
                    case TEAMS:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.TEAMS_RANKED);
                        break;
                    case TEAMS_RANKED:
                        gp.setPlayerMapViewerPref(RSWPlayer.MapViewerPref.MAPV_ALL);
                        break;
                }
                curr.gp = gp;

                gp.closeInventory();
                MapsListGUI v = new MapsListGUI(gp);
                v.openInventory(gp);
                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
            }

            private void backPage(MapsListGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.gp);
            }

            private void nextPage(MapsListGUI asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.gp);
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

    private boolean lastPage() {
        return pageNumber == (p.totalPages() - 1);
    }

    private boolean firstPage() {
        return pageNumber == 0;
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

    public void fillChest(List<RSWMap> items, RSWPlayer p) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
            inv.setItem(27, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
            inv.setItem(35, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
        }

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    RSWMap s = items.get(0);
                    inv.setItem(slot, s.getIconForPlayer(p));
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }

        inv.setItem(49, Itens.createItem(Material.COMPARATOR, 1, TranslatableLine.BUTTONS_FILTER_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_FILTER_DESC.getSingle())));
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
