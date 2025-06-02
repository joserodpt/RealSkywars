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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.currency.CurrencyAdapterAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

public class TransactionManager {
    private RSWPlayer fromPlayer;
    private Double operationQuantity = 0D;
    private final RSWPlayer toPlayer;
    private Boolean console = false;
    private final CurrencyAdapterAPI ca = RealSkywarsAPI.getInstance().getCurrencyAdapterAPI();

    public TransactionManager(RSWPlayer to, RSWPlayer from, Double coins, Operations op, boolean executeNow) {
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

    public TransactionManager(RSWPlayer to, Double coins, Operations op, boolean executeNow) {
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
                        this.fromPlayer.sendMessage(TranslatableLine.INSUFICIENT_COINS.get(this.fromPlayer).replace("%coins%", ca.getCoinsFormatted(this.fromPlayer)));
                        return;
                    }

                    if (ca.getCoins(this.fromPlayer) >= operationQuantity) {
                        this.transferCoins();
                    } else {
                        this.fromPlayer.sendMessage(TranslatableLine.INSUFICIENT_COINS.get(this.fromPlayer).replace("%coins%", ca.getCoinsFormatted(this.fromPlayer)));
                    }
                    break;
                case SET:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        TranslatableLine.CMD_NO_PERM.send(this.fromPlayer, true);
                        return;
                    }

                    this.setCoins();
                    break;
                case ADD:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        TranslatableLine.CMD_NO_PERM.send(this.fromPlayer, true);
                        return;
                    }

                    this.addCoins();
                    break;
                case REMOVE:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.admin")) {
                        TranslatableLine.CMD_NO_PERM.send(this.fromPlayer, true);
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
        this.fromPlayer.sendMessage(TranslatableLine.SENDER_COINS.get(this.fromPlayer, true).replace("%coins%", Text.formatDouble(this.operationQuantity)).replace("%player%", this.toPlayer.getDisplayName()));
        this.toPlayer.sendMessage(TranslatableLine.RECIEVER_COINS.get(this.fromPlayer, true).replace("%coins%", Text.formatDouble(this.operationQuantity)).replace("%player%", this.fromPlayer.getDisplayName()));
    }

    public void addCoins() {
        ca.addCoins(this.toPlayer, this.operationQuantity);

        this.toPlayer.sendMessage(TranslatableLine.ADDED_COINS.get(this.toPlayer, true).replace("%coins%", Text.formatDouble(this.operationQuantity)));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Coins added to Player " + this.toPlayer.getName());
        } else {
            RealSkywarsAPI.getInstance().getLogger().info("Coins " + this.operationQuantity + " added to Player " + this.toPlayer.getName());
        }
    }

    public boolean removeCoins() {
        return ca.removeCoins(this.toPlayer, operationQuantity);
    }

    public void setCoins() {
        ca.setCoins(this.toPlayer, this.operationQuantity);

        this.toPlayer.sendMessage(TranslatableLine.SET_COINS.get(this.fromPlayer, true).replace("%coins%", Text.formatDouble(this.operationQuantity)));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "Coins have been set Player " + this.toPlayer.getName());
        } else {
            RealSkywarsAPI.getInstance().getLogger().info("Coins have been set to Player " + this.toPlayer.getName());
        }
    }

    public enum Operations {SEND, ADD, REMOVE, SET}
}
