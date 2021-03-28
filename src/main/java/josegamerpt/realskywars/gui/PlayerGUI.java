package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;
import josegamerpt.realskywars.RealSkywars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerGUI {

    private static Map<UUID, PlayerGUI> inventories = new HashMap<>();
    private static Map<UUID, Integer> refresh = new HashMap<>();
    static Inventory inv;
    static RSWPlayer gp;
    private UUID uuid;

    public PlayerGUI(RSWPlayer p, UUID id) {
        this.uuid = id;
        gp = p;

        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, p.getPlayer().getName() + " Info");

        // infoMap
        ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9About",
                Arrays.asList("&fPlayer State: " + p.getState(), "&fRoom: " + p.getRoom(),
                        "&fKills: " + p.getStatistics(RSWPlayer.PlayerStatistics.TOTAL_KILLS), "&fDeaths: " + p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS), "&fCage Block: " + ((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name()));
        inv.setItem(2, infoMap);

        refresher(id, p);

        inventories.put(id, this);
    }

    public static void refresher(UUID d, RSWPlayer p) {
        int refreshTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.getPlugin(), () -> {

            // infoMap
            ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9About",
                    Arrays.asList("&fPlayer State: " + p.getState(), "&fRoom: " + p.getRoom(),
                            "&fKills: " + p.getStatistics(RSWPlayer.PlayerStatistics.TOTAL_KILLS), "&fDeaths: " + p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS), "&fCage Block: " + ((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name()));

            inv.setItem(2, infoMap);
        }, 0L, 10L);
        refresh.put(d, refreshTask);
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
                            PlayerGUI current = inventories.get(uuid);
                            if (!e.getInventory().getType().name()
                                    .equalsIgnoreCase(current.getInventory().getType().name())) {
                                return;
                            }
                            e.setCancelled(true);
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
                        inventories.get(uuid).unregister();

                        if (refresh.containsKey(uuid)) {
                            Bukkit.getScheduler().cancelTask(refresh.get(uuid));
                        }
                    }
                }
            }
        };
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
