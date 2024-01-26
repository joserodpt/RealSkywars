package joserodpt.realskywars.gui;

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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.config.RSWConfig;
import joserodpt.realskywars.gui.guis.AchievementViewer;
import joserodpt.realskywars.gui.guis.GameLogViewer;
import joserodpt.realskywars.player.ProfileContent;
import joserodpt.realskywars.gui.guis.ShopViewer;
import joserodpt.realskywars.kits.Kit;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.shop.ShopManager;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.GUIBuilder;
import joserodpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class GUIManager {

    public static void openShopMenu(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENUS_SHOP_TILE, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopViewer v = new ShopViewer(p, ShopManager.Categories.CAGE_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItem(Material.SPAWNER, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.CAGEBLOCK, false)), 10);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopViewer v = new ShopViewer(p, ShopManager.Categories.KITS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItem(Material.CHEST, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.KITS, false)), 12);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopViewer v = new ShopViewer(p, ShopManager.Categories.WIN_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItem(Material.FIREWORK_ROCKET, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.WINBLOCK, false)), 14);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ShopViewer v = new ShopViewer(p, ShopManager.Categories.BOW_PARTICLES);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItem(Material.BOW, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.BOWPARTICLE, false)), 16);

        inventory.openInventory(p.getPlayer());
    }

    public static void openSpectate(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENUS_SPECTATE_TITLE, false), 54, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (RSWPlayer s : p.getMatch().getPlayers()) {
            if (s.getPlayer() != null) {
                inventory.addItem(e -> {
                    p.teleport(s.getPlayer().getLocation());
                    p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.COMPASS_TELEPORT, true).replace("%name%", s.getDisplayName()));
                }, Itens.addLore(Itens.getHead(s.getPlayer(), 1, "&b" + s.getDisplayName()), Collections.singletonList("&c" + String.format("%.2f", s.getPlayer().getHealth()))), i);
                ++i;
            }
        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openLanguage(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_LANG_TITLE, false), 18, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;

        for (String language : RealSkywars.getPlugin().getLanguageManager().getLanguages()) {
            if (i == 17) {
                break;
            } else {
                inventory.addItem(e -> RealSkywars.getPlugin().getPlayerManager().setLanguage(p, language), Itens.createItemLore(Material.BOOK, 1, "&b" + language, Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_LANG_SELECT, false))), i);
            }
            ++i;
        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openPlayerMenu(RSWPlayer p) {
        int size = 27;
        if (!p.isInMatch()) {
            size = 45;
        }

        GUIBuilder inventory = new GUIBuilder(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_TITLE, false), size, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            new BukkitRunnable() {
                public void run() {
                    ProfileContent v = new ProfileContent(p, ShopManager.Categories.KITS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItemLore(Material.CHEST, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.KITS, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 10);

        inventory.addItem(e -> {
            p.closeInventory();


            new BukkitRunnable() {
                public void run() {
                    ProfileContent v = new ProfileContent(p, ShopManager.Categories.CAGE_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);

        }, Itens.createItemLore(Material.SPAWNER, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.CAGEBLOCK, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 12);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ProfileContent v = new ProfileContent(p, ShopManager.Categories.WIN_BLOCKS);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.WINBLOCK, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 14);

        inventory.addItem(e -> {
            p.closeInventory();

            new BukkitRunnable() {
                public void run() {
                    ProfileContent v = new ProfileContent(p, ShopManager.Categories.BOW_PARTICLES);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywars.getPlugin(), 1);
        }, Itens.createItemLore(Material.BOW, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.BOWPARTICLE, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 13);

        inventory.addItem(e -> {
            p.closeInventory();
            openAchievementGUI(p);
        }, Itens.createItemLore(Material.BOOKSHELF, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.ACHIEVEMENTS, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 16);


        //settings
        if (!p.isInMatch()) {
            if (RSWConfig.file().getBoolean("Config.Disable-Language-Selection")) {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GameLogViewer v = new GameLogViewer(p);
                    v.openInventory(p);
                }, Itens.createItemLore(Material.FILLED_MAP, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_GAME_HISTORY, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 31);
            } else {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GameLogViewer v = new GameLogViewer(p);
                    v.openInventory(p);
                }, Itens.createItemLore(Material.FILLED_MAP, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_GAME_HISTORY, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 32);
                inventory.addItem(e -> {
                    p.closeInventory();
                    GUIManager.openLanguage(p);
                }, Itens.createItemLore(Material.BOOK, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_LANG_TITLE, false), Collections.singletonList("&f> " + p.getLanguage())), 30);
            }

            if (!RSWConfig.file().getBoolean("Config.Disable-Player-Reset")) {
                inventory.addItem(e -> p.resetData(), Itens.createItemLore(Material.BARRIER, 1, RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_RESET_TITLE, false), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_RESET_ALERT, false))), 44);
            }
        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openAchievementGUI(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.ACHIEVEMENTS, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.KILLS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&b&l" + RSWPlayer.PlayerStatistics.KILLS.name(), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.WINS_SOLO);
            v.openInventory(p);
        }, Itens.createItemLore(Material.LEATHER_BOOTS, 1, "&b&l" + RSWPlayer.PlayerStatistics.WINS_SOLO.name().replace("_", " "), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.WINS_TEAMS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.CHAINMAIL_CHESTPLATE, 1, "&b&l" + RSWPlayer.PlayerStatistics.WINS_TEAMS.name().replace("_", " "), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 14);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.GAMES_PLAYED);
            v.openInventory(p);
        }, Itens.createItemLore(Material.FILLED_MAP, 1, "&b&l" + RSWPlayer.PlayerStatistics.GAMES_PLAYED.name().replace("_", " "), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 16);

        inventory.addItem(e -> {
            p.closeInventory();
            GUIManager.openPlayerMenu(p);
        }, Itens.createItemLore(Material.BIRCH_DOOR, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))), 26);

        inventory.openInventory(p.getPlayer());
    }

    public static void openKitPreview(RSWPlayer p, Kit kit, int id) {
        if (!kit.hasItems()) {
            return;
        }

        p.closeInventory();

        GUIBuilder inventory = new GUIBuilder(kit.getDisplayName(), 54, p.getUUID());

        int i = 0;
        for (ItemStack content : kit.getKitInventory().getInventory()) {
            if (content != null) {
                inventory.addItem(event -> {
                }, content, i);
            }
            ++i;
        }

        inventory.setItem(event -> {
            if (id == 0) {
                p.closeInventory();
                ProfileContent pc = new ProfileContent(p, ShopManager.Categories.KITS);
                pc.openInventory(p);
            } else {
                p.closeInventory();
                ShopViewer s = new ShopViewer(p, ShopManager.Categories.KITS);
                s.openInventory(p);
            }
        }, Itens.createItem(Material.BIRCH_DOOR, 1, ""), 53);

        inventory.openInventory(p.getPlayer());
    }
}
