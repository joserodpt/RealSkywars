package josegamerpt.realskywars.gui;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.GUIBuilder;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;
import java.util.Collections;

public class GUIManager {

    public static void openShopMenu(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder("&fShop &9Categories", 9, p.getUniqueId(),
                Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ShopViewer v = new ShopViewer(p.getUniqueId(), Enum.Categories.CAGE_BLOCKS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.SPAWNER, 1, "&9Cage Blocks",
                Collections.singletonList("&fClick here to open this category.")), 0);

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ShopViewer v = new ShopViewer(p.getUniqueId(), Enum.Categories.KITS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.CHEST, 1, "&9Kits", Collections.singletonList("&fClick here to open this category.")), 1);

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ShopViewer v = new ShopViewer(p.getUniqueId(), Enum.Categories.WIN_BLOCKS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, "&9Win Blocks",
                Collections.singletonList("&fClick here to open this category.")), 2);

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ShopViewer v = new ShopViewer(p.getUniqueId(), Enum.Categories.BOW_PARTICLES);
            v.openInventory(p);
        }, Itens.createItemLore(Material.BOW, 1, "&9Bow Particles",
                Collections.singletonList("&fClick here to open this category.")), 3);

        inventory.openInventory(p.getPlayer());
    }

    public static void openSpectate(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder("&9Players", 54, p.getUniqueId(),
                Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (RSWPlayer s : p.getRoom().getPlayers()) {
            if (s.getPlayer() != null) {
                inventory.addItem(e -> {
                    p.teleport(s.getPlayer().getLocation());
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.COMPASS_TELEPORT, true).replace("%name%", s.getDisplayName()));
                }, Itens.addLore(Itens.getHead(s.getPlayer(), 1, "&b" + s.getDisplayName()),
                        Arrays.asList("&fLife: &c" + String.format("%.2f", s.getPlayer().getHealth()), "&fClick to teleport to " + s.getDisplayName(), "")), i);
                i++;
            }
        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openVote(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder("Vote", 27, p.getUniqueId(),
                Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            if (!p.isBot() && p.getRoom().getVoters().contains(p.getUniqueId())) {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_ALREADY_VOTED, true));
            } else {
                p.getRoom().addVote(p.getUniqueId(), 1);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(p, Enum.TS.CHEST_BASIC, false)));
            }
        }, Itens.createItemLore(Material.WOODEN_SWORD, 1, LanguageManager.getString(p, Enum.TS.CHEST_BASIC, false), Collections.emptyList()), 10);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getRoom().getVoters().contains(p.getUniqueId())) {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_ALREADY_VOTED, true));
            } else {
                p.getRoom().addVote(p.getUniqueId(), 2);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(p, Enum.TS.CHEST_NORMAL, false)));
            }
        }, Itens.createItemLore(Material.CHEST, 1, LanguageManager.getString(p, Enum.TS.CHEST_NORMAL, false), Collections.emptyList()), 12);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getRoom().getVoters().contains(p.getUniqueId())) {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_ALREADY_VOTED, true));
            } else {
                p.getRoom().addVote(p.getUniqueId(), 3);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(p, Enum.TS.CHEST_OP, false)));
            }
        }, Itens.createItemLore(Material.ENDER_CHEST, 1, LanguageManager.getString(p, Enum.TS.CHEST_OP, false), Collections.emptyList()), 14);

        inventory.addItem(e -> {
            if (!p.isBot() && p.getRoom().getVoters().contains(p.getUniqueId())) {
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_ALREADY_VOTED, true));
            } else {
                p.getRoom().addVote(p.getUniqueId(), 4);
                p.sendMessage(LanguageManager.getString(p, Enum.TS.CHEST_VOTE, true).replace("%chest%", LanguageManager.getString(p, Enum.TS.CHEST_CAOS, false)));
            }
        }, Itens.createItemLoreEnchanted(Material.TNT, 1, LanguageManager.getString(p, Enum.TS.CHEST_CAOS, false), Collections.emptyList()), 16);

        inventory.openInventory(p.getPlayer());
    }

    public static void openLanguage(RSWPlayer p) {
        GUIBuilder inventory = new GUIBuilder("&9Languages", 54, p.getUniqueId(),
                Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int i = 0;
        for (String s : LanguageManager.getLanguages()) {
            inventory.addItem(e -> PlayerManager.setLanguage(p, s), Itens.createItemLore(Material.JUNGLE_SIGN, 1, "&b" + s,
                    Collections.singletonList("&fClick here to select this language.")), i);
            i++;

        }
        inventory.openInventory(p.getPlayer());
    }

    public static void openPlayerMenu(RSWPlayer p, Boolean showCriticOptions) {
        GUIBuilder inventory = new GUIBuilder("&9Your &3Profile", 9, p.getUniqueId(),
                Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ProfileContent v = new ProfileContent(p, Enum.Categories.KITS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.CHEST, 1, "&bYour &9Kits",
                Collections.singletonList("&fClick here to view this item.")), 0);

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ProfileContent v = new ProfileContent(p, Enum.Categories.CAGE_BLOCKS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.SPAWNER, 1, "&bYour &9Cage Blocks",
                Collections.singletonList("&fClick here to view this item.")), 1);

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ProfileContent v = new ProfileContent(p, Enum.Categories.WIN_BLOCKS);
            v.openInventory(p);
        }, Itens.createItemLore(Material.FIREWORK_ROCKET, 1, "&bYour &9Win Blocks",
                Collections.singletonList("&fClick here to view this item.")), 2);

        inventory.addItem(e -> {
            p.getPlayer().closeInventory();
            ProfileContent v = new ProfileContent(p, Enum.Categories.BOW_PARTICLES);
            v.openInventory(p);
        }, Itens.createItemLore(Material.BOW, 1, "&bYour &9Bow Particles",
                Collections.singletonList("&fClick here to open this category.")), 3);

        //settings
        if (showCriticOptions) {
            inventory.addItem(e -> {
                p.getPlayer().closeInventory();
                GUIManager.openLanguage(p);
            }, Itens.createItemLore(Material.JUNGLE_SIGN, 1, "&9Language",
                    Collections.singletonList("&fCurrently set: " + p.getLanguage())), 7);

            inventory.addItem(e -> p.resetData(), Itens.createItemLore(Material.BARRIER, 1, "&4Reset Your Data",
                    Collections.singletonList("&cProceed with caution. This action cannot be rolled back.")), 8);
        }

        inventory.openInventory(p.getPlayer());
    }
}
