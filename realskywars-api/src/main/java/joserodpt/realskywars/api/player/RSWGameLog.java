package joserodpt.realskywars.api.player;

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

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RSWGameLog {

    private String map;
    private int players, seconds, kills;
    private boolean ranked, win;
    private RSWMap.GameMode gameMode;
    private String dayandtime;

    private boolean dummy = false;

    public RSWGameLog(String map, String gameMode, boolean ranked, int players, int kills, boolean win, int seconds, String dayandtime) {
        this.map = map;
        this.gameMode = RSWMap.GameMode.valueOf(gameMode);
        this.ranked = ranked;
        this.players = players;
        this.win = win;
        this.kills = kills;
        this.seconds = seconds;
        this.dayandtime = dayandtime;
    }

    public RSWGameLog() {
        this.dummy = true;
    }

    public ItemStack getItem(RSWPlayer p) {
        if (this.dummy) {
            return Itens.createItem(Material.BUCKET, 1, TranslatableLine.SEARCH_NOTFOUND_NAME.getSingle());
        }

        List<String> list = TranslatableList.GAME_LOG_LIST.get(p);

        for (String s : list) {
            list.set(list.indexOf(s), s.replace("%players%", String.valueOf(this.players))
                    .replace("%map%", this.map + " &7[&f" + this.gameMode.getDisplayName(p) + "&7]")
                    .replace("%ranked%", this.ranked ? "&a&l✔" : "&c&l❌")
                    .replace("%win%", this.win ? "&a&l✔" : "&c&l❌")
                    .replace("%kills%", String.valueOf(this.kills))
                    .replace("%time%", Text.formatSeconds(this.seconds)));
        }

        return Itens.createItem(Material.FILLED_MAP, 1, "&f&l" + this.dayandtime, list);
    }
}
