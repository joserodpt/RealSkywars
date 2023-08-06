package josegamerpt.realskywars.configuration;

import josegamerpt.realskywars.RealSkywars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Languages implements Listener {

    private static String name = "languages.yml";
    private static File file;
    private static FileConfiguration customFile;

    public static void setup(Plugin p) {
        file = new File(p.getDataFolder(), name);

        if (!file.exists()) {
            RealSkywars.getPlugin().saveResource(name, true);
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration file() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            RealSkywars.getPlugin().log("Couldn't save " + name + "!");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}