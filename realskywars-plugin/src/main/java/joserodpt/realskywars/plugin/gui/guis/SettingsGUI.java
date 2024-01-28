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

import com.google.common.collect.ImmutableList;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pagination;
import joserodpt.realskywars.api.utils.PlayerInput;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.plugin.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettingsGUI {

    public class SettingEntry {

        //0 - bool, 1 - int
        public int entryType;

        private String configPath, name;
        public SettingEntry(final String name, final String configPath, final int entryType) {
            this.name = "&f"+name;
            this.configPath = configPath;
            this.entryType = entryType;
        }

        public String getName() {
            return Text.color(name);
        }

        public String getConfigPath() {
            return configPath;
        }

        public int getEntryType() {
            return entryType;
        }

        public ItemStack getItem() {
            if (entryType == 0) {
                boolean val = RSWConfig.file().getBoolean(configPath);
                return Itens.createItemLore(val ? Material.REDSTONE_TORCH : Material.LEVER, 1, this.getName() + " &f- " + (val ? "&a&lON" : "&c&lOFF"), Collections.singletonList("&7Click here to toggle this setting."));
            } else {
                return Itens.createItemLore(Material.OAK_BUTTON, Math.min(64, Math.max(1, RSWConfig.file().getInt(configPath))), this.getName() + ": " + RSWConfig.file().getInt(configPath), Collections.singletonList("&7Click here to change this value."));
            }
        }
    }

    private static final Map<UUID, SettingsGUI> inventories = new HashMap<>();
    int pageNumber = 0;
    private Pagination<SettingEntry> p;
    private final ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private ItemStack close = Itens.createItemLore(Material.OAK_DOOR, 1, "&cClose", Collections.singletonList("&fClick here to close this menu."));
    private final Inventory inv;
    private final UUID uuid;
    private final RealSkywarsAPI rsa;
    private final Map<Integer, SettingEntry> display = new HashMap<>();

    private final ImmutableList<SettingEntry> list = ImmutableList.of(
            new SettingEntry("Debug Mode", "Debug-Mode", 0),
            new SettingEntry("Use Vault As Currency Mode", "Config.Use-Vault-As-Currency", 0),
            new SettingEntry("Auto Teleport To Lobby", "Config.Auto-Teleport-To-Lobby", 0),
            new SettingEntry("Scoreboard In Lobby", "Config.Scoreboard-In-Lobby", 0),
            new SettingEntry("Spectator Shop", "Config.Spectator-Shop", 0),
            new SettingEntry("Right Click Player Info", "Config.Right-Click-Player-Info", 0),
            new SettingEntry("PlaceholderAPI In Scoreboard", "Config.PlaceholderAPI-In-Scoreboard", 0),
            new SettingEntry("Disable Player Reset", "Config.Disable-Player-Reset", 0),
            new SettingEntry("Disable Language Selection", "Config.Disable-Language-Selection", 0),
            new SettingEntry("Shuffle Items In Chests", "Config.Shuffle-Items-In-Chest", 0),

            new SettingEntry("Profile Item Slot in the Lobby", "Config.Item-Slots.Lobby.Profile", 1),
            new SettingEntry("Maps Item Slot in the Lobby", "Config.Item-Slots.Lobby.Maps", 1),
            new SettingEntry("Shop Item Slot in the Lobby", "Config.Item-Slots.Lobby.Shop", 1),
            new SettingEntry("Kit Item Slot in the Cage", "Config.Item-Slots.Cage.Kit", 1),
            new SettingEntry("Vote Item Slot in the Cage", "Config.Item-Slots.Cage.Vote", 1),
            new SettingEntry("Leave Item Slot in the Cage", "Config.Item-Slots.Cage.Leave", 1),
            new SettingEntry("Spectate Item Slot in Spectator", "Config.Item-Slots.Spectator.Spectate", 1),
            new SettingEntry("Play Again Item Slot in Spectator", "Config.Item-Slots.Spectator.Play-Again", 1),
            new SettingEntry("Shop Item Slot in Spectator", "Config.Item-Slots.Spectator.Shop", 1),
            new SettingEntry("Leave Item Slot in Spectator", "Config.Item-Slots.Spectator.Leave", 1),
            new SettingEntry("Chest1 Item Slot in Setup", "Config.Item-Slots.Setup.Chest1", 1),
            new SettingEntry("Cage Item Slot in Setup", "Config.Item-Slots.Setup.Cage", 1),
            new SettingEntry("Chest2 Item Slot in Setup", "Config.Item-Slots.Setup.Chest2", 1),
            new SettingEntry("Time Offset", "Config.Time.Offset", 1),
            new SettingEntry("Time To Start", "Config.Time-To-Start", 1),
            new SettingEntry("Min Players To Start", "Config.Min-Players-ToStart", 1),
            new SettingEntry("Time End Game", "Config.Time-EndGame", 1),
            new SettingEntry("Refresh Leaderboards", "Config.Refresh-Leaderboards", 1),
            new SettingEntry("Vote Before Seconds", "Config.Vote-Before-Seconds", 1),
            new SettingEntry("Maximum Game Time - Solo", "Config.Maximum-Game-Time.Solo", 1),
            new SettingEntry("Maximum Game Time - Teams", "Config.Maximum-Game-Time.Teams", 1),
            new SettingEntry("Kits Ender Pearl Perk Give Interval", "Config.Kits.Ender-Pearl-Perk-Give-Interval", 1),
            new SettingEntry("Default Refill Time", "Config.Default-Refill-Time", 1),
            new SettingEntry("Coins Per Win", "Config.Coins.Per-Win", 1),
            new SettingEntry("Coins Per Kill", "Config.Coins.Per-Kill", 1),
            new SettingEntry("Coins Per Death", "Config.Coins.Per-Death", 1)
    );

    public SettingsGUI(RSWPlayer as, RealSkywarsAPI rsa) {
        this.rsa = rsa;
        this.uuid = as.getUUID();
        this.inv = Bukkit.getServer().createInventory(null, 54, Text.color("&f&lReal&c&lSkywars &8| Settings (" + list.size() + ")"));

        this.load();

        this.register();
    }

    public void load() {
        this.p = new Pagination<>(28, list);
        fillChest(p.getPage(pageNumber));
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
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        SettingsGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        RSWPlayer p = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer((Player) clicker);

                        switch (e.getRawSlot()) {
                            case 49:
                                p.closeInventory();
                                GUIManager.openPluginMenu(p, current.rsa);
                                break;
                            case 26:
                            case 35:
                                if (!current.lastPage()) {
                                    nextPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                            case 18:
                            case 27:
                                if (!current.firstPage()) {
                                    backPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            SettingEntry a = current.display.get(e.getRawSlot());

                            if (a.getEntryType() == 0) {
                                RSWConfig.file().set(a.getConfigPath(), !RSWConfig.file().getBoolean(a.getConfigPath()));
                                RSWConfig.save();
                                current.load();
                            } else {
                                p.closeInventory();
                                new PlayerInput(p.getPlayer(), input -> {
                                    int val;
                                    try {
                                        val = Integer.parseInt(input);
                                    } catch (Exception ignored) {
                                        Text.send(p.getPlayer(), RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "&cNot a valid number without decimal points.");
                                        return;
                                    }

                                    RSWConfig.file().set(a.getConfigPath(), val);
                                    RSWConfig.save();
                                    Text.send(p.getPlayer(), RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "&fSetting &b" + ChatColor.stripColor(a.getName()) + "&f value has been set to &a" + val);

                                    SettingsGUI v = new SettingsGUI(p, current.rsa);
                                    v.openInventory(p);
                                }, input -> {
                                    SettingsGUI v = new SettingsGUI(p, current.rsa);
                                    v.openInventory(p);
                                });
                            }
                        }
                    }
                }
            }

            private void backPage(SettingsGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(SettingsGUI asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
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

    private boolean lastPage() {
        return pageNumber == (p.totalPages() - 1);
    }

    private boolean firstPage() {
        return pageNumber == 0;
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

    public void fillChest(List<SettingEntry> items) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_DESC))));
            inv.setItem(27, Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_BACK_DESC))));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_DESC))));
            inv.setItem(35, Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_NEXT_DESC))));
        }

        this.inv.setItem(49, close);

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    SettingEntry s = items.get(0);
                    inv.setItem(slot, s.getItem());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
