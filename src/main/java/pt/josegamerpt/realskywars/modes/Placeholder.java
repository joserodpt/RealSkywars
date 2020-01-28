package pt.josegamerpt.realskywars.modes;

import org.bukkit.Location;
import org.bukkit.World;
import pt.josegamerpt.realskywars.classes.Cage;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.classes.Team;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Calhau;

import java.util.ArrayList;
import java.util.List;

public class Placeholder implements GameRoom {

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

    public ArrayList<GamePlayer> getPlayers() {
        return null;
    }

    public int getSpectatorsCount() {
        return 0;
    }

    public List<GamePlayer> getSpectators() {
        return null;
    }

    public int getPlayersInCount() {
        return 0;
    }

    public List<GamePlayer> getPlayersIn() {
        return null;
    }

    public World getWorld() {
        return null;
    }

    public void broadcastMessage(String s, Boolean prefix) {

    }

    public void kickPlayers() {

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

    public void forceStart() {

    }

    public void removePlayer(GamePlayer p) {

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

    public ArrayList<GamePlayer> getVoters() {
        return null;
    }

    public ArrayList<Integer> getVoteList() {
        return null;
    }

    public void addPlayer(GamePlayer gp) {

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

    public ArrayList<Calhau> getBlocksPlaced() {
        return null;
    }

    public ArrayList<Calhau> getBlocksDestroyed() {
        return null;
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

    public void spectate(GamePlayer p, Location killLoc) {

    }

    public ArrayList<Location> getOpenedChests() {
        return null;
    }

    public void checkWin() {

    }

    public Enum.GameType getMode() {
        return null;
    }

    public void cancelAllTasks() {

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
}
