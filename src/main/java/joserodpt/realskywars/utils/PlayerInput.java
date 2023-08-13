package joserodpt.realskywars.utils;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import joserodpt.realskywars.RealSkywars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerInput {

	private static Map<UUID, PlayerInput> inputs = new HashMap<>();
	private UUID uuid;

	private List<String> texts = Text
			.color(Arrays.asList("&l&9Type in chat your input", "&fType &4cancel &fto cancel"));

	private InputRunnable runGo;
	private InputRunnable runCancel;
	private BukkitTask taskId;

	public PlayerInput(Player p, InputRunnable correct, InputRunnable cancel) {
		this.uuid = p.getUniqueId();
		p.closeInventory();
		this.runGo = correct;
		this.runCancel = cancel;
		this.taskId = new BukkitRunnable() {
			public void run() {
				p.getPlayer().sendTitle(texts.get(0), texts.get(1), 0, 21, 0);
			}
		}.runTaskTimer(RealSkywars.getPlugin(), 0L, 20);

		this.register();
	}

	private void register() {
		inputs.put(this.uuid, this);
	}

	private void unregister() {
		inputs.remove(this.uuid);
	}

	@FunctionalInterface
	public interface InputRunnable {
		void run(String input);
	}

	public static Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onPlayerChat(AsyncPlayerChatEvent event) {
				Player p = event.getPlayer();
				String input = event.getMessage();
				UUID uuid = p.getUniqueId();
				if (inputs.containsKey(uuid)) {
					PlayerInput current = inputs.get(uuid);
						event.setCancelled(true);
						try {
							if (input.equalsIgnoreCase("cancel")) {
								p.sendMessage(Text.color("&fInput canceled."));
								current.taskId.cancel();
								p.sendTitle("", "", 0, 1, 0);
								Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> current.runCancel.run(input), 3);
								current.unregister();
								return;
							}

							current.taskId.cancel();
							Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> current.runGo.run(input), 3);
							p.sendTitle("", "", 0, 1, 0);
							current.unregister();
						} catch (Exception e) {
							p.sendMessage(Text.color("&cAn error ocourred. Contact JoseGamer_PT on www.spigotmc.org"));
							RealSkywars.getPlugin().severe(e.getMessage());
						}
					}
				}
		};
	}
}
