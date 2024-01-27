package joserodpt.realskywars.achievements.types;

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
import joserodpt.realskywars.achievements.RSWAchievement;
import joserodpt.realskywars.managers.CurrencyManager;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.player.RSWPlayer;

import java.util.Locale;

public class RSWAchievementRCoin implements RSWAchievement {

    private final RSWPlayer.PlayerStatistics at;
    private final int goal;
    private final Double reward;

    public RSWAchievementRCoin(RSWPlayer.PlayerStatistics at, int goal, Double reward)
    {
        this.at = at;
        this.goal = goal;
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
        new CurrencyManager(RealSkywars.getPlugin().getCurrencyAdapter(), p, (Double) this.getReward(), CurrencyManager.Operations.ADD, true);
        p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.ACHIEVEMENT_GET, true).replace("%achievement%", this.goal + " - " + this.getAchievementName()).replace("%reward%", this.getRewardName()));
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
    public int getGoal() {
        return this.goal;
    }

    @Override
    public Object getReward() {
        return this.reward;
    }
}
