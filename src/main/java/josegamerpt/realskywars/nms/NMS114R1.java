package josegamerpt.realskywars.nms;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.LocaleLanguage;
import net.minecraft.server.v1_14_R1.TileEntityChest;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMS114R1 implements RSWnms {

    @Override
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
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        return LocaleLanguage.a().a(nmsStack.getItem().getName());
    }
}
