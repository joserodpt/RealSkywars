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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class PartiesManagerAPI {
    public Map<RSWPlayer, RSWPlayer> invites = new HashMap<>();

    public Boolean hasInvite(RSWPlayer p) {
        return invites.containsKey(p);
    }

    public RSWPlayer getInvite(RSWPlayer p) {
        return invites.get(p);
    }

    public abstract void sendInvite(RSWPlayer emissor, RSWPlayer recetor);

    public abstract void acceptInvite(RSWPlayer p);

    public abstract boolean checkForParties(RSWPlayer p, RSWMap swgm);
}
