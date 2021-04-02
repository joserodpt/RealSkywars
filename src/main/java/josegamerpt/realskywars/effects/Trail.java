package josegamerpt.realskywars.effects;

public interface Trail {

    enum TrailType {
        BOW, WINBLOCK
    }

    void startTask();

    void cancelTask();

    TrailType getType();

}
