package joserodpt.realskywars.api.map;

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

import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class RSWSetupMap {

    private final String name;
    private final String displayName;

    private final Map<Location, RSWCage> cages = new HashMap<>();
    private final Map<Location, RSWChest> chests = new HashMap<>();
    private final int maxPlayers;
    private final RSWMap.Mode mode;
    private Boolean tpConfirm = false;
    private World worldMap;
    private Location spectatorLocation;
    private Boolean spec = true;
    private int teams;
    private int playersPerTeam;
    private final RSWWorld.WorldType worldType;
    private String schematic = "none";

    private Location l1, l2;

    private Boolean cagesConfirmed = false, speclocConfirm = false, instantEnding = false, borderEnabled = true, ranked = false;

    public RSWSetupMap(String nome, String displayName, World w, RSWWorld.WorldType wt, int players) {
        this.name = nome.replace(".schematic", "").replace(".schem", "");
        this.displayName = displayName.replace(".schematic", "").replace(".schem", "");
        this.worldMap = w;
        this.worldType = wt;
        this.maxPlayers = players;
        this.mode = RSWMap.Mode.SOLO;
    }

    public RSWSetupMap(String nome, String displayName, World w, RSWWorld.WorldType wt, int teams, int ppert) {
        this.name = nome.replace(".schematic", "").replace(".schem", "");
        this.displayName = displayName.replace(".schematic", "").replace(".schem", "");
        this.worldMap = w;
        this.worldType = wt;
        this.teams = teams;
        this.playersPerTeam = ppert;
        this.mode = RSWMap.Mode.SOLO;
        this.maxPlayers = teams * ppert;
    }

    public Location getL1() {
        return this.l1;
    }

    public Location getL2() {
        return this.l2;
    }

    public int getPlayersPerTeam() {
        return this.playersPerTeam;
    }

    public int getTeamCount() {
        return teams;
    }

    public Map<Location, RSWCage> getCages() {
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

    public RSWMap.Mode getGameType() {
        return this.mode;
    }

    public void addCage(Location l, RSWCage c) {
        this.cages.put(l, c);
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

    public void addChest(RSWChest swChest) {
        this.chests.put(swChest.getLocation(), swChest);
    }

    public Map<Location, RSWChest> getChests() {
        return this.chests;
    }

    public Boolean isRanked() {
        return this.ranked;
    }

    public void setRanked(boolean b) {
        this.ranked = b;
    }

    public RSWWorld.WorldType getWorldType() {
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

    public String getDisplayName() {
        return this.displayName;
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

    public void setBoundaries(Location location, Location location1) {
        this.l1 = location;
        this.l2 = location1;
    }

    public void removeCage(Location location) {
        this.cages.remove(location);
    }

    public void removeChest(Location location) {
        this.chests.remove(location);
    }
}
