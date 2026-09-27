package joserodpt.realskywars.api.config;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import dev.dejvokep.boostedyaml.YamlDocument;
import joserodpt.realutils.config.YamlConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RSWLanguagesOldConfig {

    private static final String name = "languages.yml";
    private static YamlConfig config;

    public static void setup(final JavaPlugin rm) {
        File file = new File(rm.getDataFolder(), name);
        if (!file.exists()) {
            config = null;
            return;
        }

        config = YamlConfig.of(rm, file, null).load();
    }

    public static YamlDocument file() {
        return config == null ? null : config.file();
    }

    public static void save() {
        //setup() leaves it null when the legacy file does not exist
        if (config != null) {
            config.save();
        }
    }

    public static void reload() {
        if (config != null) {
            config.reload();
        }
    }
}
