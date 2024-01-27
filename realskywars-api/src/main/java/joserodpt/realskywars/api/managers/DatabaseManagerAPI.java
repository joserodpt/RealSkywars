package joserodpt.realskywars.api.managers;

import com.j256.ormlite.dao.Dao;
import joserodpt.realskywars.api.database.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DatabaseManagerAPI {
    @NotNull
    protected abstract String getDatabaseURL();

    protected abstract void getPlayerData();

    public abstract PlayerData getPlayerData(Player p);

    public abstract void savePlayerData(PlayerData playerData, boolean async);

    public abstract void deletePlayerData(PlayerData playerData, boolean async);

    public abstract Dao<PlayerData, UUID> getQueryDao();
}
