package josegamerpt.realskywars.achievements;

import josegamerpt.realskywars.player.RSWPlayer;

public interface Achievement {

    String getAchievementName();

    String getRewardName();

    void giveAchievement(RSWPlayer rswPlayer);

    enum RewardType { COINS }

    RSWPlayer.PlayerStatistics getType();

    Achievement.RewardType getRewardType();

    int getMeta();

    Object getReward();
}
