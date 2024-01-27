package joserodpt.realskywars.plugin.managers;

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

import com.j256.ormlite.stmt.QueryBuilder;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.database.PlayerData;
import joserodpt.realskywars.api.leaderboards.RSWLeaderboard;
import joserodpt.realskywars.api.managers.LeaderboardManagerAPI;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LeaderboardManager extends LeaderboardManagerAPI {
    private final RealSkywarsAPI rs;
    public LeaderboardManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    public HashMap<LeaderboardCategories, RSWLeaderboard> leaderboards = new HashMap<>();

    @Override
    public void refreshLeaderboards() {
        for (LeaderboardCategories value : LeaderboardCategories.values()) {
            try {
                this.refreshLeaderboard(value);
            } catch (Exception e) {
                RealSkywarsAPI.getInstance().getLogger().severe( "Error while loading Leaderboard for " + value.name() + " -> " + e.getMessage());
            }
        }
    }

    @Override
    public void refreshLeaderboard(LeaderboardCategories l) throws SQLException {
        QueryBuilder<PlayerData, UUID> qb = rs.getDatabaseManagerAPI().getQueryDao().queryBuilder();
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
        List<PlayerData> expansions = rs.getDatabaseManagerAPI().getQueryDao().query(qb.prepare());
        RSWLeaderboard lb = getLeaderboard(l, expansions);
        this.leaderboards.put(l, lb);
    }

    @Override
    @NotNull
    protected RSWLeaderboard getLeaderboard(LeaderboardCategories l, List<PlayerData> expansions) {
        RSWLeaderboard lb = new RSWLeaderboard();
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
        return lb;
    }

    @Override
    public RSWLeaderboard getLeaderboard(LeaderboardCategories l) {
        return this.leaderboards.get(l);
    }

}
