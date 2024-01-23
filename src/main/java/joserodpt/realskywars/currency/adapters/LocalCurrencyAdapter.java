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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.player.RSWPlayer;

public class LocalCurrencyAdapter implements CurrencyAdapter {
    @Override
    public void transferCoins(RSWPlayer toPlayer, RSWPlayer fromPlayer, double amount) {
        removeCoins(fromPlayer, amount);
        addCoins(toPlayer, amount);
    }

    @Override
    public void addCoins(RSWPlayer p, double amount) {
        p.setLocalCoins(getCoins(p) + amount);
        p.saveData(RSWPlayer.PlayerData.COINS);
    }

    @Override
    public boolean removeCoins(RSWPlayer p, double amount) {
        if (getCoins(p) >= amount) {
            setCoins(p, getCoins(p) - amount);
            p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.REMOVED_COINS, true).replace("%coins%", "" + amount));
            return true;
        }

        return false;
    }

    @Override
    public void setCoins(RSWPlayer p, double amount) {
        p.setLocalCoins(amount);
        p.saveData(RSWPlayer.PlayerData.COINS);
    }

    @Override
    public double getCoins(RSWPlayer p) {
        return p.getLocalCoins();
    }
}
