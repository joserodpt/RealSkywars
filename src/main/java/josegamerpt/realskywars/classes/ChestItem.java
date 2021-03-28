package josegamerpt.realskywars.classes;

import org.bukkit.inventory.ItemStack;

public class ChestItem {

    private int id;
    private ItemStack i;
    private int percentage;

    public ChestItem(int i, int per, ItemStack a) {
        this.id = i;
        this.i = a;
        this.percentage = per;
    }

    public ItemStack getItemStack() {
        return this.i;
    }
}
