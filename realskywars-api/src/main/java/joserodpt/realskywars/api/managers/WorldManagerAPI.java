package joserodpt.realskywars.api.managers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public abstract class WorldManagerAPI {
    public abstract void clearItems(World w);

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
