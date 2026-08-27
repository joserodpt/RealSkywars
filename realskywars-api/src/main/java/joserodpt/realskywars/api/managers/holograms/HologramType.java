package joserodpt.realskywars.api.managers.holograms;

/*
 *   _____            _  _____ _
 *  |  __ \\          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \\/ _` | |\\___ \\| |/ / | | \\ \\ /\\ / / _` | '__/ __|
 *  | | \\ \\  __/ (_| | |____) |   <| |_| |\\ V  V / (_| | |  \\__ \\
 *  |_|  \\_\\___|\\__,_|_|_____/|_|\\_\\\\__, | \\_/\\_/ \\__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;

/**
 * The kinds of lobby hologram that can be placed in the world.
 */
public enum HologramType {
    LAST_WINNER(Material.GOLD_BLOCK, "&6&lLast Winner", "Last-Winner"),
    TOP_WINS_SOLO(Material.DIAMOND, "&b&lTop Wins (Solo)", "Top-Wins-Solo"),
    TOP_WINS_TEAMS(Material.EMERALD, "&a&lTop Wins (Teams)", "Top-Wins-Teams"),
    TOP_KILLS(Material.IRON_SWORD, "&c&lTop Kills", "Top-Kills"),
    SERVER_INFO(Material.BEACON, "&9&lServer Info", "Server-Info"),
    CUSTOM(Material.PAPER, "&f&lCustom", "Custom");

    private final Material icon;
    private final String displayName;
    private final String configName;

    HologramType(Material icon, String displayName, String configName) {
        this.icon = icon;
        this.displayName = displayName;
        this.configName = configName;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getDisplayName() {
        return Text.color(this.displayName);
    }

    /** The route this type's lines live under in holograms.yml. */
    public String getConfigName() {
        return this.configName;
    }

    public static HologramType getByName(String name) {
        if (name == null) {
            return null;
        }
        for (HologramType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
