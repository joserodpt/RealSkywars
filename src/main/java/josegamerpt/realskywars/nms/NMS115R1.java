package josegamerpt.realskywars.nms;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMS115R1 implements RSWnms {

    public void playChestAnimation(Block block, boolean open) {
        Location location = block.getLocation();
        WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityChest ec = (TileEntityChest) world.getTileEntity(position);
        assert ec != null;
        world.playBlockAction(position, ec.getBlock().getBlock(), 1, open ? 1 : 0);
    }

    @Override
    public String getItemName(ItemStack itemStack) {
        net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        return LocaleLanguage.a().a(nmsStack.getItem().getName());

    }
}
