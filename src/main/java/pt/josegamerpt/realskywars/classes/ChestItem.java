package pt.josegamerpt.realskywars.classes;

import org.bukkit.inventory.ItemStack;

public class ChestItem {

	public int id;
	public ItemStack i;
	public int percentage;

	public ChestItem(int i, int per,ItemStack a) {
		this.id = i;
		this.i = a;
		this.percentage = per;
	}
}
