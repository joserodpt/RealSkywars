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

import com.google.common.collect.ImmutableList;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.plugin.gui.GUIManager;
import joserodpt.realutils.dialog.SettingsDialog;
import joserodpt.realutils.dialog.SettingsStore;
import joserodpt.realutils.gui.Pagination;
import joserodpt.realutils.input.PlayerInput;
import joserodpt.realutils.item.Items;
import joserodpt.realutils.text.Text;
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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettingsGUI {

    public class SettingEntry {

        //0 - bool, 1 - int
        public int entryType;

        private final String configPath;
        private final String name;

        public SettingEntry(final String name, final String configPath, final int entryType) {
            this.name = "&f" + name;
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
                return Items.createItem(val ? Material.REDSTONE_TORCH : Material.LEVER, 1, this.getName() + " &f- " + (val ? "&a&lON" : "&c&lOFF"), Collections.singletonList("&7Click here to toggle this setting."));
            } else {
                return Items.createItem(Material.OAK_BUTTON, Math.min(64, Math.max(1, RSWConfig.file().getInt(configPath))), this.getName() + ": " + RSWConfig.file().getInt(configPath), Collections.singletonList("&7Click here to change this value."));
            }
        }
    }

    private static final Map<UUID, SettingsGUI> inventories = new HashMap<>();
    int pageNumber = 0;
    private Pagination<SettingEntry> p;
    private final ItemStack placeholder = Items.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    private final ItemStack close = Items.createItem(Material.OAK_DOOR, 1, "&cClose", Collections.singletonList("&fClick here to close this menu."));
    private final Inventory inv;
    private final UUID uuid;
    private final RealSkywarsAPI rsa;
    private final Map<Integer, SettingEntry> display = new HashMap<>();

    private final ImmutableList<SettingEntry> list = ImmutableList.of(
            new SettingEntry("Debug Mode", "Debug-Mode", 0),
            new SettingEntry("Use Vault As Currency Mode", "Config.Use-Vault-As-Currency", 0),
            new SettingEntry("Auto Teleport To Lobby", "Config.Auto-Teleport-To-Lobby", 0),
            new SettingEntry("Scoreboard In Lobby", "Config.Scoreboard-In-Lobby", 0),

            new SettingEntry("Enable Shop", "Config.Shops.Enable-Shop", 0),
            new SettingEntry("Enable Spectator Shop", "Config.Shops.Enable-Spectator-Shop", 0),
            new SettingEntry("Enable Kit Shop", "Config.Shops.Enable-Kit-Shop", 0),
            new SettingEntry("Only Buy Kits per Match", "Config.Shops.Only-Buy-Kits-Per-Match", 0),
            new SettingEntry("Enable Cage Shop", "Config.Shops.Enable-Cage-Block-Shop", 0),
            new SettingEntry("Enable Win Block Shop", "Config.Shops.Enable-Win-Block-Shop", 0),
            new SettingEntry("Enable Bow Particles Shop", "Config.Shops.Enable-Bow-Particles-Shop", 0),

            new SettingEntry("Right Click Player Info", "Config.Right-Click-Player-Info", 0),
            new SettingEntry("PlaceholderAPI In Scoreboard", "Config.PlaceholderAPI-In-Scoreboard", 0),
            new SettingEntry("Disable Player Reset", "Config.Disable-Player-Reset", 0),
            new SettingEntry("Disable Language Selection", "Config.Disable-Language-Selection", 0),
            new SettingEntry("Disable Chest Animation", "Config.Disable-Chest-Animation", 0),
            new SettingEntry("Shuffle Items In Chests", "Config.Shuffle-Items-In-Chest", 0),
            new SettingEntry("Enable Server as Bungeecord", "Config.Bungeecord.Enabled", 0),
            new SettingEntry("Bungeecord: Kick Player instead of Moving Player to Lobby", "Config.Bungeecord.Kick-Player", 0),

            new SettingEntry("Toggle Tab Formatting", "Config.Enable-Tab-Formatting", 0),
            new SettingEntry("Disable Map Starting Messages", "Config.Disable-Map-Starting-Countdown.Message", 0),
            new SettingEntry("Disable Map Starting Actionbar", "Config.Disable-Map-Starting-Countdown.Actionbar", 0),
            new SettingEntry("Disable Lobby Items", "Config.Disable-Lobby-Items", 0),

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
            new SettingEntry("Invincibility Seconds", "Config.Invincibility-Seconds", 1),

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

    /**
     * Opens the settings: on servers that have dialogs, a menu of categories with every setting in
     * config.yml; the inventory editor everywhere else.
     */
    public static void open(final RSWPlayer p, final RealSkywarsAPI rsa) {
        final String forMaps = "for maps without their own";
        final SettingsDialog settings = new SettingsDialog("&f&lReal&c&lSkywars &8| &fSettings")
                .icon(Material.BOW)
                .onSave((player, category) -> player.sendMessage(Text.color("&fSettings saved.")));
        settings.category("&eGeneral", "&7Prefix, language, currency and the lobby")
                .text("Config.Prefix", "Plugin prefix", 64)
                .text("Config.Languages.Default-Language", "Default language", 16).note("such as en_us")
                .toggle("Debug-Mode", "Debug messages").note("after /rsw reload")
                .toggle("Config.Use-Vault-As-Currency", "Use Vault for coins").note("after a restart")
                .toggle("Config.Auto-Teleport-To-Lobby", "Send players to the lobby on join").note("after a restart")
                .toggle("Config.Scoreboard-In-Lobby", "Scoreboard in the lobby")
                .toggle("Config.Right-Click-Player-Info", "Right-click a player to see their info")
                .toggle("Config.Enable-Chat-Per-Map", "Chat only reaches players in the same map")
                .toggle("Config.Pressure-Plate-Join-Game", "Pressure plates join a game")
                .toggle("Config.Disable-Player-Reset", "Don't reset players when they join a map")
                .toggle("Config.Disable-Language-Selection", "Don't let players pick their language")
                .slider("Config.Refresh-Leaderboards", "Seconds between leaderboard refreshes", 30, 3600, 30).note("after a restart")
                .text("Config.Time.Formatting", "Date and time format", 64)
                .slider("Config.Time.Offset", "Time offset", -24, 24, 1).note("hours")
                .toggle("Config.Use-Dialogs", "Use dialogs").note("off: chat and inventory menus");
        settings.category("&dDisplay", "&7Tab list, scoreboards and countdowns")
                .toggle("Config.Enable-Tab-Formatting", "Format the tab list").note("for players who join after")
                .toggle("Config.PlaceholderAPI-In-Scoreboard", "PlaceholderAPI in the scoreboard")
                .toggle("Config.PlaceholderAPI-In-Tab", "PlaceholderAPI in the tab list")
                .toggle("Config.Disable-Chest-Animation", "Don't animate chests opening")
                .toggle("Config.Disable-Map-Starting-Countdown.Message", "No chat countdown before a game")
                .toggle("Config.Disable-Map-Starting-Countdown.Actionbar", "No action bar countdown before a game");
        settings.category("&aLobby & Items", "&7Lobby items and where each hotbar item goes")
                .toggle("Config.Disable-Lobby-Items", "No items in the lobby")
                .toggle("Config.Disable-Lobby-Void-Teleport", "Don't teleport players who fall in the lobby")
                .slider("Config.Item-Slots.Lobby.Profile", "Lobby: profile slot", 0, 8, 1)
                .slider("Config.Item-Slots.Lobby.Maps", "Lobby: maps slot", 0, 8, 1)
                .slider("Config.Item-Slots.Lobby.Shop", "Lobby: shop slot", 0, 8, 1)
                .slider("Config.Item-Slots.Cage.Kit", "Cage: kit slot", 0, 8, 1)
                .slider("Config.Item-Slots.Cage.Team-Select", "Cage: team slot", 0, 8, 1)
                .slider("Config.Item-Slots.Cage.Vote", "Cage: vote slot", 0, 8, 1)
                .slider("Config.Item-Slots.Cage.Leave", "Cage: leave slot", 0, 8, 1)
                .slider("Config.Item-Slots.Spectator.Spectate", "Spectator: spectate slot", 0, 8, 1)
                .slider("Config.Item-Slots.Spectator.Play-Again", "Spectator: play again slot", 0, 8, 1)
                .slider("Config.Item-Slots.Spectator.Shop", "Spectator: shop slot", 0, 8, 1)
                .slider("Config.Item-Slots.Spectator.Leave", "Spectator: leave slot", 0, 8, 1)
                .slider("Config.Item-Slots.Setup.Cage", "Setup: cage slot", 0, 8, 1)
                .slider("Config.Item-Slots.Setup.Chest1", "Setup: first chest slot", 0, 8, 1)
                .slider("Config.Item-Slots.Setup.Chest2", "Setup: second chest slot", 0, 8, 1)
                .slider("Config.Item-Slots.Setup.Settings", "Setup: settings slot", 0, 8, 1)
                .slider("Config.Item-Slots.Setup.Save", "Setup: save slot", 0, 8, 1);
        settings.category("&6Shops", "&7Which shops are open")
                .toggle("Config.Shops.Enable-Shop", "Shop")
                .toggle("Config.Shops.Enable-Spectator-Shop", "Spectator shop")
                .toggle("Config.Shops.Enable-Kit-Shop", "Kit shop")
                .toggle("Config.Shops.Only-Buy-Kits-Per-Match", "Kits are bought for one match only")
                .toggle("Config.Shops.Enable-Cage-Block-Shop", "Cage block shop")
                .toggle("Config.Shops.Enable-Win-Block-Shop", "Win block shop")
                .toggle("Config.Shops.Enable-Bow-Particles-Shop", "Bow particles shop");
        settings.category("&cGames", "&7Countdowns, game length, chests and the deathmatch")
                .slider("Config.Min-Players-ToStart", "Players needed to start", 1, 32, 1)
                .slider("Config.Time-To-Start", "Countdown before a game", 5, 300, 5).note(forMaps)
                .slider("Config.Invincibility-Seconds", "Seconds of invincibility at the start", 0, 60, 1).note(forMaps)
                .slider("Config.Vote-Before-Seconds", "Seconds before the start votes close", 0, 60, 1)
                .slider("Config.Maximum-Game-Time.Solo", "Longest a solo game lasts", 60, 3600, 30).note(forMaps)
                .slider("Config.Maximum-Game-Time.Teams", "Longest a teams game lasts", 60, 3600, 30).note(forMaps)
                .slider("Config.Time-EndGame", "Seconds after a game ends", 0, 120, 1).note(forMaps)
                .slider("Config.Default-Refill-Time", "Seconds between chest refills", 30, 900, 15)
                .toggle("Config.Shuffle-Items-In-Chest", "Shuffle the items in chests")
                .slider("Config.Death-Match-Shrink-Factor", "How fast the deathmatch border shrinks", 1, 10, 1)
                .slider("Config.Teams.Cage-Transfer-Seconds", "Seconds before teammates share a cage", 0, 30, 1)
                .slider("Config.Kits.Ender-Pearl-Perk-Give-Interval", "Seconds between ender pearl perk pearls", 5, 600, 5);
        settings.category("&eCoins", "&7What players earn and lose")
                .decimal("Config.Coins.Per-Win", "Coins for a win", 0, 1000, 1)
                .decimal("Config.Coins.Per-Kill", "Coins for a kill", 0, 1000, 1)
                .decimal("Config.Coins.Per-Death", "Coins for a death", -1000, 1000, 1);
        settings.category("&9Bungeecord", "&7Running as a Bungeecord game server")
                .toggle("Config.Bungeecord.Enabled", "Run as a Bungeecord server").note("after a restart")
                .toggle("Config.Bungeecord.Kick-Player", "Kick players instead of moving them to the lobby")
                .text("Config.Bungeecord.Lobby-Server", "Lobby server", 64)
                .toggle("Config.Bungeecord.Map-State-As-Motd", "Show the map's state as the MOTD");

        settings.open(p.getPlayer(), SettingsStore.of(RSWConfig.file()::get, RSWConfig.file()::set, RSWConfig::save),
                () -> new SettingsGUI(p, rsa).openInventory(p));
    }

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

    //used on reload so nobody keeps clicking a GUI built from the old config
    public static void closeAll() {
        for (final SettingsGUI current : new ArrayList<>(inventories.values())) {
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
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        SettingsGUI current = inventories.get(uuid);
                        if (!current.getInventory().equals(e.getInventory())) {
                            return;
                        }

                        e.setCancelled(true);
                        if (e.getCurrentItem() == null) {
                            return;
                        }
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
                                new PlayerInput(p.getPlayer(), true, input -> {
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
            public void onDrag(final InventoryDragEvent e) {
                final SettingsGUI current = inventories.get(e.getWhoClicked().getUniqueId());
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
                    final SettingsGUI current = inventories.get(uuid);
                    if (current != null && e.getInventory().equals(current.getInventory())) {
                        current.unregister();
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
            if (!inv.equals(openTop)) {
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
            inv.setItem(18, Items.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
            inv.setItem(27, Items.createItem(Material.YELLOW_STAINED_GLASS, 1, TranslatableLine.BUTTONS_BACK_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_BACK_DESC.getSingle())));
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, Items.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
            inv.setItem(35, Items.createItem(Material.GREEN_STAINED_GLASS, 1, TranslatableLine.BUTTONS_NEXT_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_NEXT_DESC.getSingle())));
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
