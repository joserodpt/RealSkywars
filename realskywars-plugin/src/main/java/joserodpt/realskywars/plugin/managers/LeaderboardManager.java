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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.j256.ormlite.stmt.QueryBuilder;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.database.PlayerDataRow;
import joserodpt.realskywars.api.leaderboards.RSWLeaderboard;
import joserodpt.realskywars.api.managers.LeaderboardManagerAPI;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardManager extends LeaderboardManagerAPI {
    private final RealSkywarsAPI rs;

    public LeaderboardManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    public Map<RSWLeaderboard.RSWLeaderboardCategories, RSWLeaderboard> leaderboards = new HashMap<>();

    @Override
    public void refreshLeaderboards() {
        for (RSWLeaderboard.RSWLeaderboardCategories value : RSWLeaderboard.RSWLeaderboardCategories.values()) {
            try {
                this.refreshLeaderboard(value);
            } catch (Exception e) {
                RealSkywarsAPI.getInstance().getLogger().severe("Error while loading Leaderboard for " + value.name() + " -> " + e.getMessage());
            }
        }
    }

    @Override
    public void refreshLeaderboard(RSWLeaderboard.RSWLeaderboardCategories l) throws SQLException {
        QueryBuilder<PlayerDataRow, UUID> qb = rs.getDatabaseManagerAPI().getQueryDao().queryBuilder();
        qb.orderBy(l.getDBName(), false);
        RSWLeaderboard lb = getLeaderboard(l, rs.getDatabaseManagerAPI().getQueryDao().query(qb.prepare()));
        this.leaderboards.put(l, lb);
    }

    @Override
    @NotNull
    protected RSWLeaderboard getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories l, List<PlayerDataRow> expansions) {
        RSWLeaderboard lb = new RSWLeaderboard();
        for (int i = 1; i < 11; ++i) {
            PlayerDataRow p;
            try {
                p = expansions.get(i - 1);

                if (p != null) {
                    lb.addRow(p.getUUID(), p.getName(), l.getValue(p));
                }
            } catch (Exception ignored) {
            }
        }
        return lb;
    }

    @Override
    public RSWLeaderboard getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories l) {
        return this.leaderboards.get(l);
    }

}
