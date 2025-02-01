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
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.plugin.RealSkywars;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapDashboardGUI {
    private static final Map<UUID, MapDashboardGUI> inventories = new HashMap<>();
    private Inventory inv;

    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack specon = Itens.createItem(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    private final ItemStack rankedon = Itens.createItem(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked is turned &aON&7."));
    private final ItemStack rankedoff = Itens.createItem(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked is turned &cOFF&7."));

    private final ItemStack specoff = Itens.createItem(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    private final ItemStack ieon = Itens.createItem(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &aON&7."));
    private final ItemStack ieoff = Itens.createItem(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    private final ItemStack resetRoom = Itens.createItem(Material.BARRIER, 1, "&9Reset Room", Arrays.asList("&cClick here to reset the room.", "&4NOTE: ALL PLAYERS WILL BE KICKED FROM THE GAME."));
    private final ItemStack borderon = Itens.createItem(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &aON&7."));
    private final ItemStack borderoff = Itens.createItem(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &cOFF&7."));

    private static int refreshTask;
    private final UUID uuid;
    private RSWMap game;

    public MapDashboardGUI(RSWMap g, UUID id) {
        this.uuid = id;
        this.game = g;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(g.getName() + " settings"));

        loadInv();

        refresher();
    }

    private void loadInv() {
        inv.clear();

        int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

        for (int slot : slots) {
            inv.setItem(slot, placeholder);
        }

        // ARENASTATE
        inv.setItem(10, game.getState().getStateIcon(game.isRanked()));

        inv.setItem(13, game.isBorderEnabled() ? borderon : borderoff);
        inv.setItem(14, game.isRanked() ? rankedon : rankedoff);
        inv.setItem(15, game.isSpectatorEnabled() ? specon : specoff);
        inv.setItem(16, game.isInstantEndEnabled() ? ieon : ieoff);

        // resetbutton
        inv.setItem(22, resetRoom);
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
                    UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        MapDashboardGUI current = inventories.get(uuid);
                        if (!e.getInventory().getType().name().equalsIgnoreCase(current.getInventory().getType().name())) {
                            return;
                        }

                        e.setCancelled(true);

                        RSWPlayer gp = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer(p);
                        ItemStack clickedItem = e.getCurrentItem();

                        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

                        switch (e.getRawSlot()) {
                            // reset
                            case 22:
                                TranslatableLine.ARENA_RESET.sendDefault(p, true);
                                current.game.reset();
                                TranslatableLine.MAP_RESET_DONE.sendDefault(p, true);

                                current.loadInv();
                                break;
                            case 10:
                                // arstat
                                switch (current.game.getState()) {
                                    case AVAILABLE:
                                        current.game.setState(RSWMap.MapState.STARTING);
                                        break;
                                    case FINISHING:
                                        current.game.setState(RSWMap.MapState.RESETTING);
                                        break;
                                    case PLAYING:
                                        current.game.setState(RSWMap.MapState.FINISHING);
                                        break;
                                    case RESETTING:
                                        current.game.setState(RSWMap.MapState.AVAILABLE);
                                        break;
                                    case STARTING:
                                        current.game.setState(RSWMap.MapState.WAITING);
                                        break;
                                    case WAITING:
                                        current.game.setState(RSWMap.MapState.PLAYING);
                                        break;
                                }
                                current.loadInv();

                                p.sendMessage(TranslatableLine.GAME_STATUS_SET.get(gp, true).replace("%status%", current.game.getState().getDisplayName(gp)));
                                break;
                            case 13:
                                // settings
                                current.game.setBorderEnabled(!current.game.isBorderEnabled());
                                current.game.save(RSWMap.Data.SETTINGS, true);
                                current.loadInv();
                                break;
                            case 14:
                                // settings
                                current.game.setRanked(!current.game.isRanked());
                                current.game.save(RSWMap.Data.SETTINGS, true);
                                current.loadInv();
                                break;
                            case 15:
                                // settings
                                current.game.setSpectating(!current.game.isSpectatorEnabled());
                                current.game.save(RSWMap.Data.SETTINGS, true);
                                current.loadInv();
                                break;
                            case 16:
                                current.game.setInstantEnding(!current.game.isInstantEndEnabled());
                                current.game.save(RSWMap.Data.SETTINGS, true);
                                current.loadInv();
                                break;
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

                        Bukkit.getScheduler().cancelTask(refreshTask);
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

    private void refresher() {
        refreshTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.getInstance().getPlugin(), () -> {
            ItemStack infoMap = Itens.createItem(Material.MAP, 1, "&9Info", Arrays.asList("&fMap type: &b" + game.getRSWWorld().getType().name(), "&fPlayers: &b" + game.getPlayerCount() + "/" + game.getMaxPlayers(), "&fSpectators: &b" + game.getSpectatorsCount(), "&fChest Tier: &b" + game.getChestTier().name(), "", "&fRunning Time: &b" + Text.formatSeconds(game.getTimePassed())));
            // infoMap
            inv.setItem(4, infoMap);
        }, 0L, 10L);
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
