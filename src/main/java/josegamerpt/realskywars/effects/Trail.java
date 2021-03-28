package josegamerpt.realskywars.effects;

import josegamerpt.realskywars.classes.Enum;

public interface Trail {

    void startTask();

    void cancelTask();

    Enum.TrailType getType();

}
