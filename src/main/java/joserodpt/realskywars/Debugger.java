package joserodpt.realskywars;

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

import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.logging.Level;

public class Debugger {
    public static Boolean debug = false;

    public static void printerr(Class a, String b) {
        print(Level.SEVERE, a, b);
    }

    public static void print(Class a, String b) {
        print(Level.WARNING, a, b);
    }

    private static void print(Level l, Class a, String b) {
        if (debug) {
            Bukkit.getLogger().log(l, "[RSW:DEBUG] " + getName(a).replace("josegamerpt.realskywars.", "") + " > " + b);
        }
    }

    static String getName(Class a) {
        Class<?> enclosingClass = a.getEnclosingClass();
        return Objects.requireNonNullElse(enclosingClass, a).getName();
    }

    public static void execute() {

    }
}
