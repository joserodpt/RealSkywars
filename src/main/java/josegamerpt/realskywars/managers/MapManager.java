package josegamerpt.realskywars.managers;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import josegamerpt.realskywars.Debugger;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.cages.SoloCage;
import josegamerpt.realskywars.game.modes.SWGameMode.GameState;
import josegamerpt.realskywars.chests.SWChest;
import josegamerpt.realskywars.misc.SetupRoom;
import josegamerpt.realskywars.misc.Team;
import josegamerpt.realskywars.configuration.Maps;
import josegamerpt.realskywars.gui.MapSettings;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.Solo;
import josegamerpt.realskywars.game.modes.Teams;
import josegamerpt.realskywars.player.PlayerManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Text;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

public class MapManager {

    public ArrayList<String> getRegisteredMaps() {
        Maps.reload();
        return new ArrayList<>(Maps.file().getConfigurationSection("").getKeys(false));
    }

    public void unregisterMap(SWGameMode map) {
        Maps.file().set(map.getName(), null);
        Maps.save();
        RealSkywars.getGameManager().removeRoom(map);
    }

    public void loadMaps() {
        RealSkywars.getGameManager().clearRooms();

        int id = 0;
        for (String s : getRegisteredMaps()) {
            if (getGameType(s) == null) {
                throw new IllegalStateException("Mode doesnt exist: " + s);
            }

            String worldName = Maps.file().getString(s + ".world");

            String root = RealSkywars.getPlugin().getServer().getWorldContainer().getAbsolutePath();
            File source = new File(root, worldName);
            if (!source.exists()) {
                RealSkywars.getWorldManager().copyWorld(worldName, WorldManager.CopyTo.ROOT);
            }

            boolean loaded = RealSkywars.getWorldManager().loadWorld(worldName, World.Environment.NORMAL);
            if (loaded) {
                World w = Bukkit.getWorld(worldName);

                Location specLoc = getSpecLoc(s);
                SWGameMode.Mode t = getGameType(s);
                switch (t) {
                    case SOLO:
                        Solo gs = new Solo(s, w, GameState.AVAILABLE, getCages(s, specLoc), Maps.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), getPOS1(w, s), getPOS2(w, s), getChests(worldName, s));
                        gs.saveRoom();
                        break;
                    case TEAMS:
                        ArrayList<Cage> cgs = getCages(s, specLoc);
                        ArrayList<Team> ts = new ArrayList<>();
                        int tc = 1;
                        for (Cage c : cgs) {
                            ts.add(new Team(tc, (Maps.file().getInt(s + ".number-of-players") / cgs.size()), c.getLoc(), worldName));
                            tc++;
                        }
                        Teams teas = new Teams(s, w, GameState.AVAILABLE, ts, Maps.file().getInt(s + ".number-of-players"), specLoc, isSpecEnabled(s), isInstantEndingEnabled(s), getPOS1(w, s), getPOS2(w, s), getChests(worldName, s));
                        teas.saveRoom();
                        break;
                    default:
                        throw new IllegalStateException("Mode doesnt exist: " + t.name());
                }
            }
            id++;
        }
    }

    private ArrayList<SWChest> getChests(String worldName, String section) {
        ArrayList<SWChest> chests = new ArrayList<>();
        ConfigurationSection cs = Maps.file().getConfigurationSection(section + ".Chests");
        if (cs != null) {
            Set<String> keys = cs.getKeys(false);
            for (String i : keys) {
                int x = Maps.file().getInt(section + ".Chests." + i + ".LocationX");
                int y = Maps.file().getInt(section + ".Chests." + i + ".LocationY");
                int z = Maps.file().getInt(section + ".Chests." + i + ".LocationZ");
                BlockFace f = BlockFace.valueOf(Maps.file().getString(section + ".Chests." + i + ".Face"));

                SWChest.ChestTYPE ct = SWChest.ChestTYPE.valueOf(Maps.file().getString(section + ".Chests." + i + ".Type"));

                chests.add(new SWChest(ct, worldName, x, y, z, f));
            }
        } else {
            Debugger.print(MapManager.class, "There are no chests in " + worldName + " (possibly a bug? Check config pls!)");
        }
        return chests;
    }

    private SWGameMode.Mode getGameType(String s) {
        return SWGameMode.Mode.valueOf(Maps.file().getString(s + ".Settings.GameType"));
    }

    private Boolean isInstantEndingEnabled(String s) {
        return Maps.file().getBoolean(s + ".Settings.Dragon-Ride");
    }

    private Location getPOS1(World w, String s) {
        double hx = Maps.file().getDouble(s + ".World.Border.POS1-X");
        double hy = Maps.file().getDouble(s + ".World.Border.POS1-Y");
        double hz = Maps.file().getDouble(s + ".World.Border.POS1-Z");

        return new Location(w, hx, hy, hz);
    }

    private Location getPOS2(World w, String s) {
        double hx = Maps.file().getDouble(s + ".World.Border.POS2-X");
        double hy = Maps.file().getDouble(s + ".World.Border.POS2-Y");
        double hz = Maps.file().getDouble(s + ".World.Border.POS2-Z");

        return new Location(w, hx, hy, hz);
    }

    public Boolean isSpecEnabled(String s) {
        return Maps.file().getBoolean(s + ".Settings.Spectator");
    }

    public Location getSpecLoc(String nome) {
        double x = Maps.file().getDouble(nome + ".Locations.Spectator.X");
        double y = Maps.file().getDouble(nome + ".Locations.Spectator.Y");
        double z = Maps.file().getDouble(nome + ".Locations.Spectator.Z");
        float pitch = (float) Maps.file().getDouble(nome + ".Locations.Spectator.Pitch");
        float yaw = (float) Maps.file().getDouble(nome + ".Locations.Spectator.Yaw");
        return new Location(Bukkit.getWorld(nome), x, y, z, pitch, yaw);
    }

    public SWGameMode getMap(String s) {
        for (SWGameMode g : RealSkywars.getGameManager().getGames()) {
            if (g.getName().equalsIgnoreCase(s)) {
                return g;
            }
        }
        return null;
    }

    public ArrayList<Cage> getCages(String s, Location specLoc) {
        ConfigurationSection cs = Maps.file().getConfigurationSection(s + ".Locations.Cages");
        Set<String> keys = cs.getKeys(false);
        ArrayList<Cage> locs = new ArrayList<>();
        int id = 0;
        for (String i : keys) {
            int x = Maps.file().getInt(s + ".Locations.Cages." + i + ".X");
            int y = Maps.file().getInt(s + ".Locations.Cages." + i + ".Y");
            int z = Maps.file().getInt(s + ".Locations.Cages." + i + ".Z");
            locs.add(new SoloCage(id, x, y, z, Maps.file().getString(s + ".world"), specLoc.getBlockX(), specLoc.getBlockY(), specLoc.getBlockZ()));
            id++;
        }
        return locs;
    }

    public void saveMap(SWGameMode g) {
        String s = g.getName();

        // World
        Maps.file().set(s + ".world", g.getWorld().getName());

        // Map Name
        Maps.file().set(s + ".name", s);

        // Number Players
        Maps.file().set(s + ".number-of-players", g.getMaxPlayers());

        // Locations Cages
        switch (g.getGameMode()) {
            case SOLO:
                for (Cage c : g.getCages()) {
                    Location loc = c.getLoc();
                    Maps.file().set(s + ".Locations.Cages." + c.getID() + ".X", loc.getBlockX());
                    Maps.file().set(s + ".Locations.Cages." + c.getID() + ".Y", loc.getBlockY());
                    Maps.file().set(s + ".Locations.Cages." + c.getID() + ".Z", loc.getBlockZ());
                }
                break;
            case TEAMS:
                for (Team c : g.getTeams()) {
                    Location loc = c.getTeamCage().getLoc();
                    Maps.file().set(s + ".Locations.Cages." + c.getTeamCage().getID() + ".X", loc.getBlockX());
                    Maps.file().set(s + ".Locations.Cages." + c.getTeamCage().getID() + ".Y", loc.getBlockY());
                    Maps.file().set(s + ".Locations.Cages." + c.getTeamCage().getID() + ".Z", loc.getBlockZ());
                }
                break;
        }

        //chests
        int chestID = 1;
        for (SWChest chest : g.getChests()) {
            Maps.file().set(s + ".Chests." + chestID + ".LocationX", chest.getLocation().getBlockX());
            Maps.file().set(s + ".Chests." + chestID + ".LocationY", chest.getLocation().getBlockY());
            Maps.file().set(s + ".Chests." + chestID + ".LocationZ", chest.getLocation().getBlockZ());
            BlockFace f = ((Directional) chest.getChest().getBlockData()).getFacing();
            Maps.file().set(s + ".Chests." + chestID + ".Face", f.name());
            Maps.file().set(s + ".Chests." + chestID + ".Type", chest.getType().name());
            chestID++;
        }

        // SpecLoc
        Maps.file().set(s + ".Locations.Spectator.X", g.getSpectatorLocation().getX());
        Maps.file().set(s + ".Locations.Spectator.Y", g.getSpectatorLocation().getY());
        Maps.file().set(s + ".Locations.Spectator.Z", g.getSpectatorLocation().getZ());
        Maps.file().set(s + ".Locations.Spectator.Yaw", g.getSpectatorLocation().getYaw());
        Maps.file().set(s + ".Locations.Spectator.Pitch", g.getSpectatorLocation().getPitch());

        // Settings
        Maps.file().set(s + ".Settings.Spectator", g.isSpectatorEnabled());
        Maps.file().set(s + ".Settings.Instant-Ending", g.isInstantEndEnabled());
        Maps.file().set(s + ".Settings.GameType", g.getGameMode().name());

        // Border
        Maps.file().set(s + ".World.Border.POS1-X", g.getPOS1().getX());
        Maps.file().set(s + ".World.Border.POS1-Y", g.getPOS1().getY());
        Maps.file().set(s + ".World.Border.POS1-Z", g.getPOS1().getZ());
        Maps.file().set(s + ".World.Border.POS2-X", g.getPOS2().getX());
        Maps.file().set(s + ".World.Border.POS2-Y", g.getPOS2().getY());
        Maps.file().set(s + ".World.Border.POS2-Z", g.getPOS2().getZ());

        Maps.save();
    }

    public void cancelSetup(RSWPlayer p) {
        RealSkywars.getGameManager().tpToLobby(p);
        RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);
        Maps.file().set(p.getSetup().getName(), null);
        Maps.save();
        p.setSetup(null);
    }

    public void setupTeams(RSWPlayer p, String mapname, int teams, int pperteam) {
        SetupRoom s = new SetupRoom(mapname, null, teams, pperteam);
        p.setSetup(s);

        MapSettings m = new MapSettings(s, p.getUUID());
        m.openInventory(p);
    }

    public void continueSetup(RSWPlayer p) {
        if (!p.getSetup().isTPConfirmed()) {
            p.getSetup().setTPConfirm(true);

            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.GENERATING_WORLD, true));

            World w = RealSkywars.getWorldManager().createEmptyWorld(p.getSetup().getName(), World.Environment.NORMAL);
            if (w != null) {
                w.setAutoSave(true);
                w.getBlockAt(0, 64, 0).setType(Material.BEDROCK);

                p.getSetup().setWorld(w);

                Location loc = new Location(w, 0, 65, 0);
                p.teleport(loc);

                Text.sendList(p.getPlayer(), Text.replaceVarInList(RealSkywars.getLanguageManager().getList(p, LanguageManager.TL.INITSETUP_ARENA), "%cages%", p.getSetup().getMaxPlayers() + ""), p.getSetup().getMaxPlayers());

                RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.SETUP);
                p.getPlayer().setGameMode(GameMode.CREATIVE);
            } else {
                RealSkywars.log(Level.WARNING, "Could not create setup world for " + p.getSetup().getName());
            }
        }
    }

    public void setupSolo(RSWPlayer p, String mapname, int maxP) {
        SetupRoom s = new SetupRoom(mapname, null, maxP);
        p.setSetup(s);

        MapSettings m = new MapSettings(s, p.getUUID());
        m.openInventory(p);
    }

    public void finishSetup(RSWPlayer p) {
        WorldEditPlugin w = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        try {
            com.sk89q.worldedit.regions.Region r = w.getSession(p.getPlayer()).getSelection(w.getSession(p.getPlayer()).getSelectionWorld());

            if (r != null) {
                Location pos2 = new Location(p.getWorld(), r.getMinimumPoint().getBlockX(), r.getMinimumPoint().getBlockY(), r.getMinimumPoint().getBlockZ());
                Location pos1 = new Location(p.getWorld(), r.getMaximumPoint().getBlockX(), r.getMaximumPoint().getBlockY(), r.getMaximumPoint().getBlockZ());

                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.SAVING_ARENA, true));

                // Beacon Remove
                p.getSetup().getCages().forEach(cage -> p.getSetup().getWorld().getBlockAt(cage.getLoc()).setType(Material.AIR));

                //Remove dropped items
                for (Entity entity : p.getSetup().getWorld().getEntities()) {
                    if (entity.getType() == EntityType.DROPPED_ITEM) {
                        entity.remove();
                    }
                }

                //Unload world
                RealSkywars.getWorldManager().unloadWorld(p.getSetup().getWorld().getName(), true);

                //Copy world
                RealSkywars.getWorldManager().copyWorld(p.getSetup().getWorld().getName(), WorldManager.CopyTo.RSW_FOLDER);

                //Load world again
                boolean loaded = RealSkywars.getWorldManager().loadWorld(p.getSetup().getWorld().getName(), World.Environment.NORMAL);

                if (loaded) {
                    // Save Data
                    SWGameMode.Mode gt = p.getSetup().getGameType();
                    switch (gt) {
                        case SOLO:
                            Solo gs = new Solo(p.getSetup().getName(), p.getSetup().getWorld(), GameState.AVAILABLE, p.getSetup().getCages(), p.getSetup().getMaxPlayers(), p.getSetup().getSpectatorLocation(), p.getSetup().isSpectatingON(), p.getSetup().isInstantEnding(), pos1, pos2, p.getSetup().getChests());
                            gs.saveRoom();
                            saveMap(gs);
                            break;
                        case TEAMS:
                            ArrayList<Team> ts = new ArrayList<>();
                            int tc = 1;
                            for (Cage c : p.getSetup().getCages()) {
                                ts.add(new Team(tc, p.getSetup().getPlayersPerTeam(), c.getLoc(), p.getSetup().getWorld().getName()));
                                tc++;
                            }
                            Teams t = new Teams(p.getSetup().getName(), p.getSetup().getWorld(), GameState.AVAILABLE, ts, p.getSetup().getMaxPlayers(), p.getSetup().getSpectatorLocation(), p.getSetup().isSpectatingON(), p.getSetup().isInstantEnding(), pos1, pos2, p.getSetup().getChests());
                            t.saveRoom();
                            saveMap(t);
                            break;
                        default:
                            throw new IllegalStateException("Forbiden Mode !! " + gt.name());
                    }

                    p.getSetup().getWorld().save();

                    p.setSetup(null);
                    p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.ARENA_REGISTERED, true));

                    RealSkywars.getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.LOBBY);
                }
            }
        } catch (Exception e) {
            p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_ARENA_BOUNDARIES, true));
        }
    }

    public static void saveSettings(SWGameMode game) {
        Maps.file().set(game.getName() + ".Settings.Spectator", game.isSpectatorEnabled());
        Maps.file().set(game.getName() + ".Settings.Instant-End", game.isInstantEndEnabled());
        Maps.save();
    }
}
