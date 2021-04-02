package josegamerpt.realskywars.nms;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public interface SWnms {

    void chestAnimation(Chest chest, boolean open);
    String getItemName(ItemStack itemStack);
}
