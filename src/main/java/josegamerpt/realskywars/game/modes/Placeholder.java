package josegamerpt.realskywars.game.modes;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.game.SWEvent;
import josegamerpt.realskywars.game.modes.teams.Team;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import josegamerpt.realskywars.world.SWWorld;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Placeholder implements SWGameMode {

    public String n;

    public Placeholder(String name) {
        this.n = name;
    }

    @Override
    public Countdown getStartRoomTimer() {
        return null;
    }

    @Override
    public ProjectileType getProjectile() {
        return ProjectileType.NORMAL;
    }

    @Override
    public void setRanked(Boolean ranked) {
    }

    @Override
    public Boolean isRanked() {
        return false;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    public void saveRoom() {
    }

    public String getName() {
        return this.n;
    }

    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public BossBar getBossBar() {
        return null;
    }

    @Override
    public WorldBorder getBorder() {
        return null;
    }

    public int getPlayerCount() {
        return 0;
    }

    public ArrayList<RSWPlayer> getPlayers() {
        return null;
    }

    @Override
    public ArrayList<RSWPlayer> getInRoom() {
        return null;
    }

    public int getSpectatorsCount() {
        return 0;
    }

    public List<RSWPlayer> getSpectators() {
        return null;
    }

    public SWWorld getSWWorld() {
        return null;
    }

    public void kickPlayers(String s) {
    }

    public SWGameMode.GameState getState() {
        return null;
    }

    @Override
    public void setState(GameState w) { }

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
    public void setTierType(ChestManager.ChestTier b, Boolean updateChests) {

    }

    @Override
    public void setTime(TimeType tt) {

    }

    @Override
    public void setProjectiles(ProjectileType pt) {

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
    public ChestManager.ChestTier getChestTier() {
        return null;
    }

    public int getTimePassed() {
        return 0;
    }

    @Override
    public void resetArena(OperationReason rr) {

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

    public Mode getGameMode() {
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
    public void deleteShutdown() {

    }

    @Override
    public void reset() {

    }

    @Override
    public ArenaCuboid getArena() {
        return null;
    }

    @Override
    public int getBorderSize() {
        return 0;
    }

    @Override
    public void addVote(UUID u, VoteType vt, int i) {

    }

    @Override
    public boolean hasVotedFor(VoteType vt, UUID uuid) {
        return false;
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

    @Override
    public String getShematicName() {
        return "none";
    }

}
