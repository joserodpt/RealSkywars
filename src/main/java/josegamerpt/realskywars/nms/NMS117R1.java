package josegamerpt.realskywars.nms;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import josegamerpt.realskywars.RealSkywars;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class NMS117R1 implements RSWnms {
    private final Class<?> world = ReflectionHelper.getClass("net.minecraft.world.level.World");
    private final Class<?> craft_world = ReflectionHelper.getCraftClass("CraftWorld");
    private final Class<?> block_pos = ReflectionHelper.getClass("net.minecraft.core.BlockPosition");
    private final Class<?> i_block_data  = ReflectionHelper.getClass("net.minecraft.world.level.block.state.IBlockData");
    private final Class<?> block_class = ReflectionHelper.getClass("net.minecraft.world.level.block.Block");

    @Override
    public void playChestAnimation(Block block, boolean open) {
        final Location location = block.getLocation();
        try {
            final Object invoke = craft_world.getMethod("getHandle").invoke(location.getWorld());
            assert block_pos != null;
            final Object instance = block_pos.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE).newInstance(location.getX(), location.getY(), location.getZ());
            assert world != null;
            assert i_block_data != null;
            world.getMethod("playBlockAction", block_pos, block_class, Integer.TYPE, Integer.TYPE).invoke(invoke, instance, i_block_data.getMethod("getBlock").invoke(world.getMethod("getType", block_pos).invoke(invoke, instance)), 1, open ? 1 : 0);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            RealSkywars.getPlugin().severe("Error while executing chest animation nms.");
            RealSkywars.getPlugin().severe(ex.toString());
        }
    }

    @Override
    public String getItemName(ItemStack itemStack) {
        return WordUtils.capitalizeFully(itemStack.getType().name().replace("_", " "));
    }
}
