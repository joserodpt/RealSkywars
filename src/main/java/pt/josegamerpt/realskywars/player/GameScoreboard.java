package pt.josegamerpt.realskywars.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.TL;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.utils.Text;

public class GameScoreboard {

	public GameRoom linked;

	public GameScoreboard(GameRoom r) {
		linked = r;
	}

	public void run() {
		new BukkitRunnable() {
			public void run() {
				for (GamePlayer p : linked.getGamePlayers()) {
					if (p.p != null) {
						Map<String, Integer> linhas = new HashMap<String, Integer>();

						ArrayList<String> lista = LanguageManager.getList(p, TL.SCOREBOARD_ARENA_LINES);

						int linha = lista.size();
						for (String s : lista) {
							linhas.put(variables(s, p), linha--);
						}
						displayScoreboard(p, linhas);
					}
				}
			}
		}.runTaskTimer(RealSkywars.pl, 0L, (long) 20);
	}

	private void displayScoreboard(GamePlayer p, final Map<String, Integer> elements) {
		if (p.p != null) {
			String title = variables(LanguageManager.getString(p, TS.SCOREBOARD_ARENA_TITLE, false), p);
			if (title.length() > 32) {
				title = title.substring(0, 32);
			}
			while (elements.size() > 15) {
				String minimumKey = (String) elements.keySet().toArray()[0];
				int minimum = elements.get(minimumKey);
				for (final String string : elements.keySet()) {
					if (elements.get(string) < minimum
							|| (elements.get(string) == minimum && string.compareTo(minimumKey) < 0)) {
						minimumKey = string;
						minimum = elements.get(string);
					}
				}
				elements.remove(minimumKey);
			}
			for (final String string2 : new ArrayList<String>(elements.keySet())) {
				if (string2 != null && string2.length() > 40) {
					final int value = elements.get(string2);
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
			for (final String string2 : new ArrayList<String>(p.p.getScoreboard().getEntries())) {
				if (!elements.keySet().contains(string2)) {
					p.p.getScoreboard().resetScores(string2);
				}
			}
		}
	}

	protected static String variables(String s, GamePlayer gp) {
		return s.replace("%space%", Text.makeSpace()).replace("%players%", gp.room.getCurrentPlayers() + "")
				.replace("%spectators%", gp.room.getCurrentSpectators() + "").replace("%kills%", gp.GameKills + "")
				.replace("%map%", gp.room.getName()).replace("%runtime%", gp.room.getTimePassed() + "");
	}
}
