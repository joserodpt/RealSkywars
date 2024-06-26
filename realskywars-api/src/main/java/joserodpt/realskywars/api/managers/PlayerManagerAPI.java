package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWGameLog;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerManagerAPI {
    public abstract RSWPlayer loadPlayer(Player p);

    protected abstract List<RSWGameLog> processGamesList(String s);

    protected abstract String processGamesListSave(List<RSWGameLog> gamesList);

    public abstract RSWPlayer getPlayer(Player p);

    public abstract void savePlayer(RSWPlayer p, RSWPlayer.PlayerData pd);

    public abstract void setLanguage(RSWPlayer player, String s);

    public abstract void loadPlayers();

    public abstract List<RSWShopDisplayItem> getBoughtItems(RSWPlayer player, ShopManagerAPI.ShopCategory t);

    public abstract int getPlayingPlayers(MapManagerAPI.MapGamemodes pt);

    public abstract void stopScoreboards();

    public abstract Collection<RSWPlayer> getPlayers();

    public abstract void addPlayer(RSWPlayer rswPlayer);

    public abstract void removePlayer(RSWPlayer rswPlayer);

    public abstract void trackPlayer(RSWPlayer gp);

    public abstract List<UUID> getTeleporting();

    public abstract Map<UUID, RSWMap> getFastJoin();
}
