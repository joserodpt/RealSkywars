package josegamerpt.realskywars.chests;

import josegamerpt.realskywars.utils.Itens;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SWChestItem {

    private final ItemStack itemstack;
    private final int chance;
    private final String header;

    public SWChestItem(ItemStack i, int chance, String header) {
        this.itemstack = i;
        this.chance = chance;
        this.header = header;
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }

    public int getChance() {
        return this.chance;
    }

    public ItemStack getDisplayItemStack() {
        return Itens.addLore(this.getItemStack(), Arrays.asList("&f&lChance: &b&l" + this.chance + "%", "&fClick here to change the percentage."));
    }

    public String getHeader() {
        return this.header;
    }
}
