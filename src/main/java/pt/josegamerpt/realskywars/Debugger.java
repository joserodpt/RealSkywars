package pt.josegamerpt.realskywars;

import org.bukkit.Bukkit;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.managers.GameManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.utils.Text;

public class Debugger {
	public static int debug = 1;
	public static int debugTask = 0;

	public static void print(String b) {
		if (debug == 1) {
			System.out.print(RealSkywars.getPrefix() + "[DEBUG] " + b);
		}
	}

	public static void execute() {
		if (debugTask == 1) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, new Runnable() {
				@Override
				public void run() {
					/*for (GameRoom g : GameManager.rooms) {
						print("ROOM: " + g.getName() + " | PLAYERS: " + g.getPlayersCount() + " |MAXPlayers " + g.getMaxPlayers() + " | ONTHISROOM: " + g.getPlayersInCount() + " | SPECS: " + g.getSpectatorsCount() + " | BDIZE" + g.getWorld().getWorldBorder().getSize());
					}
					 */
					PlayerManager.players.forEach(gamePlayer -> print(String.join(", ", Text.entryToList(gamePlayer.getInfoList()))));
				}
			}, 20, 20);
		}
	}
}
