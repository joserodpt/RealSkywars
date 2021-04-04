package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Players;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.misc.DisplayItem;
import josegamerpt.realskywars.misc.Selections;
import josegamerpt.realskywars.misc.Selections.Key;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class PlayerManager {

    private HashMap<Player, Player> trackingPlayers = new HashMap<>();
    private ArrayList<RSWPlayer> players = new ArrayList<>();
    public static ArrayList<UUID> teleporting = new ArrayList<>();

    public void giveItems(Player p, Items i) {
        if (p != null) {
            p.getInventory().clear();
            RSWPlayer pg = RealSkywars.getPlayerManager().getPlayer(p);
            switch (i) {
                case LOBBY:
                    p.getInventory().setItem(0, getItem(pg, Items.PROFILE));
                    p.getInventory().setItem(4, getItem(pg, Items.MAPS));
                    p.getInventory().setItem(8, getItem(pg, Items.SHOP));
                    break;
                case CAGE:
                    p.getInventory().setItem(1, getItem(pg, Items.KIT));
                    p.getInventory().setItem(4, getItem(pg, Items.CHESTS));
                    p.getInventory().setItem(7, getItem(pg, Items.LEAVE));
                    break;
                case SPECTATOR:
                    p.getInventory().setItem(1, getItem(pg, Items.SPECTATE));
                    p.getInventory().setItem(2, getItem(pg, Items.PLAYAGAIN));
                    p.getInventory().setItem(7, getItem(pg, Items.LEAVE));
                    break;
                case SETUP:
                    p.getInventory().setItem(4, getItem(pg, Items.CAGESET));
                    p.getInventory().setItem(0, getItem(pg, Items.CHEST1));
                    p.getInventory().setItem(8, getItem(pg, Items.CHEST2));
                    break;
                default:
                    break;
            }
        }
    }

    public ItemStack getItem(RSWPlayer p, Items i) {
        switch (i) {
            case KIT:
                return Itens.createItem(Material.BOW, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_KIT_NAME, false));
            case PROFILE:
                return Itens.createItem(Material.BOOK, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_PROFILE_NAME, false));
            case CAGESET:
                return Itens.createItem(Material.BEACON, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CAGESET_NAME, false));
            case MAPS:
                return Itens.createItem(Material.NETHER_STAR, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_MAPS_NAME, false));
            case SHOP:
                return Itens.createItem(Material.EMERALD, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_SHOP_NAME, false));
            case LEAVE:
                return Itens.createItem(Material.MINECART, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_LEAVE_NAME, false));
            case CHESTS:
                return Itens.createItem(Material.ENDER_CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CHESTS_NAME, false));
            case SPECTATE:
                return Itens.createItem(Material.MAP, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_SPECTATE_NAME, false));
            case PLAYAGAIN:
                return Itens.createItem(Material.TOTEM_OF_UNDYING, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_PLAYAGAIN_NAME, false));
            case CHEST1:
                return Itens.createItem(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CHEST1_NAME, false));
            case CHEST2:
                return Itens.createItem(Material.CHEST, 1, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CHEST2_NAME, false));
        }
        return new ItemStack(Material.STICK);
    }

    public void loadPlayer(Player p) {
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
                    RealSkywars.getLanguageManager().getDefaultLanguage(), new ArrayList<>(), 0, 0);
            gp.getSelections().put(Selections.Key.MAPVIEWER, Selections.Value.MAPV_ALL);
            gp.save();
            gp.saveData();
        }
        gp.heal();
        if (RealSkywars.getGameManager().tpLobbyOnJoin()) {
            RealSkywars.getGameManager().tpToLobby(gp);
        }
    }

    private Selections.Value getSelection(String mv) {
        return Selections.Value.valueOf(mv);
    }

    public RSWPlayer getPlayer(Player p) {
        for (RSWPlayer g : players) {
            if (g.getPlayer() == p) {
                return g;
            }
        }
        return null;
    }

    public void savePlayer(RSWPlayer p, RSWPlayer.PlayerData d) {
        if (p.getPlayer() != null) {
            if (!Players.file().isConfigurationSection(p.getUUID().toString())) {
                RealSkywars.log("Creating empty player file for " + p.getName() + " UUID: " + p.getUUID().toString());
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
                    Players.file().set(p.getUUID() + ".Name", p.getName());
                    break;
                case LANG:
                    Players.file().set(p.getUUID() + ".Language", p.getLanguage());
                    break;
                case COINS:
                    Players.file().set(p.getUUID() + ".Coins", p.getCoins());
                    break;
                case PREFS:
                    for (Entry<Selections.Key, Selections.Value> entry : p.getSelections().entrySet()) {
                        Selections.Key key = entry.getKey();
                        Selections.Value value = entry.getValue();
                        Players.file().set(p.getUUID() + ".Preferences." + key.name(), value.name());
                    }
                    if (p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK) != null) {
                        Players.file().set(p.getUUID() + ".Preferences.Cage-Material", ((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name());
                    }
                    break;
                case STATS:
                    Players.file().set(p.getUUID() + ".Wins.Solo", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO));
                    Players.file().set(p.getUUID() + ".Wins.Teams", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS));
                    Players.file().set(p.getUUID() + ".Kills", p.getStatistics(RSWPlayer.PlayerStatistics.TOTAL_KILLS));
                    Players.file().set(p.getUUID() + ".Deaths", p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS));
                    Players.file().set(p.getUUID() + ".Loses", p.getStatistics(RSWPlayer.PlayerStatistics.LOSES));
                    Players.file().set(p.getUUID() + ".Games-Played", p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED));
                    break;
                case BOUGHT:
                    Players.file().set(p.getUUID() + ".Bought-Items", p.getBoughtItems());
                    break;
            }
            Players.save();
        }
    }

    public void setLanguage(RSWPlayer player, String s) {
        player.setProperty(RSWPlayer.PlayerProperties.LANGUAGE, s);
        player.sendMessage(RealSkywars.getLanguageManager().getString(player, LanguageManager.TS.LANGUAGE_SET, true).replace("%language%", "" + s));
    }

    public Boolean boughtItem(RSWPlayer p, String string, ShopManager.Categories c) {
        return p.getBoughtItems().contains(ChatColor.stripColor(string + "|" + c.name()));
    }

    public void loadPlayers() {
        this.players.clear();
        Bukkit.getOnlinePlayers().forEach(player -> RealSkywars.getPlayerManager().loadPlayer(player));
    }

    public List<DisplayItem> getBoughtItems(RSWPlayer player, ShopManager.Categories t) {
        List<DisplayItem> bought = new ArrayList<>();

        for (DisplayItem a : RealSkywars.getShopManager().getCategoryContents(player, t)) {
            if (a != null && a.isBought()) {
                bought.add(a);
            }
        }

        if (bought.size() == 0) {
            bought.add(new DisplayItem());
        }
        return bought;
    }

    public int countPlayingPlayers() {
        return RealSkywars.getGameManager().getGames().stream().mapToInt(SWGameMode::getPlayersCount).sum();
    }

    public void stopScoreboards() {
        players.forEach(gamePlayer -> gamePlayer.getScoreboard().stop());
    }

    public ArrayList<RSWPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(RSWPlayer rswPlayer) {
        players.add(rswPlayer);
    }

    public void removePlayer(RSWPlayer rswPlayer) {
        players.remove(rswPlayer);
    }

    public void trackPlayer(RSWPlayer gp) {
        ArrayList<RSWPlayer> tmp = new ArrayList<>(gp.getMatch().getPlayers());
        tmp.remove(gp);

        Optional<RSWPlayer> search = tmp.stream().filter(c -> c.getState().equals(RSWPlayer.PlayerState.PLAYING)).findAny();
        if (!search.isPresent() || search.get().isBot()) {
            gp.sendMessage(RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.NO_TRACKER, true));
            return;
        }

        Player player = gp.getPlayer();
        Player target = search.get().getPlayer();

        //Credit GITHUB PlayerCompass

        trackingPlayers.put(player, target);
        gp.sendMessage(RealSkywars.getLanguageManager().getString(gp, LanguageManager.TS.TRACK_FOUND, true).replace("%player%", target.getDisplayName()));

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
        }.runTaskTimerAsynchronously(RealSkywars.getPlugin(), 5L, 30L);
    }

    public enum Items {LOBBY, CAGE, SETUP, SPECTATOR, PROFILE, CAGESET, MAPS, SHOP, LEAVE, CHESTS, SPECTATE, KIT, PLAYAGAIN, CHEST1, CHEST2}
}
