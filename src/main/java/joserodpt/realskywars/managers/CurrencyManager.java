package joserodpt.realskywars.managers;

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
import joserodpt.realskywars.currency.CurrencyAdapter;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.Text;

import java.util.logging.Level;

public class CurrencyManager {
    private RSWPlayer fromPlayer;
    private Double operationQuantity = 0D;
    private final RSWPlayer toPlayer;
    private Boolean console = false;
    private final CurrencyAdapter ca;

    public CurrencyManager(CurrencyAdapter ca, RSWPlayer to, RSWPlayer from, Double coins, Operations op, boolean executeNow) {
        this.ca = ca;
        this.toPlayer = to;
        this.fromPlayer = from;

        if (coins == null) {
            Text.send(to.getPlayer(), RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Invalid amount.");
            return;
        }

        this.operationQuantity = coins;

        if (executeNow) {
            executeOperation(op);
        }
    }

    public CurrencyManager(CurrencyAdapter ca, RSWPlayer to, Double coins, Operations op, boolean executeNow) {
        this.ca = ca;
        this.toPlayer = to;

        if (coins == null) {
            RealSkywars.getPlugin().log(Level.WARNING, "Invalid amount.");
            return;
        }

        this.operationQuantity = coins;
        this.console = true;
        if (executeNow) {
            executeOperation(op);
        }
    }

    private void executeOperation(Operations o) {
        if (!this.console) {
            switch (o) {
                case SEND:
                    if (this.operationQuantity == null) {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.INSUFICIENT_COINS, true).replace("%coins%", ca.getCoins(this.fromPlayer) + ""));
                        return;
                    }

                    if (ca.getCoins(this.fromPlayer) >= operationQuantity) {
                        this.transferCoins();
                    } else {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.INSUFICIENT_COINS, true).replace("%coins%", ca.getCoins(this.fromPlayer) + ""));
                    }
                    break;
                case SET:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.CMD_NOPERM, true));
                        return;
                    }

                    this.setCoins();
                    break;
                case ADD:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.CMD_NOPERM, true));
                        return;
                    }

                    this.addCoins();
                    break;
                case REMOVE:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.CMD_NOPERM, true));
                        return;
                    }

                    if (this.removeCoins()) {
                        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Coins removed from Player " + this.toPlayer.getName());
                    } else {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Insuficient coins to remove from " + this.toPlayer.getName());
                    }

                    break;
            }
        } else {
            if (o != null) {
                switch (o) {
                    case SEND:
                        RealSkywars.getPlugin().log(Level.INFO, "Only players can run this command.");
                        break;
                    case SET:
                        this.setCoins();
                        break;
                    case ADD:
                        this.addCoins();
                        break;
                    case REMOVE:
                        if (this.removeCoins()) {
                            RealSkywars.getPlugin().log(Level.INFO,"Coins removed from Player " + this.toPlayer.getName());
                        } else {
                            RealSkywars.getPlugin().log(Level.INFO,"Insuficient coins to remove from " + this.toPlayer.getName());
                        }

                        break;
                }
            }
        }
    }

    public void transferCoins() {
        ca.transferCoins(this.toPlayer, this.fromPlayer, this.operationQuantity);
        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.SENDER_COINS, true).replace("%coins%", "" + this.operationQuantity).replace("%player%", this.toPlayer.getDisplayName()));
        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.RECIEVER_COINS, true).replace("%coins%", "" + this.operationQuantity).replace("%player%", this.fromPlayer.getDisplayName()));
    }

    public void addCoins() {
        ca.addCoins(this.toPlayer, this.operationQuantity);

        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.ADDED_COINS, true).replace("%coins%", "" + this.operationQuantity));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Coins added to Player " + this.toPlayer.getName());
        } else {
            RealSkywars.getPlugin().log(Level.INFO,"Coins added to Player " + this.toPlayer.getName());
        }
    }

    public boolean removeCoins() {
        return ca.removeCoins(this.toPlayer, operationQuantity);
    }

    public void setCoins() {
        ca.setCoins(this.toPlayer, this.operationQuantity);

        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(toPlayer, LanguageManager.TS.SET_COINS, true).replace("%coins%", "" + this.operationQuantity));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Coins have been set Player " + this.toPlayer.getName());
        } else {
            RealSkywars.getPlugin().log(Level.INFO,"Coins have been set to Player " + this.toPlayer.getName());
        }
    }

    public enum Operations {SEND, ADD, REMOVE, SET}
}
