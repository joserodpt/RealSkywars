package josegamerpt.realskywars.world.engines;

import com.sk89q.worldedit.world.block.BlockTypes;
import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.Solo;
import josegamerpt.realskywars.utils.WorldEditUtils;
import josegamerpt.realskywars.world.SWWorld;
import josegamerpt.realskywars.world.SWWorldEngine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.logging.Level;

public class SWWorldSchematicEngine implements SWWorldEngine {

    private final World world;
    private final SWGameMode gameRoom;
    private final String schematicName;

    public SWWorldSchematicEngine(World w, SWGameMode gameMode) {
        this.schematicName = w.getName();
        this.world = w;
        this.world.setAutoSave(false);
        this.gameRoom = gameMode;

        this.resetWorld(SWGameMode.ResetReason.NORMAL);
    }


    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void resetWorld(SWGameMode.ResetReason rr) {
        Debugger.print(WorldEditUtils.class, "Resetting " + this.getName() + " - type: " + this.getType().name());

        if (rr == SWGameMode.ResetReason.SHUTDOWN) {
        } else {//delete content
            this.deleteContent();
            //place schematic
            WorldEditUtils.pasteSchematic(this.schematicName, new Location(this.world, 0, 64, 0));

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

    @Override
    public void deleteWorld() {
        this.deleteContent();
    }

    private void deleteContent() {
        //delete content
        RealSkywars.getWorldManager().clearItems(world);
        WorldEditUtils.setBlocks(this.gameRoom.getPOS1(), this.gameRoom.getPOS2(), BlockTypes.AIR);
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
