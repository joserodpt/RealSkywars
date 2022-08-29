package josegamerpt.realskywars.leaderboards;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Leaderboard {

    List<LeaderboardRow> lbr = new ArrayList<>();

    public Leaderboard() {
    }

    public void addRow(UUID uuid, String name, Object o) {
        this.lbr.add(new LeaderboardRow(uuid, name, o));
    }

    public String getIndex(int i) {
        if (i <= lbr.size()) {
            LeaderboardRow lbr = this.lbr.get(i - 1);
            lbr.setPlace(i);
            return lbr.getText();
        } else {
            return new LeaderboardRow().setPlace(i).getText();
        }
    }
}
