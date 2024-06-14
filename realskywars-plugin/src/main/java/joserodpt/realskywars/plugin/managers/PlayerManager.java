package joserodpt.realskywars.plugin.managers;

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
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.database.PlayerData;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWGameLog;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerTab;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager extends PlayerManagerAPI {
    private final RealSkywarsAPI rs;

    public PlayerManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    public List<UUID> teleporting = new ArrayList<>();
    private final Map<Player, Player> trackingPlayers = new HashMap<>();
    private final Map<UUID, RSWPlayer> players = new HashMap<>();

    @Override
    public RSWPlayer loadPlayer(Player p) {
        try {
            PlayerData playerData = rs.getDatabaseManagerAPI().getPlayerData(p);

            RSWPlayer gp = new RSWPlayer(p, RSWPlayer.PlayerState.LOBBY_OR_NOGAME, playerData.getKills(), playerData.getDeaths(), playerData.getStats_wins_solo(), playerData.getStats_wins_teams(), playerData.getCoins(), playerData.getLanguage(), playerData.getBought_items(), playerData.getLoses(), playerData.getGames_played(), playerData.getRanked_kills(), playerData.getRanked_deaths(), playerData.getStats_wins_ranked_solo(), playerData.getStats_wins_ranked_teams(), playerData.getLoses_ranked(), playerData.getRanked_games_played(), this.processGamesList(playerData.getGames_list()));

            String mapv = playerData.getMapViewerPref();
            if (mapv != null) {
                gp.setProperty(RSWPlayer.PlayerProperties.MAPVIEWER_PREF, mapv);
            }
            String cageBlock = playerData.getCageMaterial();
            if (cageBlock != null) {
                gp.setProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK, Material.getMaterial(cageBlock));
            }

            String choosenKit = playerData.getChoosen_kit();
            if (choosenKit == null || choosenKit.isEmpty()) {
                choosenKit = "none";
            }

            if (!choosenKit.equalsIgnoreCase("none")) {
                gp.setProperty(RSWPlayer.PlayerProperties.KIT, rs.getKitManagerAPI().getKit(choosenKit));
            }

            gp.heal();

            rs.getPlayerManagerAPI().addPlayer(gp);

            if (rs.getLobbyManagerAPI().tpLobbyOnJoin()) {
                rs.getLobbyManagerAPI().tpToLobby(gp);
            }
            Bukkit.getOnlinePlayers().forEach(player -> gp.getTab().add(player));
            gp.getTab().updateRoomTAB();

            rs.getPlayerManagerAPI().getPlayers().stream()
                    .filter(RSWPlayer::isInMatch)
                    .forEach(player -> {
                        RSWPlayerTab rt = player.getTab();
                        rt.remove(p);
                        rt.updateRoomTAB();
                    });

            return gp;
        } catch (Exception e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Error while loading player data for " + p.getName() + " ->" + e.getMessage());
        }
        return null;
    }

    @Override
    protected List<RSWGameLog> processGamesList(String s) {
        List<RSWGameLog> tmp = new ArrayList<>();
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
                    RSWMap.Mode mode = RSWMap.Mode.valueOf(data[1]);
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

    @Override
    protected String processGamesListSave(List<RSWGameLog> gamesList) {
        return gamesList.stream()
                .map(RSWGameLog::getSerializedData)
                .collect(Collectors.joining("/"));
    }

    @Override
    public RSWPlayer getPlayer(Player p) {
        return this.players.get(p.getUniqueId());
    }

    @Override
    public void savePlayer(RSWPlayer p, RSWPlayer.PlayerData pd) {
        if (p.getPlayer() != null) {
            PlayerData playerData = rs.getDatabaseManagerAPI().getPlayerData(p.getPlayer());

            switch (pd) {
                case KIT:
                    playerData.setKit(p.getPlayerKit().getName());
                    break;
                case BOUGHT_ITEMS:
                    playerData.setBoughtItems(p.getBoughtItems());
                    break;
                case CAGE_BLOCK:
                    playerData.setCageBlock(((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name());
                    break;
                case COINS:
                    playerData.setCoins(rs.getCurrencyAdapterAPI().getCoins(p));
                    break;
                case GAME:
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

                    playerData.setGames_list(processGamesListSave(p.getGamesList()));
                    break;
                case LANG:
                    playerData.setLanguage(p.getLanguage());
                    break;
                case MAPVIEWER_PREF:
                    playerData.setMapViewerPref(p.getPlayerMapViewerPref().name());
                    break;
            }

            rs.getDatabaseManagerAPI().savePlayerData(playerData, true);
        }
    }

    @Override
    public void setLanguage(RSWPlayer player, String s) {
        player.setProperty(RSWPlayer.PlayerProperties.LANGUAGE, s);
        player.sendMessage(TranslatableLine.LANGUAGE_SET.get(player, true).replace("%language%", s));
        player.closeInventory();
    }

    @Override
    public void loadPlayers() {
        this.players.clear();
        Bukkit.getOnlinePlayers().forEach(this::loadPlayer);
    }

    @Override
    public List<RSWShopDisplayItem> getBoughtItems(RSWPlayer player, ShopManagerAPI.ShopCategory t) {
        List<RSWShopDisplayItem> bought = rs.getShopManagerAPI().getCategoryContents(player, t).stream()
                .filter(a -> a != null && a.isBought())
                .collect(Collectors.toList());

        if (bought.isEmpty()) {
            bought.add(new RSWShopDisplayItem());
        }
        return bought;
    }

    @Override
    public int getPlayingPlayers(MapManagerAPI.MapGamemodes pt) {
        return rs.getMapManagerAPI().getMaps(pt).stream().mapToInt(RSWMap::getPlayerCount).sum();
    }

    @Override
    public void stopScoreboards() {
        players.values().forEach(gamePlayer -> gamePlayer.getScoreboard().stop());
    }

    @Override
    public Collection<RSWPlayer> getPlayers() {
        return players.values();
    }

    @Override
    public void addPlayer(RSWPlayer rswPlayer) {
        players.put(rswPlayer.getUUID(), rswPlayer);
    }

    @Override
    public void removePlayer(RSWPlayer rswPlayer) {
        players.remove(rswPlayer.getUUID());
    }

    @Override
    public void trackPlayer(RSWPlayer gp) {
        List<RSWPlayer> tmp = new ArrayList<>(gp.getMatch().getPlayers());
        tmp.remove(gp);

        Optional<RSWPlayer> search = tmp.stream().filter(c -> c.getState().equals(RSWPlayer.PlayerState.PLAYING)).findAny();
        if (search.isEmpty() || search.get().isBot()) {
            TranslatableLine.NO_TRACKER.send(gp, true);
            return;
        }

        Player player = gp.getPlayer();
        Player target = search.get().getPlayer();

        //Credit GITHUB PlayerCompass
        trackingPlayers.put(player, target);
        gp.sendMessage(TranslatableLine.TRACK_FOUND.get(gp, true).replace("%player%", target.getDisplayName()));

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
        }.runTaskTimerAsynchronously(rs.getPlugin(), 5L, 30L);
    }

    @Override
    public List<UUID> getTeleporting() {
        return teleporting;
    }

}
