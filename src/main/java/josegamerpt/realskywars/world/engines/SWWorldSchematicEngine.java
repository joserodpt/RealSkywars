package josegamerpt.realskywars.world.engines;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.sk89q.worldedit.world.block.BlockTypes;
import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.Solo;
import josegamerpt.realskywars.utils.WorldEditUtils;
import josegamerpt.realskywars.world.SWWorld;
import josegamerpt.realskywars.world.SWWorldEngine;
import josegamerpt.realskywars.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.logging.Level;

public class SWWorldSchematicEngine implements SWWorldEngine {

    private final World world;
    private final SWGameMode gameRoom;
    private final String schematicName;
    private final WorldManager wm = RealSkywars.getPlugin().getWorldManager();

    public SWWorldSchematicEngine(World w, String sname, SWGameMode gameMode) {
        this.schematicName = sname;
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
        Debugger.print(SWWorldSchematicEngine.class, "Resetting " + this.getName() + " - type: " + this.getType().name());

        switch (rr) {
            case SHUTDOWN:
                //no need
                break;
            case RESET:
                this.deleteWorld(SWGameMode.OperationReason.RESET);
            case LOAD:
                if (this.world != null) {
                    //place schematic
                    WorldEditUtils.pasteSchematic(this.schematicName, new Location(this.world, 0, 64, 0));

                    WorldBorder wb = this.world.getWorldBorder();

                    wb.setCenter(this.gameRoom.getArena().getCenter());
                    wb.setSize(this.gameRoom.getBorderSize());

                    this.gameRoom.setState(SWGameMode.GameState.AVAILABLE);
                    Debugger.print(Solo.class, "[ROOM " + this.gameRoom.getName() + "] sucessfully resetted.");
                } else {
                    RealSkywars.getPlugin().log(Level.SEVERE, "ERROR! Could not load " + this.getName());
                }
                break;
        }
    }

    @Override
    public void deleteWorld(SWGameMode.OperationReason rr) {
        RealSkywars.getPlugin().getWorldManager().clearItems(world);
        switch (rr) {
            case LOAD:
            case SHUTDOWN:
                this.wm.deleteWorld(this.getName(), true);
                break;
            case RESET:
                WorldEditUtils.setBlocks(this.gameRoom.getPOS1(), this.gameRoom.getPOS2(), BlockTypes.AIR);
                break;
        }
    }

    @Override
    public void setTime(long l) {
        this.world.setTime(l);
    }

    @Override
    public String getName() {
        return this.world != null ? this.world.getName() : this.schematicName;
    }

    @Override
    public SWWorld.WorldType getType() {
        return SWWorld.WorldType.SCHEMATIC;
    }
}
