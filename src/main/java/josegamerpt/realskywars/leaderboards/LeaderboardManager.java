package josegamerpt.realskywars.leaderboards;

import com.j256.ormlite.stmt.QueryBuilder;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.database.PlayerData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class LeaderboardManager {

    public HashMap<LeaderboardManager.Leaderboard, josegamerpt.realskywars.leaderboards.Leaderboard> leaderboards = new HashMap<>();

    public void refreshLeaderboards() {
        for (Leaderboard value : Leaderboard.values()) {
            try {
                this.refreshLeaderboard(value);
            } catch (Exception e) {
                RealSkywars.log(Level.SEVERE, "Error while loading Leaderboard for " + value.name());
                e.printStackTrace();
            }
        }
    }

    public void refreshLeaderboard(LeaderboardManager.Leaderboard l) throws SQLException {
        QueryBuilder<PlayerData, UUID> qb = RealSkywars.getDatabaseManager().getQueryDao().queryBuilder();
        String select;
        switch (l) {
            case SOLO_WINS:
                select = "stats_wins_solo";
                break;
            case KILLS:
                select = "kills";
                break;
            case DEATHS:
                select = "deaths";
                break;
            case KILLS_RANKED:
                select = "ranked_kills";
                break;
            case TEAMS_WINS:
                select = "stats_wins_teams";
                break;
            case TEAMS_RANKED_WINS:
                select = "stats_wins_ranked_teams";
                break;
            case DEATHS_RANKED:
                select = "ranked_deaths";
                break;
            case SOLO_RANKED_WINS:
                select = "stats_wins_ranked_solo";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + l);
        }
        qb.orderBy(select, false);
        List<PlayerData> expansions = RealSkywars.getDatabaseManager().getQueryDao().query(qb.prepare());
        josegamerpt.realskywars.leaderboards.Leaderboard lb = new josegamerpt.realskywars.leaderboards.Leaderboard();
        for (int i = 1; i < 11; ++i) {
            PlayerData p = null;
            try {
                p = expansions.get(i - 1);
            } catch (Exception ignored) {
            }
            if (p != null) {
                Object o;
                switch (l) {
                    case SOLO_WINS:
                        o = p.getStats_wins_solo();
                        break;
                    case KILLS:
                        o = p.getKills();
                        break;
                    case DEATHS:
                        o = p.getDeaths();
                        break;
                    case KILLS_RANKED:
                        o = p.getRanked_kills();
                        break;
                    case TEAMS_WINS:
                        o = p.getStats_wins_teams();
                        break;
                    case TEAMS_RANKED_WINS:
                        o = p.getStats_wins_ranked_teams();
                        break;
                    case DEATHS_RANKED:
                        o = p.getRanked_deaths();
                        break;
                    case SOLO_RANKED_WINS:
                        o = p.getStats_wins_ranked_solo();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + l);
                }
                lb.addRow(p.getUUID(), p.getName(), o);
            }
        }
        this.leaderboards.put(l, lb);
    }

    public josegamerpt.realskywars.leaderboards.Leaderboard getLeaderboard(LeaderboardManager.Leaderboard l) {
        return this.leaderboards.get(l);
    }

    public enum Leaderboard {
        SOLO_WINS, SOLO_RANKED_WINS, TEAMS_WINS, TEAMS_RANKED_WINS,
        KILLS, DEATHS, KILLS_RANKED, DEATHS_RANKED
    }

}
