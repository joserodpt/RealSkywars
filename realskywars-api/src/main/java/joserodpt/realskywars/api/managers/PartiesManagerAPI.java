package joserodpt.realskywars.api.managers;

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
