package pt.josegamerpt.realskywars.worlds;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

public class Worlds {

	public static World createWorld(String name) {

		WorldCreator world = new WorldCreator(name);
		world.generator(new VoidWorld());

		World w = world.createWorld();
		assert w != null;
		Block b = w.getBlockAt(0, 64, 0);
		b.setType(Material.BEDROCK);

		return w;

		// WorldCreator wc = new WorldCreator(name);
		// wc.type(WorldType.FLAT);
		// wc.generatorSettings("2;0;1;");
		// wc.createWorld();
	}

}
