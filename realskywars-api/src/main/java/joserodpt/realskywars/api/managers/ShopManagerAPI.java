package joserodpt.realskywars.api.managers;

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

import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;

import java.util.Collection;

public abstract class ShopManagerAPI {
    public abstract Collection<RSWBuyableItem> getCategoryContents(RSWBuyableItem.ItemCategory t);

    public abstract Collection<RSWBuyableItem> getBoughtItems(RSWBuyableItem.ItemCategory t, RSWPlayer p);

    public abstract void loadShopItems();
}
