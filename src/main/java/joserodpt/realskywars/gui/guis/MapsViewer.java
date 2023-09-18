package joserodpt.realskywars.gui.guis;

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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.game.modes.SWGame;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.Itens;
import joserodpt.realskywars.utils.Pagination;
import joserodpt.realskywars.utils.Text;
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

import java.util.*;
import java.util.stream.Collectors;

public class MapsViewer {

    private static final Map<UUID, MapsViewer> inventories = new HashMap<>();
    int pageNumber = 0;
    private Pagination<SWGame> p;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final Inventory inv;
    private final UUID uuid;
    private final RSWPlayer gp;
    private final Map<Integer, SWGame> display = new HashMap<>();

    public MapsViewer(RSWPlayer as, RSWPlayer.MapViewerPref t, String invName) {
        this.uuid = as.getUUID();
        this.inv = Bukkit.getServer().createInventory(null, 54, Text.color(invName) + ": " + RealSkywars.getPlugin().getLanguageManager().getString(as, select(t), false));

        this.gp = as;
        List<SWGame> items = RealSkywars.getPlugin().getGameManager().getRoomsWithSelection(t);

        this.p = new Pagination<>(28, items);
        fillChest(p.getPage(pageNumber), as);
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
                        MapsViewer current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywars.getPlugin().getPlayerManager().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                selectNext(p);
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
                            SWGame a = current.display.get(e.getRawSlot());
                            if (!a.isPlaceHolder()) {
                                a.addPlayer(p);
                            }
                        }
                    }
                }
            }

            private void selectNext(RSWPlayer gp) {
                switch (gp.getMapViewerPref()) {
                    case MAPV_ALL:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.MAPV_AVAILABLE);
                        break;
                    case MAPV_AVAILABLE:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.MAPV_WAITING);
                        break;
                    case MAPV_WAITING:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.MAPV_STARTING);
                        break;
                    case MAPV_STARTING:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.MAPV_SPECTATE);
                        break;
                    case MAPV_SPECTATE:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.SOLO);
                        break;
                    case SOLO:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.SOLO_RANKED);
                        break;
                    case SOLO_RANKED:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.TEAMS);
                        break;
                    case TEAMS:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.TEAMS_RANKED);
                        break;
                    case TEAMS_RANKED:
                        gp.setMapViewerPref(RSWPlayer.MapViewerPref.MAPV_ALL);
                        break;
                }

                gp.closeInventory();
                MapsViewer v = new MapsViewer(gp, gp.getMapViewerPref(), RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.MAPS_NAME, false));
                v.openInventory(gp);
                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
            }

            private void backPage(MapsViewer asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.gp);
            }

            private void nextPage(MapsViewer asd) {
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

    private LanguageManager.TS select(RSWPlayer.MapViewerPref t) {
        switch (t) {
            case MAPV_ALL:
                return LanguageManager.TS.MAP_ALL;
            case MAPV_WAITING:
                return LanguageManager.TS.MAP_WAITING;
            case MAPV_SPECTATE:
                return LanguageManager.TS.MAP_SPECTATE;
            case MAPV_STARTING:
                return LanguageManager.TS.MAP_STARTING;
            case MAPV_AVAILABLE:
                return LanguageManager.TS.MAP_AVAILABLE;
            case SOLO:
                return LanguageManager.TS.SOLO;
            case SOLO_RANKED:
                return LanguageManager.TS.SOLO_RANKED;
            case TEAMS_RANKED:
                return LanguageManager.TS.TEAMS_RANKED;
            case TEAMS:
                return LanguageManager.TS.TEAMS;
        }
        return null;
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

    public void fillChest(List<SWGame> items, RSWPlayer p) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
            inv.setItem(27, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
            inv.setItem(35, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
        }

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    SWGame s = items.get(0);
                    inv.setItem(slot, makeIcon(p,s));
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }

        inv.setItem(49, Itens.createItemLore(Material.COMPARATOR, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_FILTER_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_FILTER_DESC))));
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

    private ItemStack makeIcon(RSWPlayer p, SWGame g) {
        int count = 1;
        if (g.isPlaceHolder()) {
            return Itens.createItem(Material.BUCKET, count, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.ITEMS_MAP_NOTFOUND_TITLE, false));
        } else {
            if (g.getPlayerCount() > 0) {
                count = g.getPlayerCount();
            }

            return Itens.createItemLore(getStateMaterial(g), count, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.ITEMS_MAP_TITLE, false).replace("%map%", g.getName()).replace("%mode%", g.getGameMode().name()) + " " + this.rankedFormatting(g.isRanked()), variableList(RealSkywars.getPlugin().getLanguageManager().getList(p, LanguageManager.TL.ITEMS_MAP_DESCRIPTION), g));
        }
    }

    private String rankedFormatting(Boolean ranked) {
        return ranked ? "&bRANKED" : "";
    }

    private List<String> variableList(List<String> list, SWGame g) {
        return list.stream()
                .map(s -> s.replace("%players%", String.valueOf(g.getPlayerCount()))
                        .replace("%maxplayers%", String.valueOf(g.getMaxPlayers())))
                .collect(Collectors.toList());
    }

    private Material getStateMaterial(SWGame g) {
        switch (g.getState()) {
            case WAITING:
                return g.isRanked() ? Material.LIGHT_BLUE_CONCRETE : Material.LIGHT_BLUE_WOOL;
            case AVAILABLE:
                return g.isRanked() ? Material.GREEN_CONCRETE : Material.GREEN_WOOL;
            case STARTING:
                return g.isRanked() ? Material.YELLOW_CONCRETE : Material.YELLOW_WOOL;
            case PLAYING:
                return g.isRanked() ? Material.RED_CONCRETE : Material.RED_WOOL;
            case FINISHING:
                return g.isRanked() ? Material.PURPLE_CONCRETE : Material.PURPLE_WOOL;
            case RESETTING:
                return g.isRanked() ? Material.BLACK_CONCRETE : Material.BLACK_WOOL;
            default:
                return g.isRanked() ? Material.BEACON : Material.DIRT;
        }
    }
}