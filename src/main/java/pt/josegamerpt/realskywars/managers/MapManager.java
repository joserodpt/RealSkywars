package pt.josegamerpt.realskywars.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.classes.GameRoom;
import pt.josegamerpt.realskywars.classes.GameRoomSolo;
import pt.josegamerpt.realskywars.classes.GameRoomTeams;
import pt.josegamerpt.realskywars.classes.SetupRoom;
import pt.josegamerpt.realskywars.classes.Team;
import pt.josegamerpt.realskywars.classes.Enum.GameState;
import pt.josegamerpt.realskywars.classes.Enum.GameType;
import pt.josegamerpt.realskywars.classes.Enum.InteractionState;
import pt.josegamerpt.realskywars.classes.Enum.TL;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.configuration.Items;
import pt.josegamerpt.realskywars.configuration.Maps;
import pt.josegamerpt.realskywars.gui.MapSettings;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Holograms;
import pt.josegamerpt.realskywars.utils.MathUtils;
import pt.josegamerpt.realskywars.utils.Text;
import pt.josegamerpt.realskywars.worlds.Worlds;

public class MapManager {

	static String clas = "[MAPMANAGER] - ";

	public static ArrayList<String> getRegisteredMaps() {
		Maps.reload();
		ArrayList<String> worlds = new ArrayList<String>();

		ConfigurationSection cs = Maps.file().getConfigurationSection("");
		Set<String> keys = cs.getKeys(false);
		for (Iterator<String> iterator1 = keys.iterator(); iterator1.hasNext();) {
			worlds.add((String) iterator1.next());
		}
		return worlds;
	}

	public static void unregisterMap(GameRoom map) {
		Maps.file().set(map.getName(), null);
		Maps.save();
		GameManager.rooms.remove(map);

		Bukkit.getServer().unloadWorld(map.getWorld().getName(), true);
		File destDir = new File("." + File.separator + map.getWorld().getName());
		//try {
	//		System.del.deleteDirectory(destDir);
		//} catch (IOException e) {
	//		e.printStackTrace();
		//}
	}

	public static void loadMaps() {

		GameManager.rooms.clear();

		for (String s : getRegisteredMaps()) {
			GameType t = getGameType(s);
			if (t.equals(GameType.SOLO)) {
				GameRoomSolo g = new GameRoomSolo(s, GameState.AVAILABLE, getCages(s), new ArrayList<GamePlayer>(),
						Maps.file().getInt(s + ".number-of-players"), Bukkit.getServer().getWorld(s), getSpecLoc(s),
						isSpecEnabled(s), isDragEnabled(s), getBorderSize(s));
				g.saveRoom();
			}
			if (t.equals(GameType.TEAMS)) {
				GameRoomTeams g = new GameRoomTeams(s, GameState.AVAILABLE,
						Maps.file().getInt(s + ".number-of-players"), Bukkit.getServer().getWorld(s), getSpecLoc(s),
						isSpecEnabled(s), isDragEnabled(s), getBorderSize(s), getTeams(s));
				g.saveRoom();
			}
		}
	}
	
	public static ArrayList<Team> getTeams(String s)
	{
		int team = 1;
		ArrayList<pt.josegamerpt.realskywars.classes.Team> temalist = new ArrayList<pt.josegamerpt.realskywars.classes.Team>();
		ArrayList<Location> cageTeams = getCages(s);
		for (Location l : cageTeams) {
			pt.josegamerpt.realskywars.classes.Team newt = new pt.josegamerpt.realskywars.classes.Team(team,
					(Maps.file().getInt(s + ".number-of-players") / cageTeams.size()), l);
			temalist.add(newt);
			team++;
		}
		return temalist;
	}

	private static GameType getGameType(String s) {
		String as = Maps.file().getString(s + ".Settings.GameType");
		GameType g = GameType.SOLO;
		if (as.equalsIgnoreCase("SOLO")) {
			g = GameType.SOLO;
		}
		if (as.equalsIgnoreCase("TEAMS")) {
			g = GameType.TEAMS;
		}
		return g;
	}

	private static Boolean isDragEnabled(String s) {
		return Maps.file().getBoolean(s + ".Settings.Dragon-Ride");
	}

	private static Double getBorderSize(String s) {
		double hx = Maps.file().getDouble(s + ".World.Border.POS1-X");
		double hz = Maps.file().getDouble(s + ".World.Border.POS1-Z");
		double lx = Maps.file().getDouble(s + ".World.Border.POS2-X");
		double lz = Maps.file().getDouble(s + ".World.Border.POS2-Z");

		return MathUtils.calculateDistanceBetweenPoints(lx, lz, hx, hz);
	}

	public static Boolean isSpecEnabled(String s) {
		return Maps.file().getBoolean(s + ".Settings.Spectator");
	}

