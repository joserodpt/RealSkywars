package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.HashMap;

public abstract class PartiesManagerAPI {
    public HashMap<RSWPlayer, RSWPlayer> invites = new HashMap<>();

    public Boolean hasInvite(RSWPlayer p) {
        return invites.containsKey(p);
    }

    public RSWPlayer getInvite(RSWPlayer p) {
        return invites.get(p);
    }

    public abstract void sendInvite(RSWPlayer emissor, RSWPlayer recetor);

    public abstract void acceptInvite(RSWPlayer p);

    public abstract boolean checkForParties(RSWPlayer p, RSWGame swgm);
}
