package joserodpt.realskywars.api.config;

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.command.CommandSender;

public enum TranslatableLine {

    SOLO_MODE(".Modes.Solo"),
    SOLO_RANKED_MODE(".Modes.Solo-Ranked"),
    TEAMS_MODE(".Modes.Teams"),
    TEAMS_RANKED_MODE(".Modes.Teams-Ranked"),

    ACHIEVEMENT_GET(".Messages.Achievements.Get"),
    ACHIEVEMENT_REWARD(".Messages.Achievements.Reward"),
    ACHIEVEMENT_NAME_COINS(".Messages.Achievements.Names.Coins"),
    ACHIEVEMENT_GOAL(".Messages.Achievements.Goal"),

    ARENA_CANCEL(".Messages.Map.Match-Cancelled"),
    ARENA_START_COUNTDOWN(".Messages.Map.Start-Countdown"),
    LOBBY_TELEPORT(".Messages.Lobby-Teleport"),
    MATCH_END(".Messages.Map.Match-End"),
    INVINCIBILITY_END(".Messages.Map.Invincibility-End"),
    MATCH_LEAVE(".Messages.Map.Leave"),
    MATCH_SPECTATE(".Messages.Map.Player-Spectate"),
    PLAYER_JOIN_ARENA(".Messages.Map.Player-Join"),
    PLAYER_LEAVE_ARENA(".Messages.Map.Player-Leave"),
    TITLE_DEATHMATCH(".Messages.Map.Title.DeathMatch"),
    TITLE_WIN(".Messages.Map.Title.Win"),

    SCOREBOARD_SPECTATOR_TITLE(".Scoreboards.Spectate.Title"),
    SCOREBOARD_PLAYING_TITLE(".Scoreboards.Game.Title"),
    SCOREBOARD_LOBBY_TITLE(".Scoreboards.Lobby.Title"),
    SCOREBOARD_CAGE_TITLE(".Scoreboards.Cage.Title"),

    CMD_CANT_FORCESTART(".Messages.Commands.Match-Force-Start-Denial"),
    CMD_CONFIG_RELOAD(".Messages.Commands.Config-Reload"),
    CMD_ALREADY_IN_MATCH(".Messages.Map.Already-In-Match"),
    CMD_CNO_MATCH(".Messages.Commands.No-Match"),
    CMD_CLOBBY_SET(".Messages.Commands.Lobby-Set"),
    CMD_MATCH_CANCEL(".Messages.Map.Suggest-Match-Cancel"),
    CMD_COINS(".Messages.Commands.Coins"),
    CMD_NO_PERM(".Messages.Commands.No-Permission"),
    CMD_MATCH_FORCESTART(".Messages.Commands.Match-Force-Start"),
    CMD_MAPS(".Messages.Commands.Maps"),
    CMD_PLAYERS(".Messages.Commands.Players"),
    CMD_INCORRECT_NUMBER_OF_CAGES_SOLO(".Messages.Commands.Map-Incorrect-Number-Of-Cages-Solo"),
    CMD_INCORRECT_NUMBER_OF_CAGES_TEAMS(".Messages.Commands.Map-Incorrect-Number-Of-Cages-Teams"),
    CMD_SPEC_SET(".Messages.Commands.Map-Spectator-Location-Set"),
    CMD_SPEC_LOCATION_NOT_SET(".Messages.Commands.Map-Spectator-Location-Not-Set"),
    CMD_NO_CAGES_SET(".Messages.Commands.Map-No-Cages-Set"),
    CMD_NO_MAP_FOUND(".Messages.Commands.No-Map-Found"),
    CMD_MAP_FOUND(".Messages.Commands.Map-Found"),
    CMD_NO_TIER_FOUND(".Messages.Commands.No-Tier-Found"),
    NO_PLAYER_FOUND(".Messages.Commands.No-Player-Found"),
    MAP_COMMAND_REGISTERED(".Messages.Commands.Map-Registered"),
    MAP_IS_UNREGISTERED(".Messages.Commands.Map-Is-Unregistered"),
    MAP_UNREGISTERED(".Messages.Commands.Map-Unregistered"),
    MAP_DELETED(".Messages.Commands.Map-Deleted"),
    MAP_UNREGISTER_TO_EDIT(".Messages.Commands.Map-Unregister-To-Edit"),
    MAP_ALREADY_REGISTERED(".Messages.Commands.Map-Already-Registered"),
    MAP_ALREADY_UNREGISTERED(".Messages.Commands.Map-Already-Unregistered"),
    MAP_RENAMED(".Messages.Commands.Map-Renamed"),
    MAP_EXISTS(".Messages.Commands.Map-Exists"),
    LOBBYLOC_NOT_SET(".Messages.Commands.Lobby-Loc-Not-Set"),
    INSUFICIENT_COINS(".Messages.Commands.Insuficient-Coins"),
    CMD_NOT_FOUND(".Messages.Commands.Command-Not-Found"),
    MAP_RESET_DONE(".Messages.Commands.Reset-Done"),
    ADD_TIER(".Messages.Commands.Add-Tier"),
    SET_TIER(".Messages.Commands.Set-Tier"),
    KIT_EXISTS(".Messages.Commands.Kit-Exists"),

