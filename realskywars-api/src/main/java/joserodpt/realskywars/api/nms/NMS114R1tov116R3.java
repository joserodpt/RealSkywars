package joserodpt.realskywars.api.nms;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMS114R1tov116R3 implements RSWnms {
    private final Class<?> world = ReflectionHelper.getNMSClass("World");
    private final Class<?> craft_world = ReflectionHelper.getCraftBukkitClass("CraftWorld");
    private final Class<?> block_pos = ReflectionHelper.getNMSClass("BlockPosition");
    private final Class<?> i_block_data = ReflectionHelper.getNMSClass("IBlockData");
    private final Class<?> block_class = ReflectionHelper.getNMSClass("Block");

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
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException ex) {
            RealSkywarsAPI.getInstance().getLogger().severe("Error while executing chest animation nms.");
            RealSkywarsAPI.getInstance().getLogger().severe(ex.toString());
        }
    }

    private final Class<?> craft_item_stack = ReflectionHelper.getCraftBukkitClass("inventory.CraftItemStack");
    private final Class<?> nms_item_stack = ReflectionHelper.getNMSClass("ItemStack");
    private final Class<?> locale_language = ReflectionHelper.getNMSClass("LocaleLanguage");
    private final Class<?> i_chat_base_component = ReflectionHelper.getNMSClass("IChatBaseComponent");
    private final Class<?> chat_serializer = ReflectionHelper.getNMSClass("IChatBaseComponent$ChatSerializer");

    @Override
    public String getItemName(Material mat) {
        try {
            Object nmsStack = craft_item_stack.getMethod("asNMSCopy", ItemStack.class).invoke(null, new ItemStack(mat));
            Object itemName = nms_item_stack.getMethod("getItem").invoke(nmsStack);

            Method getNameMethod = locale_language.getMethod("a", i_chat_base_component);

            Object json = nms_item_stack.getMethod("C").invoke(itemName);
            Object localeLanguageInstance = locale_language.getMethod("a").invoke(null);

            String jsonString = chat_serializer.getMethod("a", i_chat_base_component).invoke(null, json).toString();
            return (String) getNameMethod.invoke(localeLanguageInstance, jsonString);
        } catch (Exception ex) {
            RealSkywarsAPI.getInstance().getLogger().severe("Error while executing getItemName nms.");
            RealSkywarsAPI.getInstance().getLogger().severe(ex.toString());
            return "-";
        }
    }
}
