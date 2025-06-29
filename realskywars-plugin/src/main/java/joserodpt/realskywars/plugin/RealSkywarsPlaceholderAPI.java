package joserodpt.realskywars.plugin;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.leaderboards.RSWLeaderboard;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class RealSkywarsPlaceholderAPI extends PlaceholderExpansion {

    private final RealSkywarsAPI rsa;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param rsa The instance of our plugin.
     */
    public RealSkywarsPlaceholderAPI(RealSkywarsAPI rsa) {
        this.rsa = rsa;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @NotNull
    @Override
    public String getAuthor() {
        return rsa.getPlugin().getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @NotNull
    @Override
    public String getIdentifier() {
        return "realskywars";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @NotNull
    @Override
    public String getVersion() {
        return rsa.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        switch (identifier.toLowerCase()) {
            case "playing":
                return rsa.getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.ALL) + "";
            case "playing_solo":
                return rsa.getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.SOLO) + "";
            case "playing_teams":
                return rsa.getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.TEAMS) + "";
            case "playing_ranked":
                return rsa.getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.RANKED) + "";
            case "kills":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getKills() + "";
            case "deaths":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getDeaths() + "";
            case "wins_solo":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getStats_wins_solo() + "";
            case "wins_teams":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getStats_wins_teams() + "";
            case "wins_ranked_solo":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getStats_wins_ranked_solo() + "";
            case "wins_ranked_teams":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getStats_wins_ranked_teams() + "";
            case "losses":
                return rsa.getDatabaseManagerAPI().getPlayerData(player).getLosses() + "";

            //SOLO
            case "solo_wins_1":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(1);
            case "solo_wins_2":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(2);
            case "solo_wins_3":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(3);
            case "solo_wins_4":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(4);
            case "solo_wins_5":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(5);
            case "solo_wins_6":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(6);
            case "solo_wins_7":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(7);
            case "solo_wins_8":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(8);
            case "solo_wins_9":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(9);
            case "solo_wins_10":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS).getIndex(10);
            //TEAMS
            case "teams_wins_1":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(1);
            case "teams_wins_2":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(2);
            case "teams_wins_3":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(3);
            case "teams_wins_4":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(4);
            case "teams_wins_5":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(5);
            case "teams_wins_6":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(6);
            case "teams_wins_7":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(7);
            case "teams_wins_8":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(8);
            case "teams_wins_9":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(9);
            case "teams_wins_10":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS).getIndex(10);
            //SOLO RANKED
            case "solo_ranked_wins_1":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(1);
            case "solo_ranked_wins_2":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(2);
            case "solo_ranked_wins_3":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(3);
            case "solo_ranked_wins_4":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(4);
            case "solo_ranked_wins_5":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(5);
            case "solo_ranked_wins_6":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(6);
            case "solo_ranked_wins_7":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(7);
            case "solo_ranked_wins_8":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(8);
            case "solo_ranked_wins_9":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(9);
            case "solo_ranked_wins_10":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.SOLO_RANKED_WINS).getIndex(10);
            //TEAMS RANKED
            case "teams_ranked_wins_1":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(1);
            case "teams_ranked_wins_2":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(2);
            case "teams_ranked_wins_3":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(3);
            case "teams_ranked_wins_4":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(4);
            case "teams_ranked_wins_5":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(5);
            case "teams_ranked_wins_6":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(6);
            case "teams_ranked_wins_7":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(7);
            case "teams_ranked_wins_8":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(8);
            case "teams_ranked_wins_9":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(9);
            case "teams_ranked_wins_10":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.TEAMS_RANKED_WINS).getIndex(10);
            //KILLS
            case "kills_1":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(1);
            case "kills_2":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(2);
            case "kills_3":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(3);
            case "kills_4":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(4);
            case "kills_5":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(5);
            case "kills_6":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(6);
            case "kills_7":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(7);
            case "kills_8":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(8);
            case "kills_9":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(9);
            case "kills_10":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS).getIndex(10);
            //KILLS RANKED
            case "kills_ranked_1":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(1);
            case "kills_ranked_2":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(2);
            case "kills_ranked_3":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(3);
            case "kills_ranked_4":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(4);
            case "kills_ranked_5":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(5);
            case "kills_ranked_6":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(6);
            case "kills_ranked_7":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(7);
            case "kills_ranked_8":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(8);
            case "kills_ranked_9":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(9);
            case "kills_ranked_10":
                return rsa.getLeaderboardManagerAPI().getLeaderboard(RSWLeaderboard.RSWLeaderboardCategories.KILLS_RANKED).getIndex(10);
        }

        return null; // Placeholder is unknown by the Expansion
    }
}