package josegamerpt.realskywars.modes;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.Team;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Placeholder implements SWGameMode {

    public String n;

    public Placeholder(String name) {
        this.n = name;
    }

    public void saveRoom() {
    }

    public String getName() {
        return this.n;
    }

    public int getMaxPlayers() {
        return 0;
    }

    public int getPlayersCount() {
        return 0;
    }

    public ArrayList<RSWPlayer> getPlayers() {
        return null;
    }

    public int getSpectatorsCount() {
        return 0;
    }

    public List<RSWPlayer> getSpectators() {
        return null;
    }

    public World getWorld() {
        return null;
    }

    public void kickPlayers(String s) {
    }

    public Enum.GameState getState() {
        return null;
    }

    public void setState(Enum.GameState w) {
    }

    public boolean isPlaceHolder() {
        return true;
    }

    public String forceStart(RSWPlayer p) {
        return "lol its a placeholder";
    }

    public void removePlayer(RSWPlayer p) {
    }

    public Location getSpectatorLocation() {
        return null;
    }

    public Location getPOS1() {
        return null;
    }

    public Location getPOS2() {
        return null;
    }

    public ArrayList<UUID> getVoters() {
        return null;
    }

    public ArrayList<Integer> getVoteList() {
        return null;
    }

    public void addPlayer(RSWPlayer gp) {
    }

    public boolean isSpectatorEnabled() {
        return false;
    }

    public boolean isInstantEndEnabled() {
        return false;
    }

    public Enum.TierType getTierType() {
        return null;
    }

    public void setTierType(Enum.TierType b) {
    }

    public int getTimePassed() {
        return 0;
    }

    public void resetArena() {
    }

    public void setSpectator(boolean b) {
    }

    public void setInstantEnd(boolean b) {
    }

    @Override
    public void spectate(RSWPlayer p, SpectateType st, Location killLoc) {

    }

    public void checkWin() {
    }

    public Enum.GameType getMode() {
        return null;
    }

    public void cancelTask(String s) {
    }

    public ArrayList<Cage> getCages() {
        return null;
    }

    @Override
    public ArrayList<Team> getTeams() {
        return null;
    }

    public int maxMembersTeam() {
        return 0;
    }

    public void clear() {

    }

    @Override
    public void reset() {

    }

    @Override
    public ArenaCuboid getArena() {
        return null;
    }

    @Override
    public int getID() {
        return -1;
    }

    @Override
    public int getBorderSize() {
        return 0;
    }

    @Override
    public void addVote(UUID u, int i) {

    }

}
