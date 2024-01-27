package joserodpt.realskywars.managers.world.engines;

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

import joserodpt.realskywars.Debugger;
import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.game.modes.SWGame;
import joserodpt.realskywars.game.modes.Solo;
import joserodpt.realskywars.managers.world.SWWorld;
import joserodpt.realskywars.managers.world.SWWorldEngine;
import joserodpt.realskywars.managers.world.WorldManager;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.Objects;
import java.util.logging.Level;

public class SWWorldDefaultEngine implements SWWorldEngine {

    private final WorldManager wm = RealSkywars.getPlugin().getWorldManager();
    private World world;
    private final SWGame gameRoom;
    private final String worldName;

    public SWWorldDefaultEngine(World w, SWGame gameMode) {
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
    public void resetWorld(SWGame.OperationReason rr) {
        Debugger.print(SWWorldDefaultEngine.class, "Resetting " + this.getName() + " - type: " + this.getType().name());

        if (Objects.requireNonNull(rr) == SWGame.OperationReason.SHUTDOWN) {//delete world
            this.deleteWorld(SWGame.OperationReason.SHUTDOWN);
        } else {
            this.deleteWorld(SWGame.OperationReason.RESET);
            //Copy world
            this.wm.copyWorld(this.getName(), WorldManager.CopyTo.ROOT);

            //Load world
            this.world = this.wm.createEmptyWorld(this.getName(), World.Environment.NORMAL);
            if (this.world != null) {
                WorldBorder wb = this.world.getWorldBorder();

                wb.setCenter(this.gameRoom.getArena().getCenter());
                wb.setSize(this.gameRoom.getBorderSize());

                this.gameRoom.setState(SWGame.GameState.AVAILABLE);
                Debugger.print(Solo.class, "[ROOM " + this.gameRoom.getName() + "] sucessfully resetted.");
            } else {
                RealSkywars.getPlugin().log(Level.SEVERE, "ERROR! Could not load " + this.getName());
            }
        }
    }

    @Override
    public void deleteWorld(SWGame.OperationReason rr) {
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
