package joserodpt.realskywars.api.managers;

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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public abstract class WorldManagerAPI {
    public abstract File[] verifiedListFiles(File directory) throws IOException;

    public abstract boolean isSymlink(File file);

    public abstract void copyWorld(String name, CopyTo t);

    public abstract World createEmptyWorld(String name, World.Environment environment);

    public abstract boolean loadWorld(String worldName, World.Environment environment);

    public abstract void unloadWorld(String w, boolean save);

    protected abstract void tpToLobby(Player p);

    public abstract void copyWorld(File source, File target);

    public abstract void deleteWorld(String name, boolean removeFile);

    public abstract void deleteDirectory(File directory) throws IOException;

    protected abstract void cleanDirectory(File directory) throws IOException;

    public abstract void forceDelete(File file) throws IOException;

    public void clearDroppedItems(World world) {
        world.getEntities().stream().filter(e -> e.getType() == EntityType.DROPPED_ITEM).forEach(org.bukkit.entity.Entity::remove);
    }

    public enum CopyTo {ROOT, RSW_FOLDER}

    public class VoidWorld extends ChunkGenerator {
        @Override
        public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
            return createChunkData(world);
        }

        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 0.0D, 128.0D, 0.0D);
        }
    }
}
