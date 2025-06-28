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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWLanguage;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.GUIBuilder;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.plugin.gui.guis.AchievementViewerGUI;
import joserodpt.realskywars.plugin.gui.guis.GameHistoryGUI;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.PlayerItemsGUI;
import joserodpt.realskywars.plugin.gui.guis.SettingsGUI;
import joserodpt.realskywars.plugin.gui.guis.ShopGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class GUIManager {

    public static void openSpectate(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_SPECTATE_TITLE.get(p), 54, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (RSWPlayer s : p.getMatch().getPlayers()) {
            if (s.getPlayer() != null) {
                inventory.addItem(e -> {
                    p.teleport(s.getPlayer().getLocation());
                    p.sendMessage(TranslatableLine.COMPASS_TELEPORT.get(p, true).replace("%name%", s.getDisplayName()));
                }, Itens.createHead(s.getPlayer(), 1, "&b" + s.getDisplayName(), Collections.singletonList("&c" + String.format("%.2f", s.getPlayer().getHealth()))), i);
                ++i;
            }
        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openLanguage(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_LANG_TITLE.get(p, true), 18, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;

        for (RSWLanguage language : RealSkywarsAPI.getInstance().getLanguageManagerAPI().getLanguages()) {
            if (i == 17) {
                break;
            } else {
                inventory.addItem(e -> RealSkywarsAPI.getInstance().getPlayerManagerAPI().setLanguage(p, language), language.getIcon(), i);
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
                    MapsListGUI v = new MapsListGUI(p);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.NETHER_STAR, 1, TranslatableLine.ITEM_MAPS_NAME.get(p)), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            new BukkitRunnable() {
                public void run() {
                    SettingsGUI v = new SettingsGUI(p, rsa);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.COMPARATOR, 1, "&e&lSettings"), 14);

        inventory.addItem(e -> p.closeInventory(), Itens.createItem(Material.OAK_DOOR, 1, "&cClose",
                Collections.singletonList("&fClick here to close this menu.")), 26);


        inventory.openInventory(p.getPlayer());
    }

    public static void openPlayerProfile(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_PLAYERP_TITLE.get(p), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            new BukkitRunnable() {
                public void run() {
                    PlayerItemsGUI v = new PlayerItemsGUI(p, RSWBuyableItem.ItemCategory.KIT);
                    v.openInventory(p);
                }
            }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
        }, Itens.createItem(Material.CHEST, 1, "&6Items", Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            openAchievementGUI(p);
        }, Itens.createItem(Material.BOOKSHELF, 1, TranslatableLine.ACHIEVEMENTS.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 12);

        inventory.addItem(e -> {
        }, Itens.createHead(p.getPlayer(), 1, "&9" + p.getName(), p.getStats()), 14);

        //settings
        if (!p.isInMatch()) {
            inventory.addItem(e -> {
                p.closeInventory();
                GameHistoryGUI v = new GameHistoryGUI(p);
                v.openInventory(p);
            }, Itens.createItem(Material.FILLED_MAP, 1, TranslatableLine.MENU_PLAYER_GAME_HISTORY.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 16);

            if (!RSWConfig.file().getBoolean("Config.Disable-Language-Selection")) {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GUIManager.openLanguage(p);
                }, Itens.createItem(Material.BOOK, 1, TranslatableLine.MENU_LANG_TITLE.get(p), Collections.singletonList("&f> " + RealSkywarsAPI.getInstance().getLanguageManagerAPI().getLanguage(p.getLanguage()).getDisplayName())), 18);
            }

            if (!RSWConfig.file().getBoolean("Config.Disable-Player-Reset")) {
                inventory.addItem(e -> p.resetData(), Itens.createItem(Material.BARRIER, 1, TranslatableLine.MENU_PLAYER_RESET_TITLE.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYER_RESET_ALERT.get(p))), 26);
            }
        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openAchievementGUI(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.ACHIEVEMENTS.get(p), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.KILLS);
            v.openInventory(p);
        }, Itens.createItem(Material.DIAMOND_SWORD, 1, RSWPlayer.PlayerStatistics.KILLS.getDisplayName(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.WINS_SOLO);
            v.openInventory(p);
        }, Itens.createItem(Material.LEATHER_BOOTS, 1, RSWPlayer.PlayerStatistics.WINS_SOLO.getDisplayName(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.WINS_TEAMS);
            v.openInventory(p);
        }, Itens.createItem(Material.CHAINMAIL_CHESTPLATE, 1, RSWPlayer.PlayerStatistics.WINS_TEAMS.getDisplayName(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 14);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewerGUI v = new AchievementViewerGUI(p, RSWPlayer.PlayerStatistics.GAMES_PLAYED);
            v.openInventory(p);
        }, Itens.createItem(Material.FILLED_MAP, 1, RSWPlayer.PlayerStatistics.GAMES_PLAYED.getDisplayName(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 16);

        inventory.addItem(e -> {
            p.closeInventory();
            GUIManager.openPlayerProfile(p);
        }, Itens.createItem(Material.BIRCH_DOOR, 1, TranslatableLine.BUTTONS_MENU_TITLE.getSingle(), Collections.singletonList(TranslatableLine.BUTTONS_MENU_DESC.getSingle())), 26);

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
            p.closeInventory();
            if (id == 0) {
                PlayerItemsGUI pc = new PlayerItemsGUI(p, RSWBuyableItem.ItemCategory.KIT);
                pc.openInventory(p);
            } else {
                ShopGUI s = new ShopGUI(p, RSWBuyableItem.ItemCategory.KIT);
                s.openInventory(p);
            }
        }, Itens.createItem(Material.BIRCH_DOOR, 1, "Exit Preview"), 53);

        inventory.openInventory(p.getPlayer());
    }
}
