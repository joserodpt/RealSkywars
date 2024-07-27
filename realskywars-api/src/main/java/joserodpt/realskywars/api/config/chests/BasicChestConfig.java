package joserodpt.realskywars.api.config.chests;

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

import dev.dejvokep.boostedyaml.YamlDocument;
import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BasicChestConfig {

    private static final String name = "basic.yml";
    private static YamlDocument document;

    public static void setup(final JavaPlugin rm) {
        try {
            File folder = new File(RealSkywarsAPI.getInstance().getPlugin().getDataFolder(), "chests");
            File file = new File(folder, name);
            document = YamlDocument.create(file, rm.getResource(name));
        } catch (final IOException e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Couldn't setup " + name + "!");
            RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
        }
    }

    public static YamlDocument file() {
        return document;
    }

    public static void save() {
        try {
            document.save();
        } catch (final IOException e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Couldn't save " + name + "!");
        }
    }

    public static void reload() {
        try {
            document.reload();
        } catch (final IOException e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Couldn't reload " + name + "!");
        }
    }
}