package josegamerpt.realskywars.leaderboards;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Leaderboard {

    private List<LeaderboardRow> lbr = new ArrayList<>();

    public Leaderboard() {}

    public void addRow(UUID uuid, String name, Object o) {
        this.lbr.add(new LeaderboardRow(uuid, name, o));
    }

    public String getIndex(int i) {
        if (i <= this.lbr.size()) {
            LeaderboardRow lbr = this.lbr.get(i - 1);
            lbr.setPlace(i);
            return lbr.getText();
        } else {
            return new LeaderboardRow().setPlace(i).getText();
        }
    }
}
