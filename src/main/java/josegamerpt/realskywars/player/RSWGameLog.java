package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RSWGameLog {

    public String map;
    public int players;
    public boolean ranked;
    public SWGameMode.Mode mode;
    public boolean win;
    public int seconds;
    public String dayandtime;

    private boolean dummy = false;

    public RSWGameLog(String map, SWGameMode.Mode mode, boolean ranked, int players, boolean win, int seconds, String dayandtime) {
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
        return this.dummy ? Itens.createItem(Material.BUCKET, 1, RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SEARCH_NOTFOUND_NAME)) :
                Itens.createItemLore(Material.FILLED_MAP, 1, "&f&l" + this.dayandtime, Arrays.asList("&fMap: &b" + this.map + " &f| Win: " + getWin(),
                        "&fPlayers: &b" + this.players,
                        "&fTime: &b" + Text.formatSeconds(this.seconds)));
    }

    private String getWin() {
        return this.win ? "&a&l✔" : "&c&l❌";
    }

    public String getSerializedData() {
        //mapa-modo-ranked-jogadores-ganhou-tempo-dia
        return this.map + ";" + this.mode.name() + ";" + this.ranked + ";" + this.players + ";" + this.win + ";" + this.seconds + ";" + this.dayandtime;
    }
}
