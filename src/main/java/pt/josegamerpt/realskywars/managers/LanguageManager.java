package pt.josegamerpt.realskywars.managers;

import java.util.ArrayList;
import java.util.HashMap;

import pt.josegamerpt.realskywars.classes.Enum.TL;
import pt.josegamerpt.realskywars.classes.Enum.TS;
import pt.josegamerpt.realskywars.classes.Enum.TSsingle;
import pt.josegamerpt.realskywars.configuration.Config;
import pt.josegamerpt.realskywars.configuration.Languages;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.Text;

public class LanguageManager {

	private static ArrayList<String> langList = new ArrayList<>();

	public static void loadLanguages() {
		langList.clear();
		langList.addAll(Languages.file().getConfigurationSection("Languages").getKeys(false));
	}

	public static String getDefaultLanguage() {
		if (langList.contains(Config.file().getString("Config.Default-Language"))) {
			return Config.file().getString("Config.Default-Language");
		} else {
			return langList.get(0);
		}
	}

	public static ArrayList<String> getList(GamePlayer p, TL tl) {
		String lang = p.Language;
		ArrayList<String> trad = new ArrayList<String>();

		switch (tl) {
		case ARENA_START:
			trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Messages.Arena.Start"));
			break;
		case END_LOG:
			trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Messages.Arena.End-Log"));
			break;
		case INITSETUP_ARENA:
			trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Messages.System.Arena.Init-Setup"));
			break;
			case SCOREBOARD_LOBBY_LINES:
				trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Scoreboards.Lobby.Lines"));
				break;
			case SCOREBOARD_CAGE_LINES:
				trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Scoreboards.Cage.Lines"));
				break;
			case SCOREBOARD_PLAYING_LINES:
				trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Scoreboards.Game.Lines"));
				break;
			case SCOREBOARD_SPECTATOR_LINES:
				trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Scoreboards.Spectate.Lines"));
				break;
			case ITEMS_MAP_DESCRIPTION:
				trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Itens.Map.Description"));
				break;
			case TITLE_ROOMJOIN:
				trad = Text.addColor(Languages.file().getList("Languages." + lang + ".Messages.System.Titles.Join-Room"));
				break;
			default:
				trad.add("List not found.");
		}

		return trad;
	}

	public static String getString(GamePlayer p, TS ts, boolean b) {
		if (b == false) {
			return Text.addColor(getString(p, ts));
		} else {
			return getPrefix() + Text.addColor(getString(p, ts));
		}
	}

	public static String getPrefix() {
		return Text.addColor(Languages.file().getString("Strings.Prefix"));
	}

	public static HashMap<String, HashMap<TS, String>> verifyLanguages() {
		HashMap<String, HashMap<TS, String>> flag = new HashMap<String, HashMap<TS, String>>();
		HashMap<TS, String> flagItem = new HashMap<TS, String>();
		for (String s : getLanguages()) {
			for (TS val : TS.values()) {
				String analyse = getString(s, val);
				if (analyse.equals("String not found.") || analyse.equals("Error finding translation.")) {
					flagItem.put(val, analyse);
				}
			}
			if (!(flagItem.size() == 0))
			{
				flag.put(s, flagItem);
			}
		}
		return flag;
	}

	private static String getString(String l, TS ts) {
		GamePlayer Decoy = new GamePlayer(null, null, null, 0, 0, null, l, null);
		return getString(Decoy, ts);
	}

	private static String getString(GamePlayer p, TS ts) {
		String lang = p.Language;
		String tr = "Not Found";

		try {
			switch (ts) {
				case CMD_CANT_FORCESTART:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Match-Force-Start-Denial"));
					break;
				case ARENA_CANCEL:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Match-Cancelled"));
					break;
				case ARENA_START_COUNTDOWN:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Start-Countdown"));
					break;
				case ITEMS_MAP_TITLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Itens.Map.Title"));
					break;
				case ITEMS_MAP_NOTFOUND_TITLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Itens.Map.Not-Found"));
					break;
				case LOBBY_TELEPORT:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Lobby-Teleport"));
					break;
				case MATCH_END:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Match-End"));
					break;
				case MATCH_LEAVE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Arena.Leave"));
					break;
				case MATCH_SPECTATE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Player-Spectate"));
				break;
			case PLAYER_JOIN_ARENA:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Player-Join"));
				break;
			case PLAYER_LEAVE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Player-Leave"));
				break;
				case SCOREBOARD_PLAYING_TITLE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Scoreboards.Game.Title"));
				break;
				case SCOREBOARD_LOBBY_TITLE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Scoreboards.Lobby.Title"));
				break;
				case SCOREBOARD_CAGE_TITLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Scoreboards.Cage.Title"));
					break;
				case SCOREBOARD_SPECTATOR_TITLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Scoreboards.Spectate.Title"));
					break;
			case TITLE_DEATHMATCH:
				tr = Text
						.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Title.DeathMatch"));
				break;
			case TITLE_WIN:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Title.Win"));
				break;
			case CMD_NOPERM:
				tr = Text
						.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Permission"));
				break;
			case CONFIG_RELOAD:
				tr = Text
						.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Config-Reload"));
				break;
			case ALREADY_IN_MATCH:
				tr = Text
						.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Already-In-Match"));
				break;
			case CMD_COINS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Coins"));
				break;
			case NO_SETUPMODE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.No-Setup-Mode"));
				break;
			case CMD_MATCH_CANCEL:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Arena.Suggest-Match-Cancel"));
				break;
			case CMD_MATCH_FORCESTART:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.Match-Force-Start"));
				break;
			case NO_MATCH:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Match"));
				break;
			case LOBBY_SET:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Lobby-Set"));
				break;
			case SETUP_NOT_FINISHED:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.Setup-Not-Finished"));
				break;
			case CMD_MAPS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Maps"));
				break;
			case CMD_PLAYERS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Players"));
				break;
			case CMD_FINISHSETUP:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Finish-Setup"));
				break;
			case NOMAP_FOUND:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Map-Found"));
				break;
			case NO_TIER_FOUND:
				tr = Text
						.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Tier-Found"));
				break;
			case TIER_SET:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Tier-Set"));
				break;
			case CHEST_BASIC:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Chests.Basic"));
				break;
			case CHEST_NORMAL:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Chests.Normal"));
				break;
			case CHEST_OP:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Chests.OP"));
				break;
			case CHEST_CAOS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Chests.CAOS"));
				break;
			case NO_PLAYER_FOUND:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Player-Found"));
				break;
			case MAP_UNREGISTERED:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.Map-Unregistered"));
				break;
			case MAP_EXISTS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Map-Exists"));
				break;
			case LOBBYLOC_NOT_SET:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.Lobby-Loc-Not-Set"));
				break;
			case INSUFICIENT_COINS:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.Insuficient-Coins"));
				break;
			case CMD_NOT_FOUND:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.Commands.Command-Not-Found"));
				break;
			case CAGES_SET:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Cages-Done"));
				break;
			case ADDED_COINS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Add"));
				break;
			case REMOVED_COINS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Remove"));
				break;
			case SET_COINS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Set"));
				break;
			case SENDER_COINS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Sender"));
				break;
			case RECIEVER_COINS:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Reciever"));
				break;
			case LANGUAGE_SET:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Language-Set"));
				break;
			case GENERATING_WORLD:
				tr = Text.addColor(
						Languages.file().getString("Languages." + lang + ".Messages.System.Generating-World"));
				break;
			case NO_ARENA_BOUNDARIES:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.No-Boundaries"));
				break;
			case SAVING_ARENA:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Saving"));
				break;
			case ARENA_REGISTERED:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Registered"));
				break;
			case CHEST_VOTE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Chests.Vote"));
				break;
			case CHEST_ALREADY_VOTED:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Chests.Already-Voted"));
				break;
			case GAME_STATUS_SET:
				tr = Text
						.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Game-Status-Set"));
				break;
			case ARENA_RESET:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Arena.Reset"));
				break;
			case MAP_RESET_DONE:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Reset-Done"));
				break;
			case ADD_TIER:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Add-Tier"));
				break;
			case SET_TIER:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.Commands.Set-Tier"));
				break;
			case SHOP_BUY:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.Buy"));
				break;
			case SHOP_ALREADY_BOUGHT:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.Already-Bought"));
				break;
			case SHOP_NO_PERM:
				tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.No-Permission"));
				break;
				//
				case NO_KIT_FOUND:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Kits.Not-Found"));
					break;
				case NOT_BUYABLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.Not-Buyable"));
					break;
				case DELETEKIT_DONE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Kits.Delete-Done"));
					break;
				case PROFILE_SELECTED:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Selection-Made"));
					break;
				case DEL_PURCHASES:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.No-Permission"));
					break;
				case KITS:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Kits"));
					break;
				case CAGEBLOCK:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Cage-Blocks"));
					break;
				case MAP_ALL:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.All"));
					break;
				case MAP_WAITING:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Waiting"));
					break;
				case MAP_SPECTATE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Spectating"));
					break;
				case MAP_STARTING:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Starting"));
					break;
				case MAP_AVAILABLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Available"));
					break;
				case MAP_PLAYING:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Playing"));
					break;
				case MAP_FINISHING:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Finishing"));
					break;
				case MAP_RESETTING:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.States.Resetting"));
					break;
				case COMPASS_TELEPORT:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Compass-Teleport"));
					break;
				case BOWPARTICLE:
					tr = Text.addColor(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Bow-Particles"));
					break;
				default:
					tr = "String not found.";
			}
		} catch (Exception e) {
			tr = "Error finding translation.";
			return tr;
		}

		return tr;
	}

	public static boolean checkSelect() {
		if (langList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static String getString(TSsingle ts) {
		if (ts.equals(TSsingle.BOSSBAR_ARENA_DEATHMATCH)) {
			return Text.addColor(Languages.file().getString("Strings.Boss-Bar.DeathMatch"));
		} else if (ts.equals(TSsingle.BOSSBAR_ARENA_END)) {
			return Text.addColor(Languages.file().getString("Strings.Boss-Bar.End"));
		} else if (ts.equals(TSsingle.BOSSBAR_ARENA_RUNTIME)) {
			return Text.addColor(Languages.file().getString("Strings.Boss-Bar.Run-Time"));
		} else if (ts.equals(TSsingle.BOSSBAR_ARENA_WAIT)) {
			return Text.addColor(Languages.file().getString("Strings.Boss-Bar.Wait"));
		} else if (ts.equals(TSsingle.BOSSBAR_ARENA_STARTING)) {
			return Text.addColor(Languages.file().getString("Strings.Boss-Bar.Starting"));
		} else {
			return "String not found.";
		}
	}

	public static ArrayList<String> getLanguages() {
		return langList;
	}
}
