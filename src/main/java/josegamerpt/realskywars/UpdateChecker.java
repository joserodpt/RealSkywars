package josegamerpt.realskywars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
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
            try (final InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); final Scanner ignored = new Scanner(inputStream)) {
                consumer.accept(new BufferedReader(new InputStreamReader(inputStream)).readLine());
            } catch (final IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}