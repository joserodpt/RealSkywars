package josegamerpt.realskywars.world;

import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.World;

public interface SWWorldEngine {

    World getWorld();

    void resetWorld(SWGameMode.OperationReason rr);

    void deleteWorld(SWGameMode.OperationReason rr);

    void setTime(long l);

    String getName();

    SWWorld.WorldType getType();
}
