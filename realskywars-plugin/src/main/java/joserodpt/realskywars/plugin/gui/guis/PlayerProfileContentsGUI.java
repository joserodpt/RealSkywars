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
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
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

public class PlayerProfileContentsGUI {

    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private static final Map<UUID, PlayerProfileContentsGUI> inventories = new HashMap<>();
    int pageNumber = 0;
    Pagination<RSWShopDisplayItem> p;
    private final Inventory inv;
    private final UUID uuid;
    private final Map<Integer, RSWShopDisplayItem> display = new HashMap<>();
    private final ShopManager.Categories cat;

    public PlayerProfileContentsGUI(RSWPlayer p, ShopManager.Categories t) {
        this.uuid = p.getUUID();
        this.cat = t;
        inv = Bukkit.getServer().createInventory(null, 54, getTitle(p, t));

        this.p = new Pagination<>(28, RealSkywarsAPI.getInstance().getPlayerManagerAPI().getBoughtItems(p, t));
        fillChest(this.p.getPage(pageNumber));
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
                        PlayerProfileContentsGUI current = inventories.get(uuid);
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
                                GUIManager.openPlayerMenu(p);
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

                            if (!a.isInteractive()) {
                                p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NOT_BUYABLE, true));
                                return;
                            }


                            if (e.getClick() == ClickType.RIGHT && current.cat == ShopManager.Categories.KITS) {
                                GUIManager.openKitPreview(p, RealSkywarsAPI.getInstance().getKitManagerAPI().getKit(a.getName()), 0);
                                return;
                            }
                            switch (current.cat) {
                                case KITS:
                                    p.setProperty(RSWPlayer.PlayerProperties.KIT, RealSkywarsAPI.getInstance().getKitManagerAPI().getKit(a.getName()));
                                    p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.KITS, false)));
                                    p.closeInventory();
                                    break;
                                case BOW_PARTICLES:
                                    p.setProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES, a.getInfo("Particle"));
                                    p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.BOWPARTICLE, false)));
                                    break;
                                case CAGE_BLOCKS:
                                    p.setProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK, a.getItemStack().getType());
                                    p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CAGEBLOCK, false)));
                                    break;
                                case WIN_BLOCKS:
                                    if (a.containsInfo("RandomBlock")) {
                                        p.setProperty(RSWPlayer.PlayerProperties.WIN_BLOCKS, "RandomBlock");
                                        p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.WINBLOCK, false)));
                                    } else {
                                        p.setProperty(RSWPlayer.PlayerProperties.WIN_BLOCKS, a.getMaterial().name());
                                        p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.WINBLOCK, false)));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            private void backPage(PlayerProfileContentsGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(PlayerProfileContentsGUI asd) {
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
            default:
                return "? not found";
        }
    }

    public void fillChest(List<RSWShopDisplayItem> items) {
        inv.clear();

        for (int i = 0; i < 9; ++i) {
            inv.setItem(i, placeholder);
        }

        display.clear();

        inv.setItem(9, placeholder);
        inv.setItem(17, placeholder);
        inv.setItem(36, placeholder);
        inv.setItem(44, placeholder);
        inv.setItem(45, placeholder);
        inv.setItem(46, placeholder);
        inv.setItem(47, placeholder);
        inv.setItem(48, placeholder);
        inv.setItem(49, placeholder);
        inv.setItem(50, placeholder);
        inv.setItem(51, placeholder);
        inv.setItem(52, placeholder);
        inv.setItem(53, placeholder);

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.get(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.get())));
            inv.setItem(27, Itens.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.get(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.get())));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.get(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.get())));
            inv.setItem(35, Itens.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.get(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.get())));
        }

        inv.setItem(49, Itens.createItem(Material.CHEST, 1, TranslatableLine.BUTTONS_MENU_TITLE.get(), Collections.singletonList(TranslatableLine.BUTTONS_MENU_DESC.get())));

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
