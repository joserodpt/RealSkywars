package joserodpt.realskywars.api.map;

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

import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;

public class RSWEvent {

    private final EventType et;
    private final RSWMap room;
    private final int time;

    public RSWEvent(RSWMap room, EventType et, int time) {
        this.room = room;
        this.et = et;
        this.time = time;
    }

    public EventType getEventType() {
        return this.et;
    }

    public String getName() {
        return Text.color(RSWLanguagesConfig.file().getString("Strings.Events." + this.et.name()) + " " + Text.formatSeconds(this.getTimeLeft()));
    }

    public int getTimeLeft() {
        return (this.room.getMaxTime() - (this.room.getMaxTime() - this.getTime())) - this.room.getTimePassed();
    }

    public void tick() {
        if (this.getTimeLeft() == 0) {
            execute();
            this.room.getEvents().remove(this);
        }
    }

    public int getTime() {
        return this.time;
    }

    public void execute() {
        switch (this.et) {
            case REFILL:
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle(TranslatableList.REFILL_EVENT_TITLE.get(rswPlayer).get(0), TranslatableList.REFILL_EVENT_TITLE.get(rswPlayer).get(1), 4, 10, 4));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.BLOCK_CHEST_LOCKED, 50, 50));
                break;
            case TNTRAIN:
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle(TranslatableList.TNTRAIN_EVENT_TITLE.get(rswPlayer).get(0), TranslatableList.TNTRAIN_EVENT_TITLE.get(rswPlayer).get(1), 4, 10, 4));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_TNT_PRIMED, 50, 50));
                this.room.getPlayers().forEach(player -> player.spawnAbovePlayer(TNTPrimed.class));
                break;
            case BORDERSHRINK:
                this.room.getBossBar().setDeathmatch();

                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.sendTitle("", TranslatableLine.TITLE_DEATHMATCH.get(rswPlayer), 10, 20, 5));
                this.room.getAllPlayers().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 50, 50));

                this.room.getBorder().setSize((double) this.room.getBorderSize() / 2, 30L);
                this.room.getBorder().setCenter(this.room.getMapCuboid().getCenter());
                break;
        }
    }

    public enum EventType {REFILL, TNTRAIN, BORDERSHRINK}
}
