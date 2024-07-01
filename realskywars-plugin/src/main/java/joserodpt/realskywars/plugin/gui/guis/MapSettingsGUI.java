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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.PlayerInput;
import joserodpt.realskywars.api.utils.Text;
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

public class MapSettingsGUI {

    private static final Map<UUID, MapSettingsGUI> inventories = new HashMap<>();
    private Inventory inv;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack confirm = Itens.createItem(Material.CHEST, 1, "&9Save Settings", Collections.singletonList("&7Click here to confirm your settings."));

    private final UUID uuid;
    private final RSWMap map;

    public MapSettingsGUI(Player p, RSWMap map) {
        this.uuid = p.getUniqueId();
        this.map = map;

        inv = Bukkit.getServer().createInventory(null, 45, Text.color(map.getMapName() + " settings"));

        loadInv();
    }

    public MapSettingsGUI(RSWPlayer p, RSWMap map) {
        this.uuid = p.getUUID();
        this.map = map;

        inv = Bukkit.getServer().createInventory(null, 45, Text.color(map.getMapName() + " settings"));

        loadInv();
    }

    private void loadInv() {
        inv.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 17, 26, 35, 44, 37, 38, 39, 41, 42, 43}) {
            inv.setItem(slot, placeholder);
        }

        inv.setItem(10, Itens.createItem(Material.ENDER_EYE, 1, "&9Spectator " + styleBool(map.isSpectatorEnabled()), Collections.singletonList("&7Spectate when a player dies. Click to toggle.")));
        inv.setItem(12, Itens.createItem(Material.FEATHER, 1, "&9Instant Ending " + styleBool(map.isInstantEndEnabled()), Arrays.asList("&7When a player wins, the game is instantly resetted", "&7and all players are teleported to the lobby. Click to toggle.")));
        inv.setItem(14, Itens.createItem(Material.DIAMOND_SWORD, 1, "&9Ranked " + styleBool(map.isRanked()), Collections.singletonList("&7Ranked Mode toggle. Click to toggle.")));
        inv.setItem(16, Itens.createItem(Material.ITEM_FRAME, 1, "&9Border " + styleBool(map.isBorderEnabled()), Collections.singletonList("&7Border toggle. Click to toggle.")));

        inv.setItem(22, Itens.createItem(Material.PISTON, 1, "&9Events", Collections.singletonList("&7Click here to edit this map's events.")));

        inv.setItem(28, Itens.createItem(Material.CLOCK, 1, "&9Max Game Time &f" + Text.formatSeconds(map.getMaxGameTime()), Collections.singletonList("&7Click to edit.")));
        inv.setItem(30, Itens.createItem(Material.CLOCK, 1, "&9End Game Time &f" + Text.formatSeconds(map.getTimeEndGame()), Collections.singletonList("&7Click to edit.")));
        inv.setItem(32, Itens.createItem(Material.CLOCK, 1, "&9Start Game Time &f" + Text.formatSeconds(map.getTimeToStart()), Collections.singletonList("&7Click to edit.")));
        inv.setItem(34, Itens.createItem(Material.CLOCK, 1, "&9Invincibility Seconds &f" + Text.formatSeconds(map.getInvincibilitySeconds()), Collections.singletonList("&7Click to edit.")));

        inv.setItem(40, confirm);
    }

    private String styleBool(boolean b) {
        return b ? "&7[&a&lON&r&7]" : "&7[&c&lOFF&r&7]";
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
                            MapSettingsGUI current = inventories.get(uuid);
                            if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                                return;
                            }

                            e.setCancelled(true);

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
                                case 22:
                                    p.closeInventory();
                                    //TODO
                                    break;
                                case 28:
                                    p.closeInventory();
                                    new PlayerInput(p, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setMaxGameTime(seconds);
                                            MapSettingsGUI gui = new MapSettingsGUI(p, current.map);
                                            gui.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 30:
                                    p.closeInventory();
                                    new PlayerInput(p, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setTimeEndGame(seconds);
                                            MapSettingsGUI gui = new MapSettingsGUI(p, current.map);
                                            gui.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 32:
                                    p.closeInventory();
                                    new PlayerInput(p, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setTimeToStart(seconds);
                                            MapSettingsGUI gui = new MapSettingsGUI(p, current.map);
                                            gui.openInventory(p);
                                        } catch (NumberFormatException e1) {
                                            p.sendMessage(Text.color("&cInvalid seconds."));
                                        }

                                    }, input -> {
                                    });
                                    break;
                                case 34:
                                    p.closeInventory();
                                    new PlayerInput(p, input -> {
                                        try {
                                            int seconds = Integer.parseInt(input);
                                            current.map.setInvincibilitySeconds(seconds);
                                            MapSettingsGUI gui = new MapSettingsGUI(p, current.map);
                                            gui.openInventory(p);
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
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unregister();
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
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
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
