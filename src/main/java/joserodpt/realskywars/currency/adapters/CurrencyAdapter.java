package joserodpt.realskywars.currency.adapters;

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

import joserodpt.realskywars.player.RSWPlayer;

public interface CurrencyAdapter {
    void transferCoins(RSWPlayer toPlayer, RSWPlayer fromPLayer, double amount);
    void addCoins(RSWPlayer p, double amount);
    boolean removeCoins(RSWPlayer p, double amount);
    void setCoins(RSWPlayer p, double amount);
    double getCoins(RSWPlayer p);
}
