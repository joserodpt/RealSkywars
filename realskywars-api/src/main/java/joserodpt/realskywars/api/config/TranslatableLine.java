package joserodpt.realskywars.api.config;

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.command.CommandSender;

public enum TranslatableLine {

    ACHIEVEMENT_GET(".Messages.System.Achievements.Get"),
    CANT_VOTE(".Messages.Vote.Cant-Vote"),
    CMD_CANT_FORCESTART(".Messages.Commands.Match-Force-Start-Denial"),
    ARENA_CANCEL(".Messages.Arena.Match-Cancelled"),
    ARENA_START_COUNTDOWN(".Messages.Arena.Start-Countdown"),
    ITEMS_MAP_TITLE(".Itens.Map.Title"),
    ITEMS_MAP_NOTFOUND_TITLE(".Itens.Map.Not-Found"),
    LOBBY_TELEPORT(".Messages.System.Lobby-Teleport"),
    MATCH_END(".Messages.Arena.Match-End"),
    MATCH_LEAVE(".Messages.System.Arena.Leave"),
    MATCH_SPECTATE(".Messages.Arena.Player-Spectate"),
    PLAYER_JOIN_ARENA(".Messages.Arena.Player-Join"),
    PLAYER_LEAVE_ARENA(".Messages.Arena.Player-Leave"),
    SCOREBOARD_PLAYING_TITLE(".Scoreboards.Game.Title"),
    SCOREBOARD_LOBBY_TITLE(".Scoreboards.Lobby.Title"),
    SCOREBOARD_CAGE_TITLE(".Scoreboards.Cage.Title"),
    SCOREBOARD_SPECTATOR_TITLE(".Scoreboards.Spectate.Title"),
    TITLE_DEATHMATCH(".Messages.Arena.Title.DeathMatch"),
    TITLE_WIN(".Messages.Arena.Title.Win"),
    CMD_NOPERM(".Messages.Commands.No-Permission"),
    CONFIG_RELOAD(".Messages.Commands.Config-Reload"),
    ALREADY_IN_MATCH(".Messages.Arena.Already-In-Match"),
    CMD_COINS(".Messages.Commands.Coins"),
    NO_SETUP_MODE(".Messages.Arena.No-Setup-Mode"),
    CMD_MATCH_CANCEL(".Messages.Arena.Suggest-Match-Cancel"),
    CMD_MATCH_FORCESTART(".Messages.Commands.Match-Force-Start"),
    NO_MATCH(".Messages.Commands.No-Match"),
    LOBBY_SET(".Messages.Commands.Lobby-Set"),
    SETUP_NOT_FINISHED(".Messages.Commands.Setup-Not-Finished"),
    CMD_MAPS(".Messages.Commands.Maps"),
    CMD_PLAYERS(".Messages.Commands.Players"),
    CMD_FINISHSETUP(".Messages.Commands.Finish-Setup"),
    NO_GAME_FOUND(".Messages.Commands.No-Game-Found"),
    GAME_FOUND(".Messages.Commands.Game-Found"),
    NO_TIER_FOUND(".Messages.Commands.No-Tier-Found"),
    TIER_SET(".Messages.System.Tier-Set"),
    CHEST_BASIC(".Messages.Vote.Chests.Basic"),
    CHEST_NORMAL(".Messages.Vote.Chests.Normal"),
    CHEST_EPIC(".Messages.Vote.Chests.Epic"),
    NO_PLAYER_FOUND(".Messages.Commands.No-Player-Found"),
    MAP_UNREGISTERED(".Messages.Commands.Map-Unregistered"),
    MAP_EXISTS(".Messages.Commands.Map-Exists"),
    KIT_EXISTS(".Messages.Commands.Kit-Exists"),
    LOBBYLOC_NOT_SET(".Messages.Commands.Lobby-Loc-Not-Set"),
    INSUFICIENT_COINS(".Messages.Commands.Insuficient-Coins"),
    CMD_NOT_FOUND(".Messages.Commands.Command-Not-Found"),
    CAGES_SET(".Messages.System.Cages-Done"),
    ADDED_COINS(".Messages.System.Coins.Add"),
    REMOVED_COINS(".Messages.System.Coins.Remove"),
    SET_COINS(".Messages.System.Coins.Set"),
    SENDER_COINS(".Messages.System.Coins.Sender"),
    RECIEVER_COINS(".Messages.System.Coins.Reciever"),
    LANGUAGE_SET(".Messages.System.Language-Set"),
    GENERATING_WORLD(".Messages.System.Generating-World"),
    NO_ARENA_BOUNDARIES(".Messages.Arena.No-Boundaries"),
    SAVING_ARENA(".Messages.Arena.Saving"),
    ARENA_REGISTERED(".Messages.Arena.Registered"),
    VOTE(".Messages.Vote.Vote"),
    ALREADY_VOTED(".Messages.Vote.Already-Voted"),
    GAME_STATUS_SET(".Messages.System.Game-Status-Set"),
    ARENA_RESET(".Messages.Arena.Reset"),
    MAP_RESET_DONE(".Messages.Commands.Reset-Done"),
    ADD_TIER(".Messages.Commands.Add-Tier"),
    SET_TIER(".Messages.Commands.Set-Tier"),
    SHOP_BUY_MESSAGE(".Messages.System.Shop.Buy"),
    SHOP_ALREADY_BOUGHT(".Messages.System.Shop.Already-Bought"),
    SHOP_NO_PERM(".Messages.System.Shop.No-Permission"),
    NO_KIT_FOUND(".Messages.System.Kits.Not-Found"),
    NOT_BUYABLE(".Messages.System.Shop.Not-Buyable"),
    DELETEKIT_DONE(".Messages.System.Kits.Delete-Done"),
    PROFILE_SELECTED(".Messages.System.Selection-Made"),
    DEL_PURCHASES(".Messages.System.Shop.No-Permission"),
    KITS(".Messages.System.Categories.Kits"),
    CAGEBLOCK(".Messages.System.Categories.Cage-Blocks"),
    MAP_ALL(".Messages.System.States.All"),
    MAP_WAITING(".Messages.System.States.Waiting"),
    MAP_SPECTATE(".Messages.System.States.Spectating"),
    MAP_STARTING(".Messages.System.States.Starting"),
    MAP_AVAILABLE(".Messages.System.States.Available"),
    MAP_PLAYING(".Messages.System.States.Playing"),
    MAP_FINISHING(".Messages.System.States.Finishing"),
    MAP_RESETTING(".Messages.System.States.Resetting"),
    COMPASS_TELEPORT(".Messages.System.Compass-Teleport"),
    BOWPARTICLE(".Messages.System.Categories.Bow-Particles"),
    TEAM_LEAVE(".Messages.System.Team.Leave"),
    TEAM_JOIN(".Messages.System.Team.Join"),
    WINBLOCK(".Messages.System.Categories.Win-Blocks"),
    WINNER_BROADCAST(".Messages.System.Arena.Winner-Broadcast"),
    TEAMMATE_DAMAGE_CANCEL(".Messages.System.Team.TeamMate-Damage-Cancel"),
    TEAM_BROADCAST_JOIN(".Messages.System.Team.Broadcast-Join"),
    TEAM_BROADCAST_LEAVE(".Messages.System.Team.Broadcast-Leave"),
    ALREADY_STARTED(".Messages.System.Arena.Already-Started"),
    STATS_ITEM_NAME(".Itens.Statistics.Name"),
    NO_TRACKER(".Messages.System.Arena.No-Tracker-Found"),
    TRACK_FOUND(".Messages.System.Arena.Tracker-Found"),
    ITEM_KIT_NAME(".Itens.Kit.Name"),
    ITEM_MAPS_NAME(".Itens.Maps.Name"),
    ITEM_CAGESET_NAME(".Itens.CageSet.Name"),
    ITEM_CHEST1_NAME(".Itens.Chest1.Name"),
    ITEM_CHEST2_NAME(".Itens.Chest2.Name"),
    ITEM_VOTE_NAME(".Itens.Vote.Name"),
    ITEM_LEAVE_NAME(".Itens.Leave.Name"),
    ITEM_PLAYAGAIN_NAME(".Itens.PlayAgain.Name"),
    ITEM_PROFILE_NAME(".Itens.Profile.Name"),
    ITEM_SHOP_NAME(".Itens.Shop.Name"),
    ITEM_SPECTATE_NAME(".Itens.Spectate.Name"),
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
    MENUS_SPECTATE_TITLE(".Menus.Spectate-Title"),
    MENUS_SHOP_TILE(".Menus.Shop-Menu-Title"),
    MENU_SPECTATOR_SHOP_TITLE(".Menus.Spectator-Shop-Menu-Title"),
    CANT_JOIN(".Messages.System.Arena.Cant-Join"),
    ROOM_FULL(".Messages.System.Arena.Room-Full"),
    SPECTATING_DISABLED(".Messages.System.Arena.Spectating-Disabled"),
    BLOCKED_COMMAND(".Messages.System.Arena.Blocked-Command"),
    MAPS_NAME(".Messages.Maps"),
    PLAY_AGAIN(".Messages.System.Arena.Play-Again"),
    PARTY_NOTINPARTY(".Messages.System.Party.Not-In-Party"),
    PARTY_KICK(".Messages.System.Party.Kick"),
    PARTY_NOT_OWNER(".Messages.System.Party.Not-Owner"),
    PARTY_DISBAND(".Messages.System.Party.Disband"),
    PARTY_CANTINVITEYOURSELF(".Messages.System.Party.Invite.Cant-Invite-Yourself"),
    PARTY_JOIN(".Messages.System.Party.Join"),
    PARTY_LEAVE(".Messages.System.Party.Leave"),
    PARTY_ALREADYCREATED(".Messages.System.Party.Already-Created"),
    PARTY_ACCEPTEDINVITE(".Messages.System.Party.Invite.Accepted"),
    PARTY_ALREADYIN(".Messages.System.Party.Already-In"),
    PARTY_CREATED(".Messages.System.Party.Created"),
    PARTY_INVITE_RECIEVED(".Messages.System.Party.Invite.Recieved"),
    PARTY_INVITE_SENT(".Messages.System.Party.Invite.Sent"),
    PARTY_INVITENOTFOUND(".Messages.System.Party.Invite.Not-Found"),
    PARTY_INSUFICIENT_ROOMSPACE(".Messages.System.Party.Insufficient-Room-Space"),
    ACHIEVEMENTS(".Messages.System.Categories.Achievements"),
    TIME_DAY(".Messages.Vote.Time.Day"),
    TIME_NIGHT(".Messages.Vote.Time.Night"),
    TIME_RAIN(".Messages.Vote.Time.Rain"),
    TIME_SUNSET(".Messages.Vote.Time.Sunset"),
    PROJECTILE_BREAK(".Messages.Vote.Projectiles.Break"),
    PROJECTILE_NORMAL(".Messages.Vote.Projectiles.Normal"),
    MENU_PLAYER_GAME_HISTORY(".Menus.Game-History-Title"),

