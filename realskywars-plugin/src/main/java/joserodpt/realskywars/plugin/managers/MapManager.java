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
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.cages.RSWSoloCage;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.config.RSWMapsConfig;
import joserodpt.realskywars.api.game.SetupRoom;
import joserodpt.realskywars.api.game.modes.RSWGame;
import joserodpt.realskywars.api.game.modes.Solo;
import joserodpt.realskywars.api.game.modes.teams.Team;
import joserodpt.realskywars.api.game.modes.teams.Teams;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.MapManagerAPI;
import joserodpt.realskywars.api.managers.world.RSWWorld;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import joserodpt.realskywars.api.utils.WorldEditUtils;
import joserodpt.realskywars.plugin.gui.guis.SetupRoomSettingsGUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class MapManager extends MapManagerAPI {
    private RealSkywarsAPI rs;
    public MapManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Override
    public void loadMaps() {
        rs.getGameManagerAPI().clearRooms();

        for (String s : this.getRegisteredMaps()) {
            RSWGame.Mode t = getGameType(s);

            if (t == null) {
                rs.getLogger().severe("Mode " + getGameType(s) + " doesnt exist! Skipping map: " + s);
                continue;
            }

            String worldName = RSWMapsConfig.file().getString(s + ".world");
            String displayName = RSWMapsConfig.file().getString(s + ".Settings.DisplayName");
            if (displayName == null || displayName.isEmpty()) {
                RSWMapsConfig.file().set(s + ".Settings.DisplayName", s);
                RSWMapsConfig.save();
                displayName = s;
            }

            RSWWorld.WorldType wt = getWorldType(RSWMapsConfig.file().getString(s + ".type"));

            boolean loaded = rs.getWorldManagerAPI().loadWorld(worldName, World.Environment.NORMAL);
            if (loaded) {
                World w = Bukkit.getWorld(worldName);

                Location specLoc = getSpecLoc(s);
                switch (t) {
                    case SOLO:
                        Solo gs = new Solo(s, displayName, w, RSWMapsConfig.file().getString(s + ".schematic"), wt, RSWGame.GameState.AVAILABLE, getCages(s, specLoc), RSWMapsConfig.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), isBorderEnabled(s), getPOS1(w, s), getPOS2(w, s), getChests(worldName, s), isRanked(s), rs);
                        gs.resetArena(RSWGame.OperationReason.LOAD);
                        rs.getGameManagerAPI().addRoom(gs);
                        break;
                    case TEAMS:
                        List<RSWCage> cgs = getCages(s, specLoc);
                        List<Team> ts = new ArrayList<>();
                        int tc = 1;
                        for (RSWCage c : cgs) {
                            ts.add(new Team(tc, (RSWMapsConfig.file().getInt(s + ".number-of-players") / cgs.size()), c.getLoc(), worldName));
                            ++tc;
                        }
                        Teams teas = new Teams(s, displayName, w, RSWMapsConfig.file().getString(s + ".schematic"), wt, RSWGame.GameState.AVAILABLE, ts, RSWMapsConfig.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), isBorderEnabled(s), getPOS1(w, s), getPOS2(w, s), getChests(worldName, s), isRanked(s), rs);
                        teas.resetArena(RSWGame.OperationReason.LOAD);
                        rs.getGameManagerAPI().addRoom(teas);
                        break;
                    default:
                        throw new IllegalStateException("Mode doesnt exist: " + t.name());
                }
            }
        }
    }

    @Override
    public List<String> getRegisteredMaps() {
        RSWMapsConfig.reload();
        return new ArrayList<>(RSWMapsConfig.file().getRoot().getRoutesAsStrings(false));
    }
    @Override
    public void unregisterMap(RSWGame map) {
        map.getAllPlayers().forEach(map::removePlayer);
        rs.getGameManagerAPI().removeRoom(map);
        RSWMapsConfig.file().remove(map.getMapName());
        rs.getGameManagerAPI().getGames(GameManager.GameModes.ALL).forEach(swGameMode -> swGameMode.save(RSWGame.Data.ALL, true));
        RSWMapsConfig.save();
    }

    @Override
    public RSWGame getMap(String s) {
        return rs.getGameManagerAPI().getGames(GameManager.GameModes.ALL).stream()
                .filter(g -> g.getMapName().equalsIgnoreCase(s))
                .findFirst()
                .orElse(null);
    }
    @Override
    public List<RSWCage> getCages(String s, Location specLoc) {
        List<RSWCage> locs = new ArrayList<>();
        int id = 0;
        for (String i : RSWMapsConfig.file().getSection(s + ".Locations.Cages").getRoutesAsStrings(false)) {
            int x = RSWMapsConfig.file().getInt(s + ".Locations.Cages." + i + ".X");
            int y = RSWMapsConfig.file().getInt(s + ".Locations.Cages." + i + ".Y");
            int z = RSWMapsConfig.file().getInt(s + ".Locations.Cages." + i + ".Z");
            locs.add(new RSWSoloCage(id, x, y, z, RSWMapsConfig.file().getString(s + ".world"), specLoc.getBlockX(), specLoc.getBlockY(), specLoc.getBlockZ()));
            ++id;
        }
        return locs;
    }
    @Override
    public void setupSolo(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int maxP) {
        SetupRoom s = new SetupRoom(mapname, displayName, null, wt, maxP);
        s.setSchematic(mapname);
        p.setSetup(s);

        SetupRoomSettingsGUI m = new SetupRoomSettingsGUI(p, s);
        m.openInventory(p);
    }
    @Override
    public void setupTeams(RSWPlayer p, String mapname, String displayName, RSWWorld.WorldType wt, int teams, int pperteam) {
        SetupRoom s = new SetupRoom(mapname, displayName,null, wt, teams, pperteam);
        s.setSchematic(mapname);
        p.setSetup(s);

        SetupRoomSettingsGUI m = new SetupRoomSettingsGUI(p, s);
        m.openInventory(p);
    }
    @Override
    public void cancelSetup(RSWPlayer p) {
        rs.getGameManagerAPI().tpToLobby(p);
        rs.getPlayerManagerAPI().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);
        RSWMapsConfig.file().set(p.getSetupRoom().getName(), null);
        RSWMapsConfig.save();
        p.setSetup(null);
    }
    @Override
    public void continueSetup(RSWPlayer p) {
        if (!p.getSetupRoom().isTPConfirmed()) {
            p.getSetupRoom().setTPConfirm(true);

            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.GENERATING_WORLD, true));

            World w = rs.getWorldManagerAPI().createEmptyWorld(p.getSetupRoom().getName().replace(".schematic", "").replace(".schem", ""), World.Environment.NORMAL);
            if (w != null) {
                w.getBlockAt(0, 64, 0).setType(Material.BEDROCK);
                Location loc = new Location(w, 0, 66, 0);

                Text.sendList(p.getPlayer(), Text.replaceVarInList(rs.getLanguageManagerAPI().getList(p, LanguageManagerAPI.TL.INITSETUP_ARENA), "%cages%", p.getSetupRoom().getMaxPlayers() + ""), p.getSetupRoom().getMaxPlayers());

                rs.getPlayerManagerAPI().giveItems(p.getPlayer(), PlayerManager.Items.SETUP);
                p.getPlayer().setGameMode(GameMode.CREATIVE);

                if (p.getSetupRoom().getWorldType() == RSWWorld.WorldType.SCHEMATIC) {
                    w.setAutoSave(false);

                    p.teleport(loc);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> WorldEditUtils.pasteSchematic(p.getSetupRoom().getSchematic(), new Location(p.getWorld(), 0, 64, 0)), 3 * 20);
                } else {
                    w.setAutoSave(true);
                    p.teleport(loc);
                }

                p.getSetupRoom().setWorld(w);
            } else {
                 rs.getLogger().warning("Could not create setup world for " + p.getSetupRoom().getName());
            }
        }
    }
    @Override
    public void finishSetup(RSWPlayer p) {
        WorldEditPlugin w = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        try {
            com.sk89q.worldedit.regions.Region r = w.getSession(p.getPlayer()).getSelection(w.getSession(p.getPlayer()).getSelectionWorld());

            if (r != null) {
                rs.getGameManagerAPI().tpToLobby(p);

                Location pos2 = new Location(p.getSetupRoom().getWorld(), r.getMinimumPoint().getBlockX(), r.getMinimumPoint().getBlockY(), r.getMinimumPoint().getBlockZ());
                Location pos1 = new Location(p.getSetupRoom().getWorld(), r.getMaximumPoint().getBlockX(), r.getMaximumPoint().getBlockY(), r.getMaximumPoint().getBlockZ());

                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.SAVING_ARENA, true));

                // Beacon Remove
                p.getSetupRoom().getCages().forEach(cage -> p.getSetupRoom().getWorld().getBlockAt(cage.getLoc()).setType(Material.AIR));

                //Remove dropped items
                rs.getWorldManagerAPI().clearItems(p.getSetupRoom().getWorld());

                //worldType
                if (p.getSetupRoom().getWorldType() == RSWWorld.WorldType.DEFAULT) {
                    //Unload world
                    rs.getWorldManagerAPI().unloadWorld(p.getSetupRoom().getWorld().getName(), true);

                    //Copy world
                    rs.getWorldManagerAPI().copyWorld(p.getSetupRoom().getWorld().getName(), WorldManager.CopyTo.RSW_FOLDER);
                }

                //Load world again
                boolean loaded = rs.getWorldManagerAPI().loadWorld(p.getSetupRoom().getWorld().getName(), World.Environment.NORMAL);

                if (loaded) {
                    // Save Data
                    RSWGame.Mode gt = p.getSetupRoom().getGameType();
                    switch (gt) {
                        case SOLO:
                            Solo gs = new Solo(p.getSetupRoom().getName(), p.getSetupRoom().getDisplayName(), p.getSetupRoom().getWorld(), p.getSetupRoom().getSchematic(), p.getSetupRoom().getWorldType(), RSWGame.GameState.AVAILABLE, p.getSetupRoom().getCages(), p.getSetupRoom().getMaxPlayers(), p.getSetupRoom().getSpectatorLocation(), p.getSetupRoom().isSpectatingON(), p.getSetupRoom().isInstantEnding(), p.getSetupRoom().isBorderEnabled(), pos1, pos2, p.getSetupRoom().getChests(), p.getSetupRoom().isRanked(), rs);

                            if (p.getSetupRoom().getWorldType() == RSWWorld.WorldType.DEFAULT) {
                                gs.getRSWWorld().resetWorld(RSWGame.OperationReason.LOAD);
                            } else {
                                gs.getRSWWorld().resetWorld(RSWGame.OperationReason.RESET);
                            }

                            rs.getGameManagerAPI().addRoom(gs);
                            gs.save(RSWGame.Data.ALL, true);

                            //set chests
                            gs.getChests().forEach(RSWChest::setChest);
                            break;
                        case TEAMS:
                            List<Team> ts = new ArrayList<>();
                            int tc = 1;
                            for (RSWCage c : p.getSetupRoom().getCages()) {
                                ts.add(new Team(tc, p.getSetupRoom().getPlayersPerTeam(), c.getLoc(), p.getSetupRoom().getWorld().getName()));
                                ++tc;
                            }
                            Teams t = new Teams(p.getSetupRoom().getName(), p.getSetupRoom().getDisplayName(), p.getSetupRoom().getWorld(), p.getSetupRoom().getSchematic(), p.getSetupRoom().getWorldType(), RSWGame.GameState.AVAILABLE, ts, p.getSetupRoom().getMaxPlayers(), p.getSetupRoom().getSpectatorLocation(), p.getSetupRoom().isSpectatingON(), p.getSetupRoom().isInstantEnding(), p.getSetupRoom().isBorderEnabled(), pos1, pos2, p.getSetupRoom().getChests(), p.getSetupRoom().isRanked(), rs);

                            if (p.getSetupRoom().getWorldType() == RSWWorld.WorldType.DEFAULT) {
                                t.getRSWWorld().resetWorld(RSWGame.OperationReason.LOAD);
                            } else {
                                t.getRSWWorld().resetWorld(RSWGame.OperationReason.RESET);
                            }

                            rs.getGameManagerAPI().addRoom(t);
                            t.save(RSWGame.Data.ALL, true);

                            //set chests
                            t.getChests().forEach(RSWChest::setChest);
                            break;
                        default:
                            throw new IllegalStateException("Forbiden Mode !! " + gt.name());
                    }

                    p.setSetup(null);
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.ARENA_REGISTERED, true));
                }
            }
        } catch (Exception e) {
            p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_ARENA_BOUNDARIES, true));
        }
    }

    @Override
    protected RSWGame.Mode getGameType(String s) {
        return (RSWMapsConfig.file().getString(s + ".Settings.GameType") == null) ? null : RSWGame.Mode.valueOf(RSWMapsConfig.file().getString(s + ".Settings.GameType"));
    }
    @Override
    protected Boolean isInstantEndingEnabled(String s) {
        return RSWMapsConfig.file().getBoolean(s + ".Settings.Instant-End");
    }
    @Override
    protected Boolean isBorderEnabled(String s) {
        return RSWMapsConfig.file().getBoolean(s + ".Settings.Border");
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
    protected List<RSWChest> getChests(String worldName, String section) {
        List<RSWChest> chests = new ArrayList<>();
        if (RSWMapsConfig.file().isSection(section + ".Chests")) {
            for (String i : RSWMapsConfig.file().getSection(section + ".Chests").getRoutesAsStrings(false)) {
                int x = RSWMapsConfig.file().getInt(section + ".Chests." + i + ".LocationX");
                int y = RSWMapsConfig.file().getInt(section + ".Chests." + i + ".LocationY");
                int z = RSWMapsConfig.file().getInt(section + ".Chests." + i + ".LocationZ");
                BlockFace f = BlockFace.valueOf(RSWMapsConfig.file().getString(section + ".Chests." + i + ".Face"));

                RSWChest.Type ct = RSWChest.Type.valueOf(RSWMapsConfig.file().getString(section + ".Chests." + i + ".Type"));
                if (ct == null) {
                    Debugger.print(MapManager.class, "CHEST FACE INVALID WHILE LOADING " + worldName + "!! >> " + RSWMapsConfig.file().getString(section + ".Chests." + i + ".Type"));
                }

                chests.add(new RSWChest(ct, worldName, x, y, z, f));
            }
        } else {
            Debugger.print(MapManager.class, "There are no chests in " + worldName + " (possibly a bug? Check config pls!)");
        }
        return chests;
    }

    @Override
    public void renameMap(String map, String displayName) {
        RSWGame g = this.getMap(map);
        if (g != null) {
            g.setDisplayName(displayName);
            g.save(RSWGame.Data.SETTINGS, true);
        }
    }
}
