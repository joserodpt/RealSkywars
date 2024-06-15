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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.GUIBuilder;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
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
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_SHOP_TILE.get(p), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Kit-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();

                new BukkitRunnable() {
                    public void run() {
                        ShopGUI v = new ShopGUI(p, ShopManagerAPI.ShopCategory.KITS);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.CHEST, 1, TranslatableLine.KITS.get(p)), 10);

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Cage-Block-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();

                new BukkitRunnable() {
                    public void run() {
                        ShopGUI v = new ShopGUI(p, ShopManagerAPI.ShopCategory.CAGE_BLOCKS);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.SPAWNER, 1, TranslatableLine.CAGEBLOCK.get(p)), 12);

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Win-Block-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();

                new BukkitRunnable() {
                    public void run() {
                        ShopGUI v = new ShopGUI(p, ShopManagerAPI.ShopCategory.WIN_BLOCKS);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.FIREWORK_ROCKET, 1, TranslatableLine.WINBLOCK.get(p)), 14);

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Bow-Particles-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();

                new BukkitRunnable() {
                    public void run() {
                        ShopGUI v = new ShopGUI(p, ShopManagerAPI.ShopCategory.BOW_PARTICLES);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.BOW, 1, TranslatableLine.BOWPARTICLE.get(p)), 16);

        inventory.openInventory(p.getPlayer());
    }

    public static void openSpectate(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_SPECTATE_TITLE.get(p), 54, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (RSWPlayer s : p.getMatch().getPlayers()) {
            if (s.getPlayer() != null) {
                inventory.addItem(e -> {
                    p.teleport(s.getPlayer().getLocation());
                    p.sendMessage(TranslatableLine.COMPASS_TELEPORT.get(p, true).replace("%name%", s.getDisplayName()));
                }, Itens.addLore(Itens.getHead(s.getPlayer(), 1, "&b" + s.getDisplayName()), Collections.singletonList("&c" + String.format("%.2f", s.getPlayer().getHealth()))), i);
                ++i;
            }
        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openLanguage(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_LANG_TITLE.get(p, true), 18, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;

        for (String language : RealSkywarsAPI.getInstance().getLanguageManagerAPI().getLanguages()) {
            if (i == 17) {
                break;
            } else {
                inventory.addItem(e -> RealSkywarsAPI.getInstance().getPlayerManagerAPI().setLanguage(p, language), Itens.renameItem(ItemStackSpringer.getItemDeSerialized(Itens.sectionToMap("Languages." + language + ".Icon")), "&e&l" + language, language), i);
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

    public static void openPlayerMenu(RSWPlayer p) {
        int size = 27;
        if (!p.isInMatch()) {
            size = 45;
        }

        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_PLAYERP_TITLE.get(p), size, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Kit-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();
                new BukkitRunnable() {
                    public void run() {
                        PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.ShopCategory.KITS);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.CHEST, 1, TranslatableLine.KITS.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 10);

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Cage-Block-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();


                new BukkitRunnable() {
                    public void run() {
                        PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.ShopCategory.CAGE_BLOCKS);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);

            }, Itens.createItem(Material.SPAWNER, 1, TranslatableLine.CAGEBLOCK.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 12);

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Win-Block-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();

                new BukkitRunnable() {
                    public void run() {
                        PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.ShopCategory.WIN_BLOCKS);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.FIREWORK_ROCKET, 1, TranslatableLine.WINBLOCK.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 14);

        if (RSWConfig.file().getBoolean("Config.Shops.Enable-Bow-Particles-Shop"))
            inventory.addItem(e -> {
                p.closeInventory();

                new BukkitRunnable() {
                    public void run() {
                        PlayerProfileContentsGUI v = new PlayerProfileContentsGUI(p, ShopManagerAPI.ShopCategory.BOW_PARTICLES);
                        v.openInventory(p);
                    }
                }.runTaskLater(RealSkywarsAPI.getInstance().getPlugin(), 1);
            }, Itens.createItem(Material.BOW, 1, TranslatableLine.BOWPARTICLE.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 13);

        inventory.addItem(e -> {
            p.closeInventory();
            openAchievementGUI(p);
        }, Itens.createItem(Material.BOOKSHELF, 1, TranslatableLine.ACHIEVEMENTS.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 16);


        //settings
        if (!p.isInMatch()) {
            if (RSWConfig.file().getBoolean("Config.Disable-Language-Selection")) {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GameHistoryGUI v = new GameHistoryGUI(p);
                    v.openInventory(p);
                }, Itens.createItem(Material.FILLED_MAP, 1, TranslatableLine.MENU_PLAYER_GAME_HISTORY.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 31);
            } else {
                inventory.addItem(e -> {
                    p.closeInventory();
                    GameHistoryGUI v = new GameHistoryGUI(p);
                    v.openInventory(p);
                }, Itens.createItem(Material.FILLED_MAP, 1, TranslatableLine.MENU_PLAYER_GAME_HISTORY.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYERP_VIEWITEM.get(p))), 32);
                inventory.addItem(e -> {
                    p.closeInventory();
                    GUIManager.openLanguage(p);
                }, Itens.createItem(Material.BOOK, 1, TranslatableLine.MENU_LANG_TITLE.get(p), Collections.singletonList("&f> " + p.getLanguage())), 30);
            }

            if (!RSWConfig.file().getBoolean("Config.Disable-Player-Reset")) {
                inventory.addItem(e -> p.resetData(), Itens.createItem(Material.BARRIER, 1, TranslatableLine.MENU_PLAYER_RESET_TITLE.get(p), Collections.singletonList(TranslatableLine.MENU_PLAYER_RESET_ALERT.get(p))), 44);
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
            GUIManager.openPlayerMenu(p);
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
            if (id == 0) {
                p.closeInventory();
                PlayerProfileContentsGUI pc = new PlayerProfileContentsGUI(p, ShopManagerAPI.ShopCategory.KITS);
                pc.openInventory(p);
            } else {
                p.closeInventory();
                ShopGUI s = new ShopGUI(p, ShopManagerAPI.ShopCategory.KITS);
                s.openInventory(p);
            }
        }, Itens.createItem(Material.BIRCH_DOOR, 1, ""), 53);

        inventory.openInventory(p.getPlayer());
    }
}
