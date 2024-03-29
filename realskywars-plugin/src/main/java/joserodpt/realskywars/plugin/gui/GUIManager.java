package joserodpt.realskywars.plugin.gui;

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

import joserodpt.realpermissions.api.utils.Items;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.GUIBuilder;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.plugin.gui.guis.AchievementViewerGUI;
import joserodpt.realskywars.plugin.gui.guis.GameHistoryGUI;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerProfileContentsGUI;
import joserodpt.realskywars.plugin.gui.guis.SettingsGUI;
import joserodpt.realskywars.plugin.gui.guis.ShopGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class GUIManager {

    public static void openShopMenu(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENUS_SHOP_TILE, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopGUI v = new ShopGUI(p, ShopManagerAPI.Categories.CAGE_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.SPAWNER, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CAGEBLOCK, false)), 10);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopGUI v = new ShopGUI(p, ShopManagerAPI.Categories.KITS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.CHEST, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.KITS, false)), 12);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopGUI v = new ShopGUI(p, ShopManagerAPI.Categories.WIN_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.FIREWORK_ROCKET, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.WINBLOCK, false)), 14);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopGUI v = new ShopGUI(p, ShopManagerAPI.Categories.BOW_PARTICLES);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.BOW, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.BOWPARTICLE, false)), 16);

        inventory.openInventory(p.getPlayer());
    }

    public static void openSpectate(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENUS_SPECTATE_TITLE, false), 54, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (RSWPlayer s : p.getMatch().getPlayers()) {
            if (s.getPlayer() != null) {
                inventory.addItem(e -> {
                    p.teleport(s.getPlayer().getLocation());
                    p.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.COMPASS_TELEPORT, true).replace("%name%", s.getDisplayName()));
                }, Itens.addLore(Itens.getHead(s.getPlayer(), 1, "&b" + s.getDisplayName()), Collections.singletonList("&c" + String.format("%.2f", s.getPlayer().getHealth()))), i);
                ++i;
            }
        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openLanguage(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_LANG_TITLE, false), 18, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;

        for (String language : RealSkywarsAPI.getInstance().getLanguageManagerAPI().getLanguages()) {
            if (i == 17) {
                break;
            } else {
                inventory.addItem(e -> RealSkywarsAPI.getInstance().getPlayerManagerAPI().setLanguage(p, language), Itens.createItemLore(Material.BOOK, 1, "&b" + language, Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_LANG_SELECT, false))), i);
            }
            ++i;
        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openPluginMenu(RSWPlayer p, RealSkywarsAPI rsa) {
        GUIBuilder inventory = new GUIBuilder("&f&lReal&b&lSkywars &r&8v" + rsa.getVersion(), 27, p.getUUID());

        inventory.addItem(e -> {
            p.closeInventory();
            new BukkitRunnable() {
                public void run() {
                    MapsListGUI v = new MapsListGUI(p, p.getMapViewerPref(), rsa.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MAPS_NAME, false));
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.NETHER_STAR, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_MAPS_NAME, false)), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            new BukkitRunnable() {
                public void run() {
                    SettingsGUI v = new SettingsGUI(p, rsa);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.COMPARATOR, 1, "&e&lSettings"), 14);

        inventory.addItem(e -> p.closeInventory(), Items.createItem(Material.OAK_DOOR, 1, "&cClose",
                Collections.singletonList("&fClick here to close this menu.")), 26);


        inventory.openInventory(p.getPlayer());
    }

    public static void openPlayerMenu(RSWPlayer p) {
        int size = 27;
        if (!p.isInMatch()) {
            size = 45;
        }

        GUIBuilder inventory = new GUIBuilder(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_TITLE, false), size, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            new BukkitRunnable() {
                public void run() {
                    PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.Categories.KITS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItemLore(Material.CHEST, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.KITS, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 10);

        inventory.addItem(e -> {
            p.closeInventory();


            new BukkitRunnable() {
                public void run() {
                    PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.Categories.CAGE_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);

        }, Itens.createItemLore(Material.SPAWNER, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.CAGEBLOCK, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 12);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.Categories.WIN_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.WINBLOCK, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 14);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.Categories.BOW_PARTICLES);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItemLore(Material.BOW, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.BOWPARTICLE, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 13);

        inventory.addItem(e -> {
            p.closeInventory();
            openAchievementGUI(p);
        }, Itens.createItemLore(Material.BOOKSHELF, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ACHIEVEMENTS, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 16);


        //settings
        if (!p.isInMatch()) {
            if (RSWConfig.file().getBoolean("Config.Disable-Language-Selection")) {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GameHistoryGUI v = new GameHistoryGUI(p);
                    v.openInventory(p);
                }, Itens.createItemLore(Material.FILLED_MAP, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_GAME_HISTORY, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 31);
            } else {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GameHistoryGUI v = new GameHistoryGUI(p);
                    v.openInventory(p);
                }, Itens.createItemLore(Material.FILLED_MAP, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_GAME_HISTORY, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 32);
                inventory.addItem(e -> {
                    p.closeInventory();
                    GUIManager.openLanguage(p);
                }, Itens.createItemLore(Material.BOOK, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_LANG_TITLE, false), Collections.singletonList("&f> " + p.getLanguage())), 30);
            }

            if (!RSWConfig.file().getBoolean("Config.Disable-Player-Reset")) {
                inventory.addItem(e -> p.resetData(), Itens.createItemLore(Material.BARRIER, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_RESET_TITLE, false), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_RESET_ALERT, false))), 44);
            }
        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openAchievementGUI(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ACHIEVEMENTS, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.KILLS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&b&l" + RSWPlayer.PlayerStatistics.KILLS.name(), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.WINS_SOLO);
            v.openInventory(p);
        }, Itens.createItemLore(Material.LEATHER_BOOTS, 1, "&b&l" + RSWPlayer.PlayerStatistics.WINS_SOLO.name().replace("_", " "), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.WINS_TEAMS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.CHAINMAIL_CHESTPLATE, 1, "&b&l" + RSWPlayer.PlayerStatistics.WINS_TEAMS.name().replace("_", " "), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 14);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.GAMES_PLAYED);
            v.openInventory(p);
        }, Itens.createItemLore(Material.FILLED_MAP, 1, "&b&l" + RSWPlayer.PlayerStatistics.GAMES_PLAYED.name().replace("_", " "), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.MENU_PLAYERP_VIEWITEM, false))), 16);

        inventory.addItem(e -> {
            p.closeInventory();
            GUIManager.openPlayerMenu(p);
        }, Itens.createItemLore(Material.BIRCH_DOOR, 1, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(LanguageManagerAPI.TSsingle.BUTTONS_MENU_DESC))), 26);

        inventory.openInventory(p.getPlayer());
    }

    public static void openKitPreview(RSWPlayer p, RSWKit SWKit, int id) {
        if (!SWKit.hasItems()) {
            return;
        }

        p.closeInventory();

        GUIBuilder inventory = new GUIBuilder(SWKit.getDisplayName(), 54, p.getUUID());

        int i = 0;
        for (ItemStack content : SWKit.getKitInventory().getInventory()) {
            if (content != null) {
                inventory.addItem(event -> {
                }, content, i);
            }
            ++i;
        }

        inventory.setItem(event -> {
            if (id == 0) {
                p.closeInventory();
                PlayerProfileContentsGUI pc = new PlayerProfileContentsGUI(p, ShopManagerAPI.Categories.KITS);
                pc.openInventory(p);
            } else {
                p.closeInventory();
                ShopGUI s = new ShopGUI(p, ShopManagerAPI.Categories.KITS);
                s.openInventory(p);
            }
        }, Itens.createItem(Material.BIRCH_DOOR, 1, ""), 53);

        inventory.openInventory(p.getPlayer());
    }
}
