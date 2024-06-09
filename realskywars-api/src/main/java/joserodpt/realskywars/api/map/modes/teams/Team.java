package joserodpt.realskywars.api.map.modes.teams;

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

import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.cages.RSWTeamCage;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final int id;
    private final int maxMembers;
    private final RSWTeamCage tc;
    private final List<RSWPlayer> members = new ArrayList<>();
    private Boolean eliminated = false, playing = false;

    public Team(int i, int maxMemb, Location c, String worldName) {
        this.id = i;
        this.tc = new RSWTeamCage(i, c.getBlockX(), c.getBlockY(), c.getBlockZ(), worldName, maxMemb);
        this.maxMembers = maxMemb;
    }

    public void addPlayer(RSWPlayer p) {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.TEAM_BROADCAST_JOIN.get(p, true).replace("%player%", p.getName())));

        this.members.add(p);
        p.setTeam(this);
        if (members.size() == 1 && p.getPlayer() != null) {
            this.tc.addPlayer(p);
        }

        p.teleport(this.tc.getLocation());
        p.sendMessage(TranslatableLine.TEAM_JOIN.get(p, true).replace("%team%", getName()));
    }

    public void removeMember(RSWPlayer p) {
        this.members.remove(p);

        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.TEAM_BROADCAST_LEAVE.get(p, true).replace("%player%", p.getName())));

        if (this.playing && members.isEmpty()) {
            this.eliminated = true;
        }
        p.setTeam(null);
        p.sendMessage(TranslatableLine.TEAM_LEAVE.get(p, true).replace("%team%", getName()));
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

    public List<RSWPlayer> getMembers() {
        return this.members;
    }

    public RSWCage getTeamCage() {
        return this.tc;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public int getMemberCount() {
        return this.getMembers().size();
    }
}
