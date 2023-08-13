package joserodpt.realskywars.game.modes.teams;

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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.cages.Cage;
import joserodpt.realskywars.cages.TeamCage;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.Text;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final int id;
    private final int maxMembers;
    private final TeamCage tc;
    private final ArrayList<RSWPlayer> members = new ArrayList<>();
    private Boolean eliminated = false, playing = false;

    public Team(int i, int maxMemb, Location c, String worldName) {
        this.id = i;
        this.tc = new TeamCage(i, c.getBlockX(), c.getBlockY(), c.getBlockZ(), worldName, maxMemb);
        this.maxMembers = maxMemb;
    }

    public void addPlayer(RSWPlayer p) {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.TEAM_BROADCAST_JOIN, true).replace("%player%", p.getName())));

        this.members.add(p);
        p.setTeam(this);
        if (members.size() == 1 && p.getPlayer() != null) {
            this.tc.addPlayer(p);
        }

        p.teleport(this.tc.getLoc());
        p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.TEAM_JOIN, true).replace("%team%", getName()));
    }

    public void removeMember(RSWPlayer p) {
        this.members.remove(p);

        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.TEAM_BROADCAST_LEAVE, true).replace("%player%", p.getName())));

        if (this.playing && members.isEmpty()) {
            this.eliminated = true;
        }
        p.setTeam(null);
        p.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.TEAM_LEAVE, true).replace("%team%", getName()));
    }

    public Boolean isTeamFull() {
        return this.maxMembers == getMembers().size();
    }

    public void sendMessage(String s) {
        this.members.forEach(rswPlayer -> rswPlayer.sendCenterMessage(Text.color(s)));
    }

    public String getName() {
        return "Team " + id;
    }

    public String getNames() {
        List<String> list = new ArrayList<>();
        this.members.forEach(rswPlayer -> list.add(rswPlayer.getDisplayName()));
        return String.join(", ", list);
    }

    public void openCage() {
        this.tc.open();
    }

    public void reset() {
        this.playing = false;
        this.eliminated = false;
        this.members.clear();
    }

    public int getMaxMembers() {
        return this.maxMembers;
    }

    public ArrayList<RSWPlayer> getMembers() {
        return this.members;
    }

    public Cage getTeamCage() {
        return this.tc;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public int getMemberCount() {
        return this.getMembers().size();
    }
}
