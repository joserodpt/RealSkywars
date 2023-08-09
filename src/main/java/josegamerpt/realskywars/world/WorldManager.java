package josegamerpt.realskywars.world;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.google.common.collect.Lists;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.LanguageManager;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class WorldManager {
    
    private RealSkywars rs;
    public WorldManager(RealSkywars rs) {
        this.rs = rs;
    }

    public void clearItems(World w) {
        for (Entity entity : w.getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }
    }

    //CREDIT to open source

    public File[] verifiedListFiles(File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    public boolean isSymlink(final File file) {
        if (file == null) {
            throw new NullPointerException("File must no be null");
        }
        return Files.isSymbolicLink(file.toPath());
    }

    public void copyWorld(String name, CopyTo t) {
        File maps = new File(rs.getDataFolder(), "maps");
        String root = rs.getServer().getWorldContainer().getAbsolutePath();
        File source = new File(root, name);
        File target = new File(maps, name);
        switch (t) {
            case ROOT:
                this.copyWorld(target, source);
                break;
            case RSW_FOLDER:
                this.copyWorld(source, target);
                break;
        }
    }

    public World createEmptyWorld(String name, Environment environment) {
        if (org.bukkit.Bukkit.getWorld(name) == null) {
            loadWorld(name, environment);
            return org.bukkit.Bukkit.getWorld(name);
        }
        return null;
    }

    public boolean loadWorld(String worldName, Environment environment) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(environment);
        worldCreator.generateStructures(false);
        worldCreator.generator(new VoidWorld());
        World world = worldCreator.createWorld();
        world.setDifficulty(Difficulty.NORMAL);
        world.setSpawnFlags(true, true);
        world.setPVP(true);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setKeepSpawnInMemory(false);
        world.setTicksPerAnimalSpawns(1);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setTicksPerMonsterSpawns(1);
        world.setAutoSave(false);

        boolean loaded = false;
        for (World w : rs.getServer().getWorlds()) {
            if (w.getName().equals(world.getName())) {
                loaded = true;
                break;
            }
        }
        return loaded;
    }

    public void unloadWorld(String w, boolean save) {
        World world = rs.getServer().getWorld(w);
        if (world != null) {
            world.getPlayers().forEach(this::tpToLobby);
        }
        rs.getServer().unloadWorld(world, save);
    }

    private void tpToLobby(Player p) {
        if (rs.getGameManager().getLobbyLocation() != null) {
            p.teleport(rs.getGameManager().getLobbyLocation());
            p.sendMessage(rs.getLanguageManager().getString(LanguageManager.TS.LOBBY_TELEPORT, true));
        } else {
            p.sendMessage(rs.getLanguageManager().getString(LanguageManager.TS.LOBBYLOC_NOT_SET, true));
        }
    }

    public void copyWorld(File source, File target) {
        try {
            List<String> ignore = Lists.newArrayList("uid.dat", "session.dat", "session.lock");
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if ((!target.exists()) && (target.mkdirs())) {
                        String[] files = source.list();
                        if (files != null) {
                            for (String file : files) {
                                File srcFile = new File(source, file);
                                File destFile = new File(target, file);
                                copyWorld(srcFile, destFile);
                            }
                        }
                    }
                } else {
                    java.io.InputStream in = new java.io.FileInputStream(source);
                    OutputStream out = new java.io.FileOutputStream(target);
                    byte[] buffer = new byte['Ѐ'];
                    int length;
                    while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (FileNotFoundException e) {
            RealSkywars.getPlugin().log(Level.SEVERE, "Failed to copy world: world not found");
            RealSkywars.getPlugin().severe(e.getMessage());
        } catch (IOException e) {
            RealSkywars.getPlugin().log(Level.SEVERE, "Failed to copy world.");
            RealSkywars.getPlugin().severe(e.getMessage());
        }
    }

    public void deleteWorld(String name, boolean removeFile) {
        this.unloadWorld(name, false);
        if (removeFile) {
            File target = new File(rs.getServer().getWorldContainer().getAbsolutePath(), name);
            try {
                deleteDirectory(target);
            } catch (IOException e) {
                RealSkywars.getPlugin().severe("Error trying to delete World " + name);
                RealSkywars.getPlugin().severe(e.getMessage());
            }
        }
    }

    public void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            RealSkywars.getPlugin().log(Level.WARNING, "Unable to delete directory " + directory);
        }
    }

    private void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public enum CopyTo {ROOT, RSW_FOLDER}

    public class VoidWorld extends ChunkGenerator {
        @Override
        public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
            return createChunkData(world);
        }

        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 0.0D, 128.0D, 0.0D);
        }
    }
}