package joserodpt.realskywars.game.modes;

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
import joserodpt.realskywars.game.modes.teams.Team;
import joserodpt.realskywars.player.RSWPlayer;

import java.util.ArrayList;

public class Placeholder extends SWGame {
    public Placeholder(String nome) {
        super(nome);
    }
    @Override
    public boolean isPlaceHolder() {
        return true;
    }
    @Override
    public String forceStart(RSWPlayer p) {
        return null;
    }
    @Override
    public boolean canStartGame() {
        return false;
    }
    @Override
    public void removePlayer(RSWPlayer p) {}
    @Override
    public void addPlayer(RSWPlayer gp) {}
    @Override
    public void resetArena(OperationReason rr) {}
    @Override
    public void checkWin() {}
    @Override
    public Mode getGameMode() {
        return null;
    }
    @Override
    public ArrayList<Cage> getCages() {
        return null;
    }
    @Override
    public ArrayList<Team> getTeams() {
        return null;
    }
    @Override
    public int maxMembersTeam() {
        return 0;
    }
    @Override
    public int getMaxTime() {
        return 0;
    }
    @Override
    public void startGameFunction() {}
    @Override
    public int minimumPlayersToStartGame() {
        return 0;
    }
}
