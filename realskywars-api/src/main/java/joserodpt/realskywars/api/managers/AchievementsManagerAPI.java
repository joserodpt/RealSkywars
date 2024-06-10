package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.achievements.RSWAchievement;
import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.List;

public abstract class AchievementsManagerAPI {
    public abstract void loadAchievements();

    public abstract List<RSWAchievement> getAchievements(RSWPlayer.PlayerStatistics ds);

    public abstract RSWAchievement getAchievement(RSWPlayer.PlayerStatistics ps, int meta);
}
