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
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.database.PlayerData;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.player.RSWGameLog;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager extends PlayerManagerAPI {
    private final RealSkywarsAPI rs;

    public PlayerManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    public List<UUID> teleporting = new ArrayList<>();
    private final HashMap<Player, Player> trackingPlayers = new HashMap<>();
    private final List<RSWPlayer> players = new ArrayList<>();

    @Override
    public void giveItems(Player p, Items i) {
        if (p != null) {
            p.getInventory().clear();
            RSWPlayer pg = this.getPlayer(p);
            switch (i) {
                case LOBBY:
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Profile"), getItem(pg, Items.PROFILE));
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Maps"), getItem(pg, Items.MAPS));
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Shop"), getItem(pg, Items.SHOP));
                    break;
                case CAGE:
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Kit"), getItem(pg, Items.KIT));
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Vote"), getItem(pg, Items.VOTE));
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Leave"), getItem(pg, Items.LEAVE));
                    break;
                case SPECTATOR:
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Spectate"), getItem(pg, Items.SPECTATE));
                    if (pg.getState() != RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
                        p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Play-Again"), getItem(pg, Items.PLAYAGAIN));
                    }
                    if (RSWConfig.file().getBoolean("Config.Spectator-Shop")) {
                        p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Shop"), getItem(pg, Items.SHOP));
                    }
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Leave"), getItem(pg, Items.LEAVE));
                    break;
                case SETUP:
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest1"), getItem(pg, Items.CHEST1));
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Cage"), getItem(pg, Items.CAGESET));
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest2"), getItem(pg, Items.CHEST2));
                    break;
                default:
                    rs.getLogger().warning(i.name() + " not registered in PlayerManager");
                    break;
            }
        }
    }

    @Override
    public ItemStack getItem(RSWPlayer p, Items i) {
        switch (i) {
            case KIT:
                return Itens.createItem(Material.BOW, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_KIT_NAME, false));
            case PROFILE:
                return Itens.createItem(Material.BOOK, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_PROFILE_NAME, false));
            case CAGESET:
                return Itens.createItem(Material.BEACON, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_CAGESET_NAME, false));
            case MAPS:
                return Itens.createItem(Material.NETHER_STAR, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_MAPS_NAME, false));
            case SHOP:
                return Itens.createItem(Material.EMERALD, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_SHOP_NAME, false));
            case LEAVE:
                return Itens.createItem(Material.MINECART, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_LEAVE_NAME, false));
            case VOTE:
                return Itens.createItem(Material.HOPPER, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_VOTE_NAME, false));
            case SPECTATE:
                return Itens.createItem(Material.MAP, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_SPECTATE_NAME, false));
            case PLAYAGAIN:
                return Itens.createItem(Material.TOTEM_OF_UNDYING, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_PLAYAGAIN_NAME, false));
            case CHEST1:
                return Itens.createItem(Material.CHEST, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_CHEST1_NAME, false));
            case CHEST2:
                return Itens.createItem(Material.CHEST, 1, rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ITEM_CHEST2_NAME, false));
        }
        return new ItemStack(Material.STICK);
    }

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
            gp.heal();

            rs.getPlayerManagerAPI().addPlayer(gp);

            if (rs.getGameManagerAPI().tpLobbyOnJoin()) {
                rs.getGameManagerAPI().tpToLobby(gp);
            }
            Bukkit.getOnlinePlayers().forEach(player -> gp.getTab().add(player));
            gp.getTab().updateRoomTAB();

            rs.getPlayerManagerAPI().getPlayers().stream()
                    .filter(RSWPlayer::isInMatch)
                    .forEach(player -> {
                        RSWPlayer.RoomTAB rt = player.getTab();
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
        return this.players.stream()
                .filter(g -> g.getPlayer() == p)
                .findFirst()
                .orElse(null);
    }

    @Override
    public RSWPlayer getPlayer(UUID u) {
        return this.players.stream()
                .filter(g -> g.getUUID().equals(u))
                .findFirst()
                .orElse(null);
    }


    @Override
    public void savePlayer(RSWPlayer p, RSWPlayer.PlayerData pd) {
        if (p.getPlayer() != null) {
            PlayerData playerData = rs.getDatabaseManagerAPI().getPlayerData(p.getPlayer());

            switch (pd) {
                case BOUGHT_ITEMS:
                    playerData.setBoughtItems(p.getBoughtItems());
                    break;
                case CAGE_BLOCK:
                    playerData.setCageBlock(((Material) p.getProperty(RSWPlayer.PlayerProperties.CAGE_BLOCK)).name());
                    break;
                case COINS:
                    playerData.setCoins(rs.getCurrencyAdapter().getCoins(p));
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
                    playerData.setMapViewerPref(p.getMapViewerPref().name());
                    break;
            }

            rs.getDatabaseManagerAPI().savePlayerData(playerData, true);
        }
    }

    @Override
    public void setLanguage(RSWPlayer player, String s) {
        player.setProperty(RSWPlayer.PlayerProperties.LANGUAGE, s);
        player.sendMessage(rs.getLanguageManagerAPI().getString(player, LanguageManagerAPI.TS.LANGUAGE_SET, true).replace("%language%", s));
        player.closeInventory();
    }

    @Override
    public void loadPlayers() {
        this.players.clear();
        Bukkit.getOnlinePlayers().forEach(this::loadPlayer);
    }

    @Override
    public List<RSWShopDisplayItem> getBoughtItems(RSWPlayer player, ShopManager.Categories t) {
        List<RSWShopDisplayItem> bought = rs.getShopManagerAPI().getCategoryContents(player, t).stream()
                .filter(a -> a != null && a.isBought())
                .collect(Collectors.toList());

        if (bought.isEmpty()) {
            bought.add(new RSWShopDisplayItem());
        }
        return bought;
    }

    @Override
    public int getPlayingPlayers(GamesManager.GameModes pt) {
        return rs.getGameManagerAPI().getGames(pt).stream().mapToInt(RSWMap::getPlayerCount).sum();
    }

    @Override
    public void stopScoreboards() {
        players.forEach(gamePlayer -> gamePlayer.getScoreboard().stop());
    }

    @Override
    public List<RSWPlayer> getPlayers() {
        return players;
    }

    @Override
    public void addPlayer(RSWPlayer rswPlayer) {
        players.add(rswPlayer);
    }

    @Override
    public void removePlayer(RSWPlayer rswPlayer) {
        players.remove(rswPlayer);
    }

    @Override
    public void trackPlayer(RSWPlayer gp) {
        List<RSWPlayer> tmp = new ArrayList<>(gp.getMatch().getPlayers());
        tmp.remove(gp);

        Optional<RSWPlayer> search = tmp.stream().filter(c -> c.getState().equals(RSWPlayer.PlayerState.PLAYING)).findAny();
        if (!search.isPresent() || search.get().isBot()) {
            gp.sendMessage(rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.NO_TRACKER, true));
            return;
        }

        Player player = gp.getPlayer();
        Player target = search.get().getPlayer();

        //Credit GITHUB PlayerCompass
        trackingPlayers.put(player, target);
        gp.sendMessage(rs.getLanguageManagerAPI().getString(gp, LanguageManagerAPI.TS.TRACK_FOUND, true).replace("%player%", target.getDisplayName()));

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
