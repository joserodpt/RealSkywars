package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;

public class CurrencyManager {

    public enum Operations{ send, add, set}

    private RSWPlayer fromEntity;
    private Double opQ;
    private RSWPlayer toPlayer;
    private Boolean console;

    public CurrencyManager(RSWPlayer to, RSWPlayer from, Double operation, Boolean console) {
        this.toPlayer = to;
        this.fromEntity = from;
        this.opQ = operation;
        this.console = console;
    }

    public CurrencyManager(RSWPlayer from, Double operation) {
        this.fromEntity = from;
        this.opQ = operation;
        this.console = true;
    }

    public Boolean canMakeOperation() {
        return fromEntity.getCoins() >= opQ;
    }

    public void transferCoins() {
        toPlayer.setCoins(toPlayer.getCoins() + opQ);
        fromEntity.setCoins(fromEntity.getCoins() - opQ);
        toPlayer.saveData();
        fromEntity.saveData();
        fromEntity.sendMessage(RealSkywars.getLanguageManager().getString(toPlayer, LanguageManager.TS.SENDER_COINS, true).replace("%coins%", "" + opQ)
                .replace("%player%", toPlayer.getDisplayName()));
        toPlayer.sendMessage(RealSkywars.getLanguageManager().getString(toPlayer, LanguageManager.TS.RECIEVER_COINS, true).replace("%coins%", "" + opQ)
                .replace("%player%", fromEntity.getDisplayName()));
    }

    public void addCoins() {
        if (console = true) {
            fromEntity.setCoins(fromEntity.getCoins() + opQ);
            fromEntity.saveData();
        }
        toPlayer.setCoins(toPlayer.getCoins() + opQ);
        toPlayer.saveData();
        toPlayer.sendMessage(RealSkywars.getLanguageManager().getString(toPlayer, LanguageManager.TS.ADDED_COINS, true).replace("%coins%", "" + opQ));
    }

    public void removeCoins() {
        if (console) {
            fromEntity.setCoins(fromEntity.getCoins() - opQ);
            fromEntity.saveData();
            return;
        }
        fromEntity.setCoins(fromEntity.getCoins() - opQ);
        fromEntity.saveData();
        toPlayer.sendMessage(RealSkywars.getLanguageManager().getString(toPlayer, LanguageManager.TS.REMOVED_COINS, true).replace("%coins%", "" + opQ));
    }

    public void setCoins() {
        if (console) {
            toPlayer.setCoins(opQ);
            toPlayer.sendMessage(RealSkywars.getLanguageManager().getString(toPlayer, LanguageManager.TS.SET_COINS, true).replace("%coins%", "" + opQ));
            fromEntity.saveData();
            toPlayer.saveData();
        }
    }
}