    CANT_VOTE(".Vote.Cant-Vote"),
    VOTE_TIME_DAY(".Vote.Time.Day"),
    VOTE_TIME_NIGHT(".Vote.Time.Night"),
    VOTE_TIME_RAIN(".Vote.Time.Rain"),
    VOTE_TIME_SUNSET(".Vote.Time.Sunset"),
    VOTE_PROJECTILE_BREAK(".Vote.Projectiles.Break"),
    VOTE_PROJECTILE_NORMAL(".Vote.Projectiles.Normal"),
    VOTE_CHEST_BASIC(".Vote.Chests.Basic"),
    VOTE_CHEST_NORMAL(".Vote.Chests.Normal"),
    VOTE_CHEST_EPIC(".Vote.Chests.Epic"),
    CAST_VOTE(".Vote.Vote"),
    ALREADY_VOTED(".Vote.Already-Voted"),

    ITEM_STATS_NAME(".Itens.Statistics.Name"),
    ITEM_MAP_NAME(".Itens.Map.Title"),
    ITEM_MAP_NOTFOUND_NAME(".Itens.Map.Not-Found"),
    ITEM_KIT_NAME(".Itens.Kit.Name"),
    ITEM_MAPS_NAME(".Itens.Maps.Name"),
    ITEM_CAGESET_NAME(".Itens.CageSet.Name"),
    ITEM_CHEST1_NAME(".Itens.Chest1.Name"),
    ITEM_CHEST2_NAME(".Itens.Chest2.Name"),
    ITEM_LEAVE_NAME(".Itens.Leave.Name"),
    ITEM_PLAYAGAIN_NAME(".Itens.PlayAgain.Name"),
    ITEM_PROFILE_NAME(".Itens.Profile.Name"),
    ITEM_SHOP_NAME(".Itens.Shop.Name"),
    ITEM_SPECTATE_NAME(".Itens.Spectate.Name"),
    ITEM_VOTE_NAME(".Itens.Vote.Name"),
    ITEM_SETTINGS_NAME(".Itens.Settings.Name"),
    ITEM_SAVE_NAME(".Itens.Save.Name"),

    MENU_PLAYER_RESET_ALERT(".Menus.Player-Profile.Reset-Data.Alert"),
    MENU_PLAYERP_VIEWITEM(".Menus.Player-Profile.View-Item"),
    MENU_PLAYER_RESET_TITLE(".Menus.Player-Profile.Reset-Data.Title"),
    MENU_VOTE_TITLE(".Menus.Vote-Title"),
    MENU_CHESTS_TITLE(".Menus.Chests-Vote-Title"),
    MENU_PROJECTILES_TITLE(".Menus.Projectiles-Vote-Title"),
    MENU_TIME_TITLE(".Menus.Time-Vote-Title"),
    MENU_LANG_SELECT(".Menus.Language.Select"),
    MENU_LANG_TITLE(".Menus.Language.Title"),
    MENU_PLAYERP_TITLE(".Menus.Player-Profile.Title"),
    MENU_SPECTATE_TITLE(".Menus.Spectate-Title"),
    MENU_SHOP_TILE(".Menus.Shop-Menu-Title"),
    MENU_SPECTATOR_SHOP_TITLE(".Menus.Spectator-Shop-Menu-Title"),
    MENU_MAPS_TITLE(".Menus.Maps-Title"),

