package josegamerpt.realskywars.achievements.types;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.achievements.Achievement;
import josegamerpt.realskywars.managers.CurrencyManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;

import java.util.Locale;

public class AchievementRCoin implements Achievement {

    private RSWPlayer.PlayerStatistics at;
    private int meta;
    private Double reward;

    public AchievementRCoin(RSWPlayer.PlayerStatistics at, int meta, Double reward)
    {
        this.at = at;
        this.meta = meta;
        this.reward = reward;
    }

    @Override
    public String getAchievementName() {
        return this.at.name().replace("_", " ");
    }

    @Override
    public String getRewardName() {
        return this.getReward() + " " + this.getRewardType().name().toLowerCase(Locale.ROOT);
    }

    @Override
    public void giveAchievement(RSWPlayer p) {
        p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ACHIEVEMENT_GET, true).replace("%achievement%", this.getAchievementName()).replace("%reward%", this.getRewardName()));
        CurrencyManager cm = new CurrencyManager(p, (Double) this.getReward());
        cm.addCoins();
    }

    @Override
    public RSWPlayer.PlayerStatistics getType() {
        return this.at;
    }

    @Override
    public RewardType getRewardType() {
        return RewardType.COINS;
    }

    @Override
    public int getMeta() {
        return this.meta;
    }

    @Override
    public Object getReward() {
        return this.reward;
    }
}
