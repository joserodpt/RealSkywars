package josegamerpt.realskywars.party;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;

import java.util.ArrayList;
import java.util.List;

public class Party {

    private final RSWPlayer owner;
    private final List<RSWPlayer> members = new ArrayList<>();
    private boolean allowJoin;

    public Party(RSWPlayer owner) {
        this.owner = owner;
    }

    public void playerJoin(RSWPlayer p) {
        p.joinParty(this.owner);
        this.members.add(p);
        this.owner.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.owner, LanguageManager.TS.PARTY_JOIN, true).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_JOIN, true).replace("%player%", p.getDisplayName())));
    }

    public void playerLeave(RSWPlayer p) {
        this.owner.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.owner, LanguageManager.TS.PARTY_LEAVE, true).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_LEAVE, true).replace("%player%", p.getDisplayName())));
    }

    public void kick(RSWPlayer p) {
        this.members.remove(p);
        this.owner.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.owner, LanguageManager.TS.PARTY_KICK, true).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_KICK, true).replace("%player%", p.getDisplayName())));
    }

    public void disband() {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_DISBAND, true).replace("%player%", this.owner.getDisplayName())));
        this.members.forEach(RSWPlayer::leaveParty);
        this.members.clear();
        this.owner.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this.owner, LanguageManager.TS.PARTY_DISBAND, true).replace("%player%", this.owner.getDisplayName()));
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
