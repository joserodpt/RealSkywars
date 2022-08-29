package josegamerpt.realskywars.game;

import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.Solo;
import josegamerpt.realskywars.managers.WorldManager;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.logging.Level;

public class SWWorld {

    private final SWGameMode gameRoom;
    private final WorldManager wm = RealSkywars.getWorldManager();
    private final String worldName;
    private World world;

    public SWWorld(SWGameMode gameRoom, World w) {
        this.gameRoom = gameRoom;
        w.setAutoSave(false);
        this.world = w;
        this.worldName = this.world.getName();
    }

    public String getWorldName() {
        return this.worldName;
    }

    public World getWorld() {
        return this.world;
    }

    public void resetWorld() {
        //replace world
        this.wm.deleteWorld(worldName, true);

        //Copy world
        this.wm.copyWorld(worldName, WorldManager.CopyTo.ROOT);

        //Load world
        this.world = this.wm.createEmptyWorld(worldName, World.Environment.NORMAL);
        if (this.world != null) {

            WorldBorder wb = world.getWorldBorder();

            wb.setCenter(this.gameRoom.getArena().getCenter());
            wb.setSize(this.gameRoom.getBorderSize());

            this.gameRoom.setState(SWGameMode.GameState.AVAILABLE);
            Debugger.print(Solo.class, "[ROOM " + this.gameRoom.getName() + "] sucessfully resetted.");
        } else {
            RealSkywars.log(Level.SEVERE, "ERROR! Could not load " + worldName);
        }
    }

    public void clear() {
        this.wm.deleteWorld(this.getWorld().getName(), true);
    }
}
