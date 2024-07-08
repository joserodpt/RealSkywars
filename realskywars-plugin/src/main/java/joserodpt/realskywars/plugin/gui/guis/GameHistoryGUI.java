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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWGameHistoryStats;
import joserodpt.realskywars.api.player.RSWGameLog;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
import joserodpt.realskywars.plugin.gui.GUIManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameHistoryGUI {

    final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private static final Map<UUID, GameHistoryGUI> inventories = new HashMap<>();
    private final Inventory inv;
    private final UUID uuid;
    private final RSWPlayer rswp;
    private final Map<Integer, RSWGameLog> display = new HashMap<>();
    private final RSWGameHistoryStats stats;
    int pageNumber = 0;
    Pagination<RSWGameLog> p;

    public GameHistoryGUI(RSWPlayer rswp) {
        this.uuid = rswp.getUUID();
        this.rswp = rswp;
        this.inv = Bukkit.getServer().createInventory(null, 54, TranslatableLine.MENU_PLAYER_GAME_HISTORY.get(rswp));

        List<RSWGameLog> items = new ArrayList<>();
        var response = RealSkywarsAPI.getInstance().getDatabaseManagerAPI().getPlayerGameHistory(rswp.getPlayer());
        this.stats = response.getValue();

        if (response.getKey().isEmpty()) {
            items.add(new RSWGameLog());
        } else {
            items = response.getKey().stream().map(s -> new RSWGameLog(s.getMap(), s.getMode(), s.isRanked(), s.getPlayerCount(), s.getKills(), s.wasWin(), s.getTime(), s.getDate())).collect(Collectors.toList());
        }

        this.p = new Pagination<>(28, items);
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
                        GameHistoryGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                clicker.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();
                                }
                                GUIManager.openPlayerProfile(p);
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

                        /*
                        if (current.display.containsKey(e.getRawSlot())) {
                            RSWGameLog a = current.display.get(e.getRawSlot());

                        }
                         */
                    }
                }
            }

            private void backPage(GameHistoryGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(GameHistoryGUI asd) {
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

    public void fillChest(List<RSWGameLog> items) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(4, stats.getItem(rswp));

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
                    RSWGameLog s = items.get(0);
                    inv.setItem(slot, s.getItem(rswp));
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }
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
