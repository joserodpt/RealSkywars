package josegamerpt.realskywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIBuilder {

    /*
     * Modified and optimized version of AdvInventory Original author:
     * http://spigotmc.org/members/25376/ - Homer04 Original utility version:
     * http://www.spigotmc.org/threads/133942/ Modified by AnyOD Compatible
     * https://www.spigotmc.org/threads/gui-creator-v2-making-inventories-was-never-easier.296898/
     * versions: 1.8 and up
     */

    private static Map<UUID, GUIBuilder> inventories = new HashMap<>();
    private Inventory inv;
    private Map<Integer, ClickRunnable> runnables = new HashMap<>();
    private UUID uuid;

    public GUIBuilder(String name, int size, UUID uuid) {
        this(name, size, uuid, null);
    }

    public GUIBuilder(String name, int size, UUID uuid, ItemStack placeholder) {
        this.uuid = uuid;
        if (size == 0) {
            return;
        }
        this.inv = Bukkit.createInventory(null, size, Text.color(name));
        if (placeholder != null) {
            for (int i = 0; i < size; ++i) {
                inv.setItem(i, placeholder);
            }
        }
        this.register();
    }

    public GUIBuilder(String name, UUID uuid, ItemStack placeholder, InventoryType t) {
        this.uuid = uuid;
        this.inv = Bukkit.createInventory(null, t, Text.color(name));
        if (placeholder != null) {
            for (int i = 0; i < t.getDefaultSize(); ++i) {
                inv.setItem(i, placeholder);
            }
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
                    Player p = (Player) clicker;
                    if (p != null) {
                        UUID uuid = p.getUniqueId();
                        if (inventories.containsKey(uuid)) {
                            GUIBuilder current = inventories.get(uuid);
                            if (!e.getInventory().getType().name()
                                    .equalsIgnoreCase(current.getInventory().getType().name())) {
                                return;
                            }
                            e.setCancelled(true);
                            int slot = e.getSlot();
                            if (current.runnables.get(slot) != null) {
                                current.runnables.get(slot).run(e);
                            }
                        }
                    }
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
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unRegister();
                    }
                }
            }
        };
    }

    public static ItemStack placeholder(DyeColor d, String n) {
        ItemStack placeholder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta placeholdermeta = placeholder.getItemMeta();
        placeholdermeta.setDisplayName(n);
        placeholder.setItemMeta(placeholdermeta);
        return placeholder;
    }

    public Inventory getInventory() {
        return inv;
    }

    public int getSize() {
        return inv.getSize();
    }

    public void setItem(ItemStack is, Integer slot, ClickRunnable executeOnClick) {
        inv.setItem(slot, is);
        runnables.put(slot, executeOnClick);
    }

    public void setItem(ClickRunnable executeOnClick, ItemStack itemstack, Integer slot) {
        ItemMeta im = itemstack.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        inv.setItem(slot, itemstack);
        runnables.put(slot, executeOnClick);
    }

    public void removeItem(int slot) {
        inv.setItem(slot, new ItemStack(Material.AIR));
    }

    public void setItem(ItemStack itemstack, Integer slot) {
        inv.setItem(slot, itemstack);
    }

    public void openInventory(Player player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.openInventory(inv);
            }
            register();
        }
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unRegister() {
        inventories.remove(this.uuid);
    }

    public void addItem(ClickRunnable clickRunnable, ItemStack i, int slot) {
        ItemMeta im = i.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        inv.setItem(slot, i);
        runnables.put(slot, clickRunnable);
    }

    @FunctionalInterface
    public interface ClickRunnable {
        void run(InventoryClickEvent event);
    }

    @FunctionalInterface
    public interface CloseRunnable {
        void run(InventoryCloseEvent event);
    }
}
