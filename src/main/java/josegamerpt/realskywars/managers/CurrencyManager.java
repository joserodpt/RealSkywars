package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;

import java.util.logging.Level;

public class CurrencyManager {
    private RSWPlayer fromPlayer;
    private Double operationQuantity = 0D;
    private final RSWPlayer toPlayer;
    private Boolean console = false;

    public CurrencyManager(RSWPlayer to, RSWPlayer from, Double coins, Operations op, boolean executeNow) {
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

    public CurrencyManager(RSWPlayer to, Double coins, Operations op, boolean executeNow) {
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
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.INSUFICIENT_COINS, true).replace("%coins%", this.fromPlayer.getCoins() + ""));
                        return;
                    }

                    if (this.fromPlayer.getCoins() >= operationQuantity) {
                        this.transferCoins();
                    } else {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.INSUFICIENT_COINS, true).replace("%coins%", this.fromPlayer.getCoins() + ""));
                    }
                    break;
                case SET:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.Admin")) {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.CMD_NOPERM, true));
                        return;
                    }

                    this.setCoins();
                    break;
                case ADD:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.Admin")) {
                        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.fromPlayer, LanguageManager.TS.CMD_NOPERM, true));
                        return;
                    }

                    this.addCoins();
                    break;
                case REMOVE:
                    if (!this.fromPlayer.getPlayer().hasPermission("rs.Admin")) {
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
        this.toPlayer.setCoins(this.toPlayer.getCoins() + this.operationQuantity);
        this.fromPlayer.setCoins(this.fromPlayer.getCoins() - this.operationQuantity);
        this.toPlayer.saveData(RSWPlayer.PlayerData.COINS);
        this.fromPlayer.saveData(RSWPlayer.PlayerData.COINS);

        this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.SENDER_COINS, true).replace("%coins%", "" + this.operationQuantity).replace("%player%", this.toPlayer.getDisplayName()));
        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.RECIEVER_COINS, true).replace("%coins%", "" + this.operationQuantity).replace("%player%", this.fromPlayer.getDisplayName()));
    }

    public void addCoins() {
        this.toPlayer.setCoins(this.toPlayer.getCoins() + this.operationQuantity);
        this.toPlayer.saveData(RSWPlayer.PlayerData.COINS);
        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.ADDED_COINS, true).replace("%coins%", "" + this.operationQuantity));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Coins added to Player " + this.toPlayer.getName());
        } else {
            RealSkywars.getPlugin().log(Level.INFO,"Coins added to Player " + this.toPlayer.getName());
        }
    }

    public boolean removeCoins() {
        if (this.toPlayer.getCoins() >= operationQuantity) {
            this.toPlayer.setCoins(this.toPlayer.getCoins() - this.operationQuantity);
            this.toPlayer.saveData(RSWPlayer.PlayerData.COINS);
            this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(toPlayer, LanguageManager.TS.REMOVED_COINS, true).replace("%coins%", "" + this.operationQuantity));
            return true;
        }

        return false;
    }

    public void setCoins() {
        this.toPlayer.setCoins(this.operationQuantity);
        this.toPlayer.saveData(RSWPlayer.PlayerData.COINS);
        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(toPlayer, LanguageManager.TS.SET_COINS, true).replace("%coins%", "" + this.operationQuantity));
        if (!this.console) {
            this.fromPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "Coins have been set Player " + this.toPlayer.getName());
        } else {
            RealSkywars.getPlugin().log(Level.INFO,"Coins have been set to Player " + this.toPlayer.getName());
        }
    }

    public enum Operations {SEND, ADD, REMOVE, SET}
}
