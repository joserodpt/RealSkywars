package joserodpt.realskywars.managers;

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
import joserodpt.realskywars.Debugger;
import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.cages.Cage;
import joserodpt.realskywars.cages.SoloCage;
import joserodpt.realskywars.chests.SWChest;
import joserodpt.realskywars.config.RSWConfigMaps;
import joserodpt.realskywars.game.SetupRoom;
import joserodpt.realskywars.game.modes.SWGame;
import joserodpt.realskywars.game.modes.Solo;
import joserodpt.realskywars.game.modes.teams.Team;
import joserodpt.realskywars.game.modes.teams.Teams;
import joserodpt.realskywars.gui.guis.SetupRoomSettings;
import joserodpt.realskywars.player.PlayerManager;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.Text;
import joserodpt.realskywars.utils.WorldEditUtils;
import joserodpt.realskywars.world.SWWorld;
import joserodpt.realskywars.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.logging.Level;

public class MapManager {
    private RealSkywars rs;
    public MapManager(RealSkywars rs) {
        this.rs = rs;
    }

    public void loadMaps() {
        rs.getGameManager().clearRooms();

        for (String s : this.getRegisteredMaps()) {
            SWGame.Mode t = getGameType(s);

            if (t == null) {
                RealSkywars.getPlugin().severe("Mode " + getGameType(s) + " doesnt exist! Skipping map: " + s);
                continue;
            }

            String worldName = RSWConfigMaps.file().getString(s + ".world");

            SWWorld.WorldType wt = getWorldType(RSWConfigMaps.file().getString(s + ".type"));

            boolean loaded = RealSkywars.getPlugin().getWorldManager().loadWorld(worldName, World.Environment.NORMAL);
            if (loaded) {
                World w = Bukkit.getWorld(worldName);

                Location specLoc = getSpecLoc(s);
                switch (t) {
                    case SOLO:
                        Solo gs = new Solo(s, w, RSWConfigMaps.file().getString(s + ".schematic"), wt, SWGame.GameState.AVAILABLE, getCages(s, specLoc), RSWConfigMaps.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), isBorderEnabled(s), getPOS1(w, s), getPOS2(w, s), getChests(worldName, s), isRanked(s), rs);
                        gs.resetArena(SWGame.OperationReason.LOAD);
                        rs.getGameManager().addRoom(gs);
                        break;
                    case TEAMS:
                        ArrayList<Cage> cgs = getCages(s, specLoc);
                        ArrayList<Team> ts = new ArrayList<>();
                        int tc = 1;
                        for (Cage c : cgs) {
                            ts.add(new Team(tc, (RSWConfigMaps.file().getInt(s + ".number-of-players") / cgs.size()), c.getLoc(), worldName));
                            ++tc;
                        }
                        Teams teas = new Teams(s, w, RSWConfigMaps.file().getString(s + ".schematic"), wt, SWGame.GameState.AVAILABLE, ts, RSWConfigMaps.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), isBorderEnabled(s), getPOS1(w, s), getPOS2(w, s), getChests(worldName, s), isRanked(s), rs);
                        teas.resetArena(SWGame.OperationReason.LOAD);
                        rs.getGameManager().addRoom(teas);
                        break;
                    default:
                        throw new IllegalStateException("Mode doesnt exist: " + t.name());
                }
            }
        }
    }

    public ArrayList<String> getRegisteredMaps() {
        RSWConfigMaps.reload();
        return new ArrayList<>(RSWConfigMaps.file().getRoot().getRoutesAsStrings(false));
    }
    public void unregisterMap(SWGame map) {
        map.getAllPlayers().forEach(map::removePlayer);
        rs.getGameManager().removeRoom(map);
        RSWConfigMaps.file().remove(map.getName());
        rs.getGameManager().getGames(GameManager.GameModes.ALL).forEach(swGameMode -> swGameMode.save(SWGame.Data.ALL, true));
        RSWConfigMaps.save();
    }

    public SWGame getMap(String s) {
        return rs.getGameManager().getGames(GameManager.GameModes.ALL).stream()
                .filter(g -> g.getName().equalsIgnoreCase(s))
                .findFirst()
                .orElse(null);
    }
    public ArrayList<Cage> getCages(String s, Location specLoc) {
        ArrayList<Cage> locs = new ArrayList<>();
        int id = 0;
        for (String i : RSWConfigMaps.file().getSection(s + ".Locations.Cages").getRoutesAsStrings(false)) {
            int x = RSWConfigMaps.file().getInt(s + ".Locations.Cages." + i + ".X");
            int y = RSWConfigMaps.file().getInt(s + ".Locations.Cages." + i + ".Y");
            int z = RSWConfigMaps.file().getInt(s + ".Locations.Cages." + i + ".Z");
            locs.add(new SoloCage(id, x, y, z, RSWConfigMaps.file().getString(s + ".world"), specLoc.getBlockX(), specLoc.getBlockY(), specLoc.getBlockZ()));
            ++id;
        }
        return locs;
    }
    public void setupSolo(RSWPlayer p, String mapname, SWWorld.WorldType wt, int maxP) {
        SetupRoom s = new SetupRoom(mapname, null, wt, maxP);
        s.setSchematic(mapname);
        p.setSetup(s);

        SetupRoomSettings m = new SetupRoomSettings(p, s);
        m.openInventory(p);
    }
    public void setupTeams(RSWPlayer p, String mapname, SWWorld.WorldType wt, int teams, int pperteam) {
        SetupRoom s = new SetupRoom(mapname, null, wt, teams, pperteam);
        s.setSchematic(mapname);
        p.setSetup(s);

        SetupRoomSettings m = new SetupRoomSettings(p, s);
        m.openInventory(p);
    }
    public void cancelSetup(RSWPlayer p) {
        rs.getGameManager().tpToLobby(p);
        rs.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);
        RSWConfigMaps.file().set(p.getSetupRoom().getName(), null);
        RSWConfigMaps.save();
        p.setSetup(null);
    }
    public void continueSetup(RSWPlayer p) {
        if (!p.getSetupRoom().isTPConfirmed()) {
            p.getSetupRoom().setTPConfirm(true);

            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.GENERATING_WORLD, true));

            World w = RealSkywars.getPlugin().getWorldManager().createEmptyWorld(p.getSetupRoom().getName().replace(".schematic", "").replace(".schem", ""), World.Environment.NORMAL);
            if (w != null) {
                w.getBlockAt(0, 64, 0).setType(Material.BEDROCK);
                Location loc = new Location(w, 0, 66, 0);

                Text.sendList(p.getPlayer(), Text.replaceVarInList(rs.getLanguageManager().getList(p, LanguageManager.TL.INITSETUP_ARENA), "%cages%", p.getSetupRoom().getMaxPlayers() + ""), p.getSetupRoom().getMaxPlayers());

                rs.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.SETUP);
                p.getPlayer().setGameMode(GameMode.CREATIVE);

                if (p.getSetupRoom().getWorldType() == SWWorld.WorldType.SCHEMATIC) {
                    w.setAutoSave(false);

                    p.teleport(loc);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> WorldEditUtils.pasteSchematic(p.getSetupRoom().getSchematic(), new Location(p.getWorld(), 0, 64, 0)), 3 * 20);
                } else {
                    w.setAutoSave(true);
                    p.teleport(loc);
                }

                p.getSetupRoom().setWorld(w);
            } else {
                RealSkywars.getPlugin().log(Level.WARNING, "Could not create setup world for " + p.getSetupRoom().getName());
            }
        }
    }
    public void finishSetup(RSWPlayer p) {
        WorldEditPlugin w = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        try {
            com.sk89q.worldedit.regions.Region r = w.getSession(p.getPlayer()).getSelection(w.getSession(p.getPlayer()).getSelectionWorld());

            if (r != null) {
                rs.getGameManager().tpToLobby(p);

                Location pos2 = new Location(p.getSetupRoom().getWorld(), r.getMinimumPoint().getBlockX(), r.getMinimumPoint().getBlockY(), r.getMinimumPoint().getBlockZ());
                Location pos1 = new Location(p.getSetupRoom().getWorld(), r.getMaximumPoint().getBlockX(), r.getMaximumPoint().getBlockY(), r.getMaximumPoint().getBlockZ());

                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.SAVING_ARENA, true));

                // Beacon Remove
                p.getSetupRoom().getCages().forEach(cage -> p.getSetupRoom().getWorld().getBlockAt(cage.getLoc()).setType(Material.AIR));

                //Remove dropped items
                RealSkywars.getPlugin().getWorldManager().clearItems(p.getSetupRoom().getWorld());

                //worldType
                if (p.getSetupRoom().getWorldType() == SWWorld.WorldType.DEFAULT) {
                    //Unload world
                    RealSkywars.getPlugin().getWorldManager().unloadWorld(p.getSetupRoom().getWorld().getName(), true);

                    //Copy world
                    RealSkywars.getPlugin().getWorldManager().copyWorld(p.getSetupRoom().getWorld().getName(), WorldManager.CopyTo.RSW_FOLDER);
                }

                //Load world again
                boolean loaded = RealSkywars.getPlugin().getWorldManager().loadWorld(p.getSetupRoom().getWorld().getName(), World.Environment.NORMAL);

                if (loaded) {
                    // Save Data
                    SWGame.Mode gt = p.getSetupRoom().getGameType();
                    switch (gt) {
                        case SOLO:
                            Solo gs = new Solo(p.getSetupRoom().getName(), p.getSetupRoom().getWorld(), p.getSetupRoom().getSchematic(), p.getSetupRoom().getWorldType(), SWGame.GameState.AVAILABLE, p.getSetupRoom().getCages(), p.getSetupRoom().getMaxPlayers(), p.getSetupRoom().getSpectatorLocation(), p.getSetupRoom().isSpectatingON(), p.getSetupRoom().isInstantEnding(), p.getSetupRoom().isBorderEnabled(), pos1, pos2, p.getSetupRoom().getChests(), p.getSetupRoom().isRanked(), rs);

                            if (p.getSetupRoom().getWorldType() == SWWorld.WorldType.DEFAULT) {
                                gs.getSWWorld().resetWorld(SWGame.OperationReason.LOAD);
                            } else {
                                gs.getSWWorld().resetWorld(SWGame.OperationReason.RESET);
                            }

                            rs.getGameManager().addRoom(gs);
                            gs.save(SWGame.Data.ALL, true);

                            //set chests
                            gs.getChests().forEach(SWChest::setChest);
                            break;
                        case TEAMS:
                            ArrayList<Team> ts = new ArrayList<>();
                            int tc = 1;
                            for (Cage c : p.getSetupRoom().getCages()) {
                                ts.add(new Team(tc, p.getSetupRoom().getPlayersPerTeam(), c.getLoc(), p.getSetupRoom().getWorld().getName()));
                                ++tc;
                            }
                            Teams t = new Teams(p.getSetupRoom().getName(), p.getSetupRoom().getWorld(), p.getSetupRoom().getSchematic(), p.getSetupRoom().getWorldType(), SWGame.GameState.AVAILABLE, ts, p.getSetupRoom().getMaxPlayers(), p.getSetupRoom().getSpectatorLocation(), p.getSetupRoom().isSpectatingON(), p.getSetupRoom().isInstantEnding(), p.getSetupRoom().isBorderEnabled(), pos1, pos2, p.getSetupRoom().getChests(), p.getSetupRoom().isRanked(), rs);

                            if (p.getSetupRoom().getWorldType() == SWWorld.WorldType.DEFAULT) {
                                t.getSWWorld().resetWorld(SWGame.OperationReason.LOAD);
                            } else {
                                t.getSWWorld().resetWorld(SWGame.OperationReason.RESET);
                            }

                            rs.getGameManager().addRoom(t);
                            t.save(SWGame.Data.ALL, true);

                            //set chests
                            t.getChests().forEach(SWChest::setChest);
                            break;
                        default:
                            throw new IllegalStateException("Forbiden Mode !! " + gt.name());
                    }

                    p.setSetup(null);
                    p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.ARENA_REGISTERED, true));
                }
            }
        } catch (Exception e) {
            p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_ARENA_BOUNDARIES, true));
        }
    }

    private SWGame.Mode getGameType(String s) {
        return (RSWConfigMaps.file().getString(s + ".Settings.GameType") == null) ? null : SWGame.Mode.valueOf(RSWConfigMaps.file().getString(s + ".Settings.GameType"));
    }
    private Boolean isInstantEndingEnabled(String s) {
        return RSWConfigMaps.file().getBoolean(s + ".Settings.Instant-End");
    }
    private Boolean isBorderEnabled(String s) {
        return RSWConfigMaps.file().getBoolean(s + ".Settings.Border");
    }
    private Location getPOS1(World w, String s) {
        double hx = RSWConfigMaps.file().getDouble(s + ".World.Border.POS1-X");
        double hy = RSWConfigMaps.file().getDouble(s + ".World.Border.POS1-Y");
        double hz = RSWConfigMaps.file().getDouble(s + ".World.Border.POS1-Z");

        return new Location(w, hx, hy, hz);
    }
    private Location getPOS2(World w, String s) {
        double hx = RSWConfigMaps.file().getDouble(s + ".World.Border.POS2-X");
        double hy = RSWConfigMaps.file().getDouble(s + ".World.Border.POS2-Y");
        double hz = RSWConfigMaps.file().getDouble(s + ".World.Border.POS2-Z");

        return new Location(w, hx, hy, hz);
    }
    public Boolean isSpecEnabled(String s) {
        return RSWConfigMaps.file().getBoolean(s + ".Settings.Spectator");
    }
    public Location getSpecLoc(String nome) {
        double x = RSWConfigMaps.file().getDouble(nome + ".Locations.Spectator.X");
        double y = RSWConfigMaps.file().getDouble(nome + ".Locations.Spectator.Y");
        double z = RSWConfigMaps.file().getDouble(nome + ".Locations.Spectator.Z");
        float pitch = RSWConfigMaps.file().getFloat(nome + ".Locations.Spectator.Pitch");
        float yaw = RSWConfigMaps.file().getFloat(nome + ".Locations.Spectator.Yaw");
        return new Location(Bukkit.getWorld(nome), x, y, z, pitch, yaw);
    }
    private SWWorld.WorldType getWorldType(String s) {
        return SWWorld.WorldType.valueOf(s);
    }
    private Boolean isRanked(String s) {
        return RSWConfigMaps.file().getBoolean(s + ".ranked");
    }
    private ArrayList<SWChest> getChests(String worldName, String section) {
        ArrayList<SWChest> chests = new ArrayList<>();
        if (RSWConfigMaps.file().isSection(section + ".Chests")) {
            for (String i : RSWConfigMaps.file().getSection(section + ".Chests").getRoutesAsStrings(false)) {
                int x = RSWConfigMaps.file().getInt(section + ".Chests." + i + ".LocationX");
                int y = RSWConfigMaps.file().getInt(section + ".Chests." + i + ".LocationY");
                int z = RSWConfigMaps.file().getInt(section + ".Chests." + i + ".LocationZ");
                BlockFace f = BlockFace.valueOf(RSWConfigMaps.file().getString(section + ".Chests." + i + ".Face"));

                SWChest.Type ct = SWChest.Type.valueOf(RSWConfigMaps.file().getString(section + ".Chests." + i + ".Type"));
                if (ct == null) {
                    Debugger.print(MapManager.class, "CHEST FACE INVALID WHILE LOADING " + worldName + "!! >> " + RSWConfigMaps.file().getString(section + ".Chests." + i + ".Type"));
                }

                chests.add(new SWChest(ct, worldName, x, y, z, f));
            }
        } else {
            Debugger.print(MapManager.class, "There are no chests in " + worldName + " (possibly a bug? Check config pls!)");
        }
        return chests;
    }
}
