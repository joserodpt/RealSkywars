package josegamerpt.realskywars.leaderboards;

import java.util.UUID;

public class LeaderboardRow {

    private final UUID uuid;
    private final String player;
    private final Object total;
    private int place;

    public LeaderboardRow(UUID uuid, String player, Object statistic) {
        this.uuid = uuid;
        this.player = player;
        this.total = statistic;
    }

    public LeaderboardRow() {
        this.uuid = UUID.randomUUID();
        this.player = "?";
        this.total = 0;
    }

    public String getPlayer() {
        return this.player;
    }

    public String getText() {
        return "&a" + this.place + ". &b" + this.player + " &f- &b" + this.total;
    }

    public LeaderboardRow setPlace(int i) {
        this.place = i;
        return this;
    }
}
