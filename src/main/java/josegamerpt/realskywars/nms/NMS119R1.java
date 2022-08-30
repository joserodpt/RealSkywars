package josegamerpt.realskywars.nms;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftChest;
import org.bukkit.inventory.ItemStack;

public class NMS119R1 implements RSWnms {

    @Override
    public void playChestAnimation(Block block, boolean open) {
        Location location = block.getLocation();
        WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        if (!(block.getState() instanceof Chest))
            return;
        Chest chest = (Chest) block.getState();
        world.a(position, ((CraftChest) chest).getHandle(), 1, open ? 1 : 0);
    }

    @Override
    public String getItemName(ItemStack itemStack) {
        return WordUtils.capitalizeFully(itemStack.getType().name().replace("_", " "));
    }
}
