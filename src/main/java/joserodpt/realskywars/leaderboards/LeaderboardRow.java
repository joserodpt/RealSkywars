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

    public UUID getUuid() {
        return uuid;
    }
}
