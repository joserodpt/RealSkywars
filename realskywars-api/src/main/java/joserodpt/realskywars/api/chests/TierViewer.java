package joserodpt.realskywars.api.chests;

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
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
import joserodpt.realskywars.api.utils.PlayerInput;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realutils.dialog.DialogForm;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TierViewer {

    private static final Map<UUID, TierViewer> inventories = new HashMap<>();
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final Inventory inv;
    private final UUID uuid;
    private final Map<Integer, RSWChestItem> display = new HashMap<>();
    private final RSWChest.Tier ct;
    private final RSWChest.Type cte;
    private int pageNumber = 0;
    private Pagination<RSWChestItem> p;
    private List<RSWChestItem> items;

    public TierViewer(RSWPlayer p, RSWChest.Tier ct, RSWChest.Type cte) {
        this.uuid = p.getUUID();
        this.ct = ct;
        this.cte = cte;
        this.inv = Bukkit.getServer().createInventory(null, 54, Text.color("&8" + ct.getDisplayName(p) + " - " + cte.name()));

        this.items = ct.getChest(cte);

        this.p = new Pagination<>(28, this.items);
        fillChest(this.p.getPage(this.pageNumber));

        this.register();
    }

    /**
     * Every item in this chest with a slider for its chance, like the list of them in this screen.
     *
     * @return false if dialogs are not supported, and nothing was shown
     */
    private boolean openChances(final RSWPlayer player) {
        if (this.items.isEmpty()) {
            return false;
        }
        final DialogForm form = new DialogForm("&8" + this.ct.getDisplayName(player) + " - " + this.cte.name(),
                "&7The chance of each item appearing in a chest, in percent.");
        for (final RSWChestItem item : this.items) {
            form.icon(item.getItemStack().getType(), "&f" + name(item) + " &7- &b" + item.getChance() + "%");
        }
        for (int i = 0; i < this.items.size(); i++) {
            final RSWChestItem item = this.items.get(i);
            //by position: a chest can hold the same material more than once
            form.slider("item_" + i, "&f" + name(item) + " &7(%)", 0, 100, 1, item.getChance())
                    .sprite(item.getItemStack().getType());
        }
        form.buttons("&aSave", "&7Back");

        return form.open(player.getPlayer(), answers -> {
            boolean changed = false;
            for (int i = 0; i < this.items.size(); i++) {
                final RSWChestItem item = this.items.get(i);
                final Double chance = answers.moved("item_" + i, item.getChance(), 1);
                if (chance != null) {
                    item.setChance((int) Math.round(chance));
                    changed = true;
                }
            }
            if (changed) {
                try {
                    this.ct.set2ChestRaw(this.cte, this.items);
                } catch (final IOException e) {
                    RealSkywarsAPI.getInstance().getLogger().warning("Couldn't save the chest chances: " + e.getMessage());
                }
            }
            new TierViewer(player, this.ct, this.cte).openInventory(player.getPlayer());
        }, () -> new TierViewer(player, this.ct, this.cte).openInventory(player.getPlayer()),
                () -> new TierViewer(player, this.ct, this.cte).openInventory(player.getPlayer()));
    }

    /** The item's own name if it has one, otherwise its material's. */
    private static String name(final RSWChestItem item) {
        final ItemStack stack = item.getItemStack();
        if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            return stack.getItemMeta().getDisplayName();
        }
        return Text.beautifyEnumName(stack.getType().name());
    }

    private boolean lastPage() {
        return pageNumber == (p.totalPages() - 1);
    }

    private boolean firstPage() {
        return pageNumber == 0;
    }

    public void fillChest(List<RSWChestItem> items) {
        inv.clear();
        display.clear();

        for (int i = 0; i < 10; ++i) {
            inv.setItem(i, placeholder);
        }

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

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    RSWChestItem s = items.get(0);
                    this.inv.setItem(slot, s.getDisplayItemStack());
                    this.display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }
    }

    public void openInventory(Player player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getOpenInventory().getTopInventory();
            if (!inv.equals(openTop)) {
                player.openInventory(inv);
            }
            register();
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    //used on reload so nobody keeps clicking a GUI built from the old config
    public static void closeAll() {
        for (final TierViewer current : new ArrayList<>(inventories.values())) {
            final Player p = Bukkit.getPlayer(current.uuid);
            if (p != null && p.getOpenInventory().getTopInventory().equals(current.getInventory())) {
                p.closeInventory();
            }
        }
        inventories.clear();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        TierViewer current = inventories.get(uuid);
                        if (!current.getInventory().equals(e.getInventory())) {
                            return;
                        }

                        e.setCancelled(true);
                        if (e.getCurrentItem() == null) {
                            return;
                        }
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
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
                            RSWChestItem a = current.display.get(e.getRawSlot());
                            //every item's chance at once, where the server has dialogs
                            if (current.openChances(p)) {
                                return;
                            }
                            p.closeInventory();

                            new PlayerInput(p.getPlayer(), input -> {
                                int val = Integer.parseInt(input.replace("%", ""));

                                a.setChance(val);

                                current.ct.set2ChestRaw(current.cte, current.items);

                                TierViewer tv = new TierViewer(p, current.ct, current.cte);
                                tv.openInventory(p.getPlayer());
                            }, input -> {
                                TierViewer tv = new TierViewer(p, current.ct, current.cte);
                                tv.openInventory(p.getPlayer());
                            });
                        }
                    }
                }
            }

            private void backPage(TierViewer asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(TierViewer asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            @EventHandler
            public void onDrag(final InventoryDragEvent e) {
                final TierViewer current = inventories.get(e.getWhoClicked().getUniqueId());
                //dragging over this GUI's slots would drop the dragged items into it
                if (current != null && current.getInventory().equals(e.getInventory())) {
                    e.setCancelled(true);
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    final TierViewer current = inventories.get(uuid);
                    if (current != null && e.getInventory().equals(current.getInventory())) {
                        current.unregister();
                    }
                }
            }
        };
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
