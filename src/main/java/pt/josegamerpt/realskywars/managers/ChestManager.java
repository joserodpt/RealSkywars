package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.classes.ChestItem;
import pt.josegamerpt.realskywars.classes.Enum.TierType;
import pt.josegamerpt.realskywars.configuration.Chests;
import pt.josegamerpt.realskywars.utils.Itens;
import pt.josegamerpt.realskywars.utils.Text;

public class ChestManager {

	static Random r = new Random();
	public static int count = 0;

	public static ArrayList<ChestItem> seeTier(TierType t) {
		ArrayList<ChestItem> loot = new ArrayList<ChestItem>();

		if (t == TierType.BASIC) {
			for (String string : Chests.file().getConfigurationSection("Loot.Basic").getKeys(false)) {
				ItemStack item = Chests.file().getItemStack("Loot.Basic." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.Basic." + string + ".Chance");
				// loot.add(item);
				ChestItem e = new ChestItem(Integer.parseInt(string), f, Itens.addLore(item, Arrays.asList(Text.addColor("&fChance Percentage: &b" + f + "%"),
						Text.addColor("&fID: &b" + string),Text.addColor("&fClick to &9edit &fthe chance."))));
				loot.add(e);
			}
		}
		if (t == TierType.NORMAL) {
			for (String string : Chests.file().getConfigurationSection("Loot.Normal").getKeys(false)) {
				ItemStack item = Chests.file().getItemStack("Loot.Normal." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.Normal." + string + ".Chance");
				// loot.add(item);
				ChestItem e = new ChestItem(Integer.valueOf(string), f, Itens.addLore(item, Arrays.asList(Text.addColor("&fChance Percentage: &b" + f + "%"),
						Text.addColor("&fID: &b" + string), Text.addColor("&fClick to &9edit &fthe chance."))));
				loot.add(e);
			}
		}
		if (t == TierType.OP) {
			for (String string : Chests.file().getConfigurationSection("Loot.OP").getKeys(false)) {
				ItemStack item = Chests.file().getItemStack("Loot.OP." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.OP." + string + ".Chance");
				// loot.add(item);
				ChestItem e = new ChestItem(Integer.valueOf(string), f, Itens.addLore(item, Arrays.asList(Text.addColor("&fChance Percentage: &b" + f + "%"),
						Text.addColor("&fID: &b" + string),Text.addColor("&fClick to &9edit &fthe chance."))));
				loot.add(e);
			}
		}
		if (t == TierType.CAOS) {
			for (String string : Chests.file().getConfigurationSection("Loot.CAOS").getKeys(false)) {
				ItemStack item = Chests.file().getItemStack("Loot.CAOS." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.CAOS." + string + ".Chance");
				// loot.add(item);
				ChestItem e = new ChestItem(Integer.valueOf(string), f, Itens.addLore(item, Arrays.asList(Text.addColor("&fChance Percentage: &b" + f + "%"),
						Text.addColor("&fID: &b" + string),Text.addColor("&fClick to &9edit &fthe chance."))));
				loot.add(e);
			}
		}
		return loot;
	}

	public static ArrayList<ItemStack> putInChest(TierType t) {
		ArrayList<ItemStack> loot = new ArrayList<>();

		if (t == TierType.BASIC) {
			for (String string : Chests.file().getConfigurationSection("Loot.Basic").getKeys(false)) {
				ItemStack item = Chests.file().getItemStack("Loot.Basic." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.Basic." + string + ".Chance");
				Float flot = (float) f / 100.0f;
				addToLoot(loot, item, flot);
			}
		}
		if (t == TierType.NORMAL) {
			for (String string : Chests.file().getConfigurationSection("Loot.Normal").getKeys(false)) {

				ItemStack item = Chests.file().getItemStack("Loot.Normal." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.Normal." + string + ".Chance");
				Float flot = (float) f / 100.0f;
				addToLoot(loot, item, flot);
			}
		}
		if (t == TierType.OP) {
			for (String string : Chests.file().getConfigurationSection("Loot.OP").getKeys(false)) {

				ItemStack item = Chests.file().getItemStack("Loot.OP." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.OP." + string + ".Chance");
				Float flot = (float) f / 100.0f;
				addToLoot(loot, item, flot);
			}
		}
		if (t == TierType.CAOS) {
			for (String string : Chests.file().getConfigurationSection("Loot.CAOS").getKeys(false)) {

				ItemStack item = Chests.file().getItemStack("Loot.CAOS." + string + ".ItemStack");
				int f = Chests.file().getInt("Loot.CAOS." + string + ".Chance");
				float flot = (float) f / 100.0f;
				addToLoot(loot, item, flot);
			}
		}
		return loot;
	}

	private static void addToLoot(ArrayList<ItemStack> l, ItemStack item, float c) {
		float chance = r.nextFloat();

		if (chance <= c)
			l.add(item);
	}

	public static void setContents(ArrayList<ItemStack> f, String string) {
		int i = 1;
		for (ItemStack item : f) {
			if (item != null) {
				int CHANCE = 50;
				if (string.equalsIgnoreCase(TierType.BASIC.name())) {
					Chests.file().set("Loot.Basic." + i + ".ItemStack", item);
					Chests.file().set("Loot.Basic." + i + ".Chance", CHANCE);
				}
				if (string.equalsIgnoreCase(TierType.NORMAL.name())) {
					Chests.file().set("Loot.Normal." + i + ".ItemStack", item);
					Chests.file().set("Loot.Normal." + i + ".Chance", CHANCE);
				}
				if (string.equalsIgnoreCase(TierType.OP.name())) {
					Chests.file().set("Loot.OP." + i + ".ItemStack", item);
					Chests.file().set("Loot.OP." + i + ".Chance", CHANCE);
				}
				if (string.equalsIgnoreCase(TierType.CAOS.name())) {
					Chests.file().set("Loot.CAOS." + i + ".ItemStack", item);
					Chests.file().set("Loot.CAOS." + i + ".Chance", CHANCE);
				}
			}
			i++;
		}
		Chests.save();
	}

	public static void savePercentage(TierType t, int slot, int parsed) {
		if (t == TierType.BASIC) {
			Chests.file().set("Loot.Basic." + slot + ".Chance", parsed);
		}
		if (t == TierType.NORMAL) {
			Chests.file().set("Loot.Normal." + slot + ".Chance", parsed);
		}
		if (t == TierType.OP) {
			Chests.file().set("Loot.OP." + slot + ".Chance", parsed);
		}
		if (t == TierType.CAOS) {
			Chests.file().set("Loot.CAOS." + slot + ".Chance", parsed);
		}
		Chests.save();
	}

	public static void addContents(ArrayList<ItemStack> f, String string) {
		int i;
		int CHANCE = 50;
		if (string.equalsIgnoreCase(TierType.BASIC.name())) {
			i = Chests.file().getConfigurationSection("Loot.Basic").getKeys(false).size();

			for (ItemStack item : f) {
				i++;
				Chests.file().set("Loot.Basic." + i + ".ItemStack", item);
				Chests.file().set("Loot.Basic." + i + ".Chance", CHANCE);
			}
		}
		if (string.equalsIgnoreCase(TierType.NORMAL.name())) {
			i = Chests.file().getConfigurationSection("Loot.Normal").getKeys(false).size();

			for (ItemStack item : f) {
				i++;
				Chests.file().set("Loot.Normal." + i + ".ItemStack", item);
				Chests.file().set("Loot.Normal." + i + ".Chance", CHANCE);
			}
		}
		if (string.equalsIgnoreCase(TierType.OP.name())) {
			i = Chests.file().getConfigurationSection("Loot.OP").getKeys(false).size();

			for (ItemStack item : f) {
				i++;
				Chests.file().set("Loot.OP." + i + ".ItemStack", item);
				Chests.file().set("Loot.OP." + i + ".Chance", CHANCE);
			}
		}
		if (string.equalsIgnoreCase(TierType.CAOS.name())) {
			i = Chests.file().getConfigurationSection("Loot.CAOS").getKeys(false).size();

			for (ItemStack item : f) {
				i++;
				Chests.file().set("Loot.CAOS." + i + ".ItemStack", item);
				Chests.file().set("Loot.CAOS." + i + ".Chance", CHANCE);
			}
		}
		Chests.save();
	}
}
