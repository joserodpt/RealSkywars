package josegamerpt.realskywars.classes;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.cages.TeamCage;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final int id;
    private ArrayList<RSWPlayer> members = new ArrayList<>();
    private final int maxMembers;
    private final TeamCage tc;
    private Boolean eliminated = false;
    private Boolean playing = false;

    public Team(int i, int maxMemb, Location c, String worldName) {
        this.id = i;
        this.tc = new TeamCage(i, c.getBlockX(), c.getBlockY(), c.getBlockZ(), worldName, maxMemb);
        this.maxMembers = maxMemb;
    }

    public void addPlayer(RSWPlayer p) {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(LanguageManager.getString(p, LanguageManager.TS.TEAM_BROADCAST_JOIN, true).replace("%player%", p.getName())));

        this.members.add(p);
        p.setTeam(this);
        if (members.size() == 1 && p.getPlayer() != null) {
            this.tc.addPlayer(p);
        }

        p.teleport(this.tc.getLoc());
        p.sendMessage(LanguageManager.getString(p, LanguageManager.TS.TEAM_JOIN, true).replace("%team%", getName()));
    }

    public void removePlayer(RSWPlayer p) {
        this.members.remove(p);

        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(LanguageManager.getString(p, LanguageManager.TS.TEAM_BROADCAST_LEAVE, true).replace("%player%", p.getName())));

        if (this.playing && members.size() == 0) {
            eliminated = true;
        }
        p.setTeam(null);
        p.sendMessage(LanguageManager.getString(p, LanguageManager.TS.TEAM_LEAVE, true).replace("%team%", getName()));
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
        for (RSWPlayer p : members) {
            list.add(p.getName());
        }
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
