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

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Format;
import joserodpt.realutils.dialog.DialogForm;
import joserodpt.realutils.input.PlayerInput;
import joserodpt.realutils.item.Items;
import joserodpt.realutils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapSettingsGUI {

    private static final Map<UUID, MapSettingsGUI> inventories = new HashMap<>();
    private Inventory inv;
    private final ItemStack placeholder = Items.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack confirm = Items.createItem(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to confirm your settings."));

    private final UUID uuid;
    private final RSWMap map;

    public MapSettingsGUI(Player p, RSWMap map) {
        this.uuid = p.getUniqueId();
        this.map = map;

        inv = Bukkit.getServer().createInventory(null, 45, Text.color(map.getName() + " settings"));

        loadInv();
    }

    public MapSettingsGUI(RSWPlayer p, RSWMap map) {
        this.uuid = p.getUUID();
        this.map = map;

        inv = Bukkit.getServer().createInventory(null, 45, Text.color(map.getName() + " settings"));

        loadInv();
    }

    private void loadInv() {
        inv.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 17, 26, 35, 44, 37, 38, 39, 41, 42, 43}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(10, Items.createItem(Material.ENDER_EYE, 1, "&9Spectator " + styleBool(map.isSpectatorEnabled()), Collections.singletonList("&7Spectate when a player dies. Click to toggle.")));
        inv.setItem(12, Items.createItem(Material.FEATHER, 1, "&9Instant Ending " + styleBool(map.isInstantEndEnabled()), Arrays.asList("&7When a player wins, the game is instantly resetted", "&7and all players are teleported to the lobby. Click to toggle.")));
        inv.setItem(14, Items.createItem(Material.DIAMOND_SWORD, 1, "&9Ranked " + styleBool(map.isRanked()), Collections.singletonList("&7Ranked Mode toggle. Click to toggle.")));
        inv.setItem(16, Items.createItem(Material.ITEM_FRAME, 1, "&9Border " + styleBool(map.isBorderEnabled()), Collections.singletonList("&7Border toggle. Click to toggle.")));

        if (map.getGameMode() == RSWMap.GameMode.TEAMS) {
            inv.setItem(20, Items.createItem(Material.WHITE_BANNER, 1, "&9Manual Team Selection " + styleBool(map.isManualTeamSelection()),
                    Arrays.asList("&7Players wait in the waiting lobby and pick their own team", "&7instead of being auto assigned. Click to toggle.",
                            "&8Set the lobby up with /rsw createwaitinglobby.")));
        }

        inv.setItem(22, Items.createItem(Material.PISTON, 1, "&9Events", Collections.singletonList("&7Click here to edit this map's events.")));

        inv.setItem(28, Items.createItem(Material.CLOCK, 1, "&9Max Game Time &f" + Format.formatSeconds(map.getMaxGameTime()), Collections.singletonList("&7Click to edit.")));
        inv.setItem(30, Items.createItem(Material.CLOCK, 1, "&9End Game Time &f" + Format.formatSeconds(map.getTimeEndGame()), Collections.singletonList("&7Click to edit.")));
        inv.setItem(32, Items.createItem(Material.CLOCK, 1, "&9Start Game Time &f" + Format.formatSeconds(map.getTimeToStart()), Collections.singletonList("&7Click to edit.")));
        inv.setItem(34, Items.createItem(Material.CLOCK, 1, "&9Invincibility Seconds &f" + Format.formatSeconds(map.getInvincibilitySeconds()), Collections.singletonList("&7Click to edit.")));

        inv.setItem(40, confirm);
    }

    /**
     * The map's four timers as sliders on one dialog. They are applied to the map straight away,
     * like the switches on this screen, and written out with its Save button.
     *
     * @return false if dialogs are not supported, and nothing was shown
     */
    private boolean openTimers(final Player p) {
        final int maxGame = this.map.getMaxGameTime();
        final int endGame = this.map.getTimeEndGame();
        final int toStart = this.map.getTimeToStart();
        final int invincibility = this.map.getInvincibilitySeconds();

        return new DialogForm("&9" + this.map.getName() + " &8| &fTimers", "&7In seconds. Saved with the map's Save button.")
                .slider("max_game", "&9Max game time", 60, 3600, 30, maxGame).sprite(Material.CLOCK)
                .slider("end_game", "&9End game time", 0, 120, 1, endGame).sprite(Material.CLOCK)
                .slider("to_start", "&9Start game time", 5, 300, 5, toStart).sprite(Material.CLOCK)
                .slider("invincibility", "&9Invincibility seconds", 0, 60, 1, invincibility).sprite(Material.CLOCK)
                .buttons("&aApply", "&7Back")
                .open(p, answers -> {
                    //only what was moved, so an untouched timer keeps a value between two steps
                    final Double max = answers.moved("max_game", maxGame, 30);
                    final Double end = answers.moved("end_game", endGame, 1);
                    final Double start = answers.moved("to_start", toStart, 5);
                    final Double invincible = answers.moved("invincibility", invincibility, 1);
                    if (max != null) {
                        this.map.setMaxGameTime((int) Math.round(max));
                    }
                    if (end != null) {
                        this.map.setTimeEndGame((int) Math.round(end));
                    }
                    if (start != null) {
                        this.map.setTimeToStart((int) Math.round(start));
                    }
                    if (invincible != null) {
                        this.map.setInvincibilitySeconds((int) Math.round(invincible));
                    }
                    new MapSettingsGUI(p, this.map).openInventory(p);
                }, () -> new MapSettingsGUI(p, this.map).openInventory(p), () -> new MapSettingsGUI(p, this.map).openInventory(p));
    }

    private String styleBool(boolean b) {
        return b ? "&7[&a&lON&r&7]" : "&7[&c&lOFF&r&7]";
    }

    //used on reload so nobody keeps clicking a GUI built from the old config
    public static void closeAll() {
        for (final MapSettingsGUI current : new ArrayList<>(inventories.values())) {
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
                            MapSettingsGUI current = inventories.get(uuid);
                            if (!current.getInventory().equals(e.getInventory())) {
                                return;
                            }

                            e.setCancelled(true);
                            if (e.getCurrentItem() == null) {
                                return;
                            }

                            //Settings
                            switch (e.getRawSlot()) {
                                case 10:
                                    current.map.setSpectating(!current.map.isSpectatorEnabled());
                                    break;
                                case 12:
                                    current.map.setInstantEnding(!current.map.isInstantEndEnabled());
                                    break;
                                case 14:
                                    current.map.setRanked(!current.map.isRanked());
                                    break;
                                case 16:
                                    current.map.setBorderEnabled(!current.map.isBorderEnabled());
                                    break;
                                case 20:
                                    if (current.map.getGameMode() == RSWMap.GameMode.TEAMS) {
                                        current.map.setManualTeamSelection(!current.map.isManualTeamSelection());
                                    }
                                    break;
                                case 22:
                                    p.closeInventory();

                                    MapEventEditorGUI gui = new MapEventEditorGUI(p, current.map);
                                    gui.openInventory(p);
                                    break;
                                case 28:
                                    //all four timers on one dialog, where the server has them
                                    if (current.openTimers(p)) {
                                        break;
                                    }
                                    p.closeInventory();
                                    new PlayerInput(p, true, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setMaxGameTime(seconds);
                                            MapSettingsGUI gui2 = new MapSettingsGUI(p, current.map);
                                            gui2.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 30:
                                    //all four timers on one dialog, where the server has them
                                    if (current.openTimers(p)) {
                                        break;
                                    }
                                    p.closeInventory();
                                    new PlayerInput(p, true, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setTimeEndGame(seconds);
                                            MapSettingsGUI gui3 = new MapSettingsGUI(p, current.map);
                                            gui3.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 32:
                                    //all four timers on one dialog, where the server has them
                                    if (current.openTimers(p)) {
                                        break;
                                    }
                                    p.closeInventory();
                                    new PlayerInput(p, true, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setTimeToStart(seconds);
                                            MapSettingsGUI gui4 = new MapSettingsGUI(p, current.map);
                                            gui4.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 34:
                                    //all four timers on one dialog, where the server has them
                                    if (current.openTimers(p)) {
                                        break;
                                    }
                                    p.closeInventory();
                                    new PlayerInput(p, true, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setInvincibilitySeconds(seconds);
                                            MapSettingsGUI gui5 = new MapSettingsGUI(p, current.map);
                                            gui5.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 40:
                                    current.map.save(RSWMap.Data.SETTINGS, true);
                                    p.closeInventory();
                                    break;

                            }
                            current.loadInv();
                        }
                    }
                }
            }

            @EventHandler
            public void onDrag(final InventoryDragEvent e) {
                final MapSettingsGUI current = inventories.get(e.getWhoClicked().getUniqueId());
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
                    final MapSettingsGUI current = inventories.get(uuid);
                    if (current != null && e.getInventory().equals(current.getInventory())) {
                        current.unregister();
                    }
                }
            }
        };
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

    public void openInventory(RSWPlayer player) {
        openInventory(player.getPlayer());
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
