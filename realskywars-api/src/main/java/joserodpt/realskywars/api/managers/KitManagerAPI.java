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

import joserodpt.realskywars.api.database.PlayerBoughtItemsRow;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.shop.RSWBuyableItem;

import java.util.Collection;

public abstract class KitManagerAPI {
    public abstract void loadKits();

    public abstract void registerKit(RSWKit k);

    public abstract void unregisterKit(RSWKit k);

    public abstract Collection<RSWKit> getKits();

    public abstract Collection<RSWBuyableItem> getKitsAsBuyables();

    public abstract RSWKit getKit(String string);

    public abstract RSWKit getKit(PlayerBoughtItemsRow playerBoughtItemsRow);
}
