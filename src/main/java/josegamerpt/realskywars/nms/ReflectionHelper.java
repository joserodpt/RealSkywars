package josegamerpt.realskywars.nms;

import josegamerpt.realskywars.RealSkywars;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
            Bukkit.getLogger().severe("Error while executing reflection (getClass) for RealSkywars");
            Bukkit.getLogger().severe(ex.toString());
            return null;
        }
    }
}