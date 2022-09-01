package josegamerpt.realskywars.world;

import josegamerpt.realskywars.game.modes.SWGameMode;
import org.bukkit.World;

public interface SWWorldEngine {

    World getWorld();

    void resetWorld(SWGameMode.ResetReason rr);

    void deleteWorld();

    void setTime(long l);

    String getName();

    SWWorld.WorldType getType();
}
