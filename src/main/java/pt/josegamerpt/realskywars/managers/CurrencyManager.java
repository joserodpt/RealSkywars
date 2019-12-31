package pt.josegamerpt.realskywars.managers;

import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.player.GamePlayer;

public class CurrencyManager {

	private GamePlayer toPlayer;
	private GamePlayer fromEntity;
	private Double opQ;
	private Boolean console;

	public CurrencyManager(GamePlayer to, GamePlayer from, Double operation, Boolean console) {
		this.toPlayer = to;
		this.fromEntity = from;
		this.opQ = operation;
		this.console = console;
	}

	public CurrencyManager(GamePlayer from, Double operation) {
		this.fromEntity = from;
		this.opQ = operation;
		this.console = true;
	}

	public Boolean canMakeOperation() {
		if (fromEntity.Coins >= opQ) {
			return true;
		} else {
			return false;
		}
	}

	public void transferCoins() {
		toPlayer.Coins = (toPlayer.Coins + opQ);
		fromEntity.Coins = (fromEntity.Coins - opQ);
		toPlayer.saveData();
		fromEntity.saveData();
		fromEntity.p.sendMessage(LanguageManager.getString(toPlayer, TS.SENDER_COINS, true).replace("%coins%", "" + opQ)
				.replace("%player%", toPlayer.p.getDisplayName()));
		toPlayer.p.sendMessage(LanguageManager.getString(toPlayer, TS.RECIEVER_COINS, true).replace("%coins%", "" + opQ)
				.replace("%player%", toPlayer.p.getDisplayName()));
	}

	public void addCoins() {
		if (console = true) {
			fromEntity.Coins = (fromEntity.Coins + opQ);
			fromEntity.saveData();
		}
		toPlayer.Coins = (toPlayer.Coins + opQ);
		toPlayer.saveData();
		toPlayer.p.sendMessage(LanguageManager.getString(toPlayer, TS.ADDED_COINS, true).replace("%coins%", "" + opQ));
	}

	public void removeCoins() {
		if (console == true) {
			fromEntity.Coins = (fromEntity.Coins - opQ);
			fromEntity.saveData();
			return;
		}
		fromEntity.Coins = (fromEntity.Coins - opQ);
		fromEntity.saveData();
		toPlayer.p
				.sendMessage(LanguageManager.getString(toPlayer, TS.REMOVED_COINS, true).replace("%coins%", "" + opQ));
	}

	public void setCoins() {
		if (console == true) {
			toPlayer.Coins = opQ;
			toPlayer.p
					.sendMessage(LanguageManager.getString(toPlayer, TS.SET_COINS, true).replace("%coins%", "" + opQ));
			fromEntity.saveData();
		}
	}
}
