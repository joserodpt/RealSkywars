package pt.josegamerpt.realskywars;

import org.bukkit.Bukkit;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.managers.GameManager;

public class Debugger {
	public static int debug = 1;

	public static void print(String b) {
		if (debug == 1) {
			System.out.print(RealSkywars.getPrefix() + "[DEBUG] " + b);
		}
	}

	public static void execute() {
		if (debug == 2) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.pl, new Runnable() {
				@Override
				public void run() {
					for (GameRoom g : GameManager.rooms) {
						print("ROOM: " + g.getName() + " | PLAYERS: " + g.getPlayersCount() + " |MAXPlayers " + g.getMaxPlayers() + " | ONTHISROOM: " + g.getPlayersInCount() + " | SPECS: " + g.getSpectatorsCount() + " | BDIZE" + g.getWorld().getWorldBorder().getSize());
					}
				}
			}, 20, 20);
		}
	}
}
