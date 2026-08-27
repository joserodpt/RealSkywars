package joserodpt.realskywars.api.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerVersionUtil {

    //matches "1.18.2" and the newer "26.2" scheme
    private static final Pattern VERSION = Pattern.compile("\\d+(?:\\.\\d+)*");
    //Bukkit#getVersion() looks like "... (MC: 1.18.2)"
    private static final Pattern MC_IN_VERSION = Pattern.compile("\\(MC:\\s*([^)]+)\\)");

    private static String simpleVersion;

    /**
     * The versioned CraftBukkit package segment, such as "v1_16_R3". Empty on
     * 1.20.5 and newer, where Paper and Spigot stopped relocating the package
     * per version. Only useful for the legacy net.minecraft.server.<v> classes.
     */
    public static String getServerVersion() {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        int index = pkg.indexOf("org.bukkit.craftbukkit");
        if (index < 0) {
            return "";
        }

        String remainder = pkg.substring(index + "org.bukkit.craftbukkit".length());
        return remainder.startsWith(".") ? remainder.substring(1) : "";
    }

    /**
     * The Minecraft version the server is running, such as "1.18.2" or "26.2".
     * <p>
     * Derived from the Bukkit API version rather than the CraftBukkit package
     * name, which is unversioned on modern servers. Never throws: an
     * unrecognisable version returns "unknown" so the caller can fall back to a
     * default adapter instead of failing to enable.
     */
    public static String getSimpleServerVersion() {
        if (simpleVersion != null) {
            return simpleVersion;
        }

        simpleVersion = resolveVersion();
        return simpleVersion;
    }

    private static String resolveVersion() {
        //"1.18.2-R0.1-SNAPSHOT" or "26.2-R0.1-SNAPSHOT"
        String bukkitVersion = Bukkit.getBukkitVersion();
        if (bukkitVersion != null) {
            String candidate = bukkitVersion.split("-")[0];
            if (VERSION.matcher(candidate).matches()) {
                return candidate;
            }
        }

        //"git-Paper-196 (MC: 1.18.2)"
        String serverVersion = Bukkit.getVersion();
        if (serverVersion != null) {
            Matcher matcher = MC_IN_VERSION.matcher(serverVersion);
            if (matcher.find()) {
                String candidate = matcher.group(1).trim();
                if (VERSION.matcher(candidate).matches()) {
                    return candidate;
                }
            }
        }

        Bukkit.getLogger().warning("[RealSkywars] Could not determine the Minecraft version from \""
                + bukkitVersion + "\" / \"" + serverVersion + "\".");
        return "unknown";
    }

    /**
     * The major version number, or -1 when unknown. 1.18.2 gives 1, while the
     * newer 26.2 scheme gives 26.
     */
    public static int getMajorVersion() {
        String[] parts = getSimpleServerVersion().split("\\.");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
