package pt.josegamerpt.realskywars.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.TL;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.managers.GameManager;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.utils.Text;

public class PlayerScoreboard {

    public GamePlayer linked;
    public BukkitTask task;

    public PlayerScoreboard(GamePlayer r) {
        linked = r;
        run();
    }

    public void stop()
    {
        task.cancel();
    }

    public void run() {
        task = new BukkitRunnable() {
            public void run() {
                ArrayList<String> lista = new ArrayList<>();
                String tit = "";
                if (linked.state != null) {
                    switch (linked.state) {
                        case LOBBY_OR_NOGAME:
                            if (!GameManager.lobbyscoreboard) {
                                return;
                            }
                            lista = LanguageManager.getList(linked, TL.SCOREBOARD_LOBBY_LINES);
                            tit = LanguageManager.getString(linked, TS.SCOREBOARD_LOBBY_TITLE, false);
                            break;
                        case CAGE:
                            lista = LanguageManager.getList(linked, TL.SCOREBOARD_CAGE_LINES);
                            tit = LanguageManager.getString(linked, TS.SCOREBOARD_CAGE_TITLE, false).replace("%map%", linked.room.getName());
                            break;
                        case SPECTATOR:
                            lista = LanguageManager.getList(linked, TL.SCOREBOARD_SPECTATOR_LINES);
                            tit = LanguageManager.getString(linked, TS.SCOREBOARD_SPECTATOR_TITLE, false).replace("%map%", linked.room.getName());
                            break;
                        case EXTERNAL_SPECTATOR:
                            lista = LanguageManager.getList(linked, TL.SCOREBOARD_SPECTATOR_LINES);
                            tit = LanguageManager.getString(linked, TS.SCOREBOARD_SPECTATOR_TITLE, false).replace("%map%", linked.room.getName());
                            break;
                        case PLAYING:
                            lista = LanguageManager.getList(linked, TL.SCOREBOARD_PLAYING_LINES);
                            tit = LanguageManager.getString(linked, TS.SCOREBOARD_PLAYING_TITLE, false).replace("%map%", linked.room.getName());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + linked.state);
                    }
                    Map<String, Integer> linhas = new HashMap<String, Integer>();

                    int linha = lista.size();
                    for (String s : lista) {
                        linhas.put(variables(s, linked), linha--);
                    }
                    displayScoreboard(tit, linked, linhas);
                }
            }
        }.runTaskTimer(RealSkywars.pl, 0L, (long) 20);
    }

    protected static String variables(String s, GamePlayer gp) {
        if (gp.room != null) {
            return s.replace("%space%", Text.makeSpace()).replace("%players%", gp.room.getPlayersCount() + "")
                    .replace("%spectators%", gp.room.getSpectatorsCount() + "").replace("%kills%", gp.gamekills + "")
                    .replace("%map%", gp.room.getName()).replace("%runtime%", gp.room.getTimePassed() + "").replace("%state%", GameManager.getStateString(gp, gp.room.getState())).replace("%mode%", gp.room.getMode().name()).replace("%wins%", gp.wins + "");
        } else {
            return s.replace("%space%", Text.makeSpace()).replace("%coins%", gp.coins + "")
                    .replace("%kills%", gp.totalkills + "").replace("%deaths%", gp.deaths + "").replace("%playing%", "" + PlayerManager.countPlayingPlayers()).replace("%wins%", gp.wins + "");
        }
    }

    private void displayScoreboard(String title, GamePlayer p, Map<String, Integer> elements) {
        if (p.p != null) {
            if (title.length() > 32) {
                title = title.substring(0, 32);
            }
            while (elements.size() > 15) {
                String minimumKey = (String) elements.keySet().toArray()[0];
                int minimum = elements.get(minimumKey);
                for (String string : elements.keySet()) {
                    if (elements.get(string) < minimum
                            || (elements.get(string) == minimum && string.compareTo(minimumKey) < 0)) {
                        minimumKey = string;
                        minimum = elements.get(string);
                    }
                }
                elements.remove(minimumKey);
            }
            for (String string2 : new ArrayList<String>(elements.keySet())) {
                if (string2 != null && string2.length() > 40) {
                    int value = elements.get(string2);
                    elements.remove(string2);
                    elements.put(string2.substring(0, 40), value);
                }
            }
            if (p.p.getScoreboard() == null
                    || p.p.getScoreboard().getObjective(p.p.getUniqueId().toString().substring(0, 16)) == null) {
                p.p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                p.p.getScoreboard().registerNewObjective(p.p.getUniqueId().toString().substring(0, 16), "dummy",
                        "dummy");
                p.p.getScoreboard().getObjective(p.p.getUniqueId().toString().substring(0, 16))
                        .setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            p.p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);
            for (String string2 : elements.keySet()) {
                if (p.p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string2).getScore() != elements
                        .get(string2)) {
                    p.p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string2)
                            .setScore((int) elements.get(string2));
                }
            }
            for (String string2 : new ArrayList<String>(p.p.getScoreboard().getEntries())) {
                if (!elements.keySet().contains(string2)) {
                    p.p.getScoreboard().resetScores(string2);
                }
            }
        }
    }
}