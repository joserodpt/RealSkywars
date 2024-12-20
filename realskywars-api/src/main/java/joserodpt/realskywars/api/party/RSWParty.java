package joserodpt.realskywars.api.party;

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

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.ArrayList;
import java.util.List;

public class RSWParty {

    private final RSWPlayer owner;
    private final List<RSWPlayer> members = new ArrayList<>();
    private boolean allowJoin;

    public RSWParty(RSWPlayer owner) {
        this.owner = owner;
    }

    public void playerJoin(RSWPlayer p) {
        p.joinParty(this.owner);
        this.members.add(p);

        this.owner.sendMessage(TranslatableLine.PARTY_JOIN.get(this.owner).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.PARTY_JOIN.get(rswPlayer).replace("%player%", p.getDisplayName())));
    }

    public void playerLeave(RSWPlayer p) {
        this.owner.sendMessage(TranslatableLine.PARTY_LEAVE.get(this.owner).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.PARTY_LEAVE.get(rswPlayer).replace("%player%", p.getDisplayName())));
    }

    public void kick(RSWPlayer p) {
        this.members.remove(p);
        this.owner.sendMessage(TranslatableLine.PARTY_KICK.get(this.owner).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.PARTY_KICK.get(rswPlayer).replace("%player%", p.getDisplayName())));
    }

    public void disband() {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.PARTY_DISBAND.get(rswPlayer).replace("%player%", this.owner.getDisplayName())));
        this.members.forEach(RSWPlayer::leaveParty);
        this.members.clear();
        this.owner.sendMessage(TranslatableLine.PARTY_DISBAND.get(this.owner).replace("%player%", this.owner.getDisplayName()));
        this.owner.leaveParty();
    }

    public boolean isOwner(RSWPlayer p) {
        return this.owner == p;
    }

    public List<RSWPlayer> getMembers() {
        return this.members;
    }

    public void setAllowJoin(boolean b) {
        this.allowJoin = b;
    }

    public boolean allowJoin() {
        return this.allowJoin;
    }

    public void sendMessage(RSWPlayer p, String s) {
        this.owner.sendMessage("&3[PARTY] " + p.getDisplayName() + " - " + s);
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage("&3[PARTY] " + p.getDisplayName() + " - " + s));
    }
}
