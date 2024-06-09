package joserodpt.realskywars.plugin.managers;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.LobbyManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LobbyManager extends LobbyManagerAPI {

    public LobbyManager(RealSkywarsAPI rs) {
    }

    private Location lobbyLOC;
    private Boolean loginTP = true;

    @Override
    public void loadLobby() {
        this.loginTP = RSWConfig.file().getBoolean("Config.Auto-Teleport-To-Lobby");
        if (RSWConfig.file().isSection("Config.Lobby")) {
            double x = RSWConfig.file().getDouble("Config.Lobby.X");
            double y = RSWConfig.file().getDouble("Config.Lobby.Y");
            double z = RSWConfig.file().getDouble("Config.Lobby.Z");
            float yaw = RSWConfig.file().getFloat("Config.Lobby.Yaw");
            float pitch = RSWConfig.file().getFloat("Config.Lobby.Pitch");
            World world = Bukkit.getServer().getWorld(RSWConfig.file().getString("Config.Lobby.World"));
            this.lobbyLOC = new Location(world, x, y, z, yaw, pitch);
        }
    }

    @Override
    public void tpToLobby(Player player) {
        if (this.lobbyLOC != null && player != null) {
            player.teleport(this.lobbyLOC);
        }
    }

    @Override
    public void tpToLobby(RSWPlayer p) {
        if (this.lobbyLOC != null) {
            tpToLobby(p.getPlayer());
            TranslatableLine.LOBBY_TELEPORT.send(p, true);
            RSWPlayerItems.LOBBY.giveSet(p);
        } else {
            TranslatableLine.LOBBYLOC_NOT_SET.send(p, true);
        }
    }

    @Override
    public Location getLobbyLocation() {
        return this.lobbyLOC;
    }

    @Override
    public boolean scoreboardInLobby() {
        return RSWConfig.file().getBoolean("Config.Scoreboard-In-Lobby");
    }

    @Override
    public void setLobbyLoc(Location location) {
        this.lobbyLOC = location;
    }

    @Override
    public boolean tpLobbyOnJoin() {
        return loginTP;
    }

    @Override
    public boolean isInLobby(World w) {
        return this.lobbyLOC != null && this.lobbyLOC.getWorld().equals(w);
    }

}
