package josegamerpt.realskywars.nms;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

import josegamerpt.realskywars.RealSkywars;

public class ReflectionHelper
{
    public static Class<?> getNMSClass(final String str) {
        return getClass("net.minecraft.server." + RealSkywars.getPlugin().getServerVersion() + "." + str);
    }
    
    public static Class<?> getCraftClass(final String str) {
        return getClass("org.bukkit.craftbukkit." + RealSkywars.getPlugin().getServerVersion() + "." + str);
    }
    
    public static Class<?> getClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (Exception ex) {
            RealSkywars.getPlugin().severe("Error while executing reflection (getClass) nms.");
            RealSkywars.getPlugin().severe(ex.toString());
            return null;
        }
    }
}