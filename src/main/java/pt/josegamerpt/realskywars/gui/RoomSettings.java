package pt.josegamerpt.realskywars.gui;

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
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.MapManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

import java.util.*;

public class RoomSettings {

    private static final Map<UUID, RoomSettings> inventories = new HashMap<>();
    static GameRoom game;
    static Inventory inv;
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack specon = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
            Collections.singletonList("&7Spectator is turned &aON &7for dead players."));
    static ItemStack specoff = Itens.createItemLore(Material.ENDER_EYE, 1, "&9Spectator",
            Collections.singletonList("&7Spectator is turned &cOFF &7for dead players."));
    static ItemStack ieon = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending",
            Collections.singletonList("&7Instant Ending is turned &aON&7."));
    static ItemStack ieoff = Itens.createItemLore(Material.DRAGON_HEAD, 1, "&9Instant Ending",
            Collections.singletonList("&7Instant Ending is turned &cOFF&7."));
    static ItemStack aAvailable = Itens.createItemLore(Material.GREEN_CONCRETE, 1, "&9Map Status",
            Arrays.asList("&fCick to change the map status.", "", "&aAvailable", "&7Starting", "&7Waiting", "&7Playing",
                    "&7Finishing", "&7Resetting"));
    static ItemStack aStarting = Itens.createItemLore(Material.YELLOW_CONCRETE, 1, "&9Map Status",
            Arrays.asList("&fCick to change the map status.", "", "&7Available", "&aStarting", "&7Waiting", "&7Playing",
                    "&7Finishing", "&7Resetting"));
    static ItemStack aWaiting = Itens.createItemLore(Material.LIGHT_BLUE_CONCRETE, 1, "&9Map Status",
            Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&aWaiting", "&7Playing",
                    "&7Finishing", "&7Resetting"));
    static ItemStack aPlaying = Itens.createItemLore(Material.RED_CONCRETE, 1, "&9Map Status",
            Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&aPlaying",
                    "&7Finishing", "&7Resetting"));
    static ItemStack aFinishing = Itens.createItemLore(Material.GRAY_CONCRETE, 1, "&9Map Status",
            Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing",
                    "&aFinishing", "&7Resetting"));
    static ItemStack aResetting = Itens.createItemLore(Material.PURPLE_CONCRETE, 1, "&9Map Status",
            Arrays.asList("&fCick to change the map status.", "", "&7Available", "&7Starting", "&7Waiting", "&7Playing",
                    "&7Finishing", "&aResetting"));
    static ItemStack resetRoom = Itens.createItemLore(Material.BARRIER, 1, "&9Reset Room",
            Arrays.asList("&cClick here to reset the room.", "&4NOTE: ALL PLAYERS WILL BE KICKED FROM THE GAME."));
    private static int refreshTask;
    private final UUID uuid;

    public RoomSettings(GameRoom g, UUID id) {
        this.uuid = id;
        game = g;

        inv = Bukkit.getServer().createInventory(null, 27, g.getName() + " Settings");

        for (int i = 0; i < 9; i++) {
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
        if (game.getState() == GameState.AVAILABLE) {
            inv.setItem(10, aAvailable);
        }
        if (game.getState() == GameState.FINISHING) {
            inv.setItem(10, aFinishing);
        }
        if (game.getState() == GameState.PLAYING) {
            inv.setItem(10, aPlaying);
        }
        if (game.getState() == GameState.RESETTING) {
            inv.setItem(10, aResetting);
        }
        if (game.getState() == GameState.STARTING) {
            inv.setItem(10, aStarting);
        }
        if (game.getState() == GameState.WAITING) {
            inv.setItem(10, aWaiting);
        }

        // DETAILS
        if (game.isSpectatorEnabled()) {
            inv.setItem(15, specon);
        } else {
            inv.setItem(15, specoff);
        }
        if (game.isInstantEndEnabled()) {
            inv.setItem(16, ieon);
        } else {
            inv.setItem(16, ieoff);
        }

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
                            if (!e.getInventory().getType().name()
                                    .equalsIgnoreCase(current.getInventory().getType().name())) {
                                return;
                            }
                            e.setCancelled(true);

                            GamePlayer gp = PlayerManager.getPlayer(p);

                            if (inv != null) {
                                if (inv.getHolder() == e.getInventory().getHolder()) {
                                    if (e.getClick().equals(ClickType.NUMBER_KEY)) {
                                        e.setCancelled(true);
                                    }
                                    e.setCancelled(true);

                                    ItemStack clickedItem = e.getCurrentItem();

                                    if (clickedItem == null || clickedItem.getType() == Material.AIR)
                                        return;

                                    // reset
                                    if (e.getRawSlot() == 22) {
                                        game.kickPlayers(LanguageManager.getString(gp, TS.ARENA_RESET, true));
                                        game.resetArena();
                                        p.sendMessage(LanguageManager.getString(gp, TS.MAP_RESET_DONE, true));

                                        ItemStack set = null;
                                        switch (game.getState())
                                        {
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
                                    }

                                    // arstat
                                    if (e.getRawSlot() == 10) {
                                        ItemStack set = null;
                                        switch (game.getState())
                                        {
                                            case AVAILABLE:
                                                game.setState(GameState.STARTING);
                                                set = aStarting;
                                                break;
                                            case FINISHING:
                                                game.setState(GameState.RESETTING);
                                                set = aResetting;
                                                break;
                                            case PLAYING:
                                                game.setState(GameState.FINISHING);
                                                set = aFinishing;
                                                break;
                                            case RESETTING:
                                                game.setState(GameState.AVAILABLE);
                                                set = aAvailable;
                                                break;
                                            case STARTING:
                                                game.setState(GameState.WAITING);
                                                set = aWaiting;
                                                break;
                                            case WAITING:
                                                game.setState(GameState.PLAYING);
                                                set = aPlaying;
                                                break;
                                        }
                                        current.getInventory().setItem(10, set);

                                        p.sendMessage(LanguageManager.getString(gp, TS.GAME_STATUS_SET, true).replace("%status%", game.getState().name()));
                                    }

                                    // settings
                                    if (e.getRawSlot() == 15) {
                                        if (game.isSpectatorEnabled()) {
                                            game.setSpectator(false);
                                            current.getInventory().setItem(15, specoff);
                                        } else {
                                            game.setSpectator(true);
                                            current.getInventory().setItem(15, specon);
                                        }
                                        MapManager.saveSettings(game);
                                    }
                                    if (e.getRawSlot() == 16) {
                                        if (game.isInstantEndEnabled()) {
                                            game.setInstantEnd(false);
                                            current.getInventory().setItem(16, ieoff);
                                        } else {
                                            game.setInstantEnd(true);
                                            current.getInventory().setItem(16, ieon);
                                        }
                                        MapManager.saveSettings(game);
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

    private void refresher() {
        refreshTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, () -> {
            ItemStack infoMap = Itens.createItemLore(Material.MAP, 1, "&9Info",
                    Arrays.asList("&fPlayers: " + game.getPlayersCount() + "/" + game.getMaxPlayers(),
                            "&fSpectators: " + game.getSpectatorsCount(), "&fChest Tier: &b" + game.getTierType().name(),
                            "&fBlocks Placed: " + game.getBlocksPlaced().size(), "&fBlocks Removed: " + game.getBlocksDestroyed().size(), "",
                            "&fRunning Time: " + game.getTimePassed()));
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
