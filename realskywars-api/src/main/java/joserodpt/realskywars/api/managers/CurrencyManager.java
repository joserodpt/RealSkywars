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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.currency.CurrencyAdapter;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

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
            Text.send(to.getPlayer(), RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Invalid amount.");
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
            RealSkywarsAPI.getInstance().getLogger().warning("Invalid amount.");
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
                        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.fromPlayer, LanguageManagerAPI.TS.INSUFICIENT_COINS, true).replace("%coins%", ca.getCoins(this.fromPlayer) + ""));
                        return;
                    }

                    if (ca.getCoins(this.fromPlayer) >= operationQuantity) {
                        this.transferCoins();
                    } else {
                        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.fromPlayer, LanguageManagerAPI.TS.INSUFICIENT_COINS, true).replace("%coins%", ca.getCoins(this.fromPlayer) + ""));
                    }
                    break;
                case SET:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.fromPlayer, LanguageManagerAPI.TS.CMD_NOPERM, true));
                        return;
                    }

                    this.setCoins();
                    break;
                case ADD:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.fromPlayer, LanguageManagerAPI.TS.CMD_NOPERM, true));
                        return;
                    }

                    this.addCoins();
                    break;
                case REMOVE:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.fromPlayer, LanguageManagerAPI.TS.CMD_NOPERM, true));
                        return;
                    }

                    if (this.removeCoins()) {
                        this.toPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Coins removed from Player " + this.toPlayer.getName());
                    } else {
                        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Insuficient coins to remove from " + this.toPlayer.getName());
                    }

                    break;
            }
        } else {
            if (o != null) {
                switch (o) {
                    case SEND:
                        RealSkywarsAPI.getInstance().getLogger().info("Only players can run this command.");
                        break;
                    case SET:
                        this.setCoins();
                        break;
                    case ADD:
                        this.addCoins();
                        break;
                    case REMOVE:
                        if (this.removeCoins()) {
                            RealSkywarsAPI.getInstance().getLogger().info("Coins removed from Player " + this.toPlayer.getName());
                        } else {
                            RealSkywarsAPI.getInstance().getLogger().info("Insuficient coins to remove from " + this.toPlayer.getName());
                        }

                        break;
                }
            }
        }
    }

    public void transferCoins() {
        ca.transferCoins(this.toPlayer, this.fromPlayer, this.operationQuantity);
        this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.toPlayer, LanguageManagerAPI.TS.SENDER_COINS, true).replace("%coins%", "" + this.operationQuantity).replace("%player%", this.toPlayer.getDisplayName()));
        this.toPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.toPlayer, LanguageManagerAPI.TS.RECIEVER_COINS, true).replace("%coins%", "" + this.operationQuantity).replace("%player%", this.fromPlayer.getDisplayName()));
    }

    public void addCoins() {
        ca.addCoins(this.toPlayer, this.operationQuantity);

        this.toPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(this.toPlayer, LanguageManagerAPI.TS.ADDED_COINS, true).replace("%coins%", "" + this.operationQuantity));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Coins added to Player " + this.toPlayer.getName());
        } else {
            RealSkywarsAPI.getInstance().getLogger().info("Coins added to Player " + this.toPlayer.getName());
        }
    }

    public boolean removeCoins() {
        return ca.removeCoins(this.toPlayer, operationQuantity);
    }

    public void setCoins() {
        ca.setCoins(this.toPlayer, this.operationQuantity);

        this.toPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getString(toPlayer, LanguageManagerAPI.TS.SET_COINS, true).replace("%coins%", "" + this.operationQuantity));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Coins have been set Player " + this.toPlayer.getName());
        } else {
            RealSkywarsAPI.getInstance().getLogger().info("Coins have been set to Player " + this.toPlayer.getName());
        }
    }

    public enum Operations {SEND, ADD, REMOVE, SET}
}
