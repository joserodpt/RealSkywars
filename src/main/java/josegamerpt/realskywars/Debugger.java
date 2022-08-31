package josegamerpt.realskywars;

import josegamerpt.realskywars.player.PlayerManager;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Debugger {
    public static Boolean debug = false;

    public static void print(Class a, String b) {
        if (debug) {
            Bukkit.getLogger().log(Level.WARNING, "[RSW:DEBUG] " + getName(a) + " : " + b);
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
            Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.getPlugin(), () -> RealSkywars.getPlayerManager().getPlayers().forEach(rswPlayer -> print(PlayerManager.class, rswPlayer.getState().name())), 20, 20);
        }
    }
}
