package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.classes.Kit;
import pt.josegamerpt.realskywars.configuration.Kits;

public class KitManager {

	private static final ArrayList<Kit> kits = new ArrayList<>();

	public static void loadKits() {
		kits.clear();
		if (Kits.file().getConfigurationSection("Kits") != null) {
			for (String s : Kits.file().getConfigurationSection("Kits").getKeys(false)) {
				int id = Integer.parseInt(s);
				String name = Kits.file().getString("Kits." + id + ".Name");
				Double price = Kits.file().getDouble("Kits." + id + ".Price");
				Material mat = Material.getMaterial(Kits.file().getString("Kits." + id + ".Icon"));
				ItemStack[] k = getKitContents(id);
				String perm = Kits.file().getString("Kits." + id + ".Permission");
				Kit kit = new Kit(id, name, price, mat, k, perm);
				kit.save();
			}
		}
	}

	public static void registerKit(Kit k) {
		Kits.file().set("Kits." + k.id + ".Name", k.name);
		Kits.file().set("Kits." + k.id + ".Price", k.price);
		Kits.file().set("Kits." + k.id + ".Icon", k.icon.name());
		Kits.file().set("Kits." + k.id + ".Contents", k.contents);
		Kits.file().set("Kits." + k.id + ".Permission", k.permission);
		Kits.file().set("Kits." + k.id + ".Perks.DoubleJump", k.doubleJump);
		Kits.file().set("Kits." + k.id + ".Perks.EnderPearl", k.enderPearlGive);
		Kits.save();
	}

	private static ItemStack[] getKitContents(int asd) {
		List<?> list = Kits.file().getList("Kits." + asd + ".Contents");
		ItemStack[] v = list.toArray(new ItemStack[0]);
		return v;
	}

	public static int getNewID() {
		if (Kits.file().getConfigurationSection("Kits") != null) {
			return Kits.file().getConfigurationSection("Kits").getKeys(false).size() + 1;
		} else {
			return 1;
		}
	}

	public static ArrayList<Kit> getKits() {
		return kits;
	}

	public static Kit getKit(int id) {
		for (Kit k : kits) {
			if (k.id == id) {
				return k;
			}
		}
		return null;
	}

	public static void unregisterKit(Kit k) {
		Kits.file().set("Kits." + k.id, null);
		Kits.save();
	}

	public static Kit getKit(String string) {
		for (Kit k : kits) {
			if (k.name.equalsIgnoreCase(string)) {
				return k;
			}
		}
		return null;
	}
}
