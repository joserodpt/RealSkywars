package josegamerpt.realskywars.configuration.chests;

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
 *
 */

import dev.dejvokep.boostedyaml.YamlDocument;
import josegamerpt.realskywars.RealSkywars;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class EPICChest {

    private static final String name = "epicchest.yml";
    private static YamlDocument document;

    public static void setup(final JavaPlugin rm) {
        try {
            document = YamlDocument.create(new File(rm.getDataFolder(), name), rm.getResource(name));
        } catch (final IOException e) {
            RealSkywars.getPlugin().severe("Couldn't setup " + name + "!");
            RealSkywars.getPlugin().severe(e.getMessage());
        }
    }

    public static YamlDocument file() {
        return document;
    }

    public static void save() {
        try {
            document.save();
        } catch (final IOException e) {
            RealSkywars.getPlugin().severe( "Couldn't save " + name + "!");
        }
    }

    public static void reload() {
        try {
            document.reload();
        } catch (final IOException e) {
            RealSkywars.getPlugin().severe( "Couldn't reload " + name + "!");
        }
    }
}