	public static Location getSpecLoc(String nome) {
		double x = Maps.file().getDouble(nome + ".Locations.Spectator.X");
		double y = Maps.file().getDouble(nome + ".Locations.Spectator.Y");
		double z = Maps.file().getDouble(nome + ".Locations.Spectator.Z");
		float pitch = (float) Maps.file().getDouble(nome + ".Locations.Spectator.Pitch");
		float yaw = (float) Maps.file().getDouble(nome + ".Locations.Spectator.Yaw");
		Location l = new Location(Bukkit.getWorld(nome), x, y, z, pitch, yaw);
		Debugger.printValue(clas + "SPECLOC FOR " + nome + " - " + l);
		return l;

	}

	public static GameRoom getMap(String name) {
		for (GameRoom g : GameManager.rooms) {
			if (name.equalsIgnoreCase(g.getName())) {
				return g;
			}
		}
		return null;
	}

	public static ArrayList<Location> getCages(String map) {
		ConfigurationSection cs = Maps.file().getConfigurationSection(map + ".Locations.Cages");
		Set<String> keys = cs.getKeys(false);
		ArrayList<Location> locs = new ArrayList<Location>();
		for (Iterator<String> iterator1 = keys.iterator(); iterator1.hasNext();) {
			String i = iterator1.next();
			double x = Maps.file().getDouble(map + ".Locations.Cages." + i + ".X");
			double y = Maps.file().getDouble(map + ".Locations.Cages." + i + ".Y");
			double z = Maps.file().getDouble(map + ".Locations.Cages." + i + ".Z");
			World w = Bukkit.getWorld(Maps.file().getString(map + ".world"));
			Location loc = new Location(w, x, y, z);
			Debugger.printValue(clas + "[GETLOCS] " + loc.toString());
			loc.add(0.5, 0, 0.5);
			locs.add(loc);
		}
		return locs;
	}

