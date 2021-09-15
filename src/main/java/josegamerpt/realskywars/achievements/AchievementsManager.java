package josegamerpt.realskywars.achievements;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.achievements.types.AchievementRCoin;
import josegamerpt.realskywars.configuration.Achievements;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

public class AchievementsManager {

    public HashMap<RSWPlayer.PlayerStatistics, List<Achievement>> achievements = new HashMap<>();

    public void loadAchievements()
    {
        for (RSWPlayer.PlayerStatistics value : RSWPlayer.PlayerStatistics.values()) {
            try {
                loadAchievement(value);
            } catch (Exception e)
            {
                RealSkywars.log(Level.SEVERE, "Error while loading achievements for " + value.name());
                e.printStackTrace();
            }
        }
    }

    public void loadAchievement(RSWPlayer.PlayerStatistics t) {
        for (String dir : Achievements.file().getConfigurationSection("").getKeys(false)) {
            String path;
            switch (dir)
            {
                case "Coins":
                    path = "Coins.";
                    switch (t) {
                        case KILLS:
                            path += "Kills";
                            break;
                        case WINS_SOLO:
                            path += "WinsSolo";
                            break;
                        case WINS_TEAMS:
                            path += "WinsTeams";
                            break;
                        default:
                            path += "skip";
                    }

                    if (path != "skip") {
                        List<Achievement> achiv = new ArrayList<>();
                        for (String meta : Achievements.file().getConfigurationSection(path).getKeys(false)) {
                            Double value = Achievements.file().getDouble(path + "." + meta);
                            achiv.add(new AchievementRCoin(t, Integer.parseInt(meta), value));
                        }

                        this.achievements.put(t, achiv);

                    }
                    break;
            }
        }
    }

    public List<Achievement> getAchievements(RSWPlayer.PlayerStatistics ds)
    {
        return this.achievements.get(ds);
    }

    public Achievement getAchievement(RSWPlayer.PlayerStatistics ps, int meta)
    {
        List<Achievement> list = this.achievements.get(ps);
        if (list != null)
        {
            Optional<Achievement> o = list.stream().filter(c -> c.getMeta() == meta).findFirst();
            if (o.isPresent())
            {
                return o.get();
            }
        }
        return null;
    }

    public ItemStack getItem(Achievement s, UUID uuid) {
        RSWPlayer p = RealSkywars.getPlayerManager().getPlayer(uuid);

        return Itens.createItemLore(getColor(s, p), 1, "&b&l" + s.getMeta(), Collections.singletonList("&aReward: &e" + s.getReward() + " coins"));
    }

    private Material getColor(Achievement s, RSWPlayer p) {
        return ((int) p.getStatistics(s.getType(), false)) > s.getMeta() ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;
    }
}
