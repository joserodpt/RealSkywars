package joserodpt.realskywars.api.utils;

import org.bukkit.Bukkit;

public final class ServerVersionUtil {

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.",
                "");
    }

    public static String getSimpleServerVersion() {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.",
                "");

        Bukkit.getLogger().info("Detected server version: " + version);

        try {
            int primaryVersion = Integer.parseInt(version.split(".")[0]);
            if (primaryVersion >= 26) {
                Bukkit.getLogger().info("Detected server version: " + version + " (using new versioning system)");
                // new minecraft numbering versioning system (26.x, 27.x, etc)
                Bukkit.getLogger().info("Using " + version.split(".")[0] + " as server version for NMS.");
                return Bukkit.getServer().getVersion().split("-")[0];
            } else {
                Bukkit.getLogger().info("Detected server version: " + version + " (using old versioning system)");
                return version;
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to parse server version.");
            throw e;
        }
    }
}