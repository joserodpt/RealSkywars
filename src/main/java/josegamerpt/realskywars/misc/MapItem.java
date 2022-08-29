package josegamerpt.realskywars.misc;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MapItem {

    private final SWGameMode g;
    private ItemStack icon;

    public MapItem(SWGameMode g, RSWPlayer gp) {
        this.g = g;
        makeIcon(gp);
    }

    private void makeIcon(RSWPlayer p) {
        int count = 1;
        if (this.g.isPlaceHolder()) {
            this.icon = Itens.createItem(Material.BUCKET, count, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEMS_MAP_NOTFOUND_TITLE, false));
        } else {
            if (this.g.getPlayersCount() > 0) {
                count = this.g.getPlayersCount();
            }

            this.icon = Itens.createItemLore(getStateMaterial(), count, RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ITEMS_MAP_TITLE, false).replace("%map%", g.getName()).replace("%mode%", g.getGameMode().name()) + " " + this.rankedFormatting(this.g.isRanked()), variableList(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.ITEMS_MAP_DESCRIPTION)));
        }
    }

    private String rankedFormatting(Boolean ranked) {
        return ranked ? "&bRANKED" : "";
    }

    private ArrayList<String> variableList(ArrayList<String> list) {
        ArrayList<String> a = new ArrayList<>();
        for (String s : list) {
            a.add(s.replace("%players%", this.g.getPlayersCount() + "").replace("%maxplayers%", this.g.getMaxPlayers() + ""));
        }
        return a;
    }

    private Material getStateMaterial() {
        switch (this.g.getState()) {
            case WAITING:
                return this.g.isRanked() ? Material.LIGHT_BLUE_CONCRETE : Material.LIGHT_BLUE_WOOL;
            case AVAILABLE:
                return this.g.isRanked() ? Material.GREEN_CONCRETE : Material.GREEN_WOOL;
            case STARTING:
                return this.g.isRanked() ? Material.YELLOW_CONCRETE : Material.YELLOW_WOOL;
            case PLAYING:
                return this.g.isRanked() ? Material.RED_CONCRETE : Material.RED_WOOL;
            case FINISHING:
                return this.g.isRanked() ? Material.PURPLE_CONCRETE : Material.PURPLE_WOOL;
            case RESETTING:
                return this.g.isRanked() ? Material.BLACK_CONCRETE : Material.BLACK_WOOL;
            default:
                return this.g.isRanked() ? Material.BEACON : Material.DIRT;
        }
    }

    public ItemStack geIcon() {
        return this.icon;
    }
}
