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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.cages.RSWTeamCage;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.TeamColorLoop;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RSWTeam {

    private final int id;
    private final int maxMembers;
    private final RSWTeamCage tc;
    private final List<RSWPlayer> members = new ArrayList<>();
    private Boolean eliminated = false, playing = false;
    private Team teamBukkit = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getTeamNameScoreboard());


    public RSWTeam(int i, int maxMemb, Location c) {
        this.id = i;
        this.tc = new RSWTeamCage(i, c.getBlockX(), c.getBlockY(), c.getBlockZ());
        this.maxMembers = maxMemb;
        if (teamBukkit == null) {
            teamBukkit = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(getTeamNameScoreboard());
        }

        teamBukkit.setColor(TeamColorLoop.getTeamColor());
    }

    public void addPlayer(RSWPlayer p) {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.TEAM_BROADCAST_JOIN.get(p, true).replace("%player%", p.getName())));

        this.members.add(p);
        p.setTeam(this);
        if (members.size() == 1 && p.getPlayer() != null) {
            this.tc.addPlayer(p);
        }

        p.teleport(this.tc.getLocation());
        this.teamBukkit.addEntry(p.getName());
        p.sendMessage(TranslatableLine.TEAM_JOIN.get(p, true).replace("%team%", getName()));
    }

    public void removeMember(RSWPlayer p) {
        this.members.remove(p);

        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.TEAM_BROADCAST_LEAVE.get(p, true).replace("%player%", p.getName())));

        if (this.playing && members.isEmpty()) {
            this.eliminated = true;
        }
        p.setTeam(null);
        this.teamBukkit.removeEntry(p.getName());
        p.sendMessage(TranslatableLine.TEAM_LEAVE.get(p, true).replace("%team%", getName()));
    }

    public Boolean isTeamFull() {
        return this.maxMembers == this.getMembers().size();
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

    public String getTeamNameScoreboard() {
        return "rswT" + UUID.randomUUID();
    }
}
