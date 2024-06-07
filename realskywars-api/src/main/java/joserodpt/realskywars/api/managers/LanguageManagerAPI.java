package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.player.RSWPlayer;

import java.util.HashMap;
import java.util.List;

public abstract class LanguageManagerAPI {
    public abstract void loadLanguages();

    public abstract String getDefaultLanguage();

    public abstract HashMap<String, HashMap<TS, String>> verifyLanguages();

    public abstract boolean areLanguagesEmpty();

    public abstract List<String> getLanguages();

    public abstract String getString(TS ts, boolean b);

    public abstract String getString(RSWPlayer p, TS ts, boolean b);

    public abstract String getPrefix();

    public abstract List<String> getList(RSWPlayer p, TL tl);

    protected abstract String getString(String l, TS ts);

    protected abstract String getString(RSWPlayer p, TS ts);

    public enum TS {
        PLAYER_JOIN_ARENA, ARENA_CANCEL, ARENA_START_COUNTDOWN, LOBBY_TELEPORT, MATCH_LEAVE, PLAYER_LEAVE_ARENA, MATCH_END, TITLE_WIN, MATCH_SPECTATE, TITLE_DEATHMATCH, CMD_NOPERM, CONFIG_RELOAD, ALREADY_IN_MATCH, CMD_COINS, NO_SETUPMODE, CMD_MATCH_CANCEL, CMD_MATCH_FORCESTART, NO_MATCH, LOBBY_SET, SETUP_NOT_FINISHED, CMD_MAPS, CMD_PLAYERS, CMD_FINISHSETUP, NO_GAME_FOUND, NO_TIER_FOUND, TIER_SET, CHEST_BASIC, CHEST_NORMAL, CHEST_EPIC, SET_TIER, ADD_TIER, NO_PLAYER_FOUND, MAP_UNREGISTERED, MAP_EXISTS, LOBBYLOC_NOT_SET, INSUFICIENT_COINS, CMD_NOT_FOUND, CAGES_SET, ADDED_COINS, REMOVED_COINS, SET_COINS, SENDER_COINS, RECIEVER_COINS, LANGUAGE_SET, GENERATING_WORLD, NO_ARENA_BOUNDARIES, SAVING_ARENA, ARENA_REGISTERED, VOTE, ALREADY_VOTED, GAME_STATUS_SET, ARENA_RESET, MAP_RESET_DONE, SHOP_BUY, SHOP_ALREADY_BOUGHT, SHOP_NO_PERM, PROFILE_SELECTED, NOT_BUYABLE, NO_KIT_FOUND, DEL_PURCHASES, CAGEBLOCK, KITS, CMD_CANT_FORCESTART, SCOREBOARD_LOBBY_TITLE, SCOREBOARD_CAGE_TITLE, SCOREBOARD_SPECTATOR_TITLE, SCOREBOARD_PLAYING_TITLE, ITEMS_MAP_NOTFOUND_TITLE, ITEMS_MAP_TITLE, MAP_ALL, MAP_WAITING, MAP_SPECTATE, MAP_STARTING, MAP_AVAILABLE, MAP_PLAYING, MAP_FINISHING, MAP_RESETTING, COMPASS_TELEPORT, BOWPARTICLE, WINBLOCK, TEAM_LEAVE, TEAM_JOIN, TEAMMATE_DAMAGE_CANCEL, WINNER_BROADCAST, TEAM_BROADCAST_JOIN, TEAM_BROADCAST_LEAVE, ALREADY_STARTED, STATS_ITEM_NAME, SOLO, TEAMS, GAME_FOUND, NO_TRACKER, TRACK_FOUND, ITEM_KIT_NAME, ITEM_PROFILE_NAME, ITEM_CAGESET_NAME, ITEM_VOTE_NAME, ITEM_LEAVE_NAME, ITEM_MAPS_NAME, ITEM_SHOP_NAME, ITEM_SPECTATE_NAME, ITEM_PLAYAGAIN_NAME, ITEM_CHEST1_NAME, ITEM_CHEST2_NAME, MENUS_SHOP_TILE, MENUS_SPECTATE_TITLE, MENU_VOTE_TITLE, MENU_LANG_TITLE, MENU_LANG_SELECT, MENU_PLAYERP_TITLE, MENU_PLAYERP_VIEWITEM, MENU_PLAYERP_RESET_ALERT, MENU_PLAYERP_RESET_TITLE, CANT_JOIN, SPECTATING_DISABLED, ROOM_FULL, BLOCKED_COMMAND, MAPS_NAME, PLAY_AGAIN, PARTY_KICK, PARTY_JOIN, PARTY_LEAVE, PARTY_NOTINPARTY, PARTY_ALREADYCREATED, PARTY_CREATED, PARTY_INSUFICIENT_ROOMSPACE, PARTY_DISBAND, PARTY_INVITE_SENT, PARTY_INVITE_RECIEVED, PARTY_ACCEPTEDINVITE, PARTY_CANTINVITEYOURSELF, PARTY_NOT_OWNER, PARTY_ALREADYIN, PARTY_INVITENOTFOUND, SOLO_RANKED, TEAMS_RANKED, ACHIEVEMENTS, ACHIEVEMENT_GET, DELETEKIT_DONE, MENU_CHESTS_TITLE, MENU_PROJECTILES_TITLE, MENU_TIME_TITLE, TIME_DAY, TIME_NIGHT, TIME_SUNSET, PROJECTILE_NORMAL, PROJECTILE_BREAK, MENU_PLAYERP_GAME_HISTORY, KIT_EXISTS, MENU_SPECTATOR_SHOP_TITLE, TIME_RAIN, CANT_VOTE
    }

    public enum TSsingle {
        BOSSBAR_ARENA_RUNTIME, BOSSBAR_ARENA_STARTING, BOSSBAR_ARENA_END, BOSSBAR_ARENA_WAIT, SEARCH_NOTFOUND_NAME, SHOP_BOUGHT, SHOP_BUY, ADMIN_SHUTDOWN, KIT_PRICE, KIT_BUY, KIT_SELECT, KIT_CONTAINS, KIT_ITEM, BUTTONS_NEXT_TITLE, BUTTONS_NEXT_DESC, BUTTONS_BACK_TITLE, BUTTONS_BACK_DESC, BUTTONS_FILTER_TITLE, BUTTONS_FILTER_DESC, BUTTONS_MENU_TITLE, BUTTONS_MENU_DESC, BOSSBAR_ARENA_DEATHMATCH
    }

    public enum TL {
        ARENA_START, ARENA_END, SCOREBOARD_LOBBY_LINES, SCOREBOARD_CAGE_LINES, SCOREBOARD_SPECTATOR_LINES, SCOREBOARD_PLAYING_LINES, ITEMS_MAP_DESCRIPTION, TITLE_ROOMJOIN, STATS_ITEM_LORE, REFILL_EVENT_TITLE, TNTRAIN_EVENT_TITLE, TAB_HEADER_MATCH, TAB_FOOTER_MATCH, TAB_HEADER_OTHER, TAB_FOOTER_OTHER, INITSETUP_ARENA
    }
}
