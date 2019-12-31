package pt.josegamerpt.realskywars.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import pt.josegamerpt.realskywars.classes.Enum.CageType;

public class Cage {

	public static void setCage(Player pl, Material m, CageType ct) {
		Location p = pl.getLocation();
		if (ct.equals(CageType.SOLO)) {
			int x = p.getBlockX();
			int y = p.getBlockY();
			int z = p.getBlockZ();

			p.getWorld().getBlockAt(x, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x, y, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 3, z).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
			p.getWorld().getBlockAt(x - 1, y, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);
		}
		if (ct.equals(CageType.TEAMS)) {
			int x = p.getBlockX();
			int y = p.getBlockY();
			int z = p.getBlockZ();

			// chao
			p.getWorld().getBlockAt(x + 1, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y - 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y - 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(m);

			y = y + 4;

			// teto
			p.getWorld().getBlockAt(x + 1, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y - 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y - 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y - 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(m);

			// paredes 1 e 3
			y = p.getBlockY();
			x = x + 2;
			p.getWorld().getBlockAt(x, y + 0, z).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x, y + 0, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 0, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);

			x = p.getBlockX();
			x = x - 2;
			p.getWorld().getBlockAt(x, y + 0, z).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x, y + 0, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z - 1).setType(m);
			p.getWorld().getBlockAt(x, y + 0, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z + 1).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z + 1).setType(m);

			// paredes 3 e 4
			x = p.getBlockX();
			z = p.getBlockZ();
			z = z - 2;
			p.getWorld().getBlockAt(x, y, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y, z).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);

			z = p.getBlockZ();
			z = z + 2;
			p.getWorld().getBlockAt(x, y, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y, z).setType(m);
			p.getWorld().getBlockAt(x, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 1, z).setType(m);
			p.getWorld().getBlockAt(x, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x - 1, y + 2, z).setType(m);
			p.getWorld().getBlockAt(x + 1, y + 2, z).setType(m);
		}
	}
}
