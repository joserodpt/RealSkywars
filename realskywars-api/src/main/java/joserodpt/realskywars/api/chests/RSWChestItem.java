package joserodpt.realskywars.api.chests;

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

import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RSWChestItem {

    private final ItemStack itemstack;
    private int chance;
    public RSWChestItem(ItemStack i, int chance) {
        this.itemstack = i;
        this.chance = chance;
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }

    public int getChance() {
        return this.chance;
    }

    public ItemStack getDisplayItemStack() {
        return Itens.addLore(this.getItemStack(), Arrays.asList("&fChance: &b" + this.chance + "%", "&7Click here to change the percentage."));
    }

    public void setChance(int val) {
        this.chance = val;
    }

    @Override
    public String toString() {
        return "SWChestItem{" +
                "itemstack=" + itemstack +
                ", chance=" + chance +
                '}';
    }
}
