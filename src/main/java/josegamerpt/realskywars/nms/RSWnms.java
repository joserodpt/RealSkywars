package josegamerpt.realskywars.nms;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public interface RSWnms {

    void chestAnimation(Chest chest, boolean open);
    String getItemName(ItemStack itemStack);
}
