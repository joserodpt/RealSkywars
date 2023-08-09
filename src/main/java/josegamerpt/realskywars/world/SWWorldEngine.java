package josegamerpt.realskywars.world;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

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
