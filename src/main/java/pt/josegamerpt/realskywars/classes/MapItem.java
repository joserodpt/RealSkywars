package pt.josegamerpt.realskywars.classes;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pt.josegamerpt.realskywars.utils.Itens;

public class MapItem {

	public GameRoom g;
	public ItemStack icon;

	public MapItem(GameRoom g) {
		this.g = g;
		makeIcon();
	}

	private void makeIcon() {
		int count = 1;
		if (g.isPlaceHolder() == true) {
			icon = Itens.createItemLore(getState(), count, "&9" + g.getName(), Arrays.asList("&fNo maps"));
		} else {
			if (g.getCurrentPlayers() > 0) {
				count = g.getCurrentPlayers();
			}

			icon = Itens.createItemLore(getState(), count, "&9" + g.getName() + " &7| &f" + g.getMode(),
					Arrays.asList("&b" + g.getCurrentPlayers() + "&f/&b" + g.getMaxPlayers(), "&fClick to join!"));
		}
	}

	private Material getState() {
		Material m;
		switch (g.getState()) {
		case WAITING:
			m = Material.LIGHT_BLUE_CONCRETE;
			break;
		case AVAILABLE:
			m = Material.GREEN_CONCRETE;
			break;
		case STARTING:
			m = Material.YELLOW_CONCRETE;
			break;
		case PLAYING:
			m = Material.RED_CONCRETE;
			break;
		case FINISHING:
			m = Material.PURPLE_CONCRETE;
			break;
		case RESETTING:
			m = Material.BLACK_CONCRETE;
			break;
		default:
			m = Material.DIRT;
		}
		return m;
	}
}
