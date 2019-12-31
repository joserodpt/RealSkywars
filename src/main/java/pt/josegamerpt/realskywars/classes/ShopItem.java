package pt.josegamerpt.realskywars.classes;

import org.bukkit.inventory.ItemStack;

public class ShopItem {

	public int id;
	public ItemStack i;
	public Double price;
	public Boolean bought = false;
	public String name;
	public String permission;
	public Boolean buyable;

	public ShopItem(ItemStack a, int id, Double per, Boolean b, String n, String perm) {
		this.id = id;
		this.i = a;
		this.price = per;
		this.bought = b;
		this.name = n;
		this.permission = perm;
		this.buyable = true;
	}
	
	public ShopItem(ItemStack a, int id, Boolean buy)
	{
		this.id = id;
		this.i = a;
		this.buyable = buy;
	}
}
