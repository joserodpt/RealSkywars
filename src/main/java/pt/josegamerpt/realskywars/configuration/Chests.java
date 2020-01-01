package pt.josegamerpt.realskywars.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import pt.josegamerpt.realskywars.RealSkywars;

public class Chests implements Listener {

	private static File file;
	private static FileConfiguration customFile;
	private static String name = "chests.yml";

	public static void setup(Plugin p) {
		file = new File(p.getDataFolder(), name);

		if (!file.exists()) {
				RealSkywars.pl.saveResource("chests.yml", true);
		}
		customFile = YamlConfiguration.loadConfiguration(file);

        Chests.file().options().header("Note: All chances are on percentages, for example, 50 = 50%");
        Chests.save();
	}

	public static FileConfiguration file() {
		return customFile;
	}

	public static void save() {
		try {
			customFile.save(file);
		} catch (IOException e) {
			RealSkywars.print("Couldn't save " + name + "!");
		}
	}

	public static void reload() {
		customFile = YamlConfiguration.loadConfiguration(file);
	}

}