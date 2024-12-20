package joserodpt.realskywars.api;

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

import com.google.common.base.Preconditions;
import joserodpt.realskywars.api.currency.CurrencyAdapterAPI;
import joserodpt.realskywars.api.managers.AchievementsManagerAPI;
import joserodpt.realskywars.api.managers.DatabaseManagerAPI;
import joserodpt.realskywars.api.managers.HologramManagerAPI;
import joserodpt.realskywars.api.managers.KitManagerAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.LeaderboardManagerAPI;
import joserodpt.realskywars.api.managers.LobbyManagerAPI;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.PartiesManagerAPI;
import joserodpt.realskywars.api.managers.PlayerManagerAPI;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.managers.WorldManagerAPI;
import joserodpt.realskywars.api.nms.RSWnms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Logger;

public abstract class RealSkywarsAPI {

    private static RealSkywarsAPI instance;

    /**
     * Gets instance of this API
     *
     * @return RealSkywarsAPI API instance
     */
    public static RealSkywarsAPI getInstance() {
        return instance;
    }

    /**
     * Sets the RealMinesAPI instance.
     * <b>Note! This method may only be called once</b>
     *
     * @param instance the new instance to set
     */
    public static void setInstance(RealSkywarsAPI instance) {
        Preconditions.checkNotNull(instance, "instance");
        Preconditions.checkArgument(RealSkywarsAPI.instance == null, "Instance already set");
        RealSkywarsAPI.instance = instance;
    }

    public abstract Logger getLogger();

    public abstract String getVersion();

    public abstract RSWnms getNMS();

    public abstract WorldManagerAPI getWorldManagerAPI();

    public abstract RSWEventsAPI getEventsAPI();

    public abstract LanguageManagerAPI getLanguageManagerAPI();

    public abstract PlayerManagerAPI getPlayerManagerAPI();

    public abstract MapManagerAPI getMapManagerAPI();

    public abstract LobbyManagerAPI getLobbyManagerAPI();

    public abstract ShopManagerAPI getShopManagerAPI();

    public abstract KitManagerAPI getKitManagerAPI();

    public abstract PartiesManagerAPI getPartiesManagerAPI();

    public abstract Random getRandom();

    public abstract DatabaseManagerAPI getDatabaseManagerAPI();

    public abstract LeaderboardManagerAPI getLeaderboardManagerAPI();

    public abstract AchievementsManagerAPI getAchievementsManagerAPI();

    public abstract HologramManagerAPI getHologramManagerAPI();

    public abstract CurrencyAdapterAPI getCurrencyAdapterAPI();

    public abstract JavaPlugin getPlugin();

    public abstract String getServerVersion();

    public abstract String getSimpleServerVersion();

    public abstract boolean hasNewUpdate();

    public abstract void reload();

    public abstract Economy getVaultEconomy();
}
