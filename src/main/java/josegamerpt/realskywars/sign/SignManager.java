package josegamerpt.realskywars.sign;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Signs;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SignManager {

    public void addSign(SWGameMode gm, Block b) {
        gm.addSign(new SWSign(gm, b));
    }

    public void removeSign(SWGameMode gm, Block b) {
        gm.removeSign(b);
    }

    public void saveSigns() {
        List<SWSign> save = new ArrayList<>();
        RealSkywars.getGameManager().getGames(PlayerManager.Modes.ALL).forEach(gameMode -> save.addAll(gameMode.getSigns()));

        Signs.file().set("Signs", null);

        for (SWSign swSign : save) {
            Signs.file().set("Signs." + swSign.getLocationSerialized(), swSign.getRoomName());
        }

        Signs.save();
    }

    public void loadSigns() {
        if (Signs.file().getConfigurationSection("Signs") != null) {
            for (String loc : Signs.file().getConfigurationSection("Signs").getKeys(false)) {
                String map = Signs.file().getString("Signs." + loc);
                SWGameMode sgm = RealSkywars.getGameManager().getGame(map);
                if (sgm != null) {
                    String[] data = loc.split("<");
                    World w = Bukkit.getWorld(data[0]);
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    int z = Integer.parseInt(data[3]);

                    this.addSign(sgm, w.getBlockAt(x, y, z));
                }
            }
        }
    }
}
