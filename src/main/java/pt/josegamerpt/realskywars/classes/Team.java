package pt.josegamerpt.realskywars.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import pt.josegamerpt.realskywars.classes.Enum.CageType;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

public class Team {

	public int id;
	public ArrayList<GamePlayer> members = new ArrayList<GamePlayer>();
	public int maxMembers;
	public Location cage;
	public Boolean eliminated = false;
	public Boolean playing = false;

	public Team(int i, int maxMemb, Location c) {
		this.id = i;
		this.cage = c;
		this.maxMembers = maxMemb;
	}

	public void addPlayer(GamePlayer p) {
		members.add(p);
		teleportToCage(p);
		p.team = this;
		if (members.size() == 1) {
			if (p.p != null) {
				//Cage.setCage(p.p, p.cageBlock, CageType.TEAMS);
			}
		}
		for (GamePlayer s : members) {
			s.sendMessage(p.getName() + " joined the team.");
		}
	}

	public void openCage() {
		Location l = cage;
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		Material m = Material.AIR;
		l.getWorld().getBlockAt(x + 1, y - 1, z).setType(m);
		l.getWorld().getBlockAt(x + 1, y - 1, z + 1).setType(m);
		l.getWorld().getBlockAt(x + 1, y - 1, z - 1).setType(m);
		l.getWorld().getBlockAt(x, y - 1, z + 1).setType(m);
		l.getWorld().getBlockAt(x, y - 1, z - 1).setType(m);
		l.getWorld().getBlockAt(x, y - 1, z).setType(m);
		l.getWorld().getBlockAt(x - 1, y - 1, z).setType(m);
		l.getWorld().getBlockAt(x - 1, y - 1, z - 1).setType(m);
		l.getWorld().getBlockAt(x - 1, y - 1, z + 1).setType(m);
		
		this.playing = true;
	}

	public void removePlayer(GamePlayer p) {
		members.remove(p);
		if (playing == true) {
			if (members.size() == 0) {
				eliminated = true;
			}
		}
		p.team = null;
		for (GamePlayer s : members) {
			s.sendMessage(p.getName() + " left the team.");
		}
		p.room.checkWin();
	}

	public Boolean isTeamFull() {
		return maxMembers == members.size();
	}

	public void sendMessage(String s) {
		for (GamePlayer p : members) {
			if (p.p != null) {
				p.p.sendMessage(Text.addColor(s));
			}
		}
	}

	public String getName() {
		return "Team " + id;
	}

	public String getNames() {
		List<String> list = new ArrayList<String>();
		for (GamePlayer p : members) {
			list.add(p.getName());
		}
		return String.join(", ", list);
	}

	public void teleportToCage(GamePlayer p) {
		p.teleport(cage);
	}
}
