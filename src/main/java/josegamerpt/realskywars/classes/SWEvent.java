package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.utils.Text;

public class SWEvent {

    public enum EventType { REFILL }
    private EventType et;

    private SWGameMode room;
    private int time;
    public SWEvent (SWGameMode room, EventType et, int time)
    {
        this.room = room;
        this.et = et;
        this.time = time;
    }

    public String getName() {
        return this.et.name() + " " + Text.formatSeconds(this.getTimeLeft());
    }

    public int getTimeLeft() {
        return (room.getMaxTime() - (room.getMaxTime() - this.getTime())) - room.getTimePassed();
    }

    public int getTime() {
        return this.time;
    }

    public void execute() { }

    public Boolean isRefill()
    {
        return this.et == EventType.REFILL;
    }

}
