package joserodpt.realskywars.game;

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

import joserodpt.realskywars.cages.Cage;
import joserodpt.realskywars.chests.SWChest;
import joserodpt.realskywars.game.modes.SWGame;
import joserodpt.realskywars.world.SWWorld;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

import static joserodpt.realskywars.game.modes.SWGame.Mode.TEAMS;

public class SetupRoom {

    private final String name;
    private final List<Cage> cages = new ArrayList<>();
    private final List<SWChest> chests = new ArrayList<>();
    private final int maxPlayers;
    private final SWGame.Mode mode;
    private Boolean tpConfirm = false;
    private World worldMap;
    private Location spectatorLocation;
    private Boolean spec = true;
    private int teams;
    private int playersPerTeam;
    private final SWWorld.WorldType worldType;
    private String schematic = "none";

    private Boolean cagesConfirmed = false, speclocConfirm = false, instantEnding = false,  borderEnabled = true, ranked = false;

    public SetupRoom(String nome, World w, SWWorld.WorldType wt, int players) {
        this.name = nome.replace(".schematic", "").replace(".schem", "");
        this.worldMap = w;
        this.worldType = wt;
        this.maxPlayers = players;
        this.mode = SWGame.Mode.SOLO;
    }

    public SetupRoom(String nome, World w, SWWorld.WorldType wt, int teams, int ppert) {
        this.name = nome.replace(".schematic", "").replace(".schem", "");
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

    public List<Cage> getCages() {
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

    public void setSpectatorLoc(Location location) {
        this.spectatorLocation = location;
    }

    public void setSpectatorConfirm(boolean b) {
        this.speclocConfirm = b;
    }

    public SWGame.Mode getGameType() {
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

    public void setInstantEnding(boolean b) {
        this.instantEnding = b;
    }

    public void setSpectating(boolean b) {
        this.spec = b;
    }

    public void addChest(SWChest swChest) {
        this.chests.add(swChest);
    }

    public List<SWChest> getChests() {
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

    public String getSchematic() {
        return this.schematic;
    }

    public void setSchematic(String name) {
        this.schematic = name;
    }

    public Boolean isBorderEnabled() {
        return this.borderEnabled;
    }

    public void setBorderEnabled(boolean b) {
        this.borderEnabled = b;
    }

    @Override
    public String toString() {
        return "SetupRoom{" +
                "name='" + name + '\'' +
                ", cages=" + cages +
                ", chests=" + chests +
                ", maxPlayers=" + maxPlayers +
                ", mode=" + mode +
                ", tpConfirm=" + tpConfirm +
                ", worldMap=" + worldMap +
                ", spectatorLocation=" + spectatorLocation +
                ", spec=" + spec +
                ", teams=" + teams +
                ", playersPerTeam=" + playersPerTeam +
                ", worldType=" + worldType +
                ", schematic='" + schematic + '\'' +
                ", cagesConfirmed=" + cagesConfirmed +
                ", speclocConfirm=" + speclocConfirm +
                ", instantEnding=" + instantEnding +
                ", borderEnabled=" + borderEnabled +
                ", ranked=" + ranked +
                '}';
    }
}