	public static void saveMapSolo(GameRoomSolo g, SetupRoom r) {
		String s = g.Name;

		// World
		Maps.file().set(s + ".world", g.worldMap.getName());

		// Map Name
		Maps.file().set(s + ".name", s);

		// Number Players
		Maps.file().set(s + ".number-of-players", g.maxPlayers);

		// Locations Cages
		int count = 1;
		for (Location loc : r.Cages) {
			Maps.file().set(s + ".Locations.Cages." + count + ".X", Integer.valueOf(loc.getBlockX()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Y", Integer.valueOf(loc.getBlockY()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Z", Integer.valueOf(loc.getBlockZ()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Yaw", Float.valueOf(loc.getYaw()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Pitch", Float.valueOf(loc.getPitch()));
			count++;
		}

		// SpecLoc
		Maps.file().set(s + ".Locations.Spectator.X", g.spectator.getX());
		Maps.file().set(s + ".Locations.Spectator.Y", g.spectator.getY());
		Maps.file().set(s + ".Locations.Spectator.Z", g.spectator.getZ());
		Maps.file().set(s + ".Locations.Spectator.Yaw", g.spectator.getYaw());
		Maps.file().set(s + ".Locations.Spectator.Pitch", g.spectator.getPitch());

		// Settings
		Maps.file().set(s + ".Settings.Spectator", g.specEnabled);
		Maps.file().set(s + ".Settings.Dragon-Ride", g.dragonEnabled);
		Maps.file().set(s + ".Settings.GameType", g.gameType.name());

		// Border
		Maps.file().set(s + ".World.Border.POS1-X", r.POS1.getX());
		Maps.file().set(s + ".World.Border.POS1-Z", r.POS1.getZ());
		Maps.file().set(s + ".World.Border.POS2-X", r.POS2.getX());
		Maps.file().set(s + ".World.Border.POS2-Z", r.POS2.getZ());

		Maps.save();
	}

	public static void cancelSetup(GamePlayer p) {
		Maps.file().set(p.setup.Name, null);
		Maps.save();
		p.setup = null;
		p.p.teleport(GameManager.lobby);
		String tp = LanguageManager.getString(p, TS.LOBBY_TELEPORT, true);
		p.sendMessage(tp);
	}

	public static void setupTeams(GamePlayer p, String mapname, int teams, int pperteam) {
		SetupRoom s = new SetupRoom(mapname, null, teams, pperteam);
		p.setup = s;
		p.istate = InteractionState.GUI_ROOMSETUP;

		MapSettings m = new MapSettings(s, p.p.getUniqueId());
		m.openInventory(p);
	}

	public static void continueSetup(GamePlayer p) {
		if (p.setup.tpConfirm == false) {

			p.istate = InteractionState.NONE;

			p.setup.tpConfirm = true;

			p.sendMessage(LanguageManager.getString(p, TS.GENERATING_WORLD, true));
			World world = Worlds.createWorld(p.setup.Name);
			p.setup.worldMap = world;

			Location loc = new Location(world, 0, 65, 0);
			p.p.teleport(loc);

			Text.sendList(p.p, LanguageManager.getList(p, TL.INITSETUP_ARENA), p.setup.maxPlayers);

			p.p.getInventory().addItem(Items.CAGESET);
			p.p.setGameMode(GameMode.CREATIVE);
		}
	}

	public static void setupSolo(GamePlayer p, String mapname, int maxP) {
		SetupRoom s = new SetupRoom(mapname, null, maxP);
		p.setup = s;
		p.istate = InteractionState.GUI_ROOMSETUP;

		MapSettings m = new MapSettings(s, p.p.getUniqueId());
		m.openInventory(p);
	}

	public static void finishSetup(GamePlayer p) {
		if (p.setup.POS1 == null || p.setup.POS2 == null) {
			p.sendMessage(LanguageManager.getString(p, TS.NO_ARENA_BOUNDARIES, true));
			return;
		}

		p.p.teleport(GameManager.lobby);
		ArrayList<String> list = new ArrayList<String>();
		list.add(LanguageManager.getString(p, TS.SAVING_ARENA, true));
		Text.sendList(p.p, list);

		p.p.getInventory().clear();

		// Beacon Remove
		Holograms.removeAll();
		for (Location l : p.setup.Cages) {
			p.setup.worldMap.getBlockAt(l).setType(Material.AIR);
		}

		// Save Data
		if (p.setup.gameType.equals(GameType.SOLO)) {
			GameRoomSolo g = new GameRoomSolo(p.setup.Name, GameState.AVAILABLE, p.setup.Cages,
					new ArrayList<GamePlayer>(), p.setup.maxPlayers, p.setup.worldMap, p.setup.spectator, p.setup.spec,
					p.setup.dragon, MathUtils.calculateDistanceBetweenPoints(p.setup.POS1.getX(), p.setup.POS1.getZ(),
							p.setup.POS2.getX(), p.setup.POS2.getZ()));
			saveMapSolo(g, p.setup);
			g.saveRoom();
		}
		if (p.setup.gameType.equals(GameType.TEAMS)) {
			GameRoomTeams g = new GameRoomTeams(p.setup.Name, GameState.AVAILABLE, p.setup.maxPlayers, p.setup.worldMap,
					p.setup.spectator, p.setup.spec, p.setup.dragon, MathUtils.calculateDistanceBetweenPoints(
							p.setup.POS1.getX(), p.setup.POS1.getZ(), p.setup.POS2.getX(), p.setup.POS2.getZ()), p.setup.teamslist);
			saveMapTeams(g, p.setup);
			g.saveRoom();
		}

		p.setup = null;
		p.sendMessage(LanguageManager.getString(p, TS.ARENA_REGISTERED, true));

		PlayerManager.giveItems(p.p, 0);
	}

	private static void saveMapTeams(GameRoomTeams g, SetupRoom r) {
		String s = g.Name;

		// World
		Maps.file().set(s + ".world", g.worldMap.getName());

		// Map Name
		Maps.file().set(s + ".name", s);

		// Number Players
		Maps.file().set(s + ".number-of-players", g.maxPlayers);

		// Locations Cages
		int count = 1;
		for (Team lt : r.teamslist) {
			Maps.file().set(s + ".Locations.Cages." + count + ".X", Integer.valueOf(lt.cage.getBlockX()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Y", Integer.valueOf(lt.cage.getBlockY()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Z", Integer.valueOf(lt.cage.getBlockZ()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Yaw", Float.valueOf(lt.cage.getYaw()));
			Maps.file().set(s + ".Locations.Cages." + count + ".Pitch", Float.valueOf(lt.cage.getPitch()));
			count++;
		}

		// SpecLoc
		Maps.file().set(s + ".Locations.Spectator.X", g.spectator.getX());
		Maps.file().set(s + ".Locations.Spectator.Y", g.spectator.getY());
		Maps.file().set(s + ".Locations.Spectator.Z", g.spectator.getZ());
		Maps.file().set(s + ".Locations.Spectator.Yaw", g.spectator.getYaw());
		Maps.file().set(s + ".Locations.Spectator.Pitch", g.spectator.getPitch());

		// Settings
		Maps.file().set(s + ".Settings.Spectator", g.specEnabled);
		Maps.file().set(s + ".Settings.Dragon-Ride", g.dragonEnabled);
		Maps.file().set(s + ".Settings.GameType", g.gameType.name());

		// Border
		Maps.file().set(s + ".World.Border.POS1-X", r.POS1.getX());
		Maps.file().set(s + ".World.Border.POS1-Z", r.POS1.getZ());
		Maps.file().set(s + ".World.Border.POS2-X", r.POS2.getX());
		Maps.file().set(s + ".World.Border.POS2-Z", r.POS2.getZ());

		Maps.save();
	}

	public static void saveSettings(GameRoom game) {
		Maps.file().set(game.getName() + ".Settings.Spectator", game.isSpectatorEnabled());
		Maps.file().set(game.getName() + ".Settings.Dragon-Ride", game.isSpectatorEnabled());
		Maps.save();
	}
}
