package joserodpt.realskywars.plugin;

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

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(final JavaPlugin plugin, final int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                final URLConnection connection = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openConnection();
                //without these a stalled connection holds an async thread for good
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                try (final InputStream inputStream = connection.getInputStream();
                     final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    consumer.accept(reader.readLine());
                }
            } catch (final IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    /**
     * Compares dotted versions numerically (1.10 is newer than 1.9). Anything after the first
     * non-digit in a part, like -SNAPSHOT, is ignored.
     */
    public static boolean isNewer(final String remote, final String local) {
        if (remote == null || local == null) {
            return false;
        }
        final String[] r = remote.trim().split("\\.");
        final String[] l = local.trim().split("\\.");
        for (int i = 0; i < Math.max(r.length, l.length); ++i) {
            final int rv = i < r.length ? leadingNumber(r[i]) : 0;
            final int lv = i < l.length ? leadingNumber(l[i]) : 0;
            if (rv != lv) {
                return rv > lv;
            }
        }
        return false;
    }

    private static int leadingNumber(final String s) {
        int end = 0;
        while (end < s.length() && Character.isDigit(s.charAt(end))) {
            ++end;
        }
        if (end == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(s.substring(0, end));
        } catch (final NumberFormatException e) {
            return 0;
        }
    }
}
