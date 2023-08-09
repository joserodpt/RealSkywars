package josegamerpt.realskywars.gui.guis;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
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

import java.util.*;

public class MapSettings {
    private static final Map<UUID, MapSettings> inventories = new HashMap<>();
    private Inventory inv;

    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    private final ItemStack rankedon = Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked is turned &aON&7."));
    private final ItemStack rankedoff = Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked is turned &cOFF&7."));

    private final ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    private final ItemStack ieon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &aON&7."));
    private final ItemStack ieoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    private final ItemStack aAvailable = Itens.createItemLore(Material.GREEN_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&aAvailable", "&7Starting", "&7Waiting", "&7Playing", "&7Finishing", "&7Resetting"));
    private final ItemStack aStarting = Itens.createItemLore(Material.YELLOW_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&aStarting", "&7Waiting", "&7Playing", "&7Finishing", "&7Resetting"));
    private final ItemStack aWaiting = Itens.createItemLore(Material.LIGHT_BLUE_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&aWaiting", "&7Playing", "&7Finishing", "&7Resetting"));
    private final  ItemStack aPlaying = Itens.createItemLore(Material.RED_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&aPlaying", "&7Finishing", "&7Resetting"));
    private final ItemStack aFinishing = Itens.createItemLore(Material.GRAY_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing", "&aFinishing", "&7Resetting"));
    private final ItemStack aResetting = Itens.createItemLore(Material.PURPLE_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing", "&7Finishing", "&aResetting"));
    private final ItemStack resetRoom = Itens.createItemLore(Material.BARRIER, 1, "&9Reset Room", Arrays.asList("&cClick here to reset the room.", "&4NOTE: ALL PLAYERS WILL BE KICKED FROM THE GAME."));
    private final ItemStack borderon = Itens.createItemLore(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &aON&7."));
    private final ItemStack borderoff = Itens.createItemLore(Material.ITEM_FRAME, 1, "&9Border", Collections.singletonList("&7Border is turned &cOFF&7."));

    private static int refreshTask;
    private final UUID uuid;
    private SWGameMode game;

    public MapSettings(SWGameMode g, UUID id) {
        this.uuid = id;
        this.game = g;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(g.getName() + " Settings"));

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
        switch (game.getState()) {
            case AVAILABLE:
                inv.setItem(10, aAvailable);
                break;
            case FINISHING:
                inv.setItem(10, aFinishing);
                break;
            case PLAYING:
                inv.setItem(10, aPlaying);
                break;
            case RESETTING:
                inv.setItem(10, aResetting);
                break;
            case STARTING:
                inv.setItem(10, aStarting);
                break;
            case WAITING:
                inv.setItem(10, aWaiting);
                break;
        }

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
                        MapSettings current = inventories.get(uuid);
                        if (!e.getInventory().getType().name().equalsIgnoreCase(current.getInventory().getType().name())) {
                            return;
                        }

                        e.setCancelled(true);

                        RSWPlayer gp = RealSkywars.getPlugin().getPlayerManager().getPlayer(p);
                        ItemStack clickedItem = e.getCurrentItem();

                        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

                        switch (e.getRawSlot()) {
                            // reset
                            case 22:
                                p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.ARENA_RESET, true));
                                current.game.reset();
                                p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.MAP_RESET_DONE, true));

                                current.loadInv();
                                break;
                            case 10:
                                // arstat
                                switch (current.game.getState()) {
                                    case AVAILABLE:
                                        current.game.setState(SWGameMode.GameState.STARTING);
                                        break;
                                    case FINISHING:
                                        current.game.setState(SWGameMode.GameState.RESETTING);
                                        break;
                                    case PLAYING:
                                        current.game.setState(SWGameMode.GameState.FINISHING);
                                        break;
                                    case RESETTING:
                                        current.game.setState(SWGameMode.GameState.AVAILABLE);
                                        break;
                                    case STARTING:
                                        current.game.setState(SWGameMode.GameState.WAITING);
                                        break;
                                    case WAITING:
                                        current.game.setState(SWGameMode.GameState.PLAYING);
                                        break;
                                }
                                current.loadInv();

                                p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.GAME_STATUS_SET, true).replace("%status%", current.game.getState().name()));
                                break;
                            case 13:
                                // settings
                                current.game.setBorderEnabled(!current.game.isBorderEnabled());
                                current.game.save(SWGameMode.Data.SETTINGS, true);
                                current.loadInv();
                                break;
                            case 14:
                                // settings
                                current.game.setRanked(!current.game.isRanked());
                                current.game.save(SWGameMode.Data.SETTINGS, true);
                                current.loadInv();
                                break;
                            case 15:
                                // settings
                                current.game.setSpectator(!current.game.isSpectatorEnabled());
                                current.game.save(SWGameMode.Data.SETTINGS, true);
                                current.loadInv();
                                break;
                            case 16:
                                current.game.setInstantEnd(!current.game.isInstantEndEnabled());
                                current.game.save(SWGameMode.Data.SETTINGS, true);
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
        refreshTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.getPlugin(), () -> {
            ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9Info", Arrays.asList("&fMap type: &b" + game.getSWWorld().getType().name(), "&fPlayers: &b" + game.getPlayerCount() + "/" + game.getMaxPlayers(), "&fSpectators: &b" + game.getSpectatorsCount(), "&fChest Tier: &b" + game.getChestTier().name(), "", "&fRunning Time: &b" + Text.formatSeconds(game.getTimePassed())));
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
