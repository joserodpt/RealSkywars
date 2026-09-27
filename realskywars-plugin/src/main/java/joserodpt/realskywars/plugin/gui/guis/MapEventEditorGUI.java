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
import joserodpt.realutils.dialog.DialogForm;
import joserodpt.realutils.gui.Pagination;
import joserodpt.realutils.input.PlayerInput;
import joserodpt.realutils.item.Items;
import joserodpt.realutils.text.Text;
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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapEventEditorGUI {

    final ItemStack placeholder = Items.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
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

        fillChest(this.p.totalPages() > 0 ? this.p.getPage(pageNumber) : Collections.emptyList());
    }

    //used on reload so nobody keeps clicking a GUI built from the old config
    public static void closeAll() {
        for (final MapEventEditorGUI current : new ArrayList<>(inventories.values())) {
            final Player p = Bukkit.getPlayer(current.uuid);
            if (p != null && p.getOpenInventory().getTopInventory().equals(current.getInventory())) {
                p.closeInventory();
            }
        }
        inventories.clear();
    }

    /**
     * Every event of this map with a slider for when it happens, in seconds into the game.
     *
     * @return false if dialogs are not supported, and nothing was shown
     */
    private boolean openTimes(final Player p) {
        final List<RSWMapEvent> events = this.map.getEvents().stream()
                .filter(e -> e.getEventType() != RSWMapEvent.EventType.BORDERSHRINK)
                .collect(Collectors.toList());
        if (events.isEmpty()) {
            return false;
        }
        int longest = this.map.getMaxGameTime();
        for (final RSWMapEvent event : events) {
            longest = Math.max(longest, event.getTime());
        }

        final DialogForm form = new DialogForm("&9" + this.map.getName() + " &8| &fEvents",
                "&7When each event happens, in seconds into the game.");
        for (int i = 0; i < events.size(); i++) {
            final RSWMapEvent event = events.get(i);
            form.slider("event_" + i, "&f" + event.getName(), 0, longest, 5, event.getTime())
                    .sprite(event.getEventType().getIcon());
        }
        form.buttons("&aSave", "&7Back");

        return form.open(p, answers -> {
            boolean changed = false;
            for (int i = 0; i < events.size(); i++) {
                final Double time = answers.moved("event_" + i, events.get(i).getTime(), 5);
                if (time != null) {
                    events.get(i).setTime((int) Math.round(time));
                    changed = true;
                }
            }
            if (changed) {
                this.map.save(RSWMap.Data.EVENTS, true);
            }
            new MapEventEditorGUI(p, this.map).openInventory(p);
        }, () -> new MapEventEditorGUI(p, this.map).openInventory(p), () -> new MapEventEditorGUI(p, this.map).openInventory(p));
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        MapEventEditorGUI current = inventories.get(uuid);
                        if (!current.getInventory().equals(e.getInventory())) {
                            return;
                        }

                        e.setCancelled(true);
                        if (e.getCurrentItem() == null) {
                            return;
                        }
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 3:
                                current.map.addEvent(new RSWMapEvent(current.map, RSWMapEvent.EventType.REFILL));
                                current.refreshPagination();
                                break;
                            case 5:
                                current.map.addEvent(new RSWMapEvent(current.map, RSWMapEvent.EventType.TNTRAIN));
                                current.refreshPagination();
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
                                current.refreshPagination();
                            } else if (!current.openTimes((Player) clicker)) {
                                //one dialog with every event's time where the server has them; typed in chat elsewhere
                                p.closeInventory();
                                new PlayerInput((Player) clicker, true, input -> {
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

                asd.fillChest(asd.p.totalPages() > 0 ? asd.p.getPage(asd.pageNumber) : Collections.emptyList());
            }

            private void nextPage(MapEventEditorGUI asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.totalPages() > 0 ? asd.p.getPage(asd.pageNumber) : Collections.emptyList());
            }

            @EventHandler
            public void onDrag(final InventoryDragEvent e) {
                final MapEventEditorGUI current = inventories.get(e.getWhoClicked().getUniqueId());
                //dragging over this GUI's slots would drop the dragged items into it
                if (current != null && current.getInventory().equals(e.getInventory())) {
                    e.setCancelled(true);
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
                    final MapEventEditorGUI current = inventories.get(uuid);
                    if (current != null && e.getInventory().equals(current.getInventory())) {
                        current.unregister();
                    }
                }
            }
        };
    }

    private void refreshPagination() {
        List<RSWMapEvent> filteredEvents = map.getEvents().stream()
                .filter(ev -> ev.getEventType() != RSWMapEvent.EventType.BORDERSHRINK)
                .collect(Collectors.toList());

        this.p = new Pagination<>(28, filteredEvents);

        if (!p.exists(pageNumber)) {
            pageNumber = 0;
        }

        fillChest(p.totalPages() > 0 ? p.getPage(pageNumber) : Collections.emptyList());
    }

    private boolean lastPage() {
        return p.totalPages() == 0 || pageNumber == (p.totalPages() - 1);
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

        inv.setItem(3, Items.createItem(RSWMapEvent.EventType.REFILL.getIcon(), 1, "&fClick to add " + RSWMapEvent.EventType.REFILL.getName()));
        inv.setItem(5, Items.createItem(RSWMapEvent.EventType.TNTRAIN.getIcon(), 1, "&fClick to add " + RSWMapEvent.EventType.TNTRAIN.getName()));

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Items.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
            inv.setItem(27, Items.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Items.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
            inv.setItem(35, Items.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
        }

        inv.setItem(49, Items.createItem(Material.CHEST, 1, TranslatableLine.BUTTONS_MENU_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_MENU_DESC.getSingle())));

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
            if (!inv.equals(openTop)) {
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