package joserodpt.realskywars.api.map.modes;

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
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.teams.RSWTeam;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;

import java.util.Collection;

public class PlaceholderMode extends RSWMap {
    public PlaceholderMode(String nome) {
        super(nome);
    }

    @Override
    public String forceStart(RSWPlayer p) {
        return null;
    }

    @Override
    public boolean canStartMap() {
        return false;
    }

    @Override
    public void removePlayer(RSWPlayer p) {
    }

    @Override
    public void addPlayer(RSWPlayer gp) {
    }

    @Override
    public void resetArena(OperationReason rr) {
    }

    @Override
    public void checkWin() {
    }

    @Override
    public GameMode getGameMode() {
        return null;
    }

    @Override
    public Collection<RSWCage> getCages() {
        return null;
    }

    @Override
    public Collection<RSWTeam> getTeams() {
        return null;
    }

    @Override
    public int getMaxTeamsNumber() {
        return 0;
    }

    @Override
    public int getMaxTeamsMembers() {
        return 0;
    }

    @Override
    public int getMaxGameTime() {
        return 0;
    }

    @Override
    public void forceStartMap() {
    }

    @Override
    public int minimumPlayersToStartMap() {
        return 0;
    }

    @Override
    public void removeCage(Location loc) {
    }

    @Override
    public void addCage(Location location) {
    }
}
