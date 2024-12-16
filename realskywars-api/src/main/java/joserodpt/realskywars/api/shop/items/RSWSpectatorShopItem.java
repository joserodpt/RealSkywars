package joserodpt.realskywars.api.shop.items;

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

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RSWSpectatorShopItem extends RSWBuyableItem {

    public RSWSpectatorShopItem(String configKey, String displayName, Material material, Double price, String permission) {
        super(configKey, displayName, material, price, permission, ItemCategory.SPEC_SHOP);
    }

    @Override
    public ItemStack getIcon(RSWPlayer p) {
        return Itens.createItem(this.getMaterial(), this.getAmount(), "&f" + this.getAmount() + "x " + this.getDisplayName(), Arrays.asList(TranslatableLine.SHOP_CLICK_2_BUY.get(p).replace("%price%", this.getPriceFormatted()), "", "&a&nF (Swap hand)&r&f to increase the item amount.", "&c&nQ (Drop)&r&f to decrease the item amount."));
    }

}
