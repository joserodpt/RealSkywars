package josegamerpt.realskywars.misc;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Languages;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.TNTPrimed;

public class SWEvent {

    private EventType et;
    private SWGameMode room;
    private int time;
    public SWEvent(SWGameMode room, EventType et, int time) {
        this.room = room;
        this.et = et;
        this.time = time;
    }

    public EventType getEventType() {
        return this.et;
    }

    public String getName() {
        return Text.color(Languages.file().getString("Strings.Events." + this.et.name()) + " " + Text.formatSeconds(this.getTimeLeft()));
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
                this.room.getInRoom().forEach(rswPlayer -> rswPlayer.sendTitle(RealSkywars.getLanguageManager().getList(rswPlayer, LanguageManager.TL.REFILL_EVENT_TITLE).get(0), RealSkywars.getLanguageManager().getList(rswPlayer, LanguageManager.TL.REFILL_EVENT_TITLE).get(1), 4, 10, 4));
                this.room.getInRoom().forEach(rswPlayer -> rswPlayer.playSound(Sound.BLOCK_CHEST_LOCKED, 50, 50));
                break;
            case TNTRAIN:
                this.room.getInRoom().forEach(rswPlayer -> rswPlayer.sendTitle(RealSkywars.getLanguageManager().getList(rswPlayer, LanguageManager.TL.TNTRAIN_EVENT_TITLE).get(0), RealSkywars.getLanguageManager().getList(rswPlayer, LanguageManager.TL.TNTRAIN_EVENT_TITLE).get(1), 4, 10, 4));
                this.room.getInRoom().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_TNT_PRIMED, 50, 50));
                for (RSWPlayer player : this.room.getPlayers()) {
                    player.spawnAbovePlayer(TNTPrimed.class);
                }
                break;
            case BORDERSHRINK:
                this.room.getBossBar().setTitle(Text.color(RealSkywars.getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_DEATHMATCH)));
                this.room.getBossBar().setProgress(0);
                this.room.getBossBar().setColor(BarColor.RED);

                this.room.getInRoom().forEach(rswPlayer -> rswPlayer.sendTitle("", Text.color(RealSkywars.getLanguageManager().getString(rswPlayer, LanguageManager.TS.TITLE_DEATHMATCH, false)), 10, 20,
                        5));
                this.room.getInRoom().forEach(rswPlayer -> rswPlayer.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 50, 50));

                this.room.getBorder().setSize(this.room.getBorderSize() / 2, 30L);
                this.room.getBorder().setCenter(this.room.getArena().getCenter());
                break;
        }
    }

    public enum EventType {REFILL, TNTRAIN, BORDERSHRINK}
}
