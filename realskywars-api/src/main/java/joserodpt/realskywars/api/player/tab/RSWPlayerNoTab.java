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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import org.bukkit.entity.Player;

import java.util.List;

public class RSWPlayerNoTab implements RSWPlayerTabInterface {

    public RSWPlayerNoTab() {
    }

    @Override
    public void addPlayers(Player p) {
    }

    @Override
    public void addPlayers(List<Player> p) {
    }

    @Override
    public void removePlayers(Player p) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void setHeaderFooter(String h, String f) {
    }

    @Override
    public void updateRoomTAB() {
    }
}
