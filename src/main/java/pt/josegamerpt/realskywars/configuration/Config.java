package pt.josegamerpt.realskywars.configuration;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import pt.josegamerpt.realskywars.RealSkywars;

public class Config implements Listener {

	private static File file;
	private static FileConfiguration customFile;
	private static final String name = "config.yml";

	public static void setup(Plugin p) {
		file = new File(p.getDataFolder(), name);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
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
			RealSkywars.print("Couldn't save " + name + "!");
		}
	}

	public static void reload() {
		customFile = YamlConfiguration.loadConfiguration(file);
	}
}