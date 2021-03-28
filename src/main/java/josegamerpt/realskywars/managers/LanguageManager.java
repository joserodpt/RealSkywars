package josegamerpt.realskywars.managers;

import josegamerpt.realskywars.classes.Enum.TL;
import josegamerpt.realskywars.classes.Enum.TS;
import josegamerpt.realskywars.classes.Enum.TSsingle;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.configuration.Languages;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class LanguageManager {

    private static ArrayList<String> langList = new ArrayList<>();

    public static void loadLanguages() {
        langList.clear();
        langList.addAll(Languages.file().getConfigurationSection("Languages").getKeys(false));
    }

    public static String getDefaultLanguage() {
        return langList.contains(Config.file().getString("Config.Default-Language")) ? Config.file().getString("Config.Default-Language") : langList.get(0);
    }

    public static ArrayList<String> getList(RSWPlayer p, TL tl) {
        String lang = p.getLanguage();
        ArrayList<String> trad = new ArrayList<>();

        switch (tl) {
            case ARENA_START:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Messages.Arena.Start"));
                break;
            case END_LOG:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Messages.Arena.End-Log"));
                break;
            case INITSETUP_ARENA:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Messages.System.Arena.Init-Setup"));
                break;
            case SCOREBOARD_LOBBY_LINES:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Scoreboards.Lobby.Lines"));
                break;
            case SCOREBOARD_CAGE_LINES:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Scoreboards.Cage.Lines"));
                break;
            case SCOREBOARD_PLAYING_LINES:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Scoreboards.Game.Lines"));
                break;
            case SCOREBOARD_SPECTATOR_LINES:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Scoreboards.Spectate.Lines"));
                break;
            case ITEMS_MAP_DESCRIPTION:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Itens.Map.Description"));
                break;
            case TITLE_ROOMJOIN:
                trad = Text.color(Languages.file().getList("Languages." + lang + ".Messages.System.Titles.Join-Room"));
                break;
            default:
                trad.add("List not found (" + tl.name() + ")");
        }

        return trad;
    }

    public static String getString(RSWPlayer p, TS ts, boolean b) {
        if (!b) {
            return Text.color(getString(p, ts));
        } else {
            return getPrefix() + Text.color(getString(p, ts));
        }
    }

    public static String getPrefix() {
        return Text.color(Languages.file().getString("Strings.Prefix"));
    }

    public static HashMap<String, HashMap<TS, String>> verifyLanguages() {
        HashMap<String, HashMap<TS, String>> flag = new HashMap<>();
        HashMap<TS, String> flagItem = new HashMap<>();
        for (String s : getLanguages()) {
            for (TS val : TS.values()) {
                String analyse = getString(s, val);
                if (analyse.equals("String not found (" + val.name() + ")") || analyse.equals("Error finding translation (" + val.name() + ")")) {
                    flagItem.put(val, analyse);
                }
            }
            if (!(flagItem.size() == 0)) {
                flag.put(s, flagItem);
            }
        }
        return flag;
    }

    private static String getString(String l, TS ts) {
        RSWPlayer Decoy = new RSWPlayer(false);
        Decoy.setLanguage(l);
        return getString(Decoy, ts);
    }

    private static String getString(RSWPlayer p, TS ts) {
        String lang = p.getLanguage();
        String tr;

        try {
            switch (ts) {
                case CMD_CANT_FORCESTART:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Match-Force-Start-Denial"));
                    break;
                case ARENA_CANCEL:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Match-Cancelled"));
                    break;
                case ARENA_START_COUNTDOWN:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Start-Countdown"));
                    break;
                case ITEMS_MAP_TITLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Itens.Map.Title"));
                    break;
                case ITEMS_MAP_NOTFOUND_TITLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Itens.Map.Not-Found"));
                    break;
                case LOBBY_TELEPORT:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Lobby-Teleport"));
                    break;
                case MATCH_END:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Match-End"));
                    break;
                case MATCH_LEAVE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Arena.Leave"));
                    break;
                case MATCH_SPECTATE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Player-Spectate"));
                    break;
                case PLAYER_JOIN_ARENA:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Player-Join"));
                    break;
                case PLAYER_LEAVE_ARENA:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Player-Leave"));
                    break;
                case SCOREBOARD_PLAYING_TITLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Scoreboards.Game.Title"));
                    break;
                case SCOREBOARD_LOBBY_TITLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Scoreboards.Lobby.Title"));
                    break;
                case SCOREBOARD_CAGE_TITLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Scoreboards.Cage.Title"));
                    break;
                case SCOREBOARD_SPECTATOR_TITLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Scoreboards.Spectate.Title"));
                    break;
                case TITLE_DEATHMATCH:
                    tr = Text
                            .color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Title.DeathMatch"));
                    break;
                case TITLE_WIN:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Title.Win"));
                    break;
                case CMD_NOPERM:
                    tr = Text
                            .color(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Permission"));
                    break;
                case CONFIG_RELOAD:
                    tr = Text
                            .color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Config-Reload"));
                    break;
                case ALREADY_IN_MATCH:
                    tr = Text
                            .color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Already-In-Match"));
                    break;
                case CMD_COINS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Coins"));
                    break;
                case NO_SETUPMODE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.No-Setup-Mode"));
                    break;
                case CMD_MATCH_CANCEL:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Arena.Suggest-Match-Cancel"));
                    break;
                case CMD_MATCH_FORCESTART:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.Match-Force-Start"));
                    break;
                case NO_MATCH:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Match"));
                    break;
                case LOBBY_SET:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Lobby-Set"));
                    break;
                case SETUP_NOT_FINISHED:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.Setup-Not-Finished"));
                    break;
                case CMD_MAPS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Maps"));
                    break;
                case CMD_PLAYERS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Players"));
                    break;
                case CMD_FINISHSETUP:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Finish-Setup"));
                    break;
                case NOMAP_FOUND:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Map-Found"));
                    break;
                case NO_TIER_FOUND:
                    tr = Text
                            .color(Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Tier-Found"));
                    break;
                case TIER_SET:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Tier-Set"));
                    break;
                case CHEST_BASIC:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Chests.Basic"));
                    break;
                case CHEST_NORMAL:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Chests.Normal"));
                    break;
                case CHEST_OP:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Chests.OP"));
                    break;
                case CHEST_CAOS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Chests.CAOS"));
                    break;
                case NO_PLAYER_FOUND:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.No-Player-Found"));
                    break;
                case MAP_UNREGISTERED:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.Map-Unregistered"));
                    break;
                case MAP_EXISTS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Map-Exists"));
                    break;
                case LOBBYLOC_NOT_SET:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.Lobby-Loc-Not-Set"));
                    break;
                case INSUFICIENT_COINS:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.Insuficient-Coins"));
                    break;
                case CMD_NOT_FOUND:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.Commands.Command-Not-Found"));
                    break;
                case CAGES_SET:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Cages-Done"));
                    break;
                case ADDED_COINS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Add"));
                    break;
                case REMOVED_COINS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Remove"));
                    break;
                case SET_COINS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Set"));
                    break;
                case SENDER_COINS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Sender"));
                    break;
                case RECIEVER_COINS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Coins.Reciever"));
                    break;
                case LANGUAGE_SET:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Language-Set"));
                    break;
                case GENERATING_WORLD:
                    tr = Text.color(
                            Languages.file().getString("Languages." + lang + ".Messages.System.Generating-World"));
                    break;
                case NO_ARENA_BOUNDARIES:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.No-Boundaries"));
                    break;
                case SAVING_ARENA:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Saving"));
                    break;
                case ARENA_REGISTERED:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Registered"));
                    break;
                case CHEST_VOTE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Chests.Vote"));
                    break;
                case CHEST_ALREADY_VOTED:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Chests.Already-Voted"));
                    break;
                case GAME_STATUS_SET:
                    tr = Text
                            .color(Languages.file().getString("Languages." + lang + ".Messages.System.Game-Status-Set"));
                    break;
                case ARENA_RESET:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Arena.Reset"));
                    break;
                case MAP_RESET_DONE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Reset-Done"));
                    break;
                case ADD_TIER:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Add-Tier"));
                    break;
                case SET_TIER:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.Commands.Set-Tier"));
                    break;
                case SHOP_BUY:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.Buy"));
                    break;
                case SHOP_ALREADY_BOUGHT:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.Already-Bought"));
                    break;
                case SHOP_NO_PERM:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.No-Permission"));
                    break;
                //
                case NO_KIT_FOUND:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Kits.Not-Found"));
                    break;
                case NOT_BUYABLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.Not-Buyable"));
                    break;
                case DELETEKIT_DONE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Kits.Delete-Done"));
                    break;
                case PROFILE_SELECTED:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Selection-Made"));
                    break;
                case DEL_PURCHASES:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Shop.No-Permission"));
                    break;
                case KITS:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Kits"));
                    break;
                case CAGEBLOCK:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Cage-Blocks"));
                    break;
                case MAP_ALL:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.All"));
                    break;
                case MAP_WAITING:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Waiting"));
                    break;
                case MAP_SPECTATE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Spectating"));
                    break;
                case MAP_STARTING:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Starting"));
                    break;
                case MAP_AVAILABLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Available"));
                    break;
                case MAP_PLAYING:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Playing"));
                    break;
                case MAP_FINISHING:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Finishing"));
                    break;
                case MAP_RESETTING:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.States.Resetting"));
                    break;
                case COMPASS_TELEPORT:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Compass-Teleport"));
                    break;
                case BOWPARTICLE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Bow-Particles"));
                    break;
                case TEAM_LEAVE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Team.Leave"));
                    break;
                case TEAM_JOIN:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Team.Join"));
                    break;
                case WINBLOCK:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Categories.Win-Blocks"));
                    break;
                case WINNER_BROADCAST:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Arena.Winner-Broadcast"));
                    break;
                case TEAMMATE_DAMAGE_CANCEL:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Team.TeamMate-Damage-Cancel"));
                    break;
                case TEAM_BROADCAST_JOIN:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Team.Broadcast-Join"));
                    break;
                case TEAM_BROADCAST_LEAVE:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Team.Broadcast-Leave"));
                    break;
                case ALREADY_STARTED:
                    tr = Text.color(Languages.file().getString("Languages." + lang + ".Messages.System.Arena.Already-Started"));
                    break;
                default:
                    tr = "String not found (" + ts.name() + ")";
            }
        } catch (Exception e) {
            tr = "Error finding translation (" + ts.name() + ") check console";
            e.printStackTrace();
            return tr;
        }

        return tr;
    }

    public static boolean checkSelect() {
        return langList.size() != 0;
    }

    public static String getString(TSsingle ts) {
        if (ts.equals(TSsingle.BOSSBAR_ARENA_DEATHMATCH)) {
            return Text.color(Languages.file().getString("Strings.Boss-Bar.DeathMatch"));
        } else if (ts.equals(TSsingle.BOSSBAR_ARENA_END)) {
            return Text.color(Languages.file().getString("Strings.Boss-Bar.End"));
        } else if (ts.equals(TSsingle.BOSSBAR_ARENA_RUNTIME)) {
            return Text.color(Languages.file().getString("Strings.Boss-Bar.Run-Time"));
        } else if (ts.equals(TSsingle.BOSSBAR_ARENA_WAIT)) {
            return Text.color(Languages.file().getString("Strings.Boss-Bar.Wait"));
        } else if (ts.equals(TSsingle.BOSSBAR_ARENA_STARTING)) {
            return Text.color(Languages.file().getString("Strings.Boss-Bar.Starting"));
        } else {
            return "String not found (" + ts.name() + ")";
        }
    }

    public static ArrayList<String> getLanguages() {
        return langList;
    }
}
