package josegamerpt.realskywars.world;

import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.world.engines.SWWorldDefaultEngine;
import josegamerpt.realskywars.world.engines.SWWorldSchematicEngine;
import org.bukkit.World;

public class SWWorld {

    private final SWWorldEngine engine;

    public SWWorld(SWGameMode gameRoom, World w, WorldType wt) {
        this.engine = (wt == WorldType.DEFAULT ? new SWWorldDefaultEngine(w, gameRoom) : new SWWorldSchematicEngine(w, gameRoom.getShematicName(), gameRoom));
    }

    public World getWorld() {
        return this.engine.getWorld();
    }

    public void resetWorld(SWGameMode.OperationReason rr) {
        this.engine.resetWorld(rr);
    }

    public void deleteWorld(SWGameMode.OperationReason rr) {
        this.engine.deleteWorld(rr);
    }

    public void setTime(long l) {
        this.engine.setTime(l);
    }

    public String getName() {
        return this.engine.getName();
    }

    public SWWorld.WorldType getType() {
        return this.engine.getType();
    }

    public enum WorldType {DEFAULT, SCHEMATIC}
}
