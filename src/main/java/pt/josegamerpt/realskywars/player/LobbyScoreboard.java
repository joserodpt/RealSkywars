package pt.josegamerpt.realskywars.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.Enum.TL;
import pt.josegamerpt.realskywars.managers.GameManager;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.utils.Text;

public class LobbyScoreboard {

	public static void update() {
		if (GameManager.lobby == null) {
			System.out.println("[RealSkyWars] (ERROR) Cannot detect the lobby world.");
			return;
		}
		if (GameManager.lobbyscoreboard == true) {
			new BukkitRunnable() {
				public void run() {
					for (GamePlayer p : PlayerManager.players) {
						if (p.p != null) {
							if (p.p.getLocation().getWorld() == GameManager.lobby.getWorld()) {
								Map<String, Integer> linhas = new HashMap<String, Integer>();

								ArrayList<String> lista = LanguageManager.getList(p, TL.SCOREBOARD_LINES);

								int linha = lista.size();
								for (String s : lista) {
									linhas.put(variables(s, p), linha--);
								}
								displayScoreboard(p, linhas);

							} else {
								if (GameManager.getRoomWorlds().contains(p.p.getLocation().getWorld()) != true) {
									Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

									Objective o = board.registerNewObjective("c", "a", "b");

									o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6"));
									o.setDisplaySlot(DisplaySlot.SIDEBAR);

									p.p.setScoreboard(board);
								}
							}
						}
					}
				}
			}.runTaskTimer(RealSkywars.pl, 0L, (long) 5);
		}
	}

	protected static String variables(String s, GamePlayer gp) {
		return s.replace("%space%", Text.makeSpace()).replace("%coins%", gp.Coins + "")
				.replace("%kills%", gp.TotalKills + "").replace("%deaths%", gp.Deaths + "");
	}

	@SuppressWarnings("deprecation")
	public static void displayScoreboard(GamePlayer p, final Map<String, Integer> elements) {
		String title = variables(LanguageManager.getString(p, TS.SCOREBOARD_TITLE, false), p);
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
			p.p.getScoreboard().registerNewObjective(p.p.getUniqueId().toString().substring(0, 16), "dummy");
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
