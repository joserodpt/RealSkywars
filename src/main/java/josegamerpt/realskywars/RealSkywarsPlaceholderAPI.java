package josegamerpt.realskywars;

import josegamerpt.realskywars.player.PlayerManager;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class RealSkywarsPlaceholderAPI extends PlaceholderExpansion {

    private RealSkywars plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public RealSkywarsPlaceholderAPI(RealSkywars plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
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
    @Override
    public String getIdentifier(){
        return "realskywars";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        if(identifier.equalsIgnoreCase("playing")) {
            return RealSkywars.getPlayerManager().getPlayingPlayers(PlayerManager.Modes.ALL) + "";
        }

        if(identifier.equalsIgnoreCase("playing_solo")) {
            return RealSkywars.getPlayerManager().getPlayingPlayers(PlayerManager.Modes.SOLO) + "";
        }

        if(identifier.equalsIgnoreCase("playing_teams")) {
            return RealSkywars.getPlayerManager().getPlayingPlayers(PlayerManager.Modes.TEAMS) + "";
        }

        if(identifier.equalsIgnoreCase("playing_ranked")) {
            return RealSkywars.getPlayerManager().getPlayingPlayers(PlayerManager.Modes.RANKED) + "";
        }

        return null; // Placeholder is unknown by the Expansion
    }
}