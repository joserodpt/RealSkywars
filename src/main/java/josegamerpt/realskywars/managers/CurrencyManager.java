package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;

public class CurrencyManager {
    private final RSWPlayer fromEntity;
    private final Double opQ;
    private final RSWPlayer toPlayer;
    private Boolean console;
    public CurrencyManager(RSWPlayer to, RSWPlayer from, Double operation, Boolean console) {
        this.toPlayer = to;
        this.fromEntity = from;
        this.opQ = operation;
        this.console = console;
    }

    public CurrencyManager(RSWPlayer from, Double operation) {
        this.toPlayer = from;
        this.fromEntity = from;
        this.opQ = operation;
        this.console = true;
    }

    public Boolean canMakeOperation() {
        return fromEntity.getCoins() >= opQ;
    }

    public void transferCoins() {
        this.toPlayer.setCoins(this.toPlayer.getCoins() + this.opQ);
        this.fromEntity.setCoins(this.fromEntity.getCoins() - this.opQ);
        this.toPlayer.saveData();
        this.fromEntity.saveData();
        this.fromEntity.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.SENDER_COINS, true).replace("%coins%", "" + this.opQ).replace("%player%", this.toPlayer.getDisplayName()));
        this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.RECIEVER_COINS, true).replace("%coins%", "" + this.opQ).replace("%player%", this.fromEntity.getDisplayName()));
    }

    public void addCoins() {
        this.toPlayer.setCoins(this.toPlayer.getCoins() + this.opQ);
        this.toPlayer.saveData();
        if (!this.console) {
            this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.ADDED_COINS, true).replace("%coins%", "" + this.opQ));
        }
    }

    public void removeCoins() {
        this.fromEntity.setCoins(this.fromEntity.getCoins() - this.opQ);
        this.fromEntity.saveData();
        if (!this.console) {
            this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.toPlayer, LanguageManager.TS.REMOVED_COINS, true).replace("%coins%", "" + this.opQ));
        }
    }

    public void setCoins() {
        if (this.console) {
            this.toPlayer.setCoins(opQ);
            this.toPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(toPlayer, LanguageManager.TS.SET_COINS, true).replace("%coins%", "" + this.opQ));
            this.fromEntity.saveData();
            this.toPlayer.saveData();
        }
    }

    public enum Operations {send, add, set}
}
