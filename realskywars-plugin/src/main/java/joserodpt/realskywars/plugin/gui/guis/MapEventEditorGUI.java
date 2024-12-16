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
import joserodpt.realskywars.api.map.RSWMapEvent;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
import joserodpt.realskywars.api.utils.PlayerInput;
import joserodpt.realskywars.api.utils.Text;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapEventEditorGUI {

    final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private static final Map<UUID, MapEventEditorGUI> inventories = new HashMap<>();
    private final Inventory inv;
    private final UUID uuid;
    private final Map<Integer, RSWMapEvent> display = new HashMap<>();
    private final RSWMap map;
    int pageNumber = 0;
    Pagination<RSWMapEvent> p;

    public MapEventEditorGUI(Player p, RSWMap map) {
        this.uuid = p.getUniqueId();
        this.map = map;
        this.inv = Bukkit.getServer().createInventory(null, 54, "Event Editor for " + map.getName());

        this.p = new Pagination<>(28, map.getEvents().stream().filter(e -> e.getEventType() != RSWMapEvent.EventType.BORDERSHRINK).collect(Collectors.toList()));
        fillChest(this.p.getPage(this.pageNumber));
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
                        MapEventEditorGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 3:
                                current.map.addEvent(new RSWMapEvent(current.map, RSWMapEvent.EventType.REFILL));

                                current.p = new Pagination<>(28, current.map.getEvents().stream().filter(ev -> ev.getEventType() != RSWMapEvent.EventType.BORDERSHRINK).collect(Collectors.toList()));
                                current.fillChest(current.p.getPage(current.pageNumber));
                                break;
                            case 5:
                                current.map.addEvent(new RSWMapEvent(current.map, RSWMapEvent.EventType.TNTRAIN));

                                current.p = new Pagination<>(28, current.map.getEvents().stream().filter(ev -> ev.getEventType() != RSWMapEvent.EventType.BORDERSHRINK).collect(Collectors.toList()));
                                current.fillChest(current.p.getPage(current.pageNumber));
                                break;
                            case 49:
                                clicker.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();
                                }

                                MapSettingsGUI gui = new MapSettingsGUI(p, current.map);
                                gui.openInventory(p);
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
                            RSWMapEvent a = current.display.get(e.getRawSlot());
                            if (e.getClick() == ClickType.DROP) {
                                current.map.removeEvent(a);

                                current.p = new Pagination<>(28, current.map.getEvents().stream().filter(ev -> ev.getEventType() != RSWMapEvent.EventType.BORDERSHRINK).collect(Collectors.toList()));
                                current.fillChest(current.p.getPage(current.pageNumber));
                            } else {
                                p.closeInventory();
                                new PlayerInput((Player) clicker, input -> {
                                    try {
                                        int seconds = Integer.parseInt(input);
                                        a.setTime(seconds);
                                        current.map.save(RSWMap.Data.EVENTS, true);

                                        MapEventEditorGUI gui2 = new MapEventEditorGUI(p.getPlayer(), current.map);
                                        gui2.openInventory(p.getPlayer());
                                    } catch (NumberFormatException e1) {
                                        p.sendMessage(Text.color("&cInvalid seconds."));
                                    }
                                }, input -> {
                                    MapEventEditorGUI gui2 = new MapEventEditorGUI(p.getPlayer(), current.map);
                                    gui2.openInventory(p.getPlayer());
                                });
                            }
                        }
                    }
                }
            }

            private void backPage(MapEventEditorGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(MapEventEditorGUI asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
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

    public void fillChest(List<RSWMapEvent> items) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 4, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(3, Itens.createItem(RSWMapEvent.EventType.REFILL.getIcon(), 1, "&fClick to add " + RSWMapEvent.EventType.REFILL.getName()));
        inv.setItem(5, Itens.createItem(RSWMapEvent.EventType.TNTRAIN.getIcon(), 1, "&fClick to add " + RSWMapEvent.EventType.TNTRAIN.getName()));

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

        inv.setItem(49, Itens.createItem(Material.CHEST, 1, TranslatableLine.BUTTONS_MENU_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_MENU_DESC.getSingle())));

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    RSWMapEvent s = items.get(0);
                    inv.setItem(slot, s.getItem());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }
    }

    public void openInventory(Player p) {
        Inventory inv = getInventory();
        InventoryView openInv = p.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = p.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                p.openInventory(inv);
            }
            register();
        }
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
