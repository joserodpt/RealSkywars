package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.classes.DisplayItem;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.Enum.Selection;
import josegamerpt.realskywars.classes.Enum.Selections;
import josegamerpt.realskywars.classes.Enum.TS;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.configuration.Items;
import josegamerpt.realskywars.configuration.Players;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class PlayerManager {

    private static ArrayList<RSWPlayer> players = new ArrayList<>();

    public static void giveItems(Player p, PlayerItems i) {
        if (p != null) {
            p.getInventory().clear();
            switch (i) {
                case LOBBY:
                    p.getInventory().setItem(1, Items.PROFILE);
                    p.getInventory().setItem(4, Items.MAPS);
                    p.getInventory().setItem(7, Items.SHOP);
                    break;
                case CAGE:
                    p.getInventory().setItem(0, Items.PROFILE);
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
        RSWPlayer gp;
        if (Players.file().isConfigurationSection(p.getUniqueId().toString())) {
            int tkills = Players.file().getInt(p.getUniqueId() + ".Kills");
            int dead = Players.file().getInt(p.getUniqueId() + ".Deaths");
            int solwin = Players.file().getInt(p.getUniqueId() + ".Wins.Solo");
            int tw = Players.file().getInt(p.getUniqueId() + ".Wins.Teams");
            int gap = Players.file().getInt(p.getUniqueId() + ".Games-Played");
            int los = Players.file().getInt(p.getUniqueId() + ".Loses");
            Double coin = Players.file().getDouble(p.getUniqueId() + ".Coins");
            List<String> bg = Players.file().getStringList(p.getUniqueId() + ".Bought-Items");
            String lang = Players.file().getString(p.getUniqueId() + ".Language");

            gp = new RSWPlayer(p, RSWPlayer.PlayerState.LOBBY_OR_NOGAME, null, tkills, dead, solwin, tw, coin, lang, bg, los, gap);
            HashMap<Selection, Selections> ss = new HashMap<>();
            String mapv = Players.file().getString(p.getUniqueId() + ".Preferences.MAPVIEWER");
            if (mapv != null) {
                Selections s = getSelection(mapv);
                ss.put(Selection.MAPVIEWER, s);
            }
            gp.setSelections(ss);
            gp.save();
        } else {
            gp = new RSWPlayer(p, RSWPlayer.PlayerState.LOBBY_OR_NOGAME, null, 0, 0, 0, 0, 0D,
                    LanguageManager.getDefaultLanguage(), Collections.singletonList(""), 0, 0);
            gp.getSelections().put(Selection.MAPVIEWER, Selections.MAPV_ALL);
            gp.save();
            gp.saveData();
        }
        gp.heal();
        if (GameManager.tpLobbyOnJoin()) {
            GameManager.tpToLobby(gp);
        }
    }

    private static Selections getSelection(String mv) {
        return Selections.valueOf(mv);
    }

    public static RSWPlayer getPlayer(Player p) {
        for (RSWPlayer g : players) {
            if (g.getPlayer() == p) {
                return g;
            }
        }
        return null;
    }

    public static void savePlayer(RSWPlayer p) {
        if (p.getPlayer() != null) {
            if (!Players.file().isConfigurationSection(p.getUniqueId().toString())) {
                RealSkywars.log("Creating empty player file for " + p.getName() + " UUID: " + p.getUniqueId().toString());
            }

            Players.file().set(p.getUniqueId() + ".Coins", p.getCoins());
            Players.file().set(p.getUniqueId() + ".Wins.Solo", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO));
            Players.file().set(p.getUniqueId() + ".Wins.Teams", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS));
            Players.file().set(p.getUniqueId() + ".Kills", p.getStatistics(RSWPlayer.PlayerStatistics.TOTAL_KILLS));
            Players.file().set(p.getUniqueId() + ".Deaths", p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS));
            Players.file().set(p.getUniqueId() + ".Loses", p.getStatistics(RSWPlayer.PlayerStatistics.LOSES));
            Players.file().set(p.getUniqueId() + ".Games-Played", p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED));
            Players.file().set(p.getUniqueId() + ".Name", p.getName());
            Players.file().set(p.getUniqueId() + ".Language", p.getLanguage());
            for (Entry<Selection, Selections> entry : p.getSelections().entrySet()) {
                Selection key = entry.getKey();
                Selections value = entry.getValue();
                Players.file().set(p.getUniqueId() + ".Preferences." + key.name(), value.name());
            }
            Players.file().set(p.getUniqueId() + ".Bought-Items", p.getBoughtItems());
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

    public static void setLanguage(RSWPlayer player, String s) {
        player.setProperty(RSWPlayer.PlayerProperties.LANGUAGE, s);
        player.sendMessage(LanguageManager.getString(player, TS.LANGUAGE_SET, true).replace("%language%", "" + s));
    }

    public static Boolean boughtItem(RSWPlayer p, String string, Enum.Categories c) {
        return p.getBoughtItems().contains(ChatColor.stripColor(string + "|" + c.name()));
    }

    public static void loadPlayers() {
        PlayerManager.players.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerManager.loadPlayer(p);
        }
    }

    public static List<DisplayItem> getBoughtItems(RSWPlayer player, Enum.Categories t) {
        List<DisplayItem> bought = new ArrayList<>();

        for (DisplayItem a : ShopManager.getCategoryContents(player, t)) {
            if (a != null && a.isBought()) {
                bought.add(a);
            }
        }

        if (bought.size() == 0) {
            bought.add(new DisplayItem());
        }
        return bought;
    }

    public static int countPlayingPlayers() {
        return GameManager.getRooms().stream().mapToInt(SWGameMode::getPlayersCount).sum();
    }

    public static void stopScoreboards() {
        players.forEach(gamePlayer -> gamePlayer.getScoreboard().stop());
    }

    public static ArrayList<RSWPlayer> getPlayers() {
        return players;
    }

    public static void addPlayer(RSWPlayer rswPlayer) {
        players.add(rswPlayer);
    }

    public static void removePlayer(RSWPlayer rswPlayer) {
        players.remove(rswPlayer);
    }

    public enum PlayerItems {LOBBY, CAGE, SPECTATOR}

}
