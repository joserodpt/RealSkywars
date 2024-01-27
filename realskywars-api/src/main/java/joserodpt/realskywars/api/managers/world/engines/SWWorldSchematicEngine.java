package joserodpt.realskywars.api.managers.world.engines;

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
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.managers.WorldManagerAPI;
import joserodpt.realskywars.api.managers.world.SWWorldEngine;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class SWWorldSchematicEngine implements SWWorldEngine {

    private final World world;
    private final RSWGame gameRoom;
    private final String schematicName;
    private final WorldManagerAPI wm = RealSkywarsAPI.getInstance().getWorldManagerAPI();

    public SWWorldSchematicEngine(World w, String sname, RSWGame gameMode) {
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
    public void resetWorld(RSWGame.OperationReason rr) {
        Debugger.print(SWWorldSchematicEngine.class, "Resetting " + this.getName() + " - type: " + this.getType().name());

        switch (rr) {
            case SHUTDOWN:
                //no need
                break;
            case RESET:
                this.deleteWorld(RSWGame.OperationReason.RESET);
            case LOAD:
                if (this.world != null) {
                    //place schematic
                    WorldEditUtils.pasteSchematic(this.schematicName, new Location(this.world, 0, 64, 0));

                    WorldBorder wb = this.world.getWorldBorder();

                    wb.setCenter(this.gameRoom.getArena().getCenter());
                    wb.setSize(this.gameRoom.getBorderSize());

                    this.gameRoom.setState(RSWGame.GameState.AVAILABLE);
                } else {
                    RealSkywarsAPI.getInstance().getLogger().severe( "ERROR! Could not load " + this.getName());
                }
                break;
        }
    }

    @Override
    public void deleteWorld(RSWGame.OperationReason rr) {
        RealSkywarsAPI.getInstance().getWorldManagerAPI().clearItems(world);
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
    public RSWWorld.WorldType getType() {
        return RSWWorld.WorldType.SCHEMATIC;
    }
}
