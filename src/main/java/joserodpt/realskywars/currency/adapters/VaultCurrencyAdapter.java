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
import joserodpt.realskywars.player.RSWPlayer;

public class VaultCurrencyAdapter implements CurrencyAdapter {

    @Override
    public void transferCoins(RSWPlayer toPlayer, RSWPlayer fromPlayer, double amount) {
        removeCoins(fromPlayer, amount);
        addCoins(toPlayer, amount);
    }

    @Override
    public void addCoins(RSWPlayer p, double amount) { RealSkywars.getVaultEconomy().depositPlayer(p.getPlayer(), amount); }

    @Override
    public boolean removeCoins(RSWPlayer p, double amount) { return RealSkywars.getVaultEconomy().withdrawPlayer(p.getPlayer(), amount).transactionSuccess(); }

    @Override
    public void setCoins(RSWPlayer p, double amount) { RealSkywars.getVaultEconomy().withdrawPlayer(p.getPlayer(), getCoins(p)); RealSkywars.getVaultEconomy().depositPlayer(p.getPlayer(), amount); }

    @Override
    public double getCoins(RSWPlayer p) { return RealSkywars.getVaultEconomy().getBalance(p.getPlayer()); }
}
