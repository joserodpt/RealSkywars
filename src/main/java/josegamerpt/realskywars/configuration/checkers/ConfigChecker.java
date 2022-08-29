package josegamerpt.realskywars.configuration.checkers;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;

import java.util.ArrayList;
import java.util.logging.Level;

public class ConfigChecker {

    private final static int latest = 1;
    private static String errors;

    public static int getConfigVersion() {
        if (Config.file().getInt("Version") == 0) {
            return 1;
        } else {
            return Config.file().getInt("Version");
        }
    }

    public static void updateConfig() {
        while (getConfigVersion() != latest) {
            int newconfig = 0;
            if (getConfigVersion() == 1) {//update to 2
                newconfig = 2;
            }
            RealSkywars.log(Level.INFO, "Config file updated to version " + newconfig + ".");
        }
        if (getConfigVersion() == latest) {
            RealSkywars.log(Level.INFO, "Your config file is updated to the latest version.");
        }
    }

    public static String getErrors() {
        return errors;
    }

    public static boolean checkForErrors() {
        ArrayList<String> errs = new ArrayList<>();


        errors = String.join(", ", errs);
        return errs.size() > 0;
    }
}