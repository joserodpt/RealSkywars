package josegamerpt.realskywars.nms;

import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.LocaleLanguage;
import net.minecraft.server.v1_16_R2.TileEntityChest;
import net.minecraft.server.v1_16_R2.World;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMS116R2 implements SWnms {

    @Override
    public void chestAnimation(Chest chest, boolean open) {
            Location location = chest.getLocation();
            World world = ((CraftWorld) location.getWorld()).getHandle();
            BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
            TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.getBlock().getBlock(), 1, open ? 1 : 0);
    }

    @Override
    public String getItemName(ItemStack itemStack) {
        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        return LocaleLanguage.a().a(nmsStack.getItem().getName());

    }
}
