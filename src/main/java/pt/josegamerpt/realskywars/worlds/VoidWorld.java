package pt.josegamerpt.realskywars.worlds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidWorld extends ChunkGenerator {
    @Override
    @NotNull
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z,
                                       @NotNull BiomeGrid biome) {
        return createChunkData(world);
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.0D, 128.0D, 0.0D);
    }
}
