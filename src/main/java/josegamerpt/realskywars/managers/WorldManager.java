package josegamerpt.realskywars.managers;

import com.google.common.collect.Lists;
import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.*;
import org.bukkit.World.Environment;
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
        File maps = new File(RealSkywars.getPlugin().getDataFolder(), "maps");
        String root = RealSkywars.getPlugin().getServer().getWorldContainer().getAbsolutePath();
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
        for (World w : RealSkywars.getPlugin().getServer().getWorlds()) {
            if (w.getName().equals(world.getName())) {
                loaded = true;
                break;
            }
        }
        return loaded;
    }

    public void unloadWorld(String w, boolean save) {
        World world = RealSkywars.getPlugin().getServer().getWorld(w);
        if (world != null) {
            world.getPlayers().forEach(this::tpToLobby);
        }
        RealSkywars.getPlugin().getServer().unloadWorld(world, save);
    }

    private void tpToLobby(Player p) {
        if (RealSkywars.getGameManager().getLobbyLocation() != null) {
            p.teleport(RealSkywars.getGameManager().getLobbyLocation());
            p.sendMessage(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.LOBBY_TELEPORT, true));
        } else {
            p.sendMessage(RealSkywars.getLanguageManager().getString(new RSWPlayer(false), LanguageManager.TS.LOBBYLOC_NOT_SET, true));
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
            RealSkywars.log(Level.SEVERE, "Failed to copy world: world not found");
            e.printStackTrace();
        } catch (IOException e) {
            RealSkywars.log(Level.SEVERE, "Failed to copy world.");
            e.printStackTrace();
        }
    }

    public void deleteWorld(String name, boolean removeFile) {
        unloadWorld(name, false);
        if (removeFile) {
            File target = new File(RealSkywars.getPlugin().getServer().getWorldContainer().getAbsolutePath(), name);
            try {
                deleteDirectory(target);
            } catch (IOException e) {
                e.printStackTrace();
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
            final String message = "Unable to delete directory " + directory;
            throw new IOException(message);
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