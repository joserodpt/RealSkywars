package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.guis.AchievementViewer;
import josegamerpt.realskywars.gui.guis.GameLogViewer;
import josegamerpt.realskywars.gui.guis.ProfileContent;
import josegamerpt.realskywars.gui.guis.ShopViewer;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.GUIBuilder;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class GUIManager {

    public static void openShopMenu(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENUS_SHOP_TILE, false), 9, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            ShopViewer v = new ShopViewer(p, ShopManager.Categories.CAGE_BLOCKS);
            v.openInventory(p);
        }, Itens.createItem(Material.SPAWNER, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CAGEBLOCK, false)), 0);

        inventory.addItem(e -> {
            p.closeInventory();
            ShopViewer v = new ShopViewer(p, ShopManager.Categories.KITS);
            v.openInventory(p);
        }, Itens.createItem(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.KITS, false)), 1);

        inventory.addItem(e -> {
            p.closeInventory();
            ShopViewer v = new ShopViewer(p, ShopManager.Categories.WIN_BLOCKS);
            v.openInventory(p);
        }, Itens.createItem(Material.FIREWORK_ROCKET, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.WINBLOCK, false)), 2);

        inventory.addItem(e -> {
            p.closeInventory();
            ShopViewer v = new ShopViewer(p, ShopManager.Categories.BOW_PARTICLES);
            v.openInventory(p);
        }, Itens.createItem(Material.BOW, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.BOWPARTICLE, false)), 3);

        inventory.openInventory(p.getPlayer());
    }

    public static void openSpectate(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENUS_SPECTATE_TITLE, false), 54, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (RSWPlayer s : p.getMatch().getPlayers()) {
            if (s.getPlayer() != null) {
                inventory.addItem(e -> {
                    p.teleport(s.getPlayer().getLocation());
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.COMPASS_TELEPORT, true).replace("%name%", s.getDisplayName()));
                }, Itens.addLore(Itens.getHead(s.getPlayer(), 1, "&b" + s.getDisplayName()), Collections.singletonList("&c" + String.format("%.2f", s.getPlayer().getHealth()))), i);
                ++i;
            }
        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openChestVote(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_CHESTS_TITLE, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.CHESTS, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Basic")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.CHESTS, 1);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CHEST_BASIC, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.WOODEN_SWORD, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CHEST_BASIC, false), Collections.emptyList()), 10);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.CHESTS, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Normal")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.CHESTS, 2);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CHEST_NORMAL, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CHEST_NORMAL, false), Collections.emptyList()), 13);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.CHESTS, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Epic")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.CHESTS, 3);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CHEST_EPIC, false)));

                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.ENDER_CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CHEST_EPIC, false), Collections.emptyList()), 16);

        inventory.addItem(e -> {
            p.closeInventory();
            openVote(p);
        }, Itens.createItemLore(Material.BIRCH_DOOR, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))), 26);

        inventory.openInventory(p.getPlayer());
    }

    public static void openTimeVote(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_TIME_TITLE, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.TIME, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Day")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.TIME, 1);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIME_DAY, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.YELLOW_CONCRETE, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIME_DAY, false), Collections.emptyList()), 10);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.TIME, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Sunset")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.TIME, 2);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIME_SUNSET, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.RED_CONCRETE, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIME_SUNSET, false), Collections.emptyList()), 13);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.TIME, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Night")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.TIME, 3);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIME_NIGHT, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.BLACK_CONCRETE, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.TIME_NIGHT, false), Collections.emptyList()), 16);


        inventory.addItem(e -> {
            p.closeInventory();
            openVote(p);
        }, Itens.createItemLore(Material.BIRCH_DOOR, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))), 26);

        inventory.openInventory(p.getPlayer());
    }

    public static void openProjectileVote(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PROJECTILES_TITLE, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.PROJECTILES, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Normal-Projectile")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.PROJECTILES, 1);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.PROJECTILE_NORMAL, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.EGG, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.PROJECTILE_NORMAL, false), Collections.emptyList()), 12);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getMatch().hasVotedFor(SWGameMode.VoteType.PROJECTILES, p.getUUID())) {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ALREADY_VOTED, true));
            } else {
                if (p.getPlayer().hasPermission("RealSkywars.Break-Projectile")) {
                    p.getMatch().addVote(p.getUUID(), SWGameMode.VoteType.PROJECTILES, 2);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.VOTE, true).replace("%thing%", RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.PROJECTILE_BREAK, false)));
                } else {
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CMD_NOPERM, true));
                }
            }
        }, Itens.createItemLore(Material.COBBLESTONE, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.PROJECTILE_BREAK, false), Collections.emptyList()), 14);

        inventory.addItem(e -> {
            p.closeInventory();
            openVote(p);
        }, Itens.createItemLore(Material.BIRCH_DOOR, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))), 26);

        inventory.openInventory(p.getPlayer());
    }

    public static void openVote(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_VOTE_TITLE, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            openChestVote(p);
        }, Itens.createItemLore(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_CHESTS_TITLE, false), Collections.emptyList()), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            openProjectileVote(p);
        }, Itens.createItemLore(Material.ARROW, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PROJECTILES_TITLE, false), Collections.emptyList()), 13);

        inventory.addItem(e -> {
            p.closeInventory();
            openTimeVote(p);
        }, Itens.createItemLore(Material.CLOCK, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_TIME_TITLE, false), Collections.emptyList()), 16);

        inventory.openInventory(p.getPlayer());
    }

    public static void openLanguage(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_LANG_TITLE, false), 18, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (String s : RealSkywars.getLanguageManager().getLanguages()) {
            inventory.addItem(e -> RealSkywars.getPlayerManager().setLanguage(p, s), Itens.createItemLore(Material.BOOK, 1, "&b" + s, Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_LANG_SELECT, false))), i);
            ++i;

        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openPlayerMenu(RSWPlayer p) {
        int size = 27;
        if (!p.isInMatch()) {
            size = 45;
        }

        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_TITLE, false), size, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            ProfileContent v = new ProfileContent(p, ShopManager.Categories.KITS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.KITS, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            ProfileContent v = new ProfileContent(p, ShopManager.Categories.CAGE_BLOCKS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.SPAWNER, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.CAGEBLOCK, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            ProfileContent v = new ProfileContent(p, ShopManager.Categories.WIN_BLOCKS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.WINBLOCK, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 14);

        inventory.addItem(e -> {
            p.closeInventory();
            ProfileContent v = new ProfileContent(p, ShopManager.Categories.BOW_PARTICLES);
            v.openInventory(p);
        }, Itens.createItemLore(Material.BOW, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.BOWPARTICLE, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 13);

        inventory.addItem(e -> {
            p.closeInventory();
            openAchievementGUI(p);
        }, Itens.createItemLore(Material.BOOKSHELF, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ACHIEVEMENTS, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 16);


        //settings
        if (!p.isInMatch()) {
            inventory.addItem(e -> {
                p.closeInventory();
                GUIManager.openLanguage(p);
            }, Itens.createItemLore(Material.BOOK, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_LANG_TITLE, false), Collections.singletonList("&f> " + p.getLanguage())), 30);

            inventory.addItem(e -> p.resetData(), Itens.createItemLore(Material.BARRIER, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_RESET_TITLE, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_RESET_ALERT, false))), 44);

            inventory.addItem(e -> {
                p.closeInventory();
                GameLogViewer v = new GameLogViewer(p);
                v.openInventory(p);
            }, Itens.createItemLore(Material.FILLED_MAP, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_GAME_HISTORY, false), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 32);

        }

        inventory.openInventory(p.getPlayer());
    }

    public static void openAchievementGUI(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ACHIEVEMENTS, false), 27, p.getUUID(), Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.KILLS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.DIAMOND_SWORD, 1, "&b&l" + RSWPlayer.PlayerStatistics.KILLS.name(), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 10);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.WINS_SOLO);
            v.openInventory(p);
        }, Itens.createItemLore(Material.LEATHER_BOOTS, 1, "&b&l" + RSWPlayer.PlayerStatistics.WINS_SOLO.name().replace("_", " "), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 12);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.WINS_TEAMS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.CHAINMAIL_CHESTPLATE, 1, "&b&l" + RSWPlayer.PlayerStatistics.WINS_TEAMS.name().replace("_", " "), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 14);

        inventory.addItem(e -> {
            p.closeInventory();
            AchievementViewer v = new AchievementViewer(p, RSWPlayer.PlayerStatistics.GAMES_PLAYED);
            v.openInventory(p);
        }, Itens.createItemLore(Material.FILLED_MAP, 1, "&b&l" + RSWPlayer.PlayerStatistics.GAMES_PLAYED.name().replace("_", " "), Collections.singletonList(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.MENU_PLAYERP_VIEWITEM, false))), 16);

        inventory.addItem(e -> {
            p.closeInventory();
            GUIManager.openPlayerMenu(p);
        }, Itens.createItemLore(Material.BIRCH_DOOR, 1, RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_TITLE), Collections.singletonList(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BUTTONS_MENU_DESC))), 26);

        inventory.openInventory(p.getPlayer());
    }

    public static void openKitPreview(RSWPlayer p, Kit kit, int id) {
        GUIBuilder inventory = new GUIBuilder(kit.getName(), 45, p.getUUID());

        int i = 0;
        for (ItemStack content : kit.getContents()) {
            if (content != null) {
                inventory.addItem(event -> {
                }, content, i);
                ++i;
            }
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
        }, Itens.createItem(Material.BIRCH_DOOR, 1, ""), 44);

        inventory.openInventory(p.getPlayer());
    }
}
