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
 *
 */

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
public interface RSWnms {
    void playChestAnimation(Block block, boolean open);
    String getItemName(ItemStack itemStack);
}