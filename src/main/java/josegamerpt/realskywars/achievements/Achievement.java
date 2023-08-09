package josegamerpt.realskywars.achievements;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import josegamerpt.realskywars.player.RSWPlayer;

public interface Achievement {
    String getAchievementName();
    String getRewardName();
    void giveAchievement(RSWPlayer rswPlayer);
    RSWPlayer.PlayerStatistics getType();
    Achievement.RewardType getRewardType();
    int getGoal();
    Object getReward();
    enum RewardType { COINS }
}
