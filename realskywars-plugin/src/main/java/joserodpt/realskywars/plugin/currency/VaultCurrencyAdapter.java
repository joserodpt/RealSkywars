package joserodpt.realskywars.plugin.currency;

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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */


import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.currency.CurrencyAdapterAPI;
import joserodpt.realskywars.api.player.RSWPlayer;

public class VaultCurrencyAdapter implements CurrencyAdapterAPI {

    @Override
    public void transferCoins(RSWPlayer toPlayer, RSWPlayer fromPlayer, double amount) {
        removeCoins(fromPlayer, amount);
        addCoins(toPlayer, amount);
    }

    @Override
    public void addCoins(RSWPlayer p, double amount) {
        RealSkywarsAPI.getInstance().getVaultEconomy().depositPlayer(p.getPlayer(), amount);
    }

    @Override
    public boolean removeCoins(RSWPlayer p, double amount) {
        return RealSkywarsAPI.getInstance().getVaultEconomy().withdrawPlayer(p.getPlayer(), amount).transactionSuccess();
    }

    @Override
    public void setCoins(RSWPlayer p, double amount) {
        RealSkywarsAPI.getInstance().getVaultEconomy().withdrawPlayer(p.getPlayer(), getCoins(p));
        RealSkywarsAPI.getInstance().getVaultEconomy().depositPlayer(p.getPlayer(), amount);
    }

    @Override
    public double getCoins(RSWPlayer p) {
        return RealSkywarsAPI.getInstance().getVaultEconomy().getBalance(p.getPlayer());
    }
}
