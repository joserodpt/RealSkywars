package joserodpt.realskywars.api.utils;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInput {

    //read and taken from the async chat thread, written from the main thread
    private static final Map<UUID, PlayerInput> inputs = new ConcurrentHashMap<>();
    private final UUID uuid;

    private final List<String> texts = Text
            .color(Arrays.asList("&l&9Type in chat your input", "&fType &4cancel &fto cancel"));

    private final InputRunnable runGo;
    private final InputRunnable runCancel;
    private final BukkitTask taskId;

    public PlayerInput(Player p, InputRunnable correct, InputRunnable cancel) {
        this.uuid = p.getUniqueId();
        p.closeInventory();
        this.runGo = correct;
        this.runCancel = cancel;
        this.taskId = new BukkitRunnable() {
            public void run() {
                p.getPlayer().sendTitle(texts.get(0), texts.get(1), 0, 21, 0);
            }
        }.runTaskTimer(RealSkywarsAPI.getInstance().getPlugin(), 0L, 20);

        this.register();
    }

    private void register() {
        PlayerInput previous = inputs.put(this.uuid, this);
        //a new prompt replaces an unanswered one, whose title task would otherwise never stop
        if (previous != null) {
            previous.taskId.cancel();
        }
    }

    /**
     * Drops every pending prompt, used on reload.
     */
    public static void clearAll() {
        for (UUID uuid : inputs.keySet()) {
            PlayerInput current = inputs.remove(uuid);
            if (current != null) {
                current.taskId.cancel();
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.sendTitle("", "", 0, 1, 0);
                }
            }
        }
    }

    @FunctionalInterface
    public interface InputRunnable {
        void run(String input) throws IOException;
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                Player p = event.getPlayer();
                String input = ChatColor.stripColor(Text.color(event.getMessage()));

                //taken in one step, so two quick messages can't both answer the same prompt
                PlayerInput current = inputs.remove(p.getUniqueId());
                if (current != null) {
                    event.setCancelled(true);
                    //this is the async chat thread: the task, the title and the callbacks belong on the main one
                    Bukkit.getScheduler().runTask(RealSkywarsAPI.getInstance().getPlugin(), () -> handleInput(p, input, current));
                }
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                //otherwise the title task runs forever, and their first chat line after rejoining would
                //answer a prompt from the previous session (like the reset data confirmation)
                PlayerInput current = inputs.remove(event.getPlayer().getUniqueId());
                if (current != null) {
                    current.taskId.cancel();
                }
            }
        };
    }

    private static void handleInput(Player p, String input, PlayerInput current) {
        try {
            current.taskId.cancel();
            p.sendTitle("", "", 0, 1, 0);
            boolean cancelled = input.equalsIgnoreCase("cancel");
            if (cancelled) {
                p.sendMessage(Text.color("&fInput canceled."));
            }
            InputRunnable r = cancelled ? current.runCancel : current.runGo;
            Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywarsAPI.getInstance().getPlugin(), () -> {
                //the prompt was answered by someone who has since logged out
                if (!p.isOnline()) {
                    return;
                }
                try {
                    r.run(input);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("An error ocourred while running the " + (cancelled ? "cancel" : "runGo") + " runnable.");
                    Bukkit.getLogger().severe(e.getMessage());
                }
            }, 3);
        } catch (Exception e) {
            p.sendMessage(Text.color("&cAn error ocourred. Contact JoseGamer_PT on www.spigotmc.org"));
            RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
        }
    }
}
