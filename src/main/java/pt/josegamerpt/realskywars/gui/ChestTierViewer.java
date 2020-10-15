package pt.josegamerpt.realskywars.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import pt.josegamerpt.realskywars.classes.ChestItem;
import pt.josegamerpt.realskywars.classes.Enum.TierType;
import pt.josegamerpt.realskywars.managers.ChestManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.utils.Pagination;
import pt.josegamerpt.realskywars.utils.SignGUI;

import java.util.*;

public class ChestTierViewer {

    private static final Map<UUID, ChestTierViewer> inventories = new HashMap<>();
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack info = Itens.createItemLore(Material.BIRCH_SIGN, 1, "&9Info",
            Arrays.asList("&fUse /rsw setchest <BASIC, NORMAL, OP, CAOS>", "&fto set the chest contents."));
    static ItemStack info2 = Itens.createItemLore(Material.BIRCH_SIGN, 1, "&9Info",
            Arrays.asList("&fYou are now seeing the contents of this tier.",
                    "&fThe tier is selected by players when the game is starting."));
    static ItemStack menu = Itens.createItemLore(Material.CHEST, 1, "&9Menu",
            Collections.singletonList("&fClick here to go back to the main menu."));
    static ItemStack next = Itens.createItemLore(Material.TRIDENT, 1, "&9Next",
            Collections.singletonList("&fClick here to go to the next page."));
    static ItemStack back = Itens.createItemLore(Material.CROSSBOW, 1, "&9Back",
            Collections.singletonList("&fClick here to go back to the next page."));
    private final Inventory inv;
    private final UUID uuid;
    private final TierType tp;
    int pageNumber = 0;
    Pagination<ChestItem> p;
    private List<ChestItem> showing;

    public ChestTierViewer(UUID id, TierType t) {
        this.uuid = id;
        inv = Bukkit.getServer().createInventory(null, 54, t.name() + " Contents");

        for (int i = 27; i < 9; i++) {
            inv.setItem(i, placeholder);
        }
        tp = t;
        int slot = 0;
        ArrayList<ChestItem> loot = ChestManager.seeTier(t);

        p = new Pagination<>(45, loot);

        if (p.totalPages() == 1) {
            for (ChestItem i : loot) {
                inv.setItem(slot, i.i);
                slot++;
            }
        } else {
            for (ChestItem i : p.getPage(pageNumber)) {
                inv.setItem(slot, i.i);
                slot++;
            }
        }
        showing = p.getPage(pageNumber);

        inv.setItem(45, back);
        inv.setItem(47, info);
        inv.setItem(49, menu);
        inv.setItem(51, info2);
        inv.setItem(53, next);

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
                    Player p = (Player) clicker;
                    if (p != null) {
                        UUID uuid = p.getUniqueId();
                        if (inventories.containsKey(uuid)) {
                            ChestTierViewer current = inventories.get(uuid);
                            if (!e.getInventory().getType().name()
                                    .equalsIgnoreCase(current.getInventory().getType().name())) {
                                return;
                            }
                            GamePlayer gp = PlayerManager.getPlayer(p);
                            e.setCancelled(true);

                            if (e.getRawSlot() == 49) {
                                ChestTierMenu r = new ChestTierMenu(p.getUniqueId());
                                p.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();

                                }
                                r.openInventory(gp);
                            }
                            if (e.getRawSlot() == 53) {
                                if (e.getCurrentItem().equals(next)) {
                                    nextPage(current);
                                }
                            }
                            if (e.getRawSlot() == 45) {
                                if (e.getCurrentItem().equals(back)) {
                                    backPage(current);
                                }
                            }
                            int i = e.getSlot();

                            if (containsItem(current.showing, e.getCurrentItem())) {
                                p.closeInventory();
                                SignGUI.openDialog(p, i + 1 + (current.pageNumber * 45), current.tp);
                            }
                        }
                    }
                }
            }

            public boolean containsItem(final List<ChestItem> list, ItemStack is) {
                return list.stream().anyMatch(o -> o.i.equals(is));
            }

            private void backPage(ChestTierViewer asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.inv.clear();
                int slot = 0;
                for (ChestItem i : asd.p.getPage(asd.pageNumber)) {
                    asd.inv.setItem(slot, i.i);
                    slot++;
                }

                asd.inv.setItem(45, back);
                asd.inv.setItem(47, info);
                asd.inv.setItem(49, menu);
                asd.inv.setItem(51, info2);
                asd.inv.setItem(53, next);

                asd.showing = asd.p.getPage(asd.pageNumber);
            }

            private void nextPage(ChestTierViewer asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
                }

                asd.inv.clear();
                int slot = 0;
                for (ChestItem i : asd.p.getPage(asd.pageNumber)) {
                    asd.inv.setItem(slot, i.i);
                    slot++;
                }

                asd.inv.setItem(45, back);
                asd.inv.setItem(47, info);
                asd.inv.setItem(49, menu);
                asd.inv.setItem(51, info2);
                asd.inv.setItem(53, next);

                asd.showing = asd.p.getPage(asd.pageNumber);
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

    public void openInventory(GamePlayer player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.p.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.p.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.p.openInventory(inv);
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
