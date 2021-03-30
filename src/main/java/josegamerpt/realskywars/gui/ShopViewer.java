package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.classes.DisplayItem;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.managers.CurrencyManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Pagination;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class ShopViewer {

    private static Map<UUID, ShopViewer> inventories = new HashMap<>();
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack menu = Itens.createItemLore(Material.CHEST, 1, "&9Menu",
            Collections.singletonList("&fClick here to go back to the main menu."));
    static ItemStack next = Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, "&aNext",
            Collections.singletonList("&fClick here to go to the next page."));
    static ItemStack back = Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, "&6Back",
            Collections.singletonList("&fClick here to go back to the next page."));
    private Inventory inv;
    private UUID uuid;
    private HashMap<Integer, DisplayItem> display = new HashMap<>();
    private Enum.Categories cat;
    int pageNumber = 0;
    Pagination<DisplayItem> p;

    public ShopViewer(UUID id, Enum.Categories t) {
        this.uuid = id;
        this.cat = t;
        inv = Bukkit.getServer().createInventory(null, 54, Text.color(t.name()));

        List<DisplayItem> items = ShopManager.getCategoryContents(PlayerManager.getPlayer(PlayerManager.searchPlayer(id)), t);

        p = new Pagination<>(28, items);

        fillChest(p.getPage(pageNumber));

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
                        RSWPlayer gp = PlayerManager.getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                clicker.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();
                                }
                                GUIManager.openShopMenu(gp);
                                break;
                            case 26:
                            case 35:
                                nextPage(current);
                                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                                break;
                            case 18:
                            case 27:
                                backPage(current);
                                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            DisplayItem a = current.display.get(e.getRawSlot());

                            if (!a.isInteractive()) {
                                gp.sendMessage(LanguageManager.getString(gp, Enum.TS.NOT_BUYABLE, true));
                                return;
                            }

                            if (gp.getPlayer().hasPermission(a.getPermission())) {
                                if (a.isBought()) {
                                    gp.sendMessage(LanguageManager.getString(gp, Enum.TS.SHOP_ALREADY_BOUGHT, true)
                                            .replace("%name%", a.getName()));
                                } else {
                                    CurrencyManager cm = new CurrencyManager(gp, a.getPrice());
                                    if (cm.canMakeOperation()) {
                                        cm.removeCoins();

                                        gp.buyItem(a.getName() + "|" + current.cat.name());

                                        a.setBought(true);
                                        current.inv.setItem(e.getRawSlot(),
                                                Itens.createItemLoreEnchanted(e.getCurrentItem().getType(), 1,
                                                        e.getCurrentItem().getItemMeta().getDisplayName(),
                                                        Collections.singletonList("&aYou already bought this!")));
                                        gp.sendMessage(LanguageManager.getString(gp, Enum.TS.SHOP_BUY, true)
                                                .replace("%name%", a.getName()).replace("%coins%", a.getPrice() + ""));
                                    } else {
                                        gp.sendMessage(LanguageManager.getString(gp, Enum.TS.INSUFICIENT_COINS, true)
                                                .replace("%coins%", gp.getCoins() + ""));
                                    }
                                }
                            } else {
                                gp.sendMessage(LanguageManager.getString(gp, Enum.TS.SHOP_NO_PERM, true));
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

        inv.setItem(18, back);
        inv.setItem(27, back);
        inv.setItem(26, next);
        inv.setItem(35, next);

        inv.setItem(49, menu);

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
