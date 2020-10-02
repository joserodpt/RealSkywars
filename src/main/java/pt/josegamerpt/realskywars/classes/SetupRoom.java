package pt.josegamerpt.realskywars.classes;

import org.bukkit.Location;
import org.bukkit.World;
import pt.josegamerpt.realskywars.cages.Cage;
import pt.josegamerpt.realskywars.classes.Enum.GameType;

import java.util.ArrayList;

public class SetupRoom {

    public Boolean tpConfirm = false;
    public String Name;
    public ArrayList<Cage> cages = new ArrayList<>();
    public ArrayList<Team> teamslist = new ArrayList<>();
    public int maxPlayers;
    public World worldMap;
    public Location spectator;
    public Boolean spec = true;
    public GameType gameType;
    public boolean confirmCages = false;
    public boolean speclocConfirm = false;
    public Boolean guiConfirm = false;
    public Location POS1;
    public Location POS2;
    public Boolean instantEnding = false;

    public int teams;
    public int playersPerTeam;

    public SetupRoom(String nome, World w, int players) {
        this.Name = nome;
        this.worldMap = w;
        this.maxPlayers = players;
        this.gameType = GameType.SOLO;
    }

    public SetupRoom(String nome, World w, int teams, int ppert) {
        this.Name = nome;
        this.worldMap = w;
        this.teams = teams;
        this.playersPerTeam = ppert;
        this.gameType = GameType.TEAMS;

        this.maxPlayers = teams * ppert;
    }
}
