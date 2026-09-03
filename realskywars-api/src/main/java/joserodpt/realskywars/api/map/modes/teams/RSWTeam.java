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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private final String teamNameScoreboard = "rswT" + UUID.randomUUID();
    private Team teamBukkit;


    public RSWTeam(int i, int maxMemb, Location c) {
        this.id = i;
        this.tc = new RSWTeamCage(i, c.getBlockX(), c.getBlockY(), c.getBlockZ());
        this.maxMembers = maxMemb;

        this.teamBukkit = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getTeamNameScoreboard());
        if (this.teamBukkit == null) {
            this.teamBukkit = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(getTeamNameScoreboard());
        }

        this.teamBukkit.setColor(this.getColor());
    }

    public void addPlayer(RSWPlayer p) {
        this.addMember(p, true);
    }

    /**
     * Adds a player to this team.
     *
     * @param intoCage whether the player is put in the team's cage right away. Manual team selection
     *                 passes false: the player keeps waiting in the waiting lobby and is only caged
     *                 by {@link #commitToCage()} shortly before the match starts. Setting a cage
     *                 earlier would make {@code RSWMap#startRoom}'s per second re-teleport pull them
     *                 out of the waiting lobby.
     */
    public void addMember(RSWPlayer p, boolean intoCage) {
        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.TEAM_BROADCAST_JOIN.get(rswPlayer, true).replace("%player%", p.getName())));

        this.members.add(p);
        p.setTeam(this);

        if (intoCage) {
            if (members.size() == 1 && p.getPlayer() != null) {
                this.tc.addPlayer(p);
            } else {
                p.teleport(this.tc.getLocation());
                p.setInvincible(true);
            }
        }

        this.teamBukkit.addEntry(p.getName());
        p.sendMessage(TranslatableLine.TEAM_JOIN.get(p, true).replace("%team%", getName()));
    }

    /**
     * Puts every member inside the team's cage. Idempotent, so it is safe to call from both the
     * countdown and the force start path.
     */
    public void commitToCage() {
        for (RSWPlayer p : new ArrayList<>(this.members)) {
            if (p.getPlayer() != null && p.getPlayerCage() != this.tc) {
                this.tc.addPlayer(p);
            }
        }
    }

    public void removeMember(RSWPlayer p) {
        this.members.remove(p);

        this.members.forEach(rswPlayer -> rswPlayer.sendMessage(TranslatableLine.TEAM_BROADCAST_LEAVE.get(rswPlayer, true).replace("%player%", p.getName())));

        if (this.playing && members.isEmpty()) {
            this.eliminated = true;
        }
        p.setTeam(null);
        //drop them from the cage's occupant list, but keep their cage reference: RSWMap#spectate
        //reads getPlayerCage() right after this to demolish an eliminated team's cage
        this.tc.forgetPlayer(p);
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

    public int getID() {
        return this.id;
    }

    /**
     * This team's colour, derived from its id so it is stable across maps and restarts.
     */
    public ChatColor getColor() {
        return TeamColorLoop.colorForIndex(this.id);
    }

    /**
     * The wool used to represent this team in menus.
     */
    public Material getIconMaterial() {
        return TeamColorLoop.woolForIndex(this.id);
    }

    /**
     * The team name coloured with {@link #getColor()}, for menus and messages.
     */
    public String getColoredName() {
        return this.getColor() + this.getName();
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
        return this.teamNameScoreboard;
    }
}
