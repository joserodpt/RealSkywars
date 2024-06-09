package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public abstract class MapManagerAPI {
    public abstract void loadMaps();

    public abstract void deleteMap(RSWMap map);

    public abstract RSWMap getMap(String s);

    public abstract List<RSWCage> getCages(String s, Location specLoc);

    public abstract void setupSolo(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int maxP);

    public abstract void setupTeams(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int teams, int pperteam);

    public abstract void cancelSetup(RSWPlayer p);

    public abstract void continueSetup(RSWPlayer p);

    public abstract void finishSetup(RSWPlayer p);

    protected abstract RSWMap.Mode getGameType(String s);

    protected abstract Boolean isInstantEndingEnabled(String s);

    protected abstract Boolean isBorderEnabled(String s);

    protected abstract Location getPOS1(World w, String s);

    protected abstract Location getPOS2(World w, String s);

    public abstract Boolean isSpecEnabled(String s);

    public abstract Location getSpecLoc(String nome);

    protected abstract RSWWorld.WorldType getWorldType(String s);

    protected abstract Boolean isRanked(String s);

    protected abstract List<RSWChest> getChests(String worldName, String section);
}
