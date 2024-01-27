package joserodpt.realskywars.api.achievements;

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


import joserodpt.realskywars.api.player.RSWPlayer;

public interface RSWAchievement {
    String getAchievementName();
    String getRewardName();
    void giveAchievement(RSWPlayer rswPlayer);
    RSWPlayer.PlayerStatistics getType();
    RSWAchievement.RewardType getRewardType();
    int getGoal();
    Object getReward();
    enum RewardType { COINS }
}
