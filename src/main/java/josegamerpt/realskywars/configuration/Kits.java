package josegamerpt.realskywars.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Kits implements Listener {

    private static String name = "kits.yml";
    private static YamlDocument document;

    public static void setup(final JavaPlugin rm) {
        try {
            document = YamlDocument.create(new File(rm.getDataFolder(), name));
        } catch (final IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't setup " + name + "!");
        }
    }

    public static YamlDocument file() {
        return document;
    }

    public static void save() {
        try {
            document.save();
        } catch (final IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save " + name + "!");
        }
    }

    public static void reload() {
        try {
            document.reload();
        } catch (final IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't reload " + name + "!");
        }
    }
}