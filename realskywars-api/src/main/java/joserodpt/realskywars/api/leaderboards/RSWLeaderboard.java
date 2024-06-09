package joserodpt.realskywars.api.leaderboards;

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

import joserodpt.realskywars.api.database.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RSWLeaderboard {

    private List<RSWLeaderboardRow> lbr = new ArrayList<>();

    public RSWLeaderboard() {
    }

    public void addRow(UUID uuid, String name, int o) {
        this.lbr.add(new RSWLeaderboardRow(uuid, name, o));
    }

    public String getIndex(int i) {
        if (i <= this.lbr.size()) {
            RSWLeaderboardRow lbr = this.lbr.get(i - 1);
            lbr.setPlace(i);
            return lbr.getText();
        } else {
            return new RSWLeaderboardRow().setPlace(i).getText();
        }
    }

    public enum RSWLeaderboardCategories {
        SOLO_WINS, SOLO_RANKED_WINS, TEAMS_WINS, TEAMS_RANKED_WINS,
        KILLS, DEATHS, KILLS_RANKED, DEATHS_RANKED;

        public String getDBName() {
            switch (this) {
                case SOLO_WINS:
                    return "stats_wins_solo";
                case KILLS:
                    return "kills";
                case DEATHS:
                    return "deaths";
                case KILLS_RANKED:
                    return "ranked_kills";
                case TEAMS_WINS:
                    return "stats_wins_teams";
                case TEAMS_RANKED_WINS:
                    return "stats_wins_ranked_teams";
                case DEATHS_RANKED:
                    return "ranked_deaths";
                case SOLO_RANKED_WINS:
                    return "stats_wins_ranked_solo";
                default:
                    return "err";
            }
        }

        public int getValue(PlayerData p) {
            switch (this) {
                case SOLO_WINS:
                    return p.getStats_wins_solo();
                case KILLS:
                    return p.getKills();
                case DEATHS:
                    return p.getDeaths();
                case KILLS_RANKED:
                    return p.getRanked_kills();
                case TEAMS_WINS:
                    return p.getStats_wins_teams();
                case TEAMS_RANKED_WINS:
                    return p.getStats_wins_ranked_teams();
                case DEATHS_RANKED:
                    return p.getRanked_deaths();
                case SOLO_RANKED_WINS:
                    return p.getStats_wins_ranked_solo();
                default:
                    return -1;
            }
        }
    }
}
