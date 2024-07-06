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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.cages.RSWSoloCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWMapsConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.PlaceholderMode;
import joserodpt.realskywars.api.map.modes.RSWSign;
import joserodpt.realskywars.api.map.modes.SoloMode;
import joserodpt.realskywars.api.map.modes.teams.RSWTeam;
import joserodpt.realskywars.api.map.modes.teams.TeamsMode;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.player.RSWPlayerItems;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.api.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MapManager extends MapManagerAPI {
    private final RealSkywarsAPI rs;

    private final Map<String, RSWMap> maps = new HashMap<>();

    public MapManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Override
    public void loadMaps() {
        this.clearMaps();

        for (String s : RSWMapsConfig.file().getRoot().getRoutesAsStrings(false)) {
            String modeSTR = RSWMapsConfig.file().getString(s + ".Settings.GameType");
            if (modeSTR == null || modeSTR.isEmpty()) {
                rs.getLogger().severe("Mode: " + s + " is invalid! Skipping map: " + s);
                continue;
            }

            try {
                RSWMap.Mode.valueOf(modeSTR);
            } catch (IllegalArgumentException e) {
                rs.getLogger().severe("Mode: " + s + " isn't supported by this version of RealSkywars! Skipping map: " + s);
                continue;
            }

            String worldName = RSWMapsConfig.file().getString(s + ".world");
            String displayName = RSWMapsConfig.file().getString(s + ".Settings.DisplayName");
            if (displayName == null || displayName.isEmpty()) {
                RSWMapsConfig.file().set(s + ".Settings.DisplayName", s);
                RSWMapsConfig.save();
                displayName = s;
            }
            displayName = Text.color(displayName);

            boolean loaded = rs.getWorldManagerAPI().loadWorld(worldName, World.Environment.NORMAL);
            if (loaded) {
                RSWWorld.WorldType wt = getWorldType(RSWMapsConfig.file().getString(s + ".type"));
                Boolean unregistered = RSWMapsConfig.file().getBoolean(s + ".Settings.Unregistered");

                Location specLoc = getSpecLoc(s);
                Map<Location, RSWCage> cgs = getMapCages(s, specLoc.getWorld());

                if (cgs.isEmpty()) {
                    Bukkit.getLogger().severe("[RealSkywars] There are no cages in " + worldName + " (possibly a bug? Check config pls!)");
                    continue;
                }

                Map<Location, RSWChest> chests = getMapChests(worldName, s);
                if (chests.isEmpty()) {
                    Bukkit.getLogger().warning("[RealSkywars] There are no chests in " + worldName + " (possibly a bug? Check config pls!)");
                }

                World w = Bukkit.getWorld(worldName);

                switch (RSWMap.Mode.valueOf(modeSTR)) {
                    case SOLO:
                        SoloMode gs = new SoloMode(s, displayName, w, RSWMapsConfig.file().getString(s + ".schematic"), wt, RSWMap.MapState.AVAILABLE, cgs, RSWMapsConfig.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), RSWMapsConfig.file().getBoolean(s + ".Settings.Border"), getPOS1(w, s), getPOS2(w, s), chests, isRanked(s), unregistered);
                        gs.resetArena(RSWMap.OperationReason.LOAD);
                        this.addMap(gs);
                        break;
                    case TEAMS:
                        int numberOfPlayers = RSWMapsConfig.file().getInt(s + ".number-of-players");
                        AtomicInteger tc = new AtomicInteger(1);

                        Map<Location, RSWTeam> ts = new HashMap<>();
                        int teamSize = numberOfPlayers / cgs.size();
                        cgs.forEach((location, value) -> ts.put(location, new RSWTeam(tc.getAndIncrement(), teamSize, location)));
                        
                        TeamsMode teas = new TeamsMode(s, displayName, w, RSWMapsConfig.file().getString(s + ".schematic"), wt, RSWMap.MapState.AVAILABLE, ts, RSWMapsConfig.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), RSWMapsConfig.file().getBoolean(s + ".Settings.Border"), getPOS1(w, s), getPOS2(w, s), chests, isRanked(s), unregistered);
                        teas.resetArena(RSWMap.OperationReason.LOAD);
                        this.addMap(teas);
                        break;
                    default:
                        throw new IllegalStateException("Mode doesnt exist: " + modeSTR);
                }
            }
        }
    }

    @Override
    public void deleteMap(RSWMap map) {
        map.kickPlayers(null);

        map.getSigns().forEach(RSWSign::delete);

        map.getRSWWorld().getWorld().getPlayers().forEach(player -> rs.getLobbyManagerAPI().tpToLobby(player));

        this.maps.remove(map.getName().toLowerCase());
        RSWMapsConfig.file().remove(map.getName());
        RSWMapsConfig.save();
    }

    @Override
    public RSWMap getMap(World w) {
        return this.maps.values().stream().filter(r -> r.getRSWWorld().getWorld().equals(w)).findFirst().orElse(null);
    }

    @Override
    public RSWMap getMap(String s) {
        s = s.toLowerCase();
        return this.maps.get(s);
    }

    @Override
    public List<RSWMap> getMapsForPlayer(RSWPlayer rswPlayer) {
        List<RSWMap> f = new ArrayList<>();
        switch (rswPlayer.getPlayerMapViewerPref()) {
            case MAPV_ALL:
                f.addAll(rswPlayer.getPlayer().hasPermission("rsw.admin") || rswPlayer.getPlayer().isOp() ? this.maps.values() : this.maps.values().stream().filter(Predicate.not(RSWMap::isUnregistered)).collect(Collectors.toList()));
                break;
            case MAPV_WAITING:
                f.addAll(this.maps.values().stream().filter(r -> r.getState().equals(RSWMap.MapState.WAITING) && !r.isUnregistered()).collect(Collectors.toList()));
                break;
            case MAPV_STARTING:
                f.addAll(this.maps.values().stream().filter(r -> r.getState().equals(RSWMap.MapState.STARTING) && !r.isUnregistered()).collect(Collectors.toList()));
                break;
            case MAPV_AVAILABLE:
                f.addAll(this.maps.values().stream().filter(r -> r.getState().equals(RSWMap.MapState.AVAILABLE) && !r.isUnregistered()).collect(Collectors.toList()));
                break;
            case MAPV_SPECTATE:
                f.addAll(this.maps.values().stream().filter(r -> (r.getState().equals(RSWMap.MapState.PLAYING) || r.getState().equals(RSWMap.MapState.FINISHING) && !r.isUnregistered())).collect(Collectors.toList()));
                break;
            case SOLO:
                f.addAll(this.getMaps(MapGamemodes.SOLO));
                break;
            case TEAMS:
                f.addAll(this.getMaps(MapGamemodes.TEAMS));
                break;
            case SOLO_RANKED:
                f.addAll(this.getMaps(MapGamemodes.SOLO_RANKED));
                break;
            case TEAMS_RANKED:
                f.addAll(this.getMaps(MapGamemodes.TEAMS_RANKED));
                break;
            default:
                break;
        }
        return f.isEmpty() ? Collections.singletonList(new PlaceholderMode("No Maps Found")) : f;
    }

    @Override
    public Collection<RSWMap> getMaps(MapGamemodes pt) {
        switch (pt) {
            case ALL:
                return this.maps.values();
            case SOLO:
                return this.maps.values().stream().filter(r -> r.getGameMode().equals(RSWMap.Mode.SOLO) && !r.isUnregistered()).collect(Collectors.toList());
            case TEAMS:
                return this.maps.values().stream().filter(r -> r.getGameMode().equals(RSWMap.Mode.TEAMS) && !r.isUnregistered()).collect(Collectors.toList());
            case RANKED:
                return this.maps.values().stream().filter(rswGame -> rswGame.isRanked() && !rswGame.isUnregistered()).collect(Collectors.toList());
            case SOLO_RANKED:
                return this.maps.values().stream().filter(r -> r.isRanked() && !r.isUnregistered() && r.getGameMode().equals(RSWMap.Mode.SOLO)).collect(Collectors.toList());
            case TEAMS_RANKED:
                return this.maps.values().stream().filter(r -> r.isRanked() && !r.isUnregistered() && r.getGameMode().equals(RSWMap.Mode.TEAMS)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Map<Location, RSWCage> getMapCages(String mapName, World w) {
        Map<Location, RSWCage> locs = new HashMap<>();
        int id = 0;
        for (String i : RSWMapsConfig.file().getSection(mapName + ".Locations.Cages").getRoutesAsStrings(false)) {
            int x = RSWMapsConfig.file().getInt(mapName + ".Locations.Cages." + i + ".X");
            int y = RSWMapsConfig.file().getInt(mapName + ".Locations.Cages." + i + ".Y");
            int z = RSWMapsConfig.file().getInt(mapName + ".Locations.Cages." + i + ".Z");
            locs.put(new Location(w, x, y, z), new RSWSoloCage(id, x, y, z));
            ++id;
        }
        return locs;
    }

    @Override
    protected Map<Location, RSWChest> getMapChests(String worldName, String section) {
        Map<Location, RSWChest> chests = new HashMap<>();
        if (RSWMapsConfig.file().isSection(section + ".Chests")) {
            for (String i : RSWMapsConfig.file().getSection(section + ".Chests").getRoutesAsStrings(false)) {
                int x = RSWMapsConfig.file().getInt(section + ".Chests." + i + ".LocationX");
                int y = RSWMapsConfig.file().getInt(section + ".Chests." + i + ".LocationY");
                int z = RSWMapsConfig.file().getInt(section + ".Chests." + i + ".LocationZ");
                BlockFace f = BlockFace.valueOf(RSWMapsConfig.file().getString(section + ".Chests." + i + ".Face"));

                RSWChest.Type ct;
                try {
                    ct = RSWChest.Type.valueOf(RSWMapsConfig.file().getString(section + ".Chests." + i + ".Type"));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Chest type invalid while loading " + worldName + "!! >> Chest id: " + i + ". Assigning NORMAL type.");
                    ct = RSWChest.Type.NORMAL;
                }

                chests.put(new Location(Bukkit.getWorld(worldName), x, y, z), new RSWChest(ct, worldName, x, y, z, f));
            }
        } else {
            Debugger.print(MapManager.class, "There are no chests in " + worldName + " (possibly a bug? Check config pls!)");
        }
        return chests;
    }

    @Override
    public void setupSolo(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int maxP) {
        TranslatableLine.GENERATING_WORLD.send(p, true);

        String cleanMapName = mapname.replace(".schematic", "").replace(".schem", "");

        World w = rs.getWorldManagerAPI().createEmptyWorld(cleanMapName, World.Environment.NORMAL);
        if (w != null) {
            RSWMap s = new SoloMode(cleanMapName, displayName, w, mapname, wt, maxP);

            w.getBlockAt(0, 64, 0).setType(Material.BEDROCK);
            Location loc = new Location(w, 0, 66, 0);

            Text.sendList(p.getPlayer(), Text.replaceVarInList(TranslatableList.EDIT_MAP.get(p), "%cages%", maxP + ""));

            RSWPlayerItems.SETUP.giveSet(p);
            p.getPlayer().setGameMode(GameMode.CREATIVE);

            if (wt == RSWWorld.WorldType.SCHEMATIC) {
                w.setAutoSave(false);

                p.teleport(loc);

                Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> WorldEditUtils.pasteSchematic(mapname, new Location(p.getWorld(), 0, 64, 0), s), 3 * 20);
            } else {
                w.setAutoSave(true);
                p.teleport(loc);
            }

            this.addMap(s);
        } else {
            rs.getLogger().warning("Could not create setup world for " + mapname);
        }
    }

    @Override
    public void setupTeams(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int teams, int pperteam) {
        TranslatableLine.GENERATING_WORLD.send(p, true);

        String cleanMapName = mapname.replace(".schematic", "").replace(".schem", "");

        World w = rs.getWorldManagerAPI().createEmptyWorld(cleanMapName, World.Environment.NORMAL);
        if (w != null) {
            RSWMap s = new TeamsMode(cleanMapName, cleanMapName, w, cleanMapName, wt, teams, pperteam);

            w.getBlockAt(0, 64, 0).setType(Material.BEDROCK);
            Location loc = new Location(w, 0, 66, 0);

            Text.sendList(p.getPlayer(), Text.replaceVarInList(TranslatableList.EDIT_MAP.get(p), "%cages%", teams + ""));

            RSWPlayerItems.SETUP.giveSet(p);
            p.getPlayer().setGameMode(GameMode.CREATIVE);

            if (wt == RSWWorld.WorldType.SCHEMATIC) {
                w.setAutoSave(false);

                p.teleport(loc);

                Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> WorldEditUtils.pasteSchematic(mapname, new Location(p.getWorld(), 0, 64, 0), s), 3 * 20);
            } else {
                w.setAutoSave(true);
                p.teleport(loc);
            }

            this.addMap(s);
        } else {
            rs.getLogger().warning("Could not create setup world for " + mapname);
        }
    }

    @Override
    public void finishMap(RSWPlayer p) {
        RSWMap map = this.getMap(p.getWorld().getName());
        if (map == null) {
            TranslatableLine.CMD_NO_MAP_FOUND.send(p, true);
            return;
        }

        if (!map.isUnregistered()) {
            TranslatableLine.MAP_UNREGISTER_TO_EDIT.send(p, true);
            return;
        }

        if (map.getCages().isEmpty()) {
            TranslatableLine.CMD_NO_CAGES_SET.send(p, true);
            return;
        }

        if (map.getGameMode() == RSWMap.Mode.SOLO && map.getCages().size() != map.getMaxPlayers()) {
            TranslatableLine.CMD_INCORRECT_NUMBER_OF_CAGES_SOLO.send(p, true);
            return;
        }

        if (map.getGameMode() == RSWMap.Mode.TEAMS && map.getCages().size() != map.getTeams().size()) {
            TranslatableLine.CMD_INCORRECT_NUMBER_OF_CAGES_TEAMS.send(p, true);
            return;
        }

        if (map.getSpectatorLocation() == null) {
            TranslatableLine.CMD_SPEC_LOCATION_NOT_SET.send(p, true);
            return;
        }

        if (map.getMapCuboid() == null || map.getPOS1() == null || map.getPOS2() == null) {
            WorldEditPlugin w = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            try {
                assert w != null;
                Region r = w.getSession(p.getPlayer()).getSelection(w.getSession(p.getPlayer()).getSelectionWorld());

                if (r != null) {
                    map.setBoundaries(new Location(map.getRSWWorld().getWorld(), r.getMinimumPoint().getBlockX(), r.getMinimumPoint().getBlockY(), r.getMinimumPoint().getBlockZ()),
                            new Location(map.getRSWWorld().getWorld(), r.getMaximumPoint().getBlockX(), r.getMaximumPoint().getBlockY(), r.getMaximumPoint().getBlockZ()));
                }
            } catch (Exception e) {
                TranslatableLine.NO_ARENA_BOUNDARIES.send(p, true);
                rs.getLogger().warning("Error while setting arena boundaries for " + map.getName() + " (possibly a bug?)");
                rs.getLogger().warning(e.getMessage());
                return;
            }
        }

        TranslatableLine.SAVING_MAP.send(p, true);

        // Beacon Remove
        map.getCages().forEach(cage -> map.getRSWWorld().getWorld().getBlockAt(cage.getLocation()).setType(Material.AIR));

        //Remove dropped items
        rs.getWorldManagerAPI().clearDroppedItems(map.getRSWWorld().getWorld());

        //worldType
        if (map.getRSWWorld().getType() == RSWWorld.WorldType.DEFAULT) {
            map.getRSWWorld().save();

            //Copy world
            rs.getWorldManagerAPI().copyWorld(map.getRSWWorld().getName(), WorldManager.CopyTo.RSW_FOLDER);
        }

        map.getCages().forEach(rswCage -> rswCage.setMap(map));

        rs.getLobbyManagerAPI().tpToLobby(p);

        // Save Data
        map.save(RSWMap.Data.ALL, true);

        map.setUnregistered(false);
        map.resetArena(RSWMap.OperationReason.RESET);
        TranslatableLine.MAP_REGISTERED.send(p, true);
    }

    @Override
    protected Boolean isInstantEndingEnabled(String s) {
        return RSWMapsConfig.file().getBoolean(s + ".Settings.Instant-End");
    }

    @Override
    protected Location getPOS1(World w, String s) {
        double hx = RSWMapsConfig.file().getDouble(s + ".World.Border.POS1-X");
        double hy = RSWMapsConfig.file().getDouble(s + ".World.Border.POS1-Y");
        double hz = RSWMapsConfig.file().getDouble(s + ".World.Border.POS1-Z");

        return new Location(w, hx, hy, hz);
    }

    @Override
    protected Location getPOS2(World w, String s) {
        double hx = RSWMapsConfig.file().getDouble(s + ".World.Border.POS2-X");
        double hy = RSWMapsConfig.file().getDouble(s + ".World.Border.POS2-Y");
        double hz = RSWMapsConfig.file().getDouble(s + ".World.Border.POS2-Z");

        return new Location(w, hx, hy, hz);
    }

    @Override
    public Boolean isSpecEnabled(String s) {
        return RSWMapsConfig.file().getBoolean(s + ".Settings.Spectator");
    }

    @Override
    public Location getSpecLoc(String nome) {
        double x = RSWMapsConfig.file().getDouble(nome + ".Locations.Spectator.X");
        double y = RSWMapsConfig.file().getDouble(nome + ".Locations.Spectator.Y");
        double z = RSWMapsConfig.file().getDouble(nome + ".Locations.Spectator.Z");
        float pitch = RSWMapsConfig.file().getFloat(nome + ".Locations.Spectator.Pitch");
        float yaw = RSWMapsConfig.file().getFloat(nome + ".Locations.Spectator.Yaw");
        return new Location(Bukkit.getWorld(nome), x, y, z, pitch, yaw);
    }

    @Override
    protected RSWWorld.WorldType getWorldType(String s) {
        return RSWWorld.WorldType.valueOf(s);
    }

    @Override
    protected Boolean isRanked(String s) {
        return RSWMapsConfig.file().getBoolean(s + ".ranked");
    }

    @Override
    public void endMaps() {
        this.endMaps = true;

        this.maps.values().parallelStream().forEach(g -> {
            g.kickPlayers(TranslatableLine.ADMIN_SHUTDOWN.getSingle());
            g.resetArena(RSWMap.OperationReason.SHUTDOWN);
            g.clear();
        });
    }

    @Override
    public void findMap(RSWPlayer player, RSWMap.Mode type) {
        UUID playerUUID = player.getUUID();
        if (!rs.getPlayerManagerAPI().getTeleporting().contains(playerUUID)) {
            rs.getPlayerManagerAPI().getTeleporting().add(playerUUID);

            Optional<RSWMap> suitableGame = findSuitableGame(type);
            if (suitableGame.isPresent()) {
                if (suitableGame.get().isFull()) {
                    TranslatableLine.ROOM_FULL.send(player, true);
                    rs.getPlayerManagerAPI().getTeleporting().remove(playerUUID);
                    return;
                }

                TranslatableLine.CMD_MAP_FOUND.send(player, true);
                if (player.isInMatch()) {
                    player.getMatch().removePlayer(player);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywarsAPI.getInstance().getPlugin(), () -> {
                    suitableGame.get().addPlayer(player);
                    rs.getPlayerManagerAPI().getTeleporting().remove(player.getUUID());
                }, 5);
            } else {
                TranslatableLine.CMD_NO_MAP_FOUND.send(player, true);
                rs.getPlayerManagerAPI().getTeleporting().remove(player.getUUID());

                if (rs.getLobbyManagerAPI().getLobbyLocation() != null && rs.getLobbyManagerAPI().getLobbyLocation().getWorld() != null && Objects.equals(rs.getLobbyManagerAPI().getLobbyLocation().getWorld(), player.getWorld())) {
                    rs.getLobbyManagerAPI().tpToLobby(player);
                }
            }
        }
    }

    @Override
    public Optional<RSWMap> findSuitableGame(RSWMap.Mode type) {
        return type == null ? this.maps.values().stream().findFirst() : this.maps.values().stream()
                .filter(game -> game.getGameMode().equals(type) &&
                        (game.getState().equals(RSWMap.MapState.AVAILABLE) ||
                                game.getState().equals(RSWMap.MapState.STARTING) ||
                                game.getState().equals(RSWMap.MapState.WAITING)))
                .findFirst();
    }

    @Override
    public void clearMaps() {
        this.maps.clear();
    }

    @Override
    public void addMap(RSWMap s) {
        this.maps.put(s.getName().toLowerCase(), s);
    }

    @Override
    public Collection<String> getMapNames() {
        return this.maps.keySet();
    }

    @Override
    public void editMap(RSWPlayer p, RSWMap map) {
        if (!map.isUnregistered()) {
            TranslatableLine.MAP_UNREGISTER_TO_EDIT.send(p, true);
            return;
        }

        if (p.getLocation().getWorld() == map.getRSWWorld().getWorld()) {
            return;
        }

        p.setGameMode(GameMode.CREATIVE);
        p.teleport(map.getSpectatorLocation());
        Text.sendList(p.getPlayer(), Text.replaceVarInList(TranslatableList.EDIT_MAP.get(p), "%cages%", map.getGameMode() == RSWMap.Mode.SOLO ? String.valueOf(map.getMaxPlayers()) : map.getTeams().size() + ""));
        RSWPlayerItems.SETUP.giveSet(p);

        map.getCages().forEach(rswCage -> map.getRSWWorld().getWorld().getBlockAt(rswCage.getLocation()).setType(Material.BEACON));
    }
}
