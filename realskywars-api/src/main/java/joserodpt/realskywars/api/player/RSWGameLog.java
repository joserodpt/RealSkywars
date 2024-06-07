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
 * @author José Rodrigues
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

    public String map;
    public int players, seconds;
    public boolean ranked, win;
    public RSWMap.Mode mode;
    public String dayandtime;

    private boolean dummy = false;

    public RSWGameLog(String map, RSWMap.Mode mode, boolean ranked, int players, boolean win, int seconds, String dayandtime) {
        this.map = map;
        this.mode = mode;
        this.ranked = ranked;
        this.players = players;
        this.win = win;
        this.seconds = seconds;
        this.dayandtime = dayandtime;
    }

    public RSWGameLog() {
        this.dummy = true;
    }

    public ItemStack getItem() {
        return this.dummy ? Itens.createItem(Material.BUCKET, 1, TranslatableLine.SEARCH_NOTFOUND_NAME.get()) :
                Itens.createItem(Material.FILLED_MAP, 1, "&f&l" + this.dayandtime, Arrays.asList("&fMap: &b" + this.map + " &f| Win: " + getWin(),
                        "&fPlayers: &b" + this.players,
                        "&fTime: &b" + Text.formatSeconds(this.seconds)));
    }

    private String getWin() {
        return this.win ? "&a&l✔" : "&c&l❌";
    }

    public String getSerializedData() {
        //mapa-modo-ranked-jogadores-ganhou-tempo-dia
        return this.map + ";" + (this.mode == null ? "Unknown" : this.mode.name()) + ";" + this.ranked + ";" + this.players + ";" + this.win + ";" + this.seconds + ";" + this.dayandtime;
    }
}
