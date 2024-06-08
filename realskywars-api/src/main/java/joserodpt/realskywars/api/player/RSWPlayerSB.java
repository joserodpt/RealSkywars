package joserodpt.realskywars.api.player;

import fr.mrmicky.fastboard.FastBoard;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.GamesManagerAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.utils.Text;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

//Player Scoreboard
public class RSWPlayerSB {

    private FastBoard fb = null;
    private final RSWPlayer p;
    private BukkitTask task;

    public RSWPlayerSB(RSWPlayer r) {
        this.p = r;
        try {
            this.fb = new FastBoard(r.getPlayer());
            if (RealSkywarsAPI.getInstance().getGameManagerAPI().getLobbyLocation() != null) {
                this.run();
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not create scoreboard for player " + r.getName() + " - " + e.getMessage());
        }
    }

    protected String variables(String s, RSWPlayer gp) {
        String tmp;

        if (gp.isInMatch()) {
            tmp = s.replace("%space%", Text.makeSpace()).replace("%players%", gp.getMatch().getPlayerCount() + "").replace("%maxplayers%", gp.getMatch().getMaxPlayers() + "").replace("%time%", gp.getMatch().getTimePassed() + "").replace("%nextevent%", nextEvent(gp.getMatch())).replace("%spectators%", gp.getMatch().getSpectatorsCount() + "").replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS, gp.getMatch().isRanked()) + "").replace("%map%", gp.getMatch().getMapName()).replace("%displayname%", gp.getMatch().getDisplayName()).replace("%runtime%", Text.formatSeconds(gp.getMatch().getTimePassed())).replace("%state%", RealSkywarsAPI.getInstance().getGameManagerAPI().getStateString(gp, gp.getMatch().getState())).replace("%mode%", gp.getMatch().getGameMode().name()).replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, gp.getMatch().isRanked()) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, gp.getMatch().isRanked()) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, gp.getMatch().isRanked()) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, gp.getMatch().isRanked()) + "");
        } else {
            tmp = s.replace("%space%", Text.makeSpace()).replace("%coins%", RealSkywarsAPI.getInstance().getCurrencyAdapter().getCoins(gp) + "").replace("%playing%", "" + RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayingPlayers(GamesManagerAPI.GameModes.ALL)).replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.KILLS, false) + "").replace("%deaths%", gp.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, false) + "").replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, false) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, false) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, false) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, false) + "").replace("%playing%", "" + RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayingPlayers(GamesManagerAPI.GameModes.ALL)).replace("%rankedkills%", gp.getStatistics(RSWPlayer.PlayerStatistics.KILLS, true) + "").replace("%rankeddeaths%", gp.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, true) + "").replace("%rankedsolowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, true) + "").replace("%rankedteamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, true) + "").replace("%rankedloses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, true) + "").replace("%rankedgamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, true) + "");
        }

        if (RSWConfig.file().getBoolean("Config.PlaceholderAPI-In-Scoreboard")) {
            tmp = PlaceholderAPI.setPlaceholders(gp.getPlayer(), tmp);
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
                            if (!RealSkywarsAPI.getInstance().getGameManagerAPI().scoreboardInLobby() || !RealSkywarsAPI.getInstance().getGameManagerAPI().isInLobby(p.getWorld())) {
                                return;
                            }
                            lista = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.SCOREBOARD_LOBBY_LINES);
                            tit = TranslatableLine.SCOREBOARD_LOBBY_TITLE.get(p);
                            break;
                        case CAGE:
                            lista = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.SCOREBOARD_CAGE_LINES);
                            tit = TranslatableLine.SCOREBOARD_CAGE_TITLE.get(p).replace("%map%", p.getMatch().getMapName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%mode%", p.getMatch().getGameMode().name());
                            break;
                        case SPECTATOR:
                        case EXTERNAL_SPECTATOR:
                            lista = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.SCOREBOARD_SPECTATOR_LINES);
                            tit = TranslatableLine.SCOREBOARD_SPECTATOR_TITLE.get(p).replace("%map%", p.getMatch().getMapName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%mode%", p.getMatch().getGameMode().name());
                            break;
                        case PLAYING:
                            lista = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.SCOREBOARD_PLAYING_LINES);
                            tit = TranslatableLine.SCOREBOARD_PLAYING_TITLE.get(p).replace("%map%", p.getMatch().getMapName()).replace("%displayname%", p.getMatch().getDisplayName()).replace("%mode%", p.getMatch().getGameMode().name());
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
