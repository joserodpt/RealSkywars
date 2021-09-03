package josegamerpt.realskywars.party;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;

import java.util.ArrayList;
import java.util.List;

public class Party {

    private RSWPlayer player;
    private List<RSWPlayer> members = new ArrayList<>();
    private boolean allowJoin;

    public Party(RSWPlayer player)
    {
        this.player = player;
    }

    public void playerJoin(RSWPlayer p)
    {
        p.joinParty(this.player);
        this.members.add(p);
        this.player.sendMessage(RealSkywars.getLanguageManager().getString(this.player, LanguageManager.TS.PARTY_JOIN, true).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_JOIN, true).replace("%player%", p.getDisplayName())));
    }

    public void playerLeave(RSWPlayer p)
    {
        this.player.sendMessage(RealSkywars.getLanguageManager().getString(this.player, LanguageManager.TS.PARTY_LEAVE, true).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_LEAVE, true).replace("%player%", p.getDisplayName())));
    }

    public void kick(RSWPlayer p)
    {
        this.members.remove(p);
        this.player.sendMessage(RealSkywars.getLanguageManager().getString(this.player, LanguageManager.TS.PARTY_KICK, true).replace("%player%", p.getDisplayName()));
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_KICK, true).replace("%player%", p.getDisplayName())));
    }

    public void disband() {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getLanguageManager().getString(rswPlayer, LanguageManager.TS.PARTY_DISBAND, true).replace("%player%", this.player.getDisplayName())));
        this.members.forEach(RSWPlayer::leaveParty);
        this.members.clear();
        this.player.sendMessage(RealSkywars.getLanguageManager().getString(this.player, LanguageManager.TS.PARTY_DISBAND, true).replace("%player%", this.player.getDisplayName()));
        this.player.leaveParty();

    }

    public boolean isOwner(RSWPlayer p) {
        return this.player == p;
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
}
