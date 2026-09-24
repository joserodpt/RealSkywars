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
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.PLAYER;

public class PlayerGUI {

    private Inventory inv;
    private static final Map<UUID, PlayerGUI> inventories = new HashMap<>();
    private static final Map<UUID, Integer> refresh = new HashMap<>();
    private final UUID uuid;

    public PlayerGUI(RSWPlayer p, UUID id, RSWPlayer target) {
        this.uuid = id;

        this.inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, target.getName());

        // infoMap
        List<String> lore = TranslatableList.STATS_ITEM_LORE.get(p)
                .stream()
                .map(s -> variables(s, target))
                .collect(Collectors.toCollection(ArrayList::new));

        inv.setItem(2, Itens.createItem(Material.MAP, 1, TranslatableLine.ITEM_STATS_NAME.with(PLAYER, target.getDisplayName()).get(p), lore));
    }

    //used on reload so nobody keeps clicking a GUI built from the old config
    public static void closeAll() {
        for (final PlayerGUI current : new ArrayList<>(inventories.values())) {
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
                    Player p = (Player) clicker;
                    if (p != null) {
                        UUID uuid = p.getUniqueId();
                        if (inventories.containsKey(uuid)) {
                            PlayerGUI current = inventories.get(uuid);
                            if (!current.getInventory().equals(e.getInventory())) {
                                return;
                            }
                            e.setCancelled(true);
                            if (e.getCurrentItem() == null) {
                                return;
                            }
                        }
                    }
                }
            }

            @EventHandler
            public void onDrag(final InventoryDragEvent e) {
                final PlayerGUI current = inventories.get(e.getWhoClicked().getUniqueId());
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
                    final PlayerGUI current = inventories.get(uuid);
                    if (current != null && e.getInventory().equals(current.getInventory())) {
                        current.unregister();

                        if (refresh.containsKey(uuid)) {
                            Bukkit.getScheduler().cancelTask(refresh.get(uuid));
                        }
                    }
                }
            }
        };
    }

    protected String variables(String s, RSWPlayer gp) {
        return s.replace("%space%", Text.makeSpace()).replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.KILLS, false) + "").replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapterAPI().getCoinsFormatted(gp)).replace("%deaths%", gp.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, false) + "").replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, false) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, false) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, false) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, false) + "");
    }

    public void openInventory(RSWPlayer player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getPlayer().getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getPlayer().getOpenInventory().getTopInventory();
            if (!inv.equals(openTop)) {
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
