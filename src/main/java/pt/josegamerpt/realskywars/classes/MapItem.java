package pt.josegamerpt.realskywars.classes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Itens;

import java.util.ArrayList;
import java.util.List;

public class MapItem {

    public GameRoom g;
    public ItemStack icon;

    public MapItem(GameRoom g, GamePlayer gp) {
        this.g = g;
        makeIcon(gp);
    }

    private void makeIcon(GamePlayer p) {
        int count = 1;
        if (g.isPlaceHolder()) {
            icon = Itens.createItem(Material.BUCKET, count, LanguageManager.getString(p, Enum.TS.ITEMS_MAP_NOTFOUND_TITLE, false));
        } else {
            if (g.getPlayersCount() > 0) {
                count = g.getPlayersCount();
            }

            icon = Itens.createItemLore(getState(), count, LanguageManager.getString(p, Enum.TS.ITEMS_MAP_TITLE, false).replace("%map%", g.getName()).replace("%mode%", g.getMode().name()),
                    variableList(LanguageManager.getList(p, Enum.TL.ITEMS_MAP_DESCRIPTION)));
        }
    }

    private List<String> variableList(ArrayList<String> list) {
        List<String> a = new ArrayList<>();
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
}
