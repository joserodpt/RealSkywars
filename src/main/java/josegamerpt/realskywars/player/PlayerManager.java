package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.classes.DisplayItem;
import josegamerpt.realskywars.classes.Selections;
import josegamerpt.realskywars.classes.Selections.Key;
import josegamerpt.realskywars.configuration.Items;
import josegamerpt.realskywars.configuration.Players;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.modes.SWGameMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class PlayerManager {

    static HashMap<Player, Player> trackingPlayers = new HashMap<>();
    private static ArrayList<RSWPlayer> players = new ArrayList<>();

    public static void giveItems(Player p, PlayerItems i) {
        if (p != null) {
            p.getInventory().clear();
            switch (i) {
                case LOBBY:
                    p.getInventory().setItem(0, Items.PROFILE);
                    p.getInventory().setItem(4, Items.MAPS);
                    p.getInventory().setItem(8, Items.SHOP);
                    break;
                case CAGE:
                    p.getInventory().setItem(1, Items.KIT);
                    p.getInventory().setItem(4, Items.CHESTS);
                    p.getInventory().setItem(7, Items.LEAVE);
                    break;
                case SPECTATOR:
                    p.getInventory().setItem(1, Items.SPECTATE);
                    p.getInventory().setItem(2, Items.PLAYAGAIN);
                    p.getInventory().setItem(7, Items.LEAVE);
                    break;
                case SETUP:
                    p.getInventory().setItem(4, Items.CAGESET);
                    p.getInventory().setItem(0, Items.CHEST1);
                    p.getInventory().setItem(8, Items.CHEST2);
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
            ArrayList<String> bg = (ArrayList<String>) Players.file().getStringList(p.getUniqueId() + ".Bought-Items");
            String lang = Players.file().getString(p.getUniqueId() + ".Language");
            String cageBlock = Players.file().getString(p.getUniqueId() + ".Preferences.Cage-Material");

            gp = new RSWPlayer(p, RSWPlayer.PlayerState.LOBBY_OR_NOGAME, null, tkills, dead, solwin, tw, coin, lang, bg, los, gap);
            HashMap<Selections.Key, Selections.Value> ss = new HashMap<>();
            String mapv = Players.file().getString(p.getUniqueId() + ".Preferences.MAPVIEWER");
            if (mapv != null) {
                Selections.Value s = getSelection(mapv);
                ss.put(Key.MAPVIEWER, s);
            }
            if (cageBlock != null) {
                gp.setProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK, Material.valueOf(cageBlock));
            }
            gp.setSelections(ss);
            gp.save();
        } else {
            gp = new RSWPlayer(p, RSWPlayer.PlayerState.LOBBY_OR_NOGAME, null, 0, 0, 0, 0, 0D,
                    LanguageManager.getDefaultLanguage(), new ArrayList<>(), 0, 0);
            gp.getSelections().put(Selections.Key.MAPVIEWER, Selections.Value.MAPV_ALL);
            gp.save();
            gp.saveData();
        }
        gp.heal();
        if (GameManager.tpLobbyOnJoin()) {
            GameManager.tpToLobby(gp);
        }
    }

    private static Selections.Value getSelection(String mv) {
        return Selections.Value.valueOf(mv);
    }

    public static RSWPlayer getPlayer(Player p) {
        for (RSWPlayer g : players) {
            if (g.getPlayer() == p) {
                return g;
            }
        }
        return null;
    }

    public static void savePlayer(RSWPlayer p, RSWPlayer.PlayerData d) {
        if (p.getPlayer() != null) {
            if (!Players.file().isConfigurationSection(p.getUniqueId().toString())) {
                RealSkywars.log("Creating empty player file for " + p.getName() + " UUID: " + p.getUniqueId().toString());
            }
            switch (d) {
                case ALL:
                    savePlayer(p, RSWPlayer.PlayerData.NAME);
                    savePlayer(p, RSWPlayer.PlayerData.LANG);
                    savePlayer(p, RSWPlayer.PlayerData.COINS);
                    savePlayer(p, RSWPlayer.PlayerData.PREFS);
                    savePlayer(p, RSWPlayer.PlayerData.STATS);
                    savePlayer(p, RSWPlayer.PlayerData.BOUGHT);
                    break;
                case NAME:
                    Players.file().set(p.getUniqueId() + ".Name", p.getName());
                    break;
                case LANG:
                    Players.file().set(p.getUniqueId() + ".Language", p.getLanguage());
                    break;
                case COINS:
                    Players.file().set(p.getUniqueId() + ".Coins", p.getCoins());
                    break;
                case PREFS:
                    for (Entry<Selections.Key, Selections.Value> entry : p.getSelections().entrySet()) {
                        Selections.Key key = entry.getKey();
                        Selections.Value value = entry.getValue();
                        Players.file().set(p.getUniqueId() + ".Preferences." + key.name(), value.name());
                    }
                    if (p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK) != null) {
                        Players.file().set(p.getUniqueId() + ".Preferences.Cage-Material", ((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name());
                    }
                    break;
                case STATS:
                    Players.file().set(p.getUniqueId() + ".Wins.Solo", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO));
                    Players.file().set(p.getUniqueId() + ".Wins.Teams", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS));
                    Players.file().set(p.getUniqueId() + ".Kills", p.getStatistics(RSWPlayer.PlayerStatistics.TOTAL_KILLS));
                    Players.file().set(p.getUniqueId() + ".Deaths", p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS));
                    Players.file().set(p.getUniqueId() + ".Loses", p.getStatistics(RSWPlayer.PlayerStatistics.LOSES));
                    Players.file().set(p.getUniqueId() + ".Games-Played", p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED));
                    break;
                case BOUGHT:
                    Players.file().set(p.getUniqueId() + ".Bought-Items", p.getBoughtItems());
                    break;
            }
            Players.save();
        }
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
        player.sendMessage(LanguageManager.getString(player, LanguageManager.TS.LANGUAGE_SET, true).replace("%language%", "" + s));
    }

    public static Boolean boughtItem(RSWPlayer p, String string, ShopManager.Categories c) {
        return p.getBoughtItems().contains(ChatColor.stripColor(string + "|" + c.name()));
    }

    public static void loadPlayers() {
        PlayerManager.players.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerManager.loadPlayer(p);
        }
    }

    public static List<DisplayItem> getBoughtItems(RSWPlayer player, ShopManager.Categories t) {
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
        return GameManager.getGames().stream().mapToInt(SWGameMode::getPlayersCount).sum();
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

    public static void trackPlayer(RSWPlayer gp) {

        ArrayList<RSWPlayer> tmp = new ArrayList<>(gp.getMatch().getPlayers());
        tmp.remove(gp);

        Optional<RSWPlayer> search = tmp.stream().filter(c -> c.getState().equals(RSWPlayer.PlayerState.PLAYING)).findAny();
        if (!search.isPresent() || search.get().isBot()) {
            gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.NO_TRACKER, true));
            return;
        }

        Player player = gp.getPlayer();
        Player target = search.get().getPlayer();

        //Credit GITHUB PlayerCompass

        trackingPlayers.put(player, target);
        gp.sendMessage(LanguageManager.getString(gp, LanguageManager.TS.TRACK_FOUND, true).replace("%player%", target.getDisplayName()));

        new BukkitRunnable() {
            public void run() {
                //Cancel task if player is offline or is no longer tracking target
                if (!player.isOnline() || !trackingPlayers.containsKey(player) || !trackingPlayers.get(player).equals(target))
                    this.cancel();

                    //Cancel task if target is offline
                else if (!target.isOnline() || search.get().getState() != RSWPlayer.PlayerState.PLAYING) {
                    if (gp.isInMatch()) {
                        player.setCompassTarget(gp.getMatch().getSpectatorLocation());
                    }
                    this.cancel();
                }

                player.setCompassTarget(target.getLocation());
            }
        }.runTaskTimerAsynchronously(RealSkywars.getPlugin(), 5L, 10L);
    }

    public enum PlayerItems {LOBBY, CAGE, SETUP, SPECTATOR}

}
