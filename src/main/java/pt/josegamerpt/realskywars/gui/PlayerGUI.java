package pt.josegamerpt.realskywars.gui;

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
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerGUI {

    private static final Map<UUID, PlayerGUI> inventories = new HashMap<>();
    private static final Map<UUID, Integer> refresh = new HashMap<>();
    static Inventory inv;
    static GamePlayer gp;
    private final UUID uuid;

    public PlayerGUI(GamePlayer p, UUID id) {
        this.uuid = id;
        gp = p;

        inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, p.p.getName() + " Info");

        ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9About",
                Arrays.asList("&fPlayer State: " + p.state, "&fRoom: " + p.room,
                        "&fKills: " + p.totalkills, "&fDeaths: " + p.deaths, "&fCage Block:" + p.cageBlock));
        // infoMap
        inv.setItem(2, infoMap);

        refresher(id);

        inventories.put(id, this);
    }

    public static void refresher(UUID d) {
        int refreshTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, () -> {

            String roomName;
            if (gp.room == null) {
                roomName = "None";
            } else {
                roomName = gp.room.getName();
            }
            ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9About",
                    Arrays.asList("&fPlayer State: " + gp.state,
                            "&fRoom: " + roomName, "&fKills: " + gp.totalkills, "&fDeaths: " + gp.deaths, "&fCage Block:" + gp.cageBlock));

            // infoMap
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
