package josegamerpt.realskywars.world.engines;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.Solo;
import josegamerpt.realskywars.world.SWWorld;
import josegamerpt.realskywars.world.SWWorldEngine;
import josegamerpt.realskywars.world.WorldManager;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.logging.Level;

public class SWWorldDefaultEngine implements SWWorldEngine {

    private final WorldManager wm = RealSkywars.getWorldManager();
    private World world;
    private final SWGameMode gameRoom;
    private final String worldName;

    public SWWorldDefaultEngine(World w, SWGameMode gameMode) {
        this.worldName = w.getName();
        this.world = w;
        this.world.setAutoSave(false);
        this.gameRoom = gameMode;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void resetWorld(SWGameMode.OperationReason rr) {
        Debugger.print(SWWorldDefaultEngine.class, "Resetting " + this.getName() + " - type: " + this.getType().name());

        switch (rr) {
            case SHUTDOWN:
                //delete world
                this.deleteWorld(SWGameMode.OperationReason.SHUTDOWN);
                break;
            default:
                this.deleteWorld(SWGameMode.OperationReason.RESET);
                //Copy world
                this.wm.copyWorld(this.getName(), WorldManager.CopyTo.ROOT);

                //Load world
                this.world = this.wm.createEmptyWorld(this.getName(), World.Environment.NORMAL);
                if (this.world != null) {
                    WorldBorder wb = this.world.getWorldBorder();

                    wb.setCenter(this.gameRoom.getArena().getCenter());
                    wb.setSize(this.gameRoom.getBorderSize());

                    this.gameRoom.setState(SWGameMode.GameState.AVAILABLE);
                    Debugger.print(Solo.class, "[ROOM " + this.gameRoom.getName() + "] sucessfully resetted.");
                } else {
                    RealSkywars.log(Level.SEVERE, "ERROR! Could not load " + this.getName());
                }
                break;
        }
    }

    @Override
    public void deleteWorld(SWGameMode.OperationReason rr) {
        switch (rr) {
            case LOAD:
                break;
            case SHUTDOWN:
            case RESET:
                this.wm.deleteWorld(this.getName(), true);
                break;
        }
    }

    @Override
    public void setTime(long l) {
        this.world.setTime(l);
    }

    @Override
    public String getName() {
        return this.world != null ? this.world.getName() : this.worldName;
    }

    @Override
    public SWWorld.WorldType getType() {
        return SWWorld.WorldType.DEFAULT;
    }
}
