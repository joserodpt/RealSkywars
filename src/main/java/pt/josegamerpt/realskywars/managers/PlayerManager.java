package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pt.josegamerpt.realskywars.classes.DisplayItem;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Enum.PlayerState;
import pt.josegamerpt.realskywars.classes.Enum.Selection;
import pt.josegamerpt.realskywars.classes.Enum.Selections;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.configuration.Items;
import pt.josegamerpt.realskywars.configuration.Players;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

public class PlayerManager {

    public static ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();

    public static void giveItems(Player p, PlayerItems i) {
        if (p != null) {
            p.getInventory().clear();
            switch (i) {
                case LOBBY:
                    p.getInventory().setItem(2, Itens.getHead(p, 1, "&9Your Profile"));
                    p.getInventory().setItem(4, Items.MAPS);
                    p.getInventory().setItem(6, Items.SHOP);
                    break;
                case CAGE:
                    p.getInventory().setItem(0, Items.KITS);
                    p.getInventory().setItem(4, Items.CHESTS);
                    p.getInventory().setItem(8, Items.LEAVE);
                    break;
                case SPECTATOR:
                    p.getInventory().setItem(0, Items.SPECTATE);
                    p.getInventory().setItem(8, Items.LEAVE);
                    break;
                default:
                    break;
            }
        }
    }

    public static void loadPlayer(Player p) {
        if (Players.file().isConfigurationSection(p.getUniqueId().toString()) == true) {
            int tkills = Players.file().getInt(p.getUniqueId() + ".Kills");
            int dead = Players.file().getInt(p.getUniqueId() + ".Deaths");
            Double coin = Players.file().getDouble(p.getUniqueId() + ".Coins");
            List<String> bg = Players.file().getStringList(p.getUniqueId() + ".Bought-Items");
            String lang = Players.file().getString(p.getUniqueId() + ".Language");
            GamePlayer gp = new GamePlayer(p, PlayerState.LOBBY_OR_NOGAME, null, tkills, dead, coin, lang, bg);
            HashMap<Selection, Selections> ss = new HashMap<Selection, Selections>();
            String MAPVIEWER = Players.file().getString(p.getUniqueId() + ".Preferences.MAPVIEWER");
            if (MAPVIEWER != null) {
                Selections s = getSelection(MAPVIEWER);
                ss.put(Selection.MAPVIEWER, s);
            }
            gp.selections = ss;
            gp.save();
        } else {
            GamePlayer gp = new GamePlayer(p, PlayerState.LOBBY_OR_NOGAME, null, 0, 0, 0D,
                    LanguageManager.getDefaultLanguage(), Arrays.asList(""));
            gp.selections.put(Selection.MAPVIEWER, Selections.MAPV_ALL);
            gp.save();
            gp.saveData();
        }
    }

    private static Selections getSelection(String mv) {
        if (mv.equals("MAPV_SPECTATE")) {
            return Selections.MAPV_SPECTATE;
        }
        if (mv.equals("MAPV_AVAILABLE")) {
            return Selections.MAPV_AVAILABLE;
        }
        if (mv.equals("MAPV_STARTING")) {
            return Selections.MAPV_STARTING;
        }
        if (mv.equals("MAPV_WAITING")) {
            return Selections.MAPV_WAITING;
        }
        if (mv.equals("MAPV_ALL")) {
            return Selections.MAPV_ALL;
        }
        return null;
    }

    public static GamePlayer getPlayer(Player p) {
        for (GamePlayer g : players) {
            if (g.p == p) {
                return g;
            }
        }
        return null;
    }

    public static void savePlayer(GamePlayer p) {
        if (p.p != null) {
            Players.file().set(p.p.getUniqueId() + ".Coins", p.coins);
            Players.file().set(p.p.getUniqueId() + ".Kills", p.totalkills);
            Players.file().set(p.p.getUniqueId() + ".Deaths", p.deaths);
            Players.file().set(p.p.getUniqueId() + ".Name", p.p.getName());
            Players.file().set(p.p.getUniqueId() + ".Language", p.language);
            for (Entry<Selection, Selections> entry : p.selections.entrySet()) {
                Selection key = entry.getKey();
                Selections value = entry.getValue();
                Players.file().set(p.p.getUniqueId() + ".Preferences." + key.name(), value.name());
            }
            Players.file().set(p.p.getUniqueId() + ".Bought-Items", p.bought);
            Players.save();
        }
    }

    public static Player searchPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public static Player searchPlayer(UUID u) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(u)) {
                return p;
            }
        }
        return null;
    }

    public static boolean isPlayerInMatch(Player p) {
        if (getPlayer(p) != null) {
            if (getPlayer(p).room == null) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static void setLanguage(GamePlayer player, String s) {
        player.language = s;
        player.p.sendMessage(LanguageManager.getString(player, TS.LANGUAGE_SET, true).replace("%language%", "" + s));
    }

    public static Boolean boughtItem(GamePlayer p, String string, Enum.Categories c) {
        List<String> bought = p.bought;
        String prod = string + "|" + c.name();
        if (bought.contains(ChatColor.stripColor(prod))) {
            return true;
        } else {
            return false;
        }
    }

    public static void loadPlayers() {
        PlayerManager.players.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerManager.loadPlayer(p);
        }
    }

    public static List<DisplayItem> getBoughtItems(GamePlayer player, Enum.Categories t) {
        List<DisplayItem> bought = new ArrayList<DisplayItem>();
        switch (t) {
            case CAGEBLOCK:
                for (DisplayItem a : ShopManager.getCategoryContents(player, Enum.Categories.CAGEBLOCK)) {
                    if (a != null) {
                        if (a.bought == true) {
                            bought.add(a);
                        }
                    }
                }
                break;
            case BOWPARTICLE:
                for (DisplayItem a : ShopManager.getCategoryContents(player, Enum.Categories.BOWPARTICLE)) {
                    if (a != null) {
                        if (a.bought == true) {
                            bought.add(a);
                        }
                    }
                }
                break;
            case WINBLOCKS:
                for (DisplayItem a : ShopManager.getCategoryContents(player, Enum.Categories.WINBLOCKS)) {
                    if (a != null) {
                        if (a.bought == true) {
                            bought.add(a);
                        }
                    }
                }
                break;
            case KITS:
                for (DisplayItem a : ShopManager.getCategoryContents(player, Enum.Categories.KITS)) {
                    if (a != null) {
                        if (a.bought == true) {
                            bought.add(a);
                        }
                    }
                }
                break;
            default:
                //
                break;
        }
        if (bought.size() == 0) {
            bought.add(new DisplayItem());
        }
        return bought;
    }

    public static Selections getSelection(GamePlayer p, Selection m) {
        Selections s = null;
        s = p.selections.get(m);
        return s;
    }

    public static void setSelection(GamePlayer p, Selection s, Selections ss) {
        if (!p.selections.containsKey(s)) {
            p.selections.put(s, ss);
        } else {
            p.selections.remove(s);
            p.selections.put(s, ss);
        }
    }

    public static void tpLobby(GamePlayer p) {
        if (GameManager.lobbyscoreboard) {
            p.teleport(GameManager.lobby);
            p.sendMessage(LanguageManager.getString(p, TS.LOBBY_TELEPORT, true));
        }
    }

    public static int countPlayingPlayers() {
        int i = 0;
        for (GamePlayer g : players) {
            if (g.room != null) {
                i++;
            }
        }
        return i;
    }

    public enum PlayerItems {
        LOBBY, CAGE, SPECTATOR
    }
}
