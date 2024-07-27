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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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

    public ItemStack getItem(RSWPlayer p) { //TODO TRANSLATE
        return this.dummy ? Itens.createItem(Material.BUCKET, 1, TranslatableLine.SEARCH_NOTFOUND_NAME.getSingle()) :
                Itens.createItem(Material.FILLED_MAP, 1, "&f&l" + this.dayandtime, Arrays.asList("&fMap: &b" + this.map + " &7[&f" + this.gameMode.getDisplayName(p) + "&7]",
                        "&fPlayers: &b" + this.players,
                        "&fRanked: " + (this.ranked ? "&a&l✔" : "&c&l❌"),
                        "&fWin: " + (this.win ? "&a&l✔" : "&c&l❌"),
                        "&fKills: &b" + this.kills,
                        "&fTime: &b" + Text.formatSeconds(this.seconds)));
    }
}
