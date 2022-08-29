package josegamerpt.realskywars.nms;

import org.apache.commons.lang.WordUtils;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class NMS118R2 implements RSWnms {

    @Override
    public void chestAnimation(Chest chest, boolean open) {}

    @Override
    public String getItemName(ItemStack itemStack) {
        return WordUtils.capitalizeFully(itemStack.getType().name().replace("_", " "));
    }
}