    MAP_ALL(".States.All"),
    MAP_STATE_WAITING(".States.Waiting"),
    MAP_SPECTATE(".States.Spectating"),
    MAP_STATE_STARTING(".States.Starting"),
    MAP_STATE_AVAILABLE(".States.Available"),
    MAP_STATE_PLAYING(".States.Playing"),
    MAP_STATE_FINISHING(".States.Finishing"),
    MAP_STATE_RESETTING(".States.Resetting"),

    KITS(".Shop-Categories.Kits"),
    CAGEBLOCK(".Shop-Categories.Cage-Blocks"),
    BOWPARTICLE(".Shop-Categories.Bow-Particles"),
    WINBLOCK(".Shop-Categories.Win-Blocks"),

    KIT_PRICE(".Kits.Price"),
    KIT_CONTAINS(".Kits.Contains"),
    KIT_ITEM(".Kits.Item"),
    KIT_BUY(".Kits.Buy"),
    KIT_SELECT(".Kits.Select"),
    KIT_NOT_FOUND(".Kits.Not-Found"),
    KIT_DELETE(".Kits.Delete"),
    KIT_CREATED(".Kits.Created"),

    CANT_JOIN(".Messages.Map.Cant-Join"),
    ROOM_FULL(".Messages.Map.Room-Full"),
    SPECTATING_DISABLED(".Messages.Map.Spectating-Disabled"),
    BLOCKED_COMMAND(".Messages.Map.Blocked-Command"),
    PLAY_AGAIN(".Messages.Map.Play-Again"),
    PARTY_NOTINPARTY(".Messages.Party.Not-In-Party"),
    PARTY_KICK(".Messages.Party.Kick"),
    PARTY_NOT_OWNER(".Messages.Party.Not-Owner"),
    PARTY_DISBAND(".Messages.Party.Disband"),
    PARTY_CANTINVITEYOURSELF(".Messages.Party.Invite.Cant-Invite-Yourself"),
    PARTY_JOIN(".Messages.Party.Join"),
    PARTY_LEAVE(".Messages.Party.Leave"),
    PARTY_ALREADYCREATED(".Messages.Party.Already-Created"),
    PARTY_ACCEPTEDINVITE(".Messages.Party.Invite.Accepted"),
    PARTY_ALREADYIN(".Messages.Party.Already-In"),
    PARTY_CREATED(".Messages.Party.Created"),
    PARTY_INVITE_RECIEVED(".Messages.Party.Invite.Recieved"),
    PARTY_INVITE_SENT(".Messages.Party.Invite.Sent"),
    PARTY_INVITENOTFOUND(".Messages.Party.Invite.Not-Found"),
    PARTY_INSUFICIENT_ROOMSPACE(".Messages.Party.Insufficient-Room-Space"),
    ACHIEVEMENTS(".Menus.Achievements-Title"),
    MENU_PLAYER_GAME_HISTORY(".Menus.Game-History-Title"),
    TIER_SET(".Messages.Tier-Set"),
    CAGES_SET(".Messages.Cages-Done"),
    ADDED_COINS(".Messages.Coins.Add"),
    REMOVED_COINS(".Messages.Coins.Remove"),
    SET_COINS(".Messages.Coins.Set"),
    SENDER_COINS(".Messages.Coins.Sender"),
    RECIEVER_COINS(".Messages.Coins.Reciever"),
    LANGUAGE_SET(".Messages.Language-Set"),
    GENERATING_WORLD(".Messages.Generating-World"),
    NO_ARENA_BOUNDARIES(".Messages.Map.No-Boundaries"),
    SAVING_MAP(".Messages.Map.Saving"),
    MAP_REGISTERED(".Messages.Map.Registered"),
    GAME_STATUS_SET(".Messages.Game-Status-Set"),
    ARENA_RESET(".Messages.Map.Reset"),
    SHOP_BUY_MESSAGE(".Messages.Shop.Buy"),
    SHOP_ALREADY_BOUGHT(".Messages.Shop.Already-Bought"),
    SHOP_CLICK_2_BUY(".Messages.Shop.Click-To-Buy"),
    SHOP_CLICK_2_SELECT(".Messages.Shop.Click-To-Select"),
    SHOP_NO_PERM(".Messages.Shop.No-Permission"),
    NOT_BUYABLE(".Messages.Shop.Not-Buyable"),
    PROFILE_SELECTED(".Messages.Selection-Made"),
    DEL_PURCHASES(".Messages.Shop.No-Permission"),
    COMPASS_TELEPORT(".Messages.Compass-Teleport"),
    TEAM_LEAVE(".Messages.Team.Leave"),
    TEAM_JOIN(".Messages.Team.Join"),
    WINNER_BROADCAST(".Messages.Map.Winner-Broadcast"),
    TEAMMATE_DAMAGE_CANCEL(".Messages.Team.TeamMate-Damage-Cancel"),
    TEAM_BROADCAST_JOIN(".Messages.Team.Broadcast-Join"),
    TEAM_BROADCAST_LEAVE(".Messages.Team.Broadcast-Leave"),
    ALREADY_STARTED(".Messages.Map.Already-Started"),
    NO_TRACKER(".Messages.Map.No-Tracker-Found"),
    TRACK_FOUND(".Messages.Map.Tracker-Found"),

