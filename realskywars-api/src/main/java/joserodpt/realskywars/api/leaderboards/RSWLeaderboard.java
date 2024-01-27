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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RSWLeaderboard {

    private List<RSWLeaderboardRow> lbr = new ArrayList<>();

    public RSWLeaderboard() {}

    public void addRow(UUID uuid, String name, Object o) {
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
}
