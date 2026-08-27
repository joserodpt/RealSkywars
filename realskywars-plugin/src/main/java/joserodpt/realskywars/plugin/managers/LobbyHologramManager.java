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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWHologramConfig;
import joserodpt.realskywars.api.leaderboards.RSWLeaderboard;
import joserodpt.realskywars.api.managers.LobbyHologramManagerAPI;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.holograms.HologramType;
import joserodpt.realskywars.api.managers.holograms.RSWHologram;
import joserodpt.realskywars.api.managers.holograms.RSWLobbyHologram;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the lobby holograms: the last winner display, the leaderboards and the
 * server info board. Locations are persisted in holograms.yml so they survive a
 * restart, and every hologram is refreshed on a shared timer.
 */
public class LobbyHologramManager extends LobbyHologramManagerAPI {

    private static final String DATA_ROUTE = "Holograms";

    private final RealSkywarsAPI rsa;
    private final Map<String, RSWLobbyHologram> holograms = new LinkedHashMap<>();
    private BukkitTask refreshTask;

    public LobbyHologramManager(RealSkywarsAPI rsa) {
        this.rsa = rsa;
    }

    /** Reads holograms.yml and spawns every hologram it lists. */
    @Override
    public void loadHolograms() {
        this.clear();

        if (RSWHologramConfig.file() == null || !enabled()) {
            return;
        }

        if (RSWHologramConfig.file().isSection(DATA_ROUTE)) {
            for (String id : RSWHologramConfig.file().getSection(DATA_ROUTE).getRoutesAsStrings(false)) {
                String route = DATA_ROUTE + "." + id;

                HologramType type = HologramType.getByName(RSWHologramConfig.file().getString(route + ".Type"));
                if (type == null) {
                    this.rsa.getLogger().warning("Unknown hologram type for " + id + "! Skipping it.");
                    continue;
                }

                Location loc = readLocation(route);
                if (loc == null) {
                    this.rsa.getLogger().warning("Could not read the location of hologram " + id + " (is its world loaded?). Skipping it.");
                    continue;
                }

                this.spawn(id, type, loc, false);
            }
        }

        this.startRefreshTask();
    }

    /** Creates a hologram and, unless loading, writes it to holograms.yml. */
    @Override
    public RSWLobbyHologram createHologram(HologramType type, Location loc) {
        if (RSWHologramConfig.file() == null) {
            this.rsa.getLogger().severe("Cannot create a hologram: holograms.yml was not loaded.");
            return null;
        }

        String id = type.name().toLowerCase() + "_" + (this.holograms.size() + 1);
        while (this.holograms.containsKey(id)) {
            id = id + "_";
        }
        return this.spawn(id, type, loc, true);
    }

    private RSWLobbyHologram spawn(String id, HologramType type, Location loc, boolean save) {
        RSWLobbyHologram holo = RSWLobbyHologram.of(this.rsa.getHologramManagerAPI().getSelectedType(), id, type);

        try {
            holo.spawn(loc);
        } catch (RuntimeException | LinkageError e) {
            //a hologram plugin refusing to place one must not abort startup
            this.rsa.getLogger().warning("Could not place hologram " + id + ": " + e.getMessage());
            return null;
        }

        this.holograms.put(id, holo);

        if (save) {
            String route = DATA_ROUTE + "." + id;
            RSWHologramConfig.file().set(route + ".Type", type.name());
            RSWHologramConfig.file().set(route + ".World", loc.getWorld().getName());
            RSWHologramConfig.file().set(route + ".X", loc.getX());
            RSWHologramConfig.file().set(route + ".Y", loc.getY());
            RSWHologramConfig.file().set(route + ".Z", loc.getZ());
            RSWHologramConfig.save();
        }

        try {
            this.refresh(holo);
        } catch (RuntimeException e) {
            this.rsa.getLogger().warning("Could not fill hologram " + id + ": " + e.getMessage());
        }
        return holo;
    }

    /** Removes a hologram from the world and from holograms.yml. */
    @Override
    public void removeHologram(String id) {
        //config guarded below: a hologram can exist in memory without a config
        RSWLobbyHologram holo = this.holograms.remove(id);
        if (holo != null) {
            holo.delete();
        }

        if (RSWHologramConfig.file() != null) {
            RSWHologramConfig.file().remove(DATA_ROUTE + "." + id);
            RSWHologramConfig.save();
        }
    }

    @Override
    public RSWLobbyHologram getHologram(String id) {
        return this.holograms.get(id);
    }

    @Override
    public Collection<RSWLobbyHologram> getHolograms() {
        return new ArrayList<>(this.holograms.values());
    }

    /** Deletes every hologram from the world, leaving holograms.yml untouched. */
    @Override
    public void clear() {
        this.holograms.values().forEach(RSWLobbyHologram::delete);
        this.holograms.clear();
    }

