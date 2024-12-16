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
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.database.PlayerBoughtItemsRow;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.TransactionManager;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
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
import org.bukkit.event.inventory.ClickType;
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

public class ShopGUI {

    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "&6");
    private static final Map<UUID, ShopGUI> inventories = new HashMap<>();
    private int pageNumber = 0;
    private Pagination<RSWBuyableItem> p;
    private final Inventory inv;
    private final RSWPlayer rswp;
    private final Map<Integer, RSWBuyableItem> display = new HashMap<>();
    private RSWBuyableItem.ItemCategory cat;

    public ShopGUI(RSWPlayer rswp, RSWBuyableItem.ItemCategory t) {
        this.rswp = rswp;
        this.cat = t;
        this.inv = Bukkit.getServer().createInventory(null, 54, this.cat.getCategoryTitle(rswp));

        List<RSWBuyableItem> items = new ArrayList<>(RealSkywarsAPI.getInstance().getShopManagerAPI().getCategoryContents(this.cat));

        if (!items.isEmpty()) {
            p = new Pagination<>(28, items);
            fillChest(p.getPage(pageNumber));
        } else {
            fillChest(Collections.singletonList(new RSWBuyableItem()));
        }

        register();
    }


    private void fillChest(List<RSWBuyableItem> items) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 45, 53, 52, 51, 50, 49, 48, 47, 46, 45, 36, 27, 18, 9, 44}) {
            inv.setItem(slot, placeholder);
        }

        if (!firstPage()) {
            inv.setItem(18, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
            inv.setItem(27, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
        }

        if (!lastPage()) {
            inv.setItem(26, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
            inv.setItem(35, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
        }

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Cage-Block-Shop") && cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {
            inv.setItem(47, Itens.createItem(Material.SPAWNER, 1, TranslatableLine.CAGEBLOCK.get(rswp)));
        } else {
            inv.setItem(47, placeholder);
        }

        inv.setItem(48, Itens.createItem(Material.LEATHER_CHESTPLATE, 1, TranslatableLine.KITS.get(rswp)));

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Bow-Particles-Shop") && cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {
            inv.setItem(50, Itens.createItem(Material.BOW, 1, TranslatableLine.BOWPARTICLE.get(rswp)));
        } else {
            inv.setItem(50, placeholder);
        }

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Win-Block-Shop") && cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {
            inv.setItem(51, Itens.createItem(Material.FIREWORK_ROCKET, 1, TranslatableLine.WINBLOCK.get(rswp)));
        } else {
            inv.setItem(51, placeholder);
        }

        int pointer = 0;
        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43};
        for (RSWBuyableItem item : items) {
            inv.setItem(slots[pointer], item.getIcon(this.rswp));
            display.put(slots[pointer], item);
            ++pointer;
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
                            case 47:
                                p.closeInventory();
                                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Cage-Block-Shop") && current.cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {

                                    if (RSWConfig.file().getBoolean("Config.Shops.Only-Buy-Kits-Per-Match")) {
                                        PlayerItemsGUI kitShop = new PlayerItemsGUI(p, RSWBuyableItem.ItemCategory.CAGE_BLOCK);
                                        kitShop.openInventory(p);
                                    } else {
                                        ShopGUI kitShop = new ShopGUI(p, RSWBuyableItem.ItemCategory.CAGE_BLOCK);
                                        kitShop.openInventory(p);
                                    }

                                    return;
                                }
                                break;
                            case 48:
                                p.closeInventory();
                                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Kit-Shop") && current.cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {
                                    ShopGUI kitShop = new ShopGUI(p, RSWBuyableItem.ItemCategory.KIT);
                                    kitShop.openInventory(p);
                                    return;
                                }
                                break;
                            case 50:
                                p.closeInventory();
                                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Bow-Particles-Shop") && current.cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {

                                    if (RSWConfig.file().getBoolean("Config.Shops.Only-Buy-Kits-Per-Match")) {
                                        PlayerItemsGUI kitShop = new PlayerItemsGUI(p, RSWBuyableItem.ItemCategory.BOW_PARTICLE);
                                        kitShop.openInventory(p);
                                    } else {
                                        ShopGUI kitShop = new ShopGUI(p, RSWBuyableItem.ItemCategory.BOW_PARTICLE);
                                        kitShop.openInventory(p);
                                    }

                                    return;
                                }
                                break;
                            case 51:
                                p.closeInventory();
                                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Win-Block-Shop") && current.cat != RSWBuyableItem.ItemCategory.SPEC_SHOP) {

                                    if (RSWConfig.file().getBoolean("Config.Shops.Only-Buy-Kits-Per-Match")) {
                                        PlayerItemsGUI kitShop = new PlayerItemsGUI(p, RSWBuyableItem.ItemCategory.WIN_BLOCK);
                                        kitShop.openInventory(p);
                                    } else {
                                        ShopGUI kitShop = new ShopGUI(p, RSWBuyableItem.ItemCategory.WIN_BLOCK);
                                        kitShop.openInventory(p);
                                    }

                                    return;
                                }
                                break;
                            case 18:
                            case 27:
                                if (!current.firstPage()) {
                                    backPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                    return;
                                }
                                break;
                            case 26:
                            case 35:
                                if (!current.lastPage()) {
                                    nextPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                    return;
                                }
                                break;
                        }


                        if (current.display.containsKey(e.getRawSlot())) {
                            RSWBuyableItem a = current.display.get(e.getRawSlot());

                            if (current.cat == RSWBuyableItem.ItemCategory.SPEC_SHOP) {
                                switch (e.getClick()) {
                                    case SWAP_OFFHAND:
                                        a.addAmount(1);
                                        current.inv.setItem(e.getRawSlot(), a.getIcon(current.rswp));
                                        break;
                                    case DROP:
                                        a.addAmount(-1);
                                        current.inv.setItem(e.getRawSlot(), a.getIcon(current.rswp));
                                        break;
                                    default:
                                        if (p.getPlayer().hasPermission(a.getPermission())) {
                                            TransactionManager cm = new TransactionManager(p, a.getPrice(), TransactionManager.Operations.REMOVE, false);
                                            p.closeInventory();

                                            if (cm.removeCoins()) {
                                                p.getWorld().dropItem(p.getLocation(), new ItemStack(a.getMaterial(), a.getAmount()));
                                                a.setAmount(1);
                                                p.sendMessage(TranslatableLine.SHOP_BUY_MESSAGE.get(p, true).replace("%name%", a.getDisplayName()).replace("%coins%", a.getPriceFormatted()));
                                            } else {
                                                a.setAmount(1);
                                                p.sendMessage(TranslatableLine.INSUFICIENT_COINS.get(p, true).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapterAPI().getCoinsFormatted(p)));
                                            }
                                        } else {
                                            a.setAmount(1);
                                            TranslatableLine.SHOP_NO_PERM.send(p, true);
                                        }
                                        break;
                                }
                                p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                            } else {
                                if (a.isDummy()) {
                                    TranslatableLine.NOT_BUYABLE.send(p, true);
                                    return;
                                }

                                if (e.getCurrentItem().hasItemMeta()) {
                                    if (e.getCurrentItem().getItemMeta().hasEnchants()) {
                                        p.sendMessage(TranslatableLine.SHOP_ALREADY_BOUGHT.get(p, true).replace("%name%", a.getDisplayName()));
                                        return;
                                    }
                                }

                                if (e.getClick() == ClickType.RIGHT && a instanceof RSWKit) {
                                    GUIManager.openKitPreview(p, (RSWKit) a, 1);
                                    return;
                                }

                                if (p.getPlayer().hasPermission(a.getPermission())) {
                                    p.closeInventory();

                                    //if the item is a kit and buy kit per match is enabled
                                    if (a instanceof RSWKit && RSWConfig.file().getBoolean("Config.Shops.Only-Buy-Kits-Per-Match")) {
                                        TransactionManager cm = new TransactionManager(p, a.getPrice(), TransactionManager.Operations.REMOVE, false);
                                        if (cm.removeCoins()) {
                                            p.setKit((RSWKit) a);
                                            p.sendMessage(TranslatableLine.SHOP_BUY_MESSAGE.get(p, true).replace("%name%", a.getDisplayName()).replace("%coins%", a.getPriceFormatted()));
                                        } else {
                                            p.sendMessage(TranslatableLine.INSUFICIENT_COINS.get(p, true).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapterAPI().getCoinsFormatted(p)));
                                        }
                                        return;
                                    }

                                    if (a.isBought(p).getKey()) {
                                        p.sendMessage(TranslatableLine.SHOP_ALREADY_BOUGHT.get(p, true).replace("%name%", a.getDisplayName()));
                                    } else {
                                        TransactionManager cm = new TransactionManager(p, a.getPrice(), TransactionManager.Operations.REMOVE, false);
                                        if (cm.removeCoins()) {
                                            RealSkywarsAPI.getInstance().getDatabaseManagerAPI().saveNewBoughtItem(new PlayerBoughtItemsRow(p, a.getName(), current.cat.name()), true);
                                            p.sendMessage(TranslatableLine.SHOP_BUY_MESSAGE.get(p, true).replace("%name%", a.getDisplayName()).replace("%coins%", a.getPriceFormatted()));
                                        } else {
                                            p.sendMessage(TranslatableLine.INSUFICIENT_COINS.get(p, true).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapterAPI().getCoinsFormatted(p)));
                                        }
                                    }
                                } else {
                                    TranslatableLine.SHOP_NO_PERM.send(p, true);
                                }
                            }
                        }
                    }
                }
            }

            private void backPage(ShopGUI current) {
                if (current.p.exists(current.pageNumber - 1)) {
                    --current.pageNumber;
                }

                current.fillChest(current.p.getPage(current.pageNumber));
            }

            private void nextPage(ShopGUI current) {
                if (current.p.exists(current.pageNumber + 1)) {
                    ++current.pageNumber;
                }

                current.fillChest(current.p.getPage(current.pageNumber));
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
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
        }
    }

    private Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.rswp.getUUID(), this);
    }

    private void unregister() {
        inventories.remove(this.rswp.getUUID());
    }
}
