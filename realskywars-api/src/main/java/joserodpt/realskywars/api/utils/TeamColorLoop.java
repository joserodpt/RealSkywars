package joserodpt.realskywars.api.utils;

import org.bukkit.ChatColor;

public class TeamColorLoop {

    static int loop = 15;

    public static ChatColor getTeamColor() {
        --loop;
        if (loop < 0) {
            loop = 15;
        }

        return ChatColor.values()[loop];
    }

}