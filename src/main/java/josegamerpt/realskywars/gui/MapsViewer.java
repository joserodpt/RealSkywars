package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.misc.MapItem;
import josegamerpt.realskywars.misc.Selections;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Pagination;
import josegamerpt.realskywars.utils.Text;
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

public class MapsViewer {

    private static Map<UUID, MapsViewer> inventories = new HashMap<>();
    int pageNumber = 0;
    Pagination<SWGameMode> p;
    private ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private Inventory inv;
    private UUID uuid;
    private RSWPlayer gp;
    private HashMap<Integer, SWGameMode> display = new HashMap<>();
    private Selections.Values selMap;

    public MapsViewer(RSWPlayer as, Selections.Values t, String invName) {
        this.uuid = as.getUUID();
        inv = Bukkit.getServer().createInventory(null, 54, Text.color(invName) + ": " + RealSkywars.getLanguageManager().getString(as, select(t), false));

        gp = as;
        this.selMap = t;
        List<SWGameMode> items = RealSkywars.getGameManager().getRoomsWithSelection(t);

        p = new Pagination<>(28, items);
        fillChest(p.getPage(pageNumber), as);

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
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        MapsViewer current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer gp = RealSkywars.getPlayerManager().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                selectNext(gp);
                                break;
                            case 26:
                            case 35:
                                nextPage(current);
                                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                break;
                            case 18:
                            case 27:
                                backPage(current);
                                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            SWGameMode a = current.display.get(e.getRawSlot());
                            if (!a.isPlaceHolder()) {
                                a.addPlayer(gp);
                            }
                        }
                    }
                }
            }

            private void selectNext(RSWPlayer gp) {
                switch (gp.getSelection(Selections.Key.MAPVIEWER)) {
                    case MAPV_ALL:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.MAPV_AVAILABLE);
                        break;
                    case MAPV_AVAILABLE:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.MAPV_WAITING);
                        break;
                    case MAPV_WAITING:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.MAPV_STARTING);
                        break;
                    case MAPV_STARTING:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.MAPV_SPECTATE);
                        break;
                    case MAPV_SPECTATE:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.SOLO);
                        break;
                    case SOLO:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.SOLO_RANKED);
                        break;
                    case SOLO_RANKED:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.TEAMS);
                        break;
                    case TEAMS:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.TEAMS_RANKED);
                        break;
                    case TEAMS_RANKED:
                        gp.setSelection(Selections.Key.MAPVIEWER, Selections.Values.MAPV_ALL);
                        break;
                }

                gp.getPlayer().closeInventory();
                MapsViewer v = new MapsViewer(gp, gp.getSelection(Selections.Key.MAPVIEWER), RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.MAPS_NAME, false));
                v.openInventory(gp);
                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
            }

            private void backPage(MapsViewer asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.gp);
            }

            private void nextPage(MapsViewer asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
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

    private LanguageManager.TS select(Selections.Values t) {
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
        }
    }

    public void fillChest(List<SWGameMode> items, RSWPlayer p) {

        inv.clear();
        display.clear();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, placeholder);
        }

        inv.setItem(45, placeholder);
        inv.setItem(46, placeholder);
        inv.setItem(47, placeholder);
        inv.setItem(48, placeholder);
        inv.setItem(49, placeholder);
        inv.setItem(50, placeholder);
        inv.setItem(51, placeholder);
        inv.setItem(52, placeholder);
        inv.setItem(53, placeholder);
        inv.setItem(36, placeholder);
        inv.setItem(44, placeholder);
        inv.setItem(9, placeholder);
        inv.setItem(17, placeholder);

        inv.setItem(18, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE),
                Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
        inv.setItem(27, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE),
                Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
        inv.setItem(26, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE),
                Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
        inv.setItem(35, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE),
                Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (items.size() != 0) {
                    SWGameMode s = items.get(0);
                    MapItem a = new MapItem(s, p);
                    inv.setItem(slot, a.geIcon());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            slot++;
        }

        inv.setItem(49, Itens.createItemLore(Material.SIGN, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_FILTER_TITLE),
                Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_FILTER_DESC))));
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
