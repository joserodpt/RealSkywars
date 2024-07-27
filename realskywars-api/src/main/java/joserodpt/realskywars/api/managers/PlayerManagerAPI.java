package joserodpt.realskywars.api.managers;

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

import joserodpt.realskywars.api.config.RSWLanguage;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerManagerAPI {
    public abstract void loadPlayer(Player p);

    public abstract RSWPlayer getPlayer(Player p);

    public abstract void savePlayer(RSWPlayer p, RSWPlayer.PlayerData pd);

    public abstract void setLanguage(RSWPlayer player, RSWLanguage lang);

    public abstract void loadPlayers();

    public abstract int getPlayingPlayers(MapManagerAPI.MapGamemodes pt);

    public abstract void stopScoreboards();

    public abstract Collection<RSWPlayer> getPlayers();

    public abstract void addPlayer(RSWPlayer rswPlayer);

    public abstract void removePlayer(RSWPlayer rswPlayer);

    public abstract void trackPlayer(RSWPlayer gp);

    public abstract List<UUID> getTeleporting();

    public abstract Map<UUID, RSWMap> getFastJoin();
}
