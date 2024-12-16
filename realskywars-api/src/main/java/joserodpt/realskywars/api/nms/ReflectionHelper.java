package joserodpt.realskywars.api.nms;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.Bukkit;

public class ReflectionHelper {
    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

    public static Class<?> getNMSClass(final String str) {
        return getClass("net.minecraft.server." + RealSkywarsAPI.getInstance().getServerVersion() + "." + str);
    }

    public static Class<?> getCraftBukkitClass(final String str) {
        return getClass(CRAFTBUKKIT_PACKAGE + "." + str);
    }

    public static Class<?> getClass(final String className) {
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            RealSkywarsAPI.getInstance().getLogger().severe("Error while executing reflection (getClass) nms.");
            RealSkywarsAPI.getInstance().getLogger().severe(ex.toString());
            return null;
        }
    }
}