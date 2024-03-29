package joserodpt.realskywars.plugin.managers;

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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LanguageManager extends LanguageManagerAPI {

    private final ArrayList<String> langList = new ArrayList<>();

    @Override
    public void loadLanguages() {
        this.langList.clear();
        this.langList.addAll(RSWLanguagesConfig.file().getSection("Languages").getRoutesAsStrings(false));
    }

    @Override
    public String getDefaultLanguage() {
        return this.langList.contains(RSWConfig.file().getString("Config.Default-Language")) ? RSWConfig.file().getString("Config.Default-Language") : langList.get(0);
    }

    @Override
    public HashMap<String, HashMap<TS, String>> verifyLanguages() {
        HashMap<String, HashMap<TS, String>> flag = new HashMap<>();
        HashMap<TS, String> flagItem = new HashMap<>();
        for (String s : getLanguages()) {
            for (TS val : TS.values()) {
                String analyse = getString(s, val);
                if (analyse.equals("String not found (" + val.name() + ")") || analyse.equals("Error finding translation (" + val.name() + ")")) {
                    flagItem.put(val, analyse);
                }
            }
            if (!(flagItem.isEmpty())) {
                flag.put(s, flagItem);
            }
        }
        return flag;
    }

    @Override
    public boolean areLanguagesEmpty() {
        return langList.isEmpty();
    }

    @Override
    public String getString(TSsingle ts) {
        try {
            switch (ts) {
                default:
                    return "String not found (" + ts.name() + ")";
                case BOSSBAR_ARENA_END:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Boss-Bar.End"));
                case BOSSBAR_ARENA_WAIT:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Boss-Bar.Wait"));
                case BOSSBAR_ARENA_RUNTIME:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Boss-Bar.Run-Time"));
                case BOSSBAR_ARENA_STARTING:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Boss-Bar.Starting"));
                case BOSSBAR_ARENA_DEATHMATCH:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Boss-Bar.DeathMatch"));
                case SEARCH_NOTFOUND_NAME:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Search.Not-Found"));
                case SHOP_BUY:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Shop.Buy"));
                case SHOP_BOUGHT:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Shop.Already-Bought"));
                case ADMIN_SHUTDOWN:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Admin-Shutdown"));
                case KIT_BUY:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Kit.Buy"));
                case KIT_ITEM:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Kit.Items"));
                case KIT_PRICE:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Kit.Price"));
                case KIT_SELECT:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Kit.Select"));
                case KIT_CONTAINS:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Kit.Contains"));
                case BUTTONS_BACK_DESC:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Back-Button.Description"));
                case BUTTONS_FILTER_DESC:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Filter-Button.Description"));
                case BUTTONS_NEXT_DESC:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Next-Button.Description"));
                case BUTTONS_BACK_TITLE:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Back-Button.Title"));
                case BUTTONS_FILTER_TITLE:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Filter-Button.Title"));
                case BUTTONS_NEXT_TITLE:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Next-Button.Title"));
                case BUTTONS_MENU_DESC:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Main-Menu-Button.Description"));
                case BUTTONS_MENU_TITLE:
                    return Text.color(RSWLanguagesConfig.file().getString("Strings.Menus.Main-Menu-Button.Title"));
            }
        } catch (Exception e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Error finding translation for RealSkywars: " + e.getMessage());
            return "Error finding translation (" + ts.name() + ") check console";
        }
    }

    @Override
    public List<String> getLanguages() {
        return langList;
    }

    @Override
    public String getString(TS ts, boolean b) {
        return getString(new RSWPlayer(false), ts, b);
    }

    @Override
    public String getString(RSWPlayer p, TS ts, boolean b) {
        if (!b) {
            return Text.color(getString(p, ts));
        } else {
            return getPrefix() + Text.color(getString(p, ts));
        }
    }

    @Override
    public String getPrefix() {
        return Text.color(RSWLanguagesConfig.file().getString("Strings.Prefix"));
    }

    @Override
    public List<String> getList(RSWPlayer p, TL tl) {
        String lang = p.getLanguage();
        List<String> trad = new ArrayList<>();

        switch (tl) {
            case ARENA_START:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.Start"));
                break;
            case ARENA_END:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.End-Log"));
                break;
            case INITSETUP_ARENA:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.Arena.Init-Setup"));
                break;
            case SCOREBOARD_LOBBY_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Lobby.Lines"));
                break;
            case SCOREBOARD_CAGE_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Cage.Lines"));
                break;
            case SCOREBOARD_PLAYING_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Game.Lines"));
                break;
            case SCOREBOARD_SPECTATOR_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Spectate.Lines"));
                break;
            case ITEMS_MAP_DESCRIPTION:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Itens.Map.Description"));
                break;
            case TITLE_ROOMJOIN:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.Titles.Join-Room"));
                break;
            case STATS_ITEM_LORE:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Itens.Statistics.Lore"));
                break;
            case REFILL_EVENT_TITLE:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.Events.Refill"));
                break;
            case TNTRAIN_EVENT_TITLE:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.Events.TNTRain"));
                break;
            case TAB_HEADER_MATCH:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.InGame.Header"));
                break;
            case TAB_FOOTER_MATCH:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.InGame.Footer"));
                break;
            case TAB_HEADER_OTHER:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.Other.Header"));
                break;
            case TAB_FOOTER_OTHER:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.Other.Footer"));
                break;
            default:
                trad.add("List not found (" + tl.name() + ")");
        }

        return trad;
    }

    @Override
    protected String getString(String l, TS ts) {
        RSWPlayer d = new RSWPlayer(false);
        d.setLanguage(l);
        return getString(d, ts);
    }

    @Override
    protected String getString(RSWPlayer p, TS ts) {
        String lang = p.getLanguage();
        String tr;

        try {
            switch (ts) {
                case ACHIEVEMENT_GET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Achievements.Get"));
                    break;
                case CANT_VOTE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Cant-Vote"));
                    break;
                case CMD_CANT_FORCESTART:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Match-Force-Start-Denial"));
                    break;
                case ARENA_CANCEL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Match-Cancelled"));
                    break;
                case ARENA_START_COUNTDOWN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Start-Countdown"));
                    break;
                case ITEMS_MAP_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Map.Title"));
                    break;
                case ITEMS_MAP_NOTFOUND_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Map.Not-Found"));
                    break;
                case LOBBY_TELEPORT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Lobby-Teleport"));
                    break;
                case MATCH_END:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Match-End"));
                    break;
                case MATCH_LEAVE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Leave"));
                    break;
                case MATCH_SPECTATE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Player-Spectate"));
                    break;
                case PLAYER_JOIN_ARENA:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Player-Join"));
                    break;
                case PLAYER_LEAVE_ARENA:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Player-Leave"));
                    break;
                case SCOREBOARD_PLAYING_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Scoreboards.Game.Title"));
                    break;
                case SCOREBOARD_LOBBY_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Scoreboards.Lobby.Title"));
                    break;
                case SCOREBOARD_CAGE_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Scoreboards.Cage.Title"));
                    break;
                case SCOREBOARD_SPECTATOR_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Scoreboards.Spectate.Title"));
                    break;
                case TITLE_DEATHMATCH:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Title.DeathMatch"));
                    break;
                case TITLE_WIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Title.Win"));
                    break;
                case CMD_NOPERM:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.No-Permission"));
                    break;
                case CONFIG_RELOAD:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Config-Reload"));
                    break;
                case ALREADY_IN_MATCH:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Already-In-Match"));
                    break;
                case CMD_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Coins"));
                    break;
                case NO_SETUPMODE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.No-Setup-Mode"));
                    break;
                case CMD_MATCH_CANCEL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Suggest-Match-Cancel"));
                    break;
                case CMD_MATCH_FORCESTART:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Match-Force-Start"));
                    break;
                case NO_MATCH:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.No-Match"));
                    break;
                case LOBBY_SET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Lobby-Set"));
                    break;
                case SETUP_NOT_FINISHED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Setup-Not-Finished"));
                    break;
                case CMD_MAPS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Maps"));
                    break;
                case CMD_PLAYERS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Players"));
                    break;
                case CMD_FINISHSETUP:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Finish-Setup"));
                    break;
                case NO_GAME_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.No-Game-Found"));
                    break;
                case GAME_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Game-Found"));
                    break;
                case NO_TIER_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.No-Tier-Found"));
                    break;
                case TIER_SET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Tier-Set"));
                    break;
                case CHEST_BASIC:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Chests.Basic"));
                    break;
                case CHEST_NORMAL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Chests.Normal"));
                    break;
                case CHEST_EPIC:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Chests.Epic"));
                    break;
                case NO_PLAYER_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.No-Player-Found"));
                    break;
                case MAP_UNREGISTERED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Map-Unregistered"));
                    break;
                case MAP_EXISTS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Map-Exists"));
                    break;
                case KIT_EXISTS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Kit-Exists"));
                    break;
                case LOBBYLOC_NOT_SET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Lobby-Loc-Not-Set"));
                    break;
                case INSUFICIENT_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Insuficient-Coins"));
                    break;
                case CMD_NOT_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Command-Not-Found"));
                    break;
                case CAGES_SET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Cages-Done"));
                    break;
                case ADDED_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Coins.Add"));
                    break;
                case REMOVED_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Coins.Remove"));
                    break;
                case SET_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Coins.Set"));
                    break;
                case SENDER_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Coins.Sender"));
                    break;
                case RECIEVER_COINS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Coins.Reciever"));
                    break;
                case LANGUAGE_SET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Language-Set"));
                    break;
                case GENERATING_WORLD:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Generating-World"));
                    break;
                case NO_ARENA_BOUNDARIES:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.No-Boundaries"));
                    break;
                case SAVING_ARENA:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Saving"));
                    break;
                case ARENA_REGISTERED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Registered"));
                    break;
                case VOTE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Vote"));
                    break;
                case ALREADY_VOTED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Already-Voted"));
                    break;
                case GAME_STATUS_SET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Game-Status-Set"));
                    break;
                case ARENA_RESET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Arena.Reset"));
                    break;
                case MAP_RESET_DONE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Reset-Done"));
                    break;
                case ADD_TIER:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Add-Tier"));
                    break;
                case SET_TIER:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Commands.Set-Tier"));
                    break;
                case SHOP_BUY:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Shop.Buy"));
                    break;
                case SHOP_ALREADY_BOUGHT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Shop.Already-Bought"));
                    break;
                case SHOP_NO_PERM:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Shop.No-Permission"));
                    break;
                //
                case NO_KIT_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Kits.Not-Found"));
                    break;
                case NOT_BUYABLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Shop.Not-Buyable"));
                    break;
                case DELETEKIT_DONE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Kits.Delete-Done"));
                    break;
                case PROFILE_SELECTED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Selection-Made"));
                    break;
                case DEL_PURCHASES:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Shop.No-Permission"));
                    break;
                case KITS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Categories.Kits"));
                    break;
                case CAGEBLOCK:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Categories.Cage-Blocks"));
                    break;
                case MAP_ALL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.All"));
                    break;
                case MAP_WAITING:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Waiting"));
                    break;
                case MAP_SPECTATE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Spectating"));
                    break;
                case MAP_STARTING:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Starting"));
                    break;
                case MAP_AVAILABLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Available"));
                    break;
                case MAP_PLAYING:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Playing"));
                    break;
                case MAP_FINISHING:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Finishing"));
                    break;
                case MAP_RESETTING:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.States.Resetting"));
                    break;
                case COMPASS_TELEPORT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Compass-Teleport"));
                    break;
                case BOWPARTICLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Categories.Bow-Particles"));
                    break;
                case TEAM_LEAVE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Team.Leave"));
                    break;
                case TEAM_JOIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Team.Join"));
                    break;
                case WINBLOCK:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Categories.Win-Blocks"));
                    break;
                case WINNER_BROADCAST:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Winner-Broadcast"));
                    break;
                case TEAMMATE_DAMAGE_CANCEL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Team.TeamMate-Damage-Cancel"));
                    break;
                case TEAM_BROADCAST_JOIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Team.Broadcast-Join"));
                    break;
                case TEAM_BROADCAST_LEAVE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Team.Broadcast-Leave"));
                    break;
                case ALREADY_STARTED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Already-Started"));
                    break;
                case STATS_ITEM_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Statistics.Name"));
                    break;
                case SOLO:
                    tr = "&eSolo";
                    break;
                case TEAMS:
                    tr = "&9Teams";
                    break;
                case NO_TRACKER:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.No-Tracker-Found"));
                    break;
                case TRACK_FOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Tracker-Found"));
                    break;
                case ITEM_KIT_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Kit.Name"));
                    break;
                case ITEM_MAPS_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Maps.Name"));
                    break;
                case ITEM_CAGESET_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.CageSet.Name"));
                    break;
                case ITEM_CHEST1_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Chest1.Name"));
                    break;
                case ITEM_CHEST2_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Chest2.Name"));
                    break;
                case ITEM_VOTE_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Vote.Name"));
                    break;
                case ITEM_LEAVE_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Leave.Name"));
                    break;
                case ITEM_PLAYAGAIN_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.PlayAgain.Name"));
                    break;
                case ITEM_PROFILE_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Profile.Name"));
                    break;
                case ITEM_SHOP_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Shop.Name"));
                    break;
                case ITEM_SPECTATE_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Itens.Spectate.Name"));
                    break;
                case MENU_PLAYERP_RESET_ALERT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Player-Profile.Reset-Data.Alert"));
                    break;
                case MENU_PLAYERP_VIEWITEM:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Player-Profile.View-Item"));
                    break;
                case MENU_PLAYERP_RESET_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Player-Profile.Reset-Data.Title"));
                    break;
                case MENU_VOTE_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Vote-Title"));
                    break;
                case MENU_CHESTS_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Chests-Vote-Title"));
                    break;
                case MENU_PROJECTILES_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Projectiles-Vote-Title"));
                    break;
                case MENU_TIME_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Time-Vote-Title"));
                    break;
                case MENU_LANG_SELECT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Language.Select"));
                    break;
                case MENU_LANG_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Language.Title"));
                    break;
                case MENU_PLAYERP_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Player-Profile.Title"));
                    break;
                case MENUS_SPECTATE_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Spectate-Title"));
                    break;
                case MENUS_SHOP_TILE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Shop-Menu-Title"));
                    break;
                case MENU_SPECTATOR_SHOP_TITLE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Spectator-Shop-Menu-Title"));
                    break;
                case CANT_JOIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Cant-Join"));
                    break;
                case ROOM_FULL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Room-Full"));
                    break;
                case SPECTATING_DISABLED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Spectating-Disabled"));
                    break;
                case BLOCKED_COMMAND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Blocked-Command"));
                    break;
                case MAPS_NAME:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Maps"));
                    break;
                case PLAY_AGAIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Arena.Play-Again"));
                    break;
                case PARTY_NOTINPARTY:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Not-In-Party"));
                    break;
                case PARTY_KICK:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Kick"));
                    break;
                case PARTY_NOT_OWNER:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Not-Owner"));
                    break;
                case PARTY_DISBAND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Disband"));
                    break;
                case PARTY_CANTINVITEYOURSELF:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Invite.Cant-Invite-Yourself"));
                    break;
                case PARTY_JOIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Join"));
                    break;
                case PARTY_LEAVE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Leave"));
                    break;
                case PARTY_ALREADYCREATED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Already-Created"));
                    break;
                case PARTY_ACCEPTEDINVITE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Invite.Accepted"));
                    break;
                case PARTY_ALREADYIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Already-In"));
                    break;
                case PARTY_CREATED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Created"));
                    break;
                case PARTY_INVITE_RECIEVED:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Invite.Recieved"));
                    break;
                case PARTY_INVITE_SENT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Invite.Sent"));
                    break;
                case PARTY_INVITENOTFOUND:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Invite.Not-Found"));
                    break;
                case PARTY_INSUFICIENT_ROOMSPACE:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Party.Insufficient-Room-Space"));
                    break;
                case SOLO_RANKED:
                    tr = getString(p, TS.SOLO, false) + " &b&LRANKED";
                    break;
                case TEAMS_RANKED:
                    tr = getString(p, TS.TEAMS, false) + " &b&LRANKED";
                    break;
                case ACHIEVEMENTS:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.System.Categories.Achievements"));
                    break;
                case TIME_DAY:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Time.Day"));
                    break;
                case TIME_NIGHT:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Time.Night"));
                    break;
                case TIME_RAIN:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Time.Rain"));
                    break;
                case TIME_SUNSET:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Time.Sunset"));
                    break;
                case PROJECTILE_BREAK:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Projectiles.Break"));
                    break;
                case PROJECTILE_NORMAL:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Messages.Vote.Projectiles.Normal"));
                    break;
                case MENU_PLAYERP_GAME_HISTORY:
                    tr = Text.color(RSWLanguagesConfig.file().getString("Languages." + lang + ".Menus.Game-History-Title"));
                    break;
                default:
                    tr = "String not found (" + ts.name() + ")";
            }
        } catch (Exception e) {
            tr = "Error finding translation (" + ts.name() + ") check console";
            RealSkywarsAPI.getInstance().getLogger().severe("Error finding translation for RealSkywars: " + e.getMessage());
            return tr;
        }

        return tr;
    }

}
