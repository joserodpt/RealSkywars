package josegamerpt.realskywars.gui.guis;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.MapManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class RoomSettings {

    static SWGameMode game;
    static Inventory inv;
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    static ItemStack rankedon = Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked is turned &aON&7."));
    static ItemStack rankedoff = Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&9Ranked", Collections.singletonList("&7Ranked is turned &cOFF&7."));

    static ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator", Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    static ItemStack ieon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &aON&7."));
    static ItemStack ieoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending", Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    static ItemStack aAvailable = Itens.createItemLore(Material.GREEN_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&aAvailable", "&7Starting", "&7Waiting", "&7Playing", "&7Finishing", "&7Resetting"));
    static ItemStack aStarting = Itens.createItemLore(Material.YELLOW_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&aStarting", "&7Waiting", "&7Playing", "&7Finishing", "&7Resetting"));
    static ItemStack aWaiting = Itens.createItemLore(Material.LIGHT_BLUE_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&aWaiting", "&7Playing", "&7Finishing", "&7Resetting"));
    static ItemStack aPlaying = Itens.createItemLore(Material.RED_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&aPlaying", "&7Finishing", "&7Resetting"));
    static ItemStack aFinishing = Itens.createItemLore(Material.GRAY_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing", "&aFinishing", "&7Resetting"));
    static ItemStack aResetting = Itens.createItemLore(Material.PURPLE_CONCRETE, 1, "&9Map Status", Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing", "&7Finishing", "&aResetting"));
    static ItemStack resetRoom = Itens.createItemLore(Material.BARRIER, 1, "&9Reset Room", Arrays.asList("&cClick here to reset the room.", "&4NOTE: ALL PLAYERS WILL BE KICKED FROM THE GAME."));
    private static final Map<UUID, RoomSettings> inventories = new HashMap<>();
    private static int refreshTask;
    private final UUID uuid;

    public RoomSettings(SWGameMode g, UUID id) {
        this.uuid = id;
        game = g;

        inv = Bukkit.getServer().createInventory(null, 27, Text.color(g.getName() + " Settings"));

        for (int i = 0; i < 9; ++i) {
            inv.setItem(i, placeholder);
        }

        inv.setItem(18, placeholder);
        inv.setItem(19, placeholder);
        inv.setItem(20, placeholder);
        inv.setItem(21, placeholder);
        inv.setItem(22, placeholder);
        inv.setItem(23, placeholder);
        inv.setItem(24, placeholder);
        inv.setItem(25, placeholder);
        inv.setItem(26, placeholder);

        inv.setItem(9, placeholder);
        inv.setItem(17, placeholder);

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

        inv.setItem(14, game.isRanked() ? rankedon : rankedoff);
        inv.setItem(15, game.isSpectatorEnabled() ? specon : specoff);
        inv.setItem(16, game.isInstantEndEnabled() ? ieon : ieoff);

        // resetbutton
        inv.setItem(22, resetRoom);

        refresher();

        inventories.put(id, this);
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
                            RoomSettings current = inventories.get(uuid);
                            if (!e.getInventory().getType().name().equalsIgnoreCase(current.getInventory().getType().name())) {
                                return;
                            }
                            e.setCancelled(true);

                            RSWPlayer gp = RealSkywars.getPlugin().getPlayerManager().getPlayer(p);

                            if (inv != null) {
                                if (inv.getHolder() == e.getInventory().getHolder()) {
                                    if (e.getClick().equals(ClickType.NUMBER_KEY)) {
                                        e.setCancelled(true);
                                    }
                                    e.setCancelled(true);

                                    ItemStack clickedItem = e.getCurrentItem();

                                    if (clickedItem == null || clickedItem.getType() == Material.AIR) return;


                                    switch (e.getRawSlot()) {
                                        // reset
                                        case 22:
                                            p.closeInventory();
                                            p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.ARENA_RESET, true));
                                            game.reset();
                                            p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.MAP_RESET_DONE, true));

                                            ItemStack set = null;
                                            switch (game.getState()) {
                                                case AVAILABLE:
                                                    set = aAvailable;
                                                    break;
                                                case FINISHING:
                                                    set = aFinishing;
                                                    break;
                                                case PLAYING:
                                                    set = aPlaying;
                                                    break;
                                                case RESETTING:
                                                    set = aResetting;
                                                    break;
                                                case STARTING:
                                                    set = aStarting;
                                                    break;
                                                case WAITING:
                                                    set = aWaiting;
                                                    break;
                                            }
                                            current.getInventory().setItem(10, set);

                                            break;
                                        case 10:
                                            // arstat
                                            ItemStack set2 = null;
                                            switch (game.getState()) {
                                                case AVAILABLE:
                                                    game.setState(SWGameMode.GameState.STARTING);
                                                    set2 = aStarting;
                                                    break;
                                                case FINISHING:
                                                    game.setState(SWGameMode.GameState.RESETTING);
                                                    set2 = aResetting;
                                                    break;
                                                case PLAYING:
                                                    game.setState(SWGameMode.GameState.FINISHING);
                                                    set2 = aFinishing;
                                                    break;
                                                case RESETTING:
                                                    game.setState(SWGameMode.GameState.AVAILABLE);
                                                    set2 = aAvailable;
                                                    break;
                                                case STARTING:
                                                    game.setState(SWGameMode.GameState.WAITING);
                                                    set2 = aWaiting;
                                                    break;
                                                case WAITING:
                                                    game.setState(SWGameMode.GameState.PLAYING);
                                                    set2 = aPlaying;
                                                    break;
                                            }
                                            current.getInventory().setItem(10, set2);

                                            p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(gp, LanguageManager.TS.GAME_STATUS_SET, true).replace("%status%", game.getState().name()));
                                            break;
                                        case 14:
                                            // settings
                                            game.setRanked(!game.isRanked());
                                            if (game.isRanked()) {
                                                current.getInventory().setItem(14, rankedon);
                                            } else {
                                                current.getInventory().setItem(14, rankedoff);
                                            }
                                            MapManager.saveSettings(game);
                                            break;
                                        case 15:
                                            // settings
                                            game.setSpectator(!game.isSpectatorEnabled());
                                            if (game.isSpectatorEnabled()) {
                                                current.getInventory().setItem(15, specon);
                                            } else {
                                                current.getInventory().setItem(15, specoff);
                                            }
                                            MapManager.saveSettings(game);
                                            break;
                                        case 16:
                                            game.setInstantEnd(!game.isInstantEndEnabled());
                                            if (game.isInstantEndEnabled()) {
                                                current.getInventory().setItem(16, ieon);
                                            } else {
                                                current.getInventory().setItem(16, ieoff);
                                            }
                                            MapManager.saveSettings(game);
                                            break;
                                    }
                                }
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
            ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9Info", Arrays.asList("&fPlayers: " + game.getPlayerCount() + "/" + game.getMaxPlayers(), "&fSpectators: " + game.getSpectatorsCount(), "&fChest Tier: &b" + game.getChestTier().name(), "", "&fRunning Time: " + game.getTimePassed(), "Map type:" + game.getSWWorld().getType().name()));
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
