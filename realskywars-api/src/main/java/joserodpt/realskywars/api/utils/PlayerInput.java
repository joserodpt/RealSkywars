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
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Arrays;

/**
 * Asks a player to type something, through RealUtils' prompt: a dialog's text box on servers that
 * have them, the chat everywhere else. Kept here, with its own constructor and
 * {@link InputRunnable}, so every screen that asks for input is unchanged.
 */
public class PlayerInput {

    public PlayerInput(final Player p, final InputRunnable correct, final InputRunnable cancel) {
        new joserodpt.realutils.input.PlayerInput(p, true, correct::run, cancel::run);
    }

    /** Where the prompt's words come from, in each player's own language. Called once the plugin is enabled. */
    public static void setup(final Plugin plugin) {
        joserodpt.realutils.input.PlayerInput.setup(plugin,
                p -> Arrays.asList("&l&9Type in chat your input", "&fType &4cancel &fto cancel"),
                p -> Arrays.asList(line(p, TranslatableLine.DIALOG_INPUT_TITLE), line(p, TranslatableLine.DIALOG_INPUT_DESCRIPTION)),
                p -> p.sendMessage(Text.color(line(p, TranslatableLine.DIALOG_INPUT_CANCELLED))),
                p -> p.sendMessage(Text.color("&cAn error occurred.")));
    }

    /** A line in the player's language, or the default one for a player RealSkywars doesn't know yet. */
    public static String line(final Player p, final TranslatableLine line) {
        final RSWPlayer player = RealSkywarsAPI.getInstance().getPlayerManagerAPI().getPlayer(p);
        return player == null ? line.getDefault() : line.get(player);
    }

    /** Drops every pending prompt, used on reload. */
    public static void clearAll() {
        joserodpt.realutils.input.PlayerInput.cancelAll();
    }

    public static Listener getListener() {
        return joserodpt.realutils.input.PlayerInput.getListener();
    }

    @FunctionalInterface
    public interface InputRunnable {
        void run(String input) throws IOException;
    }
}