    STATISTIC_WINS_SOLO(".Statistics.Wins.Solo"),
    STATISTIC_WINS_TEAMS(".Statistics.Wins.Teams"),
    STATISTIC_KILLS(".Statistics.Kills"),
    STATISTIC_DEATHS(".Statistics.Deaths"),
    STATISTIC_LOSES(".Statistics.Loses"),
    STATISTIC_GAMES_PLAYED(".Statistics.Games.Played"),
    STATISTIC_GAMES_BALANCE(".Statistics.Games.Balance"),
    STATISTIC_GAMES_KILLS(".Statistics.Games.Kills"),

    //singles
    BOSSBAR_ARENA_RUNTIME("Strings.Boss-Bar.Run-Time"),
    BOSSBAR_ARENA_STARTING("Strings.Boss-Bar.Starting"),
    BOSSBAR_ARENA_END("Strings.Boss-Bar.End"),
    BOSSBAR_ARENA_WAIT("Strings.Boss-Bar.Wait"),
    SEARCH_NOTFOUND_NAME("Strings.Search.Not-Found"),
    ADMIN_SHUTDOWN("Strings.Admin-Shutdown"),

    BUNGEECORD_FULL("Strings.BungeeCord.Full"),
    BUNGEECORD_NO_AVAILABLE_MAPS("Strings.BungeeCord.No-Available-Maps"),
    BUNGEECORD_KICK_MESSAGE("Strings.BungeeCord.Kick-Message"),
    BUNGEECORD_RESETTING_MESSAGE("Strings.BungeeCord.Resetting"),

    BUTTONS_NEXT_TITLE("Strings.Menus.Next-Button.Title"),
    BUTTONS_NEXT_DESC("Strings.Menus.Next-Button.Description"),
    BUTTONS_BACK_TITLE("Strings.Menus.Back-Button.Title"),
    BUTTONS_BACK_DESC("Strings.Menus.Back-Button.Description"),
    BUTTONS_FILTER_TITLE("Strings.Menus.Filter-Button.Title"),
    BUTTONS_FILTER_DESC("Strings.Menus.Filter-Button.Description"),
    BUTTONS_MENU_TITLE("Strings.Menus.Main-Menu-Button.Title"),
    BUTTONS_MENU_DESC("Strings.Menus.Main-Menu-Button.Description"),
    BOSSBAR_ARENA_DEATHMATCH("Strings.Boss-Bar.DeathMatch");

    private final String configPath;

    TranslatableLine(String configPath) {
        this.configPath = configPath;
    }

    public String getSingle() {
        return Text.color(RSWLanguagesConfig.file().getString(this.configPath));
    }

    public String getInLanguage(String l) {
        return Text.color(RSWLanguagesConfig.file().getString("Languages." + l + this.configPath));
    }

    public String get(RSWPlayer player) {
        return get(player, false);
    }

    public String get(RSWPlayer player, boolean prefix) {
        return Text.color((prefix ? RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() : "") + getInLanguage(player.getLanguage()));
    }

    public String getDefault() {
        return getInLanguage(RSWLanguagesConfig.file().getString("Default-Language"));
    }

    public void sendDefault(CommandSender p, boolean prefix) {
        p.sendMessage(prefix ? RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + getDefault() : getDefault());
    }

    public void sendSingle(RSWPlayer p) {
        if (p.getPlayer() != null) {
            p.sendMessage(getSingle());
        }
    }

    public void send(RSWPlayer p, boolean prefix) {
        if (p.getPlayer() != null) {
            p.sendMessage(get(p, prefix));
        }
    }

    public String getPath() {
        return this.configPath;
    }
}
