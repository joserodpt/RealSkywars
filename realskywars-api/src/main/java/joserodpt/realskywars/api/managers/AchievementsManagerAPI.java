package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.achievements.RSWAchievement;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class AchievementsManagerAPI {
    public abstract void loadAchievements();

    public abstract List<RSWAchievement> getAchievements(RSWPlayer.PlayerStatistics ds);

    public abstract RSWAchievement getAchievement(RSWPlayer.PlayerStatistics ps, int meta);

    public abstract ItemStack getItem(RSWAchievement s, UUID uuid);

    protected abstract Material getColor(RSWAchievement s, RSWPlayer p);
}
