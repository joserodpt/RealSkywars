package josegamerpt.realskywars.worlds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidWorld extends ChunkGenerator {
    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z,
                                       BiomeGrid biome) {
        return createChunkData(world);
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.0D, 128.0D, 0.0D);
    }
}