    //singles
    BOSSBAR_ARENA_RUNTIME("Strings.Boss-Bar.Run-Time"),
    BOSSBAR_ARENA_STARTING("Strings.Boss-Bar.Starting"),
    BOSSBAR_ARENA_END("Strings.Boss-Bar.End"),
    BOSSBAR_ARENA_WAIT("Strings.Boss-Bar.Wait"),
    SEARCH_NOTFOUND_NAME("Strings.Search.Not-Found"),
    SHOP_BOUGHT("Strings.Shop.Already-Bought"),
    SHOP_BUY("Strings.Shop.Buy"),
    ADMIN_SHUTDOWN("Strings.Admin-Shutdown"),
    KIT_PRICE("Strings.Kit.Price"),
    KIT_BUY("Strings.Kit.Buy"),
    KIT_SELECT("Strings.Kit.Select"),
    KIT_CONTAINS("Strings.Kit.Contains"),
    KIT_ITEM("Strings.Kit.Items"),

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

    public String get(RSWPlayer player) {
        return get(player, false);
    }

    public String get(RSWPlayer player, boolean prefix) {
        return Text.color((prefix ? RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() : "") + RSWLanguagesConfig.file().getString("Languages." + player.getLanguage() + this.configPath));
    }


    public String getDefault() {
        return Text.color("Languages." + RSWConfig.file().getString("Config.Default-Language") + RSWLanguagesConfig.file().getString(this.configPath));
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
}
