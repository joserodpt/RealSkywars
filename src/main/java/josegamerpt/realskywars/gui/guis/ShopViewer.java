package josegamerpt.realskywars.gui.guis;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.gui.GUIManager;
import josegamerpt.realskywars.managers.CurrencyManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.misc.DisplayItem;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Pagination;
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

import java.util.*;

public class ShopViewer {

    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private static final Map<UUID, ShopViewer> inventories = new HashMap<>();
    int pageNumber = 0;
    Pagination<DisplayItem> p;
    private final Inventory inv;
    private final UUID uuid;
    private final HashMap<Integer, DisplayItem> display = new HashMap<>();
    private final ShopManager.Categories cat;

    public ShopViewer(RSWPlayer swPl, ShopManager.Categories t) {
        this.uuid = swPl.getUUID();
        this.cat = t;
        inv = Bukkit.getServer().createInventory(null, 54, getTitle(swPl, t));

        List<DisplayItem> items = RealSkywars.getShopManager().getCategoryContents(swPl, t);

        if (items.size() > 0) {
            p = new Pagination<>(28, items);
            fillChest(p.getPage(pageNumber));
        } else {
            fillChest(Collections.emptyList());
        }
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
                        ShopViewer current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                clicker.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();
                                }
                                GUIManager.openShopMenu(p);
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
                            DisplayItem a = current.display.get(e.getRawSlot());

                            if (!a.isInteractive()) {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NOT_BUYABLE, true));
                                return;
                            }

                            if (e.getClick() == ClickType.RIGHT && current.cat == ShopManager.Categories.KITS) {
                                p.getPlayer().closeInventory();
                                GUIManager.openKitPreview(p, RealSkywars.getKitManager().getKit(a.getID()), 1);
                                return;
                            }

                            if (p.getPlayer().hasPermission(a.getPermission())) {
                                if (a.isBought()) {
                                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SHOP_ALREADY_BOUGHT, true).replace("%name%", a.getName()));
                                } else {
                                    CurrencyManager cm = new CurrencyManager(p, a.getPrice());
                                    if (cm.canMakeOperation()) {
                                        cm.removeCoins();

                                        p.buyItem(a.getName() + "|" + current.cat.name());

                                        a.setBought(true);
                                        current.inv.setItem(e.getRawSlot(), Itens.createItemLoreEnchanted(e.getCurrentItem().getType(), 1, e.getCurrentItem().getItemMeta().getDisplayName(), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SHOP_ALREADY_BOUGHT, false).replace("%name%", a.getName()))));
                                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SHOP_BUY, true).replace("%name%", a.getName()).replace("%coins%", a.getPrice() + ""));
                                    } else {
                                        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.INSUFICIENT_COINS, true).replace("%coins%", p.getCoins() + ""));
                                    }
                                }
                            } else {
                                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SHOP_NO_PERM, true));
                            }
                        }
                    }
                }
            }

            private void backPage(ShopViewer asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(ShopViewer asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
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

    private String getTitle(RSWPlayer p, ShopManager.Categories t) {
        switch (t) {
            case KITS:
                return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.KITS, false);
            case BOW_PARTICLES:
                return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.BOWPARTICLE, false);
            case WIN_BLOCKS:
                return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.WINBLOCK, false);
            case CAGE_BLOCKS:
                return RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CAGEBLOCK, false);
            default:
                return "? not found";
        }
    }

    public void fillChest(List<DisplayItem> items) {
        inv.clear();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, placeholder);
        }

        display.clear();

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

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
            inv.setItem(27, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_BACK_DESC))));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
            inv.setItem(35, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_NEXT_DESC))));
        }

        inv.setItem(49, Itens.createItemLore(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))));

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (items.size() != 0) {
                    DisplayItem s = items.get(0);
                    inv.setItem(slot, s.getItemStack());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            slot++;
        }
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
            if (openTop != null && openTop.getHolder().equals(inv.getHolder())) {
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
