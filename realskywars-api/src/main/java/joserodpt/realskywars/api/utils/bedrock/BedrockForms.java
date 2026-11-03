package joserodpt.realskywars.api.utils.bedrock;

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
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realutils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Native Bedrock list forms, through Floodgate. Geyser shows a Java inventory by faking a chest
 * block next to the player, which has no room inside a 1x1 cage, so Bedrock players get these
 * instead of the inventory menus. Java players never see them.
 *
 * <p>Based on the forms Augu0107 contributed in issue #54.</p>
 */
public final class BedrockForms {

    //null until the first check, false for good once Floodgate turns out to be missing or broken
    private static Boolean floodgate;

    private BedrockForms() {
    }

    /** True only if Floodgate is installed and this player joined through Geyser. */
    public static boolean isBedrock(Player player) {
        if (player == null) {
            return false;
        }
        if (floodgate == null) {
            floodgate = Bukkit.getPluginManager().isPluginEnabled("floodgate");
        }
        if (!floodgate) {
            return false;
        }
        try {
            return FloodgateHook.isBedrock(player.getUniqueId());
        } catch (Throwable t) {
            floodgate = false;
            RealSkywarsAPI.getInstance().getLogger().warning("Floodgate API unavailable, Bedrock forms are disabled: " + t);
            return false;
        }
    }

    public static boolean isBedrock(RSWPlayer p) {
        return p != null && !p.isBot() && isBedrock(p.getPlayer());
    }

    /**
     * Sends a form with one button per entry. {@code onClick} gets the index of the tapped button
     * and runs on the main thread, and not at all if the form is closed or the player has left.
     *
     * @return false if the form could not be sent, so the caller opens its inventory menu instead
     */
    public static boolean send(RSWPlayer p, String title, String content, List<String> buttons, IntConsumer onClick) {
        Player player = p.getPlayer();
        if (player == null) {
            return false;
        }

        List<String> colored = new ArrayList<>(buttons.size());
        for (String button : buttons) {
            colored.add(Text.color(button));
        }

        try {
            return FloodgateHook.sendList(player.getUniqueId(), Text.color(title), Text.color(content), colored,
                    id -> Bukkit.getScheduler().runTask(RealSkywarsAPI.getInstance().getPlugin(), () -> {
                        if (p.getPlayer() != null && p.getPlayer().isOnline()) {
                            onClick.accept(id);
                        }
                    }));
        } catch (Throwable t) {
            RealSkywarsAPI.getInstance().getLogger().warning("Could not send the Bedrock form: " + t);
            return false;
        }
    }
}
