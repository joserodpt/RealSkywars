package joserodpt.realskywars.api.managers;

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
    public Boolean endMaps = false;

    public abstract void loadMaps();

    public abstract void deleteMap(RSWMap map);

    public abstract RSWMap getMap(World w);

    public abstract RSWMap getMap(String s);

    public abstract void endMaps();

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

    public abstract void findMap(RSWPlayer player, RSWMap.Mode type);

    public abstract Optional<RSWMap> findSuitableGame(RSWMap.Mode type);

    public abstract void clearMaps();

    public abstract void addMap(RSWMap s);

    public abstract Collection<String> getMapNames();

    public abstract void editMap(RSWPlayer p, RSWMap sw);

    public enum MapGamemodes {SOLO, SOLO_RANKED, TEAMS, TEAMS_RANKED, RANKED, ALL}
}
