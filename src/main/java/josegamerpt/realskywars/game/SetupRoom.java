package josegamerpt.realskywars.game;

import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.world.SWWorld;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

import static josegamerpt.realskywars.game.modes.SWGameMode.Mode.TEAMS;

public class SetupRoom {

    private final String name;
    private final ArrayList<Cage> cages = new ArrayList<>();
    private final ArrayList<SWChest> chests = new ArrayList<>();
    private final int maxPlayers;
    private final SWGameMode.Mode mode;
    private Boolean tpConfirm = false;
    private World worldMap;
    private Location spectatorLocation;
    private Boolean spec = true;
    private boolean cagesConfirmed = false;
    private boolean speclocConfirm = false;
    private Boolean guiConfirm = false;
    private Boolean instantEnding = false;

    private int teams;
    private int playersPerTeam;
    private Boolean ranked = false;
    private final SWWorld.WorldType worldType;

    public SetupRoom(String nome, World w, SWWorld.WorldType wt, int players) {
        this.name = nome;
        this.worldMap = w;
        this.worldType = wt;
        this.maxPlayers = players;
        this.mode = SWGameMode.Mode.SOLO;
    }

    public SetupRoom(String nome, World w, SWWorld.WorldType wt, int teams, int ppert) {
        this.name = nome;
        this.worldMap = w;
        this.worldType = wt;
        this.teams = teams;
        this.playersPerTeam = ppert;
        this.mode = TEAMS;

        this.maxPlayers = teams * ppert;
    }

    public int getPlayersPerTeam() {
        return this.playersPerTeam;
    }

    public int getTeamCount() {
        return teams;
    }

    public ArrayList<Cage> getCages() {
        return this.cages;
    }

    public void confirmCages(boolean b) {
        this.cagesConfirmed = b;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean areCagesConfirmed() {
        return this.cagesConfirmed;
    }

    public boolean isSpectatorLocConfirmed() {
        return this.speclocConfirm;
    }

    public boolean isGUIConfirmed() {
        return this.guiConfirm;
    }

    public void setSpectatorLoc(Location location) {
        this.spectatorLocation = location;
    }

    public void setSpectatorConfirm(boolean b) {
        this.speclocConfirm = b;
    }

    public SWGameMode.Mode getGameType() {
        return this.mode;
    }

    public void addCage(Cage c) {
        this.cages.add(c);
    }

    public World getWorld() {
        return this.worldMap;
    }

    public void setWorld(World world) {
        this.worldMap = world;
    }

    public String getName() {
        return this.name;
    }

    public Location getSpectatorLocation() {
        return this.spectatorLocation;
    }

    public Boolean isSpectatingON() {
        return this.spec;
    }

    public Boolean isInstantEnding() {
        return this.instantEnding;
    }

    public boolean isTPConfirmed() {
        return this.tpConfirm;
    }

    public void setTPConfirm(boolean b) {
        this.tpConfirm = b;
    }

    public void setGUIConfirm(boolean b) {
        this.guiConfirm = b;
    }

    public void setInstantEnding(boolean b) {
        this.instantEnding = b;
    }

    public void setSpectating(boolean b) {
        this.spec = b;
    }

    public void addChest(SWChest swChest) {
        this.chests.add(swChest);
    }

    public ArrayList<SWChest> getChests() {
        return this.chests;
    }

    public Boolean isRanked() {
        return this.ranked;
    }

    public void setRanked(boolean b) {
        this.ranked = b;
    }

    public SWWorld.WorldType getWorldType() {
        return this.worldType;
    }
}
