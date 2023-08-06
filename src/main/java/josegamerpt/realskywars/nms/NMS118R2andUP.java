package josegamerpt.realskywars.nms;

import org.apache.commons.lang.WordUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class NMS118R2andUP implements RSWnms {

    @Override
    public void playChestAnimation(Block block, boolean open) {
        final Chest chest = (Chest) block.getState();
        if (open) {
            chest.open();
        } else {
            chest.close();
        }

        chest.update();
    }
    @Override
    public String getItemName(ItemStack itemStack) {
        return WordUtils.capitalizeFully(itemStack.getType().name().replace("_", " "));
    }
}
