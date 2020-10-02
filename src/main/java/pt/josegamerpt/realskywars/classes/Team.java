package pt.josegamerpt.realskywars.classes;

import org.bukkit.Location;
import pt.josegamerpt.realskywars.cages.TeamCage;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

import java.util.ArrayList;
import java.util.List;

public class Team {

    public int id;
    public ArrayList<GamePlayer> members = new ArrayList<>();
    public int maxMembers;
    public Boolean eliminated = false;
    public Boolean playing = false;
    public TeamCage tc;

    public Team(int i, int maxMemb, Location c) {
        this.id = i;
        this.tc = new TeamCage(i, c);
        this.maxMembers = maxMemb;
    }

    public void addPlayer(GamePlayer p) {
        for (GamePlayer s : members) {
            s.sendMessage(LanguageManager.getString(p, Enum.TS.TEAM_BROADCAST_JOIN, true).replace("%player%", p.getName()));
        }
        members.add(p);
        p.team = this;
        if (members.size() == 1) {
            if (p.p != null) {
                this.tc.addPlayer(p);
            }
        }
        p.teleport(this.tc.getLocation());
        p.sendMessage(LanguageManager.getString(p, Enum.TS.TEAM_JOIN, true).replace("%team%", getName()));
    }

    public void removePlayer(GamePlayer p) {
        for (GamePlayer s : members) {
            s.sendMessage(LanguageManager.getString(p, Enum.TS.TEAM_BROADCAST_LEAVE, true).replace("%player%", p.getName()));
        }
        members.remove(p);
        if (playing) {
            if (members.size() == 0) {
                eliminated = true;
            }
        }
        p.team = null;
        p.sendMessage(LanguageManager.getString(p, Enum.TS.TEAM_LEAVE, true).replace("%team%", getName()));
    }

    public Boolean isTeamFull() {
        return maxMembers == members.size();
    }

    public void sendMessage(String s) {
        for (GamePlayer p : members) {
            if (p.p != null) {
                p.p.sendMessage(Text.addColor(s));
            }
        }
    }

    public String getName() {
        return "Team " + id;
    }

    public String getNames() {
        List<String> list = new ArrayList<>();
        for (GamePlayer p : members) {
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
        this.tc.cageSet = false;
        this.members.clear();
    }
}
