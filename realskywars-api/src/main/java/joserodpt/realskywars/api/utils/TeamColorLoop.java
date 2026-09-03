package joserodpt.realskywars.api.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class TeamColorLoop {

    /**
     * The colours teams cycle through, in order. Only the 16 colour codes are listed - the
     * formatting codes that follow them in {@link ChatColor} are not valid team colours.
     */
    private static final ChatColor[] TEAM_COLORS = {
            ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.GOLD, ChatColor.DARK_PURPLE,
            ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_RED,
            ChatColor.WHITE, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLACK
    };

    static int loop = 15;

    public static ChatColor getTeamColor() {
        --loop;
        if (loop < 0) {
            loop = 15;
        }

        return ChatColor.values()[loop];
    }

    /**
     * The colour for a team, derived from its id so that "Team 1" always looks the same on every
     * map and across restarts. {@link #getTeamColor()} mutates a global counter and cannot do that.
     *
     * @param index the team id (1 based).
     */
    public static ChatColor colorForIndex(int index) {
        return TEAM_COLORS[Math.floorMod(index - 1, TEAM_COLORS.length)];
    }

    /**
     * The wool matching {@link #colorForIndex(int)}, for use as a team's menu icon.
     */
    public static Material woolForIndex(int index) {
        switch (colorForIndex(index)) {
            case RED:
            case DARK_RED:
                return Material.RED_WOOL;
            case BLUE:
                return Material.LIGHT_BLUE_WOOL;
            case DARK_BLUE:
                return Material.BLUE_WOOL;
            case GREEN:
                return Material.LIME_WOOL;
            case DARK_GREEN:
                return Material.GREEN_WOOL;
            case YELLOW:
                return Material.YELLOW_WOOL;
            case GOLD:
                return Material.ORANGE_WOOL;
            case LIGHT_PURPLE:
                return Material.PINK_WOOL;
            case DARK_PURPLE:
                return Material.PURPLE_WOOL;
            case AQUA:
                return Material.CYAN_WOOL;
            case DARK_AQUA:
                return Material.CYAN_WOOL;
            case GRAY:
                return Material.LIGHT_GRAY_WOOL;
            case DARK_GRAY:
                return Material.GRAY_WOOL;
            case BLACK:
                return Material.BLACK_WOOL;
            default:
                return Material.WHITE_WOOL;
        }
    }
}
