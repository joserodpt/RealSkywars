package joserodpt.realskywars.nms;

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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

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
