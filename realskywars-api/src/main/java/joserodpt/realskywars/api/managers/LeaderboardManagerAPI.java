package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.database.PlayerData;
import joserodpt.realskywars.api.leaderboards.RSWLeaderboard;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public abstract class LeaderboardManagerAPI {
    public abstract void refreshLeaderboards();

    public abstract void refreshLeaderboard(LeaderboardCategories l) throws SQLException;

    @NotNull
    protected abstract RSWLeaderboard getLeaderboard(LeaderboardCategories l, List<PlayerData> expansions);

    public abstract RSWLeaderboard getLeaderboard(LeaderboardCategories l);

    public enum LeaderboardCategories {
        SOLO_WINS, SOLO_RANKED_WINS, TEAMS_WINS, TEAMS_RANKED_WINS,
        KILLS, DEATHS, KILLS_RANKED, DEATHS_RANKED
    }
}
