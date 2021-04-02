package josegamerpt.realskywars.modes;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.classes.SWEvent;
import josegamerpt.realskywars.classes.Team;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import org.bukkit.Location;
import org.bukkit.World;

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

    public SWGameMode.GameState getState() {
        return null;
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

    @Override
    public void setTierType(ChestManager.TierType b, Boolean updateChests) {

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

    @Override
    public ChestManager.TierType getTierType() {
        return null;
    }

    public int getTimePassed() {
        return 0;
    }

    public void resetArena() {
    }

    @Override
    public void setState(GameState w) {

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

    public SWGameMode.GameType getGameType() {
        return null;
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

    @Override
    public ArrayList<SWChest> getChests() {
        return null;
    }

    @Override
    public ArrayList<SWEvent> getEvents() {
        return null;
    }

    @Override
    public int getMaxTime() {
        return 0;
    }

    @Override
    public SWChest getChest(Location location) {
        return null;
    }

}
