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
import joserodpt.realskywars.api.managers.CurrencyManager;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
import joserodpt.realskywars.plugin.RealSkywars;
import joserodpt.realskywars.plugin.gui.GUIManager;
import joserodpt.realskywars.plugin.managers.ShopManager;
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
import java.util.stream.Collectors;

public class ShopGUI {

    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private static final Map<UUID, ShopGUI> inventories = new HashMap<>();
    private int pageNumber = 0;
    private Pagination<RSWShopDisplayItem> p;
    private final Inventory inv;
    private final UUID uuid;
    private final Map<Integer, RSWShopDisplayItem> display = new HashMap<>();
    private final ShopManagerAPI.Categories cat;

    public ShopGUI(RSWPlayer swPl, ShopManagerAPI.Categories t) {
        this.uuid = swPl.getUUID();
        this.cat = t;
        inv = Bukkit.getServer().createInventory(null, 54, getTitle(swPl, t));

        List<RSWShopDisplayItem> items = RealSkywarsAPI.getInstance().getShopManagerAPI().getCategoryContents(swPl, t).stream().sorted(Comparator.comparingDouble(RSWShopDisplayItem::getPrice))
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            p = new Pagination<>(28, items);
            fillChest(p.getPage(pageNumber));
        } else {
            fillChest(Collections.emptyList());
        }
    }

    public void fillChest(List<RSWShopDisplayItem> items) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0,1,2,3,4,5,6,7,8,9,17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_DESC))));
            inv.setItem(27, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_DESC))));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_DESC))));
            inv.setItem(35, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_DESC))));
        }

        if (this.cat != ShopManagerAPI.Categories.SPEC_SHOP) {
            inv.setItem(49, Itens.createItem(Material.CHEST, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_MENU_DESC))));
        }

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    RSWShopDisplayItem s = items.get(0);
                    inv.setItem(slot, s.getItemStack());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }
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
                        ShopGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                clicker.closeInventory();
                                if (current.cat != ShopManagerAPI.Categories.SPEC_SHOP) {
                                    GUIManager.openShopMenu(p);
                                }
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
                            RSWShopDisplayItem a = current.display.get(e.getRawSlot());

                            if (current.cat == ShopManager.Categories.SPEC_SHOP) {
                                switch (e.getClick()) {
                                    case SWAP_OFFHAND:
                                        a.addAmount(1);
                                        current.inv.setItem(e.getRawSlot(), a.getItemStack());
                                        break;
                                    case DROP:
                                        a.addAmount(-1);
                                        current.inv.setItem(e.getRawSlot(), a.getItemStack());
                                        break;
                                    default:
                                        if (p.getPlayer().hasPermission(a.getPermission())) {
                                            CurrencyManager cm = new CurrencyManager(RealSkywarsAPI.getInstance().getCurrencyAdapter(), p, a.getPrice(), CurrencyManager.Operations.REMOVE, false);
                                            p.closeInventory();

                                            if (cm.removeCoins()) {
                                                p.getWorld().dropItem(p.getLocation(), new ItemStack(a.getMaterial(), a.getAmount()));

                                                p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SHOP_BUY, true).replace("%name%", a.getName()).replace("%coins%", a.getPrice() + ""));
                                            } else {
                                                p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.INSUFICIENT_COINS, true).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapter().getCoins(p) + ""));
                                            }
                                        } else {
                                            p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SHOP_NO_PERM, true));
                                        }
                                        break;
                                }
                                p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                            } else {
                                if (!a.isInteractive()) {
                                    p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NOT_BUYABLE, true));
                                    return;
                                }

                                if (e.getClick() == ClickType.RIGHT && current.cat == ShopManager.Categories.KITS) {
                                    GUIManager.openKitPreview(p, RealSkywars.getInstance().getKitManagerAPI().getKit(a.getName()), 1);
                                    return;
                                }

                                if (p.getPlayer().hasPermission(a.getPermission())) {
                                    p.closeInventory();
                                    if (a.isBought()) {
                                        p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SHOP_ALREADY_BOUGHT, true).replace("%name%", a.getName()));
                                    } else {
                                        CurrencyManager cm = new CurrencyManager(RealSkywarsAPI.getInstance().getCurrencyAdapter(), p, a.getPrice(), CurrencyManager.Operations.REMOVE, false);
                                        if (cm.removeCoins()) {
                                            p.buyItem(a.getName() + "|" + current.cat.name());

                                            a.setBought(true);
                                            current.inv.setItem(e.getRawSlot(), Itens.createItemLoreEnchanted(e.getCurrentItem().getType(), 1, e.getCurrentItem().getItemMeta().getDisplayName(), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SHOP_ALREADY_BOUGHT, false).replace("%name%", a.getName()))));
                                            p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SHOP_BUY, true).replace("%name%", a.getName()).replace("%coins%", a.getPrice() + ""));
                                        } else {
                                            p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.INSUFICIENT_COINS, true).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapter().getCoins(p) + ""));
                                        }
                                    }
                                } else {
                                    p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SHOP_NO_PERM, true));
                                }
                            }
                        }
                    }
                }
            }

            private void backPage(ShopGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(ShopGUI asd) {
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

    private String getTitle(RSWPlayer p, ShopManager.Categories t) {
        switch (t) {
            case KITS:
                return RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.KITS, false);
            case BOW_PARTICLES:
                return RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.BOWPARTICLE, false);
            case WIN_BLOCKS:
                return RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.WINBLOCK, false);
            case CAGE_BLOCKS:
                return RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CAGEBLOCK, false);
            case SPEC_SHOP:
                return RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_SPECTATOR_SHOP_TITLE, false);
            default:
                return "? not found";
        }
    }

    private boolean lastPage() {
        return p == null || pageNumber == (p.totalPages() - 1);
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
