package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.classes.DisplayItem;
import josegamerpt.realskywars.managers.KitManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.player.PlayerManager;
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

public class ProfileContent {

    private static Map<UUID, ProfileContent> inventories = new HashMap<>();
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack menu = Itens.createItemLore(Material.CHEST, 1, "&9Menu",
            Collections.singletonList("&fClick here to go back to the main menu."));
    static ItemStack next = Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, "&aNext",
            Collections.singletonList("&fClick here to go to the next page."));
    static ItemStack back = Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, "&6Back",
            Collections.singletonList("&fClick here to go back to the next page."));
    private Inventory inv;
    private UUID uuid;
    private List<DisplayItem> items;
    private HashMap<Integer, DisplayItem> display = new HashMap<>();
    private ShopManager.Categories cat;
    int pageNumber = 0;
    Pagination<DisplayItem> p;
    private Boolean disableMenu = false;

    public ProfileContent(RSWPlayer as, ShopManager.Categories t) {
        this.uuid = as.getUniqueId();
        this.cat = t;
        inv = Bukkit.getServer().createInventory(null, 54, Text.color("Seeing " + t.name()));

        items = PlayerManager.getBoughtItems(as, t);

        p = new Pagination<>(28, items);
        fillChest(p.getPage(pageNumber), false);

        this.register();
    }

    public ProfileContent(Player as, ShopManager.Categories t, String invName) {
        this.uuid = as.getUniqueId();
        this.cat = t;
        inv = Bukkit.getServer().createInventory(null, 54, Text.color(invName));

        items = PlayerManager.getBoughtItems(PlayerManager.getPlayer(as), t);

        p = new Pagination<>(28, items);
        fillChest(p.getPage(pageNumber), true);

        disableMenu = true;

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
                        ProfileContent current = inventories.get(uuid);
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
                                GUIManager.openPlayerMenu(gp,!gp.isInMatch());
                                break;
                            case 26:
                            case 35:
                                nextPage(current);
                                gp.getPlayer().playSound(gp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                break;
                            case 18:
                            case 27:
                                backPage(current);
                                gp.getPlayer().playSound(gp.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            DisplayItem a = current.display.get(e.getRawSlot());

                            if (!a.isInteractive()) {
                                gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.NOT_BUYABLE, true));
                                return;
                            }

                            switch (current.cat) {
                                case KITS:
                                    gp.setProperty(RSWPlayer.PlayerProperties.KIT, KitManager.getKit(a.getID()));
                                    gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", LanguageManager.getString(gp, LanguageManager.TS.KITS, false)));
                                    gp.getPlayer().closeInventory();
                                    break;
                                case BOW_PARTICLES:
                                    gp.setProperty(RSWPlayer.PlayerProperties.BOW_PARTICLES, a.getInfo("Particle"));
                                    gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", LanguageManager.getString(gp, LanguageManager.TS.BOWPARTICLE, false)));
                                    break;
                                case CAGE_BLOCKS:
                                    gp.setProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK, a.getItemStack().getType());
                                    gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", LanguageManager.getString(gp, LanguageManager.TS.CAGEBLOCK, false)));
                                    break;
                                case WIN_BLOCKS:
                                    if (a.containsInfo("RandomBlock")) {
                                        gp.setProperty(RSWPlayer.PlayerProperties.WIN_BLOCKS, "RandomBlock");
                                        gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", LanguageManager.getString(gp, LanguageManager.TS.WINBLOCK, false)));
                                    } else {
                                        gp.setProperty(RSWPlayer.PlayerProperties.WIN_BLOCKS, a.getMaterial().name());
                                        gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.PROFILE_SELECTED, true).replace("%name%", a.getName()).replace("%type%", LanguageManager.getString(gp, LanguageManager.TS.WINBLOCK, false)));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            private void backPage(ProfileContent asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.disableMenu);
            }

            private void nextPage(ProfileContent asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.disableMenu);
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

    public void fillChest(List<DisplayItem> items, Boolean b) {

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

        if (b) {
            inv.setItem(49, placeholder);
        } else {
            inv.setItem(49, menu);
        }

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
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.getPlayer().openInventory(inv);
            }
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
