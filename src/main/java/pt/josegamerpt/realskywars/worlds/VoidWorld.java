package pt.josegamerpt.realskywars.worlds;

import java.util.Random;


import com.sun.istack.internal.NotNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

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
