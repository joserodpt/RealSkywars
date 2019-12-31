package pt.josegamerpt.realskywars.classes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

import pt.josegamerpt.realskywars.classes.Enum.GameType;

public class SetupRoom {

	public Boolean tpConfirm = false;
	public String Name;
	public ArrayList<Location> Cages = new ArrayList<Location>();
	public ArrayList<Team> teamslist = new ArrayList<Team>();
	public int maxPlayers;
	public ArrayList<Location> Chests = new ArrayList<Location>();
	public World worldMap;
	public Location spectator;
	public Boolean spec = true;
	public Location lowPoint;
	public Location highPoint;
	public GameType gameType;
	public int placedCages = 1;
	public boolean confirmCages = false;
	public boolean speclocConfirm = false;
	public Boolean guiConfirm = false;
	public Location POS1;
	public Location POS2;
	public Boolean dragon = false;

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
