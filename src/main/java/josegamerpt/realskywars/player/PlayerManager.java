package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.database.PlayerData;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.misc.DisplayItem;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerManager {
    private RealSkywars rs;
    public PlayerManager(RealSkywars rs) {
        this.rs = rs;
    }

    public static ArrayList<UUID> teleporting = new ArrayList<>();
    private final HashMap<Player, Player> trackingPlayers = new HashMap<>();
    private final ArrayList<RSWPlayer> players = new ArrayList<>();

    public void giveItems(Player p, Items i) {
        if (p != null) {
            p.getInventory().clear();
            RSWPlayer pg = this.getPlayer(p);
            switch (i) {
                case LOBBY:
                    p.getInventory().setItem(0, getItem(pg, Items.PROFILE));
                    p.getInventory().setItem(4, getItem(pg, Items.MAPS));
                    p.getInventory().setItem(8, getItem(pg, Items.SHOP));
                    break;
                case CAGE:
                    p.getInventory().setItem(1, getItem(pg, Items.KIT));
                    p.getInventory().setItem(4, getItem(pg, Items.VOTE));
                    p.getInventory().setItem(7, getItem(pg, Items.LEAVE));
                    break;
                case SPECTATOR:
                    p.getInventory().setItem(1, getItem(pg, Items.SPECTATE));
                    if (pg.getState() != RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
                        p.getInventory().setItem(2, getItem(pg, Items.PLAYAGAIN));
                    }
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
                return Itens.createItem(Material.BOW, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_KIT_NAME, false));
            case PROFILE:
                return Itens.createItem(Material.BOOK, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_PROFILE_NAME, false));
            case CAGESET:
                return Itens.createItem(Material.BEACON, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CAGESET_NAME, false));
            case MAPS:
                return Itens.createItem(Material.NETHER_STAR, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_MAPS_NAME, false));
            case SHOP:
                return Itens.createItem(Material.EMERALD, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_SHOP_NAME, false));
            case LEAVE:
                return Itens.createItem(Material.MINECART, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_LEAVE_NAME, false));
            case VOTE:
                return Itens.createItem(Material.HOPPER, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_VOTE_NAME, false));
            case SPECTATE:
                return Itens.createItem(Material.MAP, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_SPECTATE_NAME, false));
            case PLAYAGAIN:
                return Itens.createItem(Material.TOTEM_OF_UNDYING, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_PLAYAGAIN_NAME, false));
            case CHEST1:
                return Itens.createItem(Material.CHEST, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CHEST1_NAME, false));
            case CHEST2:
                return Itens.createItem(Material.CHEST, 1, rs.getLanguageManager().getString(p, LanguageManager.TS.ITEM_CHEST2_NAME, false));
        }
        return new ItemStack(Material.STICK);
    }

    public void loadPlayer(Player p) {
        try {
            PlayerData playerData = rs.getDatabaseManager().getPlayerData(p);

            RSWPlayer gp = new RSWPlayer(p, RSWPlayer.PlayerState.LOBBY_OR_NOGAME, playerData.getKills(), playerData.getDeaths(), playerData.getStats_wins_solo(), playerData.getStats_wins_teams(), playerData.getCoins(), playerData.getLanguage(), playerData.getBought_items(), playerData.getLoses(), playerData.getGames_played(), playerData.getRanked_kills(), playerData.getRanked_deaths(), playerData.getStats_wins_ranked_solo(), playerData.getStats_wins_ranked_teams(), playerData.getLoses_ranked(), playerData.getRanked_games_played(), this.processGamesList(playerData.getGames_list()));

            String mapv = playerData.getMapViewerPref();
            if (mapv != null) {
                gp.setProperty(RSWPlayer.PlayerProperties.MAPVIEWER_PREF, mapv);
            }
            String cageBlock = playerData.getCageMaterial();
            if (cageBlock != null) {
                gp.setProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK, Material.getMaterial(cageBlock));
            }
            gp.heal();

            rs.getPlayerManager().addPlayer(gp);

            if (rs.getGameManager().tpLobbyOnJoin()) {
                rs.getGameManager().tpToLobby(gp);
            }
            Bukkit.getOnlinePlayers().forEach(player -> gp.getTab().add(player));
            gp.getTab().updateRoomTAB();

            for (RSWPlayer player : rs.getPlayerManager().getPlayers()) {
                if (player.isInMatch()) {
                    RSWPlayer.RoomTAB rt = player.getTab();
                    rt.remove(p);
                    rt.updateRoomTAB();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<RSWGameLog> processGamesList(String s) {
        ArrayList<RSWGameLog> tmp = new ArrayList<>();
        if (s != null) {
            String[] split = s.split("/");
            //max size is 140 (5 pages)
            int max = Math.min(split.length, 140);
            for (int i = 0; i < max; ++i) {
                String obj = split[i];
                //mapa-modo-ranked-jogadores-ganhou-tempo-dia
                String[] data = obj.split(";");
                if (data.length == 7) {
                    String mapa = data[0];
                    SWGameMode.Mode mode = SWGameMode.Mode.valueOf(data[1]);
                    boolean ranked = Boolean.parseBoolean(data[2]);
                    int jogadores = Integer.parseInt(data[3]);
                    boolean win = Boolean.parseBoolean(data[4]);
                    int seconds = Integer.parseInt(data[5]);
                    String dayandtime = data[6];
                    tmp.add(0, new RSWGameLog(mapa, mode, ranked, jogadores, win, seconds, dayandtime));

                }
            }
        }
        return tmp;
    }

    private String processGamesListSave(ArrayList<RSWGameLog> gamesList) {
        ArrayList<String> join = new ArrayList<>();
        gamesList.forEach(rswGameLog -> join.add(0, rswGameLog.getSerializedData()));
        return Strings.join(join, '/');
    }

    public RSWPlayer getPlayer(Player p) {
        for (RSWPlayer g : this.players) {
            if (g.getPlayer() == p) {
                return g;
            }
        }
        return null;
    }

    public RSWPlayer getPlayer(UUID u) {
        for (RSWPlayer g : this.players) {
            if (g.getUUID() == u) {
                return g;
            }
        }
        return null;
    }

    public void savePlayer(RSWPlayer p) {
        if (p.getPlayer() != null) {
            PlayerData playerData = rs.getDatabaseManager().getPlayerData(p.getPlayer());

            playerData.setName(p.getName());

            playerData.setLanguage(p.getLanguage());

            playerData.setCoins(p.getCoins());

            playerData.setMapViewerPref(p.getMapViewerPref().name());

            playerData.setCageBlock(((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name());

            playerData.setWinsSolo(p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, false), false);
            playerData.setWinsSolo(p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, true), true);

            playerData.setWinsTeams(p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, false), false);
            playerData.setWinsTeams(p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, true), true);

            playerData.setKills(p.getStatistics(RSWPlayer.PlayerStatistics.KILLS, false), false);
            playerData.setKills(p.getStatistics(RSWPlayer.PlayerStatistics.KILLS, true), true);

            playerData.setDeaths(p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, false), false);
            playerData.setDeaths(p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, true), true);

            playerData.setLoses(p.getStatistics(RSWPlayer.PlayerStatistics.LOSES, false), false);
            playerData.setLoses(p.getStatistics(RSWPlayer.PlayerStatistics.LOSES, true), true);

            playerData.setGamesPlayed(p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, false), false);
            playerData.setGamesPlayed(p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, true), true);

            playerData.setBoughtItems(p.getBoughtItems());

            playerData.setGames_list(processGamesListSave(p.getGamesList()));

            rs.getDatabaseManager().savePlayerData(playerData, true);
        }
    }

    public void setLanguage(RSWPlayer player, String s) {
        player.setProperty(RSWPlayer.PlayerProperties.LANGUAGE, s);
        player.sendMessage(rs.getLanguageManager().getString(player, LanguageManager.TS.LANGUAGE_SET, true).replace("%language%", "" + s));
    }

    public Boolean boughtItem(RSWPlayer p, String string, ShopManager.Categories c) {
        return p.getBoughtItems().contains(Text.strip(string + "|" + c.name()));
    }

    public void loadPlayers() {
        this.players.clear();
        Bukkit.getOnlinePlayers().forEach(this::loadPlayer);
    }

    public List<DisplayItem> getBoughtItems(RSWPlayer player, ShopManager.Categories t) {
        List<DisplayItem> bought = new ArrayList<>();

        for (DisplayItem a : rs.getShopManager().getCategoryContents(player, t)) {
            if (a != null && a.isBought()) {
                bought.add(a);
            }
        }

        if (bought.isEmpty()) {
            bought.add(new DisplayItem());
        }
        return bought;
    }

    public int getPlayingPlayers(Modes pt) {
        return rs.getGameManager().getGames(pt).stream().mapToInt(SWGameMode::getPlayerCount).sum();
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
            gp.sendMessage(rs.getLanguageManager().getString(gp, LanguageManager.TS.NO_TRACKER, true));
            return;
        }

        Player player = gp.getPlayer();
        Player target = search.get().getPlayer();

        //Credit GITHUB PlayerCompass
        trackingPlayers.put(player, target);
        gp.sendMessage(rs.getLanguageManager().getString(gp, LanguageManager.TS.TRACK_FOUND, true).replace("%player%", target.getDisplayName()));

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

    public enum Modes {SOLO, SOLO_RANKED, TEAMS, TEAMS_RANKED, RANKED, ALL}

    public enum Items {LOBBY, CAGE, SETUP, SPECTATOR, PROFILE, CAGESET, MAPS, SHOP, LEAVE, VOTE, SPECTATE, KIT, PLAYAGAIN, CHEST1, CHEST2}
}
