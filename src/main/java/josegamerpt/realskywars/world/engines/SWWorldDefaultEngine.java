package josegamerpt.realskywars.world.engines;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.Solo;
import josegamerpt.realskywars.utils.WorldEditUtils;
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

        this.resetWorld(SWGameMode.ResetReason.ADMIN);
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void resetWorld(SWGameMode.ResetReason rr) {
        Debugger.print(WorldEditUtils.class, "Resetting " + this.getName() + " - type: " + this.getType().name());

        if (rr == SWGameMode.ResetReason.SHUTDOWN) {
        } else {//replace world
            this.deleteContent();
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
        }
    }

    private void deleteContent() {
        this.wm.deleteWorld(this.getName(), true);
    }

    @Override
    public void deleteWorld() {
        this.deleteContent();
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
