package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.player.RSWGameLog;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWShopDisplayItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class PlayerManagerAPI {
    public abstract void giveItems(Player p, Items i);

    public abstract ItemStack getItem(RSWPlayer p, Items i);

    public abstract RSWPlayer loadPlayer(Player p);

    protected abstract List<RSWGameLog> processGamesList(String s);

    protected abstract String processGamesListSave(List<RSWGameLog> gamesList);

    public abstract RSWPlayer getPlayer(Player p);

    public abstract RSWPlayer getPlayer(UUID u);

    public abstract void savePlayer(RSWPlayer p, RSWPlayer.PlayerData pd);

    public abstract void setLanguage(RSWPlayer player, String s);

    public abstract void loadPlayers();

    public abstract List<RSWShopDisplayItem> getBoughtItems(RSWPlayer player, ShopManagerAPI.Categories t);

    public abstract int getPlayingPlayers(GameManagerAPI.GameModes pt);

    public abstract void stopScoreboards();

    public abstract List<RSWPlayer> getPlayers();

    public abstract void addPlayer(RSWPlayer rswPlayer);

    public abstract void removePlayer(RSWPlayer rswPlayer);

    public abstract void trackPlayer(RSWPlayer gp);

    public abstract List<UUID> getTeleporting();

    public enum Items {LOBBY, CAGE, SETUP, SPECTATOR, PROFILE, CAGESET, MAPS, SHOP, LEAVE, VOTE, SPECTATE, KIT, PLAYAGAIN, CHEST1, CHEST2}
}
