package joserodpt.realskywars.api.achievements.types;

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
import joserodpt.realskywars.api.achievements.RSWAchievement;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.TransactionManager;
import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.Locale;

public class RSWAchievementRCoin implements RSWAchievement {

    private final RSWPlayer.PlayerStatistics at;
    private final int goal;
    private final Double reward;

    public RSWAchievementRCoin(RSWPlayer.PlayerStatistics at, int goal, Double reward) {
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
        new TransactionManager(RealSkywarsAPI.getInstance().getCurrencyAdapter(), p, (Double) this.getReward(), TransactionManager.Operations.ADD, true);
        p.sendMessage(TranslatableLine.ACHIEVEMENT_GET.get(p, true).replace("%achievement%", this.goal + " - " + this.getAchievementName()).replace("%reward%", this.getRewardName()));
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
