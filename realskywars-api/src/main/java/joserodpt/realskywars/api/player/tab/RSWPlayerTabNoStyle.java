package joserodpt.realskywars.api.player.tab;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RSWPlayerTabNoStyle implements RSWPlayerTabInterface {

    private final RSWPlayer player;
    private final List<Player> show = new ArrayList<>();

    public RSWPlayerTabNoStyle(RSWPlayer player) {
        this.player = player;
        clear();
        updateRoomTAB();
    }

    @Override
    public void addPlayers(Player p) {
        if (p.getUniqueId() != this.player.getUUID() && !this.show.contains(p)) {
            this.show.add(p);
        }
    }

    @Override
    public void addPlayers(List<Player> p) {
        this.show.addAll(p);
    }

    @Override
    public void removePlayers(Player p) {
        this.show.remove(p);
    }

    @Override
    public void reset() {
        this.show.addAll(Bukkit.getOnlinePlayers());
    }

    @Override
    public void clear() {
        this.show.clear();
    }

    @Override
    public void setHeaderFooter(String h, String f) {
    }


    @Override
    public void updateRoomTAB() {
        if (!this.player.isBot()) {
            Bukkit.getOnlinePlayers().forEach(pl -> this.player.hidePlayer(RealSkywarsAPI.getInstance().getPlugin(), pl));
            this.show.forEach(rswPlayer -> this.player.showPlayer(RealSkywarsAPI.getInstance().getPlugin(), rswPlayer));
        }
    }
}
