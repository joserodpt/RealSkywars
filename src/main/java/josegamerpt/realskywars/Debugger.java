package josegamerpt.realskywars;

import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.modes.Teams;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Debugger {
	public static Boolean debug = false;

	public static void print(Class a, String b) {
		if (debug) {
			Bukkit.getLogger().log(Level.WARNING, "[RSW,DEBUG] " + getName(a) +" : " + b);
		}
	}

	public static void print(Class a, String b, Level l) {
		if (debug) {
			Bukkit.getLogger().log(l, "[RSW,DEBUG] " + getName(a) +" : " + b);
		}
	}

	static String getName(Class a) {
		Class<?> enclosingClass = a.getEnclosingClass();
		if (enclosingClass != null) {
			return enclosingClass.getName();
		} else {
			return a.getName();
		}
	}

	public static void execute() {
		if (false) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.getPlugin(), () -> {

				for (SWGameMode room : GameManager.getRooms()) {
					if (room.getMode().equals(Enum.GameType.TEAMS))
					{
						print(Debugger.class, ((Teams) room).getAliveTeams() + "");
					}
				}

			}, 20, 20);
		}
	}
}