    @Override
    public void startRefreshTask() {
        this.stopRefreshTask();

        if (!enabled()) {
            return;
        }

        int interval = Math.max(20, RSWHologramConfig.file().getInt("Config.Refresh-Interval", 600));
        this.refreshTask = new BukkitRunnable() {
            @Override
            public void run() {
                refreshAll();
            }
        }.runTaskTimer(this.rsa.getPlugin(), interval, interval);
    }

    @Override
    public void stopRefreshTask() {
        if (this.refreshTask != null) {
            this.refreshTask.cancel();
            this.refreshTask = null;
        }
    }

    @Override
    public void refreshAll() {
        this.holograms.values().forEach(this::refresh);
    }

    /** Rebuilds one hologram's lines from its type and the config template. */
    @Override
    public void refresh(RSWLobbyHologram holo) {
        if (RSWHologramConfig.file() == null) {
            return;
        }

        List<String> template = RSWHologramConfig.file().getStringList("Config.Lines." + holo.getType().getConfigName());
        if (template.isEmpty()) {
            template = new ArrayList<>();
            template.add(holo.getType().getDisplayName());
        }

        List<String> lines = new ArrayList<>();
        for (String line : template) {
            lines.add(this.placeholders(line, holo.getType()));
        }

        holo.setLines(lines);
    }

    private String placeholders(String line, HologramType type) {
        String result = line
                .replace("%winner%", RSWHologramConfig.file().getString("Data.Last-Winner.Name", "-"))
                .replace("%map%", RSWHologramConfig.file().getString("Data.Last-Winner.Map", "-"))
                .replace("%date%", RSWHologramConfig.file().getString("Data.Last-Winner.Date", "-"))
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%playing%", String.valueOf(this.rsa.getPlayerManagerAPI().getPlayingPlayers(MapManagerAPI.MapGamemodes.ALL)))
                .replace("%maps%", String.valueOf(this.rsa.getMapManagerAPI().getMaps(MapManagerAPI.MapGamemodes.ALL).size()))
                .replace("%games%", String.valueOf(this.runningGames()));

        RSWLeaderboard.RSWLeaderboardCategories category = categoryOf(type);
        if (category != null) {
            RSWLeaderboard leaderboard = this.rsa.getLeaderboardManagerAPI().getLeaderboard(category);
            int size = Math.max(1, RSWHologramConfig.file().getInt("Config.Leaderboard-Size", 10));

            for (int place = 1; place <= size; place++) {
                String token = "%" + place + "%";
                if (result.contains(token)) {
                    result = result.replace(token, leaderboard == null ? "-" : leaderboard.getIndex(place));
                }
            }
        }

        return result;
    }

    private static RSWLeaderboard.RSWLeaderboardCategories categoryOf(HologramType type) {
        switch (type) {
            case TOP_WINS_SOLO:
                return RSWLeaderboard.RSWLeaderboardCategories.SOLO_WINS;
            case TOP_WINS_TEAMS:
                return RSWLeaderboard.RSWLeaderboardCategories.TEAMS_WINS;
            case TOP_KILLS:
                return RSWLeaderboard.RSWLeaderboardCategories.KILLS;
            default:
                return null;
        }
    }

    private long runningGames() {
        return this.rsa.getMapManagerAPI().getMaps(MapManagerAPI.MapGamemodes.ALL).stream()
                .filter(map -> map.getState() == RSWMap.MapState.PLAYING || map.getState() == RSWMap.MapState.FINISHING)
                .count();
    }

    /** Records the winner shown by the LAST_WINNER holograms. */
    @Override
    public void setLastWinner(String winner, String map) {
        if (RSWHologramConfig.file() == null) {
            return;
        }

        RSWHologramConfig.file().set("Data.Last-Winner.Name", winner);
        RSWHologramConfig.file().set("Data.Last-Winner.Map", map);
        RSWHologramConfig.file().set("Data.Last-Winner.Date", Text.getDateAndTime());
        RSWHologramConfig.save();

        this.holograms.values().stream()
                .filter(holo -> holo.getType() == HologramType.LAST_WINNER)
                .forEach(this::refresh);
    }

    private boolean enabled() {
        return RSWHologramConfig.file() != null && RSWHologramConfig.file().getBoolean("Config.Enabled", true);
    }

    private Location readLocation(String route) {
        World world = Bukkit.getWorld(RSWHologramConfig.file().getString(route + ".World", ""));
        if (world == null) {
            return null;
        }

        return new Location(world,
                RSWHologramConfig.file().getDouble(route + ".X"),
                RSWHologramConfig.file().getDouble(route + ".Y"),
                RSWHologramConfig.file().getDouble(route + ".Z"));
    }
}
