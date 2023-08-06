package josegamerpt.realskywars.nms;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
public interface RSWnms {
    void playChestAnimation(Block block, boolean open);
    String getItemName(ItemStack itemStack);
}