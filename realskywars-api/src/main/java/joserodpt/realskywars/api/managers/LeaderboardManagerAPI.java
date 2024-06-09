package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.database.PlayerData;
import joserodpt.realskywars.api.leaderboards.RSWLeaderboard;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public abstract class LeaderboardManagerAPI {
    public abstract void refreshLeaderboards();

    public abstract void refreshLeaderboard(RSWLeaderboard.RSWLeaderboardCategories l) throws SQLException;

    @NotNull
    protected abstract RSWLeaderboard getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories l, List<PlayerData> expansions);

    public abstract RSWLeaderboard getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories l);
}
