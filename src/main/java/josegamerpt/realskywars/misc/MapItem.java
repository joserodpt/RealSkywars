package josegamerpt.realskywars.misc;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MapItem {

    private SWGameMode g;
    private ItemStack icon;

    public MapItem(SWGameMode g, RSWPlayer gp) {
        this.g = g;
        makeIcon(gp);
    }

    private void makeIcon(RSWPlayer p) {
        int count = 1;
        if (g.isPlaceHolder()) {
            icon = Itens.createItem(Material.BUCKET, count, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEMS_MAP_NOTFOUND_TITLE, false));
        } else {
            if (g.getPlayersCount() > 0) {
                count = g.getPlayersCount();
            }

            icon = Itens.createItemLore(getState(), count, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEMS_MAP_TITLE, false).replace("%map%", g.getName()).replace("%mode%", g.getGameMode().name()),
                    variableList(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.ITEMS_MAP_DESCRIPTION)));
        }
    }

    private ArrayList<String> variableList(ArrayList<String> list) {
        ArrayList<String> a = new ArrayList<>();
        for (String s : list) {
            a.add(s.replace("%players%", g.getPlayersCount() + "").replace("%maxplayers%", g.getMaxPlayers() + ""));
        }
        return a;
    }

    private Material getState() {
        Material m;
        switch (g.getState()) {
            case WAITING:
                m = Material.LIGHT_BLUE_CONCRETE;
                break;
            case AVAILABLE:
                m = Material.GREEN_CONCRETE;
                break;
            case STARTING:
                m = Material.YELLOW_CONCRETE;
                break;
            case PLAYING:
                m = Material.RED_CONCRETE;
                break;
            case FINISHING:
                m = Material.PURPLE_CONCRETE;
                break;
            case RESETTING:
                m = Material.BLACK_CONCRETE;
                break;
            default:
                m = Material.DIRT;
        }
        return m;
    }

    public ItemStack geIcon() {
        return this.icon;
    }
}
