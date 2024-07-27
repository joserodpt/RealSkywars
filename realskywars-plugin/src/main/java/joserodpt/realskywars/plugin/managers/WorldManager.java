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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.google.common.collect.Lists;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.WorldManagerAPI;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

public class WorldManager extends WorldManagerAPI {

    private final RealSkywarsAPI rs;

    public WorldManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    //CREDIT to open source
    @Override
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

    @Override
    public boolean isSymlink(final File file) {
        if (file == null) {
            throw new NullPointerException("File must no be null");
        }
        return Files.isSymbolicLink(file.toPath());
    }

    @Override
    public void copyWorld(String name, CopyTo t) {
        File maps = new File(rs.getPlugin().getDataFolder(), "maps");
        String root = rs.getPlugin().getServer().getWorldContainer().getAbsolutePath();
        File source = new File(root, name);
        File target = new File(maps, name);
        switch (t) {
            case ROOT:
                this.copyWorld(name, target, source);
                break;
            case RSW_FOLDER:
                this.copyWorld(name, source, target);
                break;
        }
    }

    @Override
    public World createEmptyWorld(String name, Environment environment) {
        if (org.bukkit.Bukkit.getWorld(name) == null) {
            loadWorld(name, environment);
            return org.bukkit.Bukkit.getWorld(name);
        }
        return org.bukkit.Bukkit.getWorld(name);
    }

    @Override
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
        for (World w : rs.getPlugin().getServer().getWorlds()) {
            if (w.getName().equals(world.getName())) {
                loaded = true;
                break;
            }
        }
        return loaded;
    }

    @Override
    public void unloadWorld(String w, boolean save) {
        World world = rs.getPlugin().getServer().getWorld(w);
        if (world != null) {
            world.getPlayers().forEach(this::tpToLobby);
        }
        rs.getPlugin().getServer().unloadWorld(world, save);
    }

    @Override
    protected void tpToLobby(Player p) {
        if (rs.getLobbyManagerAPI().getLobbyLocation() != null) {
            p.teleport(rs.getLobbyManagerAPI().getLobbyLocation());
            TranslatableLine.LOBBY_TELEPORT.sendDefault(p, true);
        } else {
            TranslatableLine.LOBBY_NOT_SET.sendDefault(p, true);
        }
    }

    @Override
    public void copyWorld(String name, File source, File target) {
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
                                copyWorld(name, srcFile, destFile);
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
            RealSkywarsAPI.getInstance().getLogger().severe("Failed to copy world: + " + name + " not found");
            RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
        } catch (IOException e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Failed to copy world: + " + name);
            RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void deleteWorld(String name, boolean removeFile) {
        this.unloadWorld(name, false);
        if (removeFile) {
            File target = new File(rs.getPlugin().getServer().getWorldContainer().getAbsolutePath(), name);
            try {
                deleteDirectory(target);
            } catch (IOException e) {
                RealSkywarsAPI.getInstance().getLogger().severe("Error trying to delete World " + name);
                RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
            }
        }
    }

    @Override
    public void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            RealSkywarsAPI.getInstance().getLogger().warning("Unable to delete directory " + directory);
        }
    }

    @Override
    protected void cleanDirectory(final File directory) throws IOException {
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

    @Override
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

}