package josegamerpt.realskywars.classes;

import org.bukkit.inventory.ItemStack;

public class SWChestItem {

    private final ItemStack itemstack;
    private final int chance;

    public SWChestItem(ItemStack i, int chance)
    {
        this.itemstack = i;
        this.chance = chance;
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }

    public int getChance()
    {
        return this.chance;
    }
}
