package josegamerpt.realskywars.effects;

public interface Trail {

    void startTask();

    void cancelTask();

    TrailType getType();

    enum TrailType {
        BOW, WINBLOCK
    }

}
