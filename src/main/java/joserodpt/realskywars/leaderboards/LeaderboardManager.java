package joserodpt.realskywars.leaderboards;

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
import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.database.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class LeaderboardManager {
    private RealSkywars rs;
    public LeaderboardManager(RealSkywars rs) {
        this.rs = rs;
    }

    public HashMap<LeaderboardCategories, Leaderboard> leaderboards = new HashMap<>();

    public void refreshLeaderboards() {
        for (LeaderboardCategories value : LeaderboardCategories.values()) {
            try {
                this.refreshLeaderboard(value);
            } catch (Exception e) {
                RealSkywars.getPlugin().log(Level.SEVERE, "Error while loading Leaderboard for " + value.name() + " -> " + e.getMessage());
            }
        }
    }

    public void refreshLeaderboard(LeaderboardCategories l) throws SQLException {
        QueryBuilder<PlayerData, UUID> qb = rs.getDatabaseManager().getQueryDao().queryBuilder();
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
        List<PlayerData> expansions = rs.getDatabaseManager().getQueryDao().query(qb.prepare());
        Leaderboard lb = getLeaderboard(l, expansions);
        this.leaderboards.put(l, lb);
    }

    @NotNull
    private Leaderboard getLeaderboard(LeaderboardCategories l, List<PlayerData> expansions) {
        Leaderboard lb = new Leaderboard();
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

    public Leaderboard getLeaderboard(LeaderboardCategories l) {
        return this.leaderboards.get(l);
    }

    public enum LeaderboardCategories {
        SOLO_WINS, SOLO_RANKED_WINS, TEAMS_WINS, TEAMS_RANKED_WINS,
        KILLS, DEATHS, KILLS_RANKED, DEATHS_RANKED
    }

}
