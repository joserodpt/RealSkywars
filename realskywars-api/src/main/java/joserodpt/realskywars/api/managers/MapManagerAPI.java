package joserodpt.realskywars.api.managers;

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

import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class MapManagerAPI {
    public Boolean shutdown = false;

    public abstract void loadMaps();

    public abstract void deleteMap(RSWMap map);

    public abstract RSWMap getMap(World w);

    public abstract RSWMap getMap(String s);

    public abstract void endMaps(boolean shutdown);

    public abstract List<RSWMap> getMapsForPlayer(RSWPlayer rswPlayer);

    public abstract Collection<RSWMap> getMaps(MapGamemodes pt);

    protected abstract Map<Location, RSWCage> getMapCages(String s, World w);

    protected abstract Map<Location, RSWChest> getMapChests(String worldName, String section);

    public abstract void setupSolo(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int maxP);

    public abstract void setupTeams(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int teams, int pperteam);

    public abstract void finishMap(RSWPlayer p);

    protected abstract RSWWorld.WorldType getWorldType(String s);

    protected abstract Boolean isInstantEndingEnabled(String s);

    protected abstract Location getPOS1(World w, String s);

    protected abstract Location getPOS2(World w, String s);

    public abstract Boolean isSpecEnabled(String s);

    public abstract Location getSpecLoc(String nome);

    protected abstract Boolean isRanked(String s);

    public abstract void findNextMap(RSWPlayer player, RSWMap.GameMode type);

    public abstract Optional<RSWMap> findSuitableGame(RSWMap.GameMode type);

    public abstract void clearMaps();

    public abstract void addMap(RSWMap s);

    public abstract Collection<String> getMapNames();

    public abstract void editMap(RSWPlayer p, RSWMap sw);

    public abstract void duplicateMap(RSWMap original, String newName);

    public enum MapGamemodes {SOLO, SOLO_RANKED, TEAMS, TEAMS_RANKED, RANKED, ALL}
}
