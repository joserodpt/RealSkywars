package josegamerpt.realskywars.achievements;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.achievements.types.AchievementRCoin;
import josegamerpt.realskywars.configuration.Achievements;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AchievementsManager {
    private RealSkywars rs;
    public AchievementsManager(RealSkywars rs) {
        this.rs = rs;
    }

    public HashMap<RSWPlayer.PlayerStatistics, List<Achievement>> achievements = new HashMap<>();

    public void loadAchievements() {
        int cats = 0, achi = 0;
        this.achievements.clear();
        //load coin achievements
        for (String dir : Achievements.file().getConfigurationSection("Coins").getKeys(false)) {
            ++cats;
            RSWPlayer.PlayerStatistics t = null;

            switch (dir) {
                case "Kills":
                    t = RSWPlayer.PlayerStatistics.KILLS;
                    break;
                case "Wins-Solo":
                    t = RSWPlayer.PlayerStatistics.WINS_SOLO;
                    break;
                case "Wins-Teams":
                    t = RSWPlayer.PlayerStatistics.WINS_TEAMS;
                    break;
                case "Games-Played":
                    t = RSWPlayer.PlayerStatistics.GAMES_PLAYED;
                    break;
            }

            List<Achievement> achiv = new ArrayList<>();

            String path = "Coins." + dir;
            for (String meta : Achievements.file().getConfigurationSection(path).getKeys(false)) {
                ++achi;
                Double value = Achievements.file().getDouble(path + "." + meta);
                achiv.add(new AchievementRCoin(t, Integer.parseInt(meta), value));
            }

            this.achievements.put(t, achiv);
        }

        RealSkywars.getPlugin().log("Loaded " + achi + " rewards for " + cats + " coin categories.");
    }

    public List<Achievement> getAchievements(RSWPlayer.PlayerStatistics ds) {
        return this.achievements.get(ds);
    }

    public Achievement getAchievement(RSWPlayer.PlayerStatistics ps, int meta) {
        List<Achievement> list = this.achievements.get(ps);
        if (list != null) {
            Optional<Achievement> o = list.stream().filter(c -> c.getGoal() == meta).findFirst();
            if (o.isPresent()) {
                return o.get();
            }
        }
        return null;
    }

    public ItemStack getItem(Achievement s, UUID uuid) {
        RSWPlayer p = rs.getPlayerManager().getPlayer(uuid);

        return Itens.createItemLore(getColor(s, p), 1, "&b&l" + s.getGoal(), Collections.singletonList("&aReward: &e" + s.getReward() + " coins"));
    }

    private Material getColor(Achievement s, RSWPlayer p) {
        return ((int) p.getStatistics(s.getType(), false)) >= s.getGoal() ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;
    }
}
