package pt.josegamerpt.realskywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.Enum.TierType;
import pt.josegamerpt.realskywars.gui.ChestTierViewer;
import pt.josegamerpt.realskywars.managers.ChestManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.nms.SignUtils;

import static org.apache.commons.lang.StringUtils.isNumeric;

public class SignGUI {

	static SignUtils sign = new SignUtils(RealSkywars.pl);

	public static void openDialog(Player target, int slot, TierType t) {
		sign.newMenu(target, Lists.newArrayList("&f", "&bInput a %", "&aabove", "&c(c to cancel)")).reopenIfFail()
				.response((player, strings) -> {
					String numb = ChatColor.stripColor(strings[0].replace("%", ""));
					if (numb.equalsIgnoreCase("c"))
					{
						Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
							@Override
							public void run() {
								ChestTierViewer r = new ChestTierViewer(player.getUniqueId(), t);
								r.openInventory(PlayerManager.getPlayer(player));
							}
						}, 3);
						return true;
					}

					if (isNumeric(numb) == true) {
						int parsed = Integer.parseInt(numb);
						ChestManager.savePercentage(t, slot, parsed);
						Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
							@Override
							public void run() {
								ChestTierViewer r = new ChestTierViewer(player.getUniqueId(), t);
								r.openInventory(PlayerManager.getPlayer(player));
							}
						}, 3);
						return true;
					}
					return false;
				}).open();
	}
}
