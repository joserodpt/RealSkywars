package joserodpt.realskywars.api.player;

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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import fr.mrmicky.fastboard.FastBoard;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.utils.Text;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

public class RSWPlayerScoreboard {

    private FastBoard fb = null;
    private final RSWPlayer p;
    private BukkitTask task;

    public RSWPlayerScoreboard(RSWPlayer r) {
        this.p = r;
        try {
            this.fb = new FastBoard(r.getPlayer());
            if (RealSkywarsAPI.getInstance().getLobbyManagerAPI().getLobbyLocation() != null) {
                this.run();
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not create scoreboard for player " + r.getName() + " - " + e.getMessage());
        }
    }

    protected String variables(String s, RSWPlayer p) {
        String tmp;

        if (p.isInMatch()) {
            tmp = s.replace("%space%", Text.makeSpace()).replace("%players%", p.getMatch().getPlayerCount() + "").replace("%maxplayers%", p.getMatch().getMaxPlayers() + "").replace("%time%", p.getMatch().getTimePassed() + "").replace("%nextevent%", nextEvent(p.getMatch())).replace("%spectators%", p.getMatch().getSpectatorsCount() + "").replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS) + "").replace("%map%", p.getMatch().getName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%runtime%", Text.formatSeconds(p.getMatch().getTimePassed())).replace("%state%", p.getMatch().getState().getDisplayName(p)).replace("%mode%", p.getMatch().getGameMode().getDisplayName(p)).replace("%solowins%", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO) + "").replace("%teamwins%", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS) + "").replace("%loses%", p.getStatistics(RSWPlayer.PlayerStatistics.LOSES) + "").replace("%gamesplayed%", p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED) + "");
        } else {
            tmp = s.replace("%space%", Text.makeSpace()).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapterAPI().getCoins(p) + "").replace("%playing%", "" + RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.ALL)).replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.KILLS, false) + "").replace("%deaths%", p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, false) + "").replace("%solowins%", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, false) + "").replace("%teamwins%", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, false) + "").replace("%loses%", p.getStatistics(RSWPlayer.PlayerStatistics.LOSES, false) + "").replace("%gamesplayed%", p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED) + "").replace("%playing%", "" + RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.ALL)).replace("%rankedkills%", p.getStatistics(RSWPlayer.PlayerStatistics.KILLS, true) + "").replace("%rankeddeaths%", p.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, true) + "").replace("%rankedsolowins%", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, true) + "").replace("%rankedteamwins%", p.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, true) + "").replace("%rankedloses%", p.getStatistics(RSWPlayer.PlayerStatistics.LOSES, true) + "").replace("%rankedgamesplayed%", p.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, true) + "");
        }

        if (RSWConfig.file().getBoolean("Config.PlaceholderAPI-In-Scoreboard")) {
            tmp = PlaceholderAPI.setPlaceholders(p.getPlayer(), tmp);
        }

        return tmp;
    }

    private String nextEvent(RSWMap match) {
        return match.getEvents().isEmpty() ? "-" : match.getEvents().get(0).getName();
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.fb.delete();
    }

    public void run() {
        this.task = new BukkitRunnable() {
            public void run() {
                List<String> lista;
                String tit;
                if (p.getState() != null) {
                    switch (p.getState()) {
                        case LOBBY_OR_NOGAME:
                            if (!RealSkywarsAPI.getInstance().getLobbyManagerAPI().scoreboardInLobby() || !RealSkywarsAPI.getInstance().getLobbyManagerAPI().isInLobby(p.getWorld())) {
                                return;
                            }
                            lista = TranslatableList.SCOREBOARD_LOBBY_LINES.get(p);
                            tit = TranslatableLine.SCOREBOARD_LOBBY_TITLE.get(p);
                            break;
                        case CAGE:
                            lista = TranslatableList.SCOREBOARD_CAGE_LINES.get(p);
                            tit = TranslatableLine.SCOREBOARD_CAGE_TITLE.get(p).replace("%map%", p.getMatch().getName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%mode%", p.getMatch().getGameMode().name());
                            break;
                        case SPECTATOR:
                        case EXTERNAL_SPECTATOR:
                            lista = TranslatableList.SCOREBOARD_SPECTATOR_LINES.get(p);
                            tit = TranslatableLine.SCOREBOARD_SPECTATOR_TITLE.get(p).replace("%map%", p.getMatch().getName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%mode%", p.getMatch().getGameMode().name());
                            break;
                        case PLAYING:
                            lista = TranslatableList.SCOREBOARD_PLAYING_LINES.get(p);
                            tit = TranslatableLine.SCOREBOARD_PLAYING_TITLE.get(p).replace("%map%", p.getMatch().getName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%mode%", p.getMatch().getGameMode().name());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value SCOREBOARD!!! : " + p.getState());
                    }

                    List<String> send = lista.stream()
                            .map(s -> variables(s, p))
                            .collect(Collectors.toList());
                    displayScoreboard(variables(tit, p), send);
                }
            }
        }.runTaskTimer(RealSkywarsAPI.getInstance().getPlugin(), 0L, 20);
    }

    private void displayScoreboard(String title, List<String> elements) {
        this.fb.updateTitle(title);
        this.fb.updateLines(elements);
    }
}
