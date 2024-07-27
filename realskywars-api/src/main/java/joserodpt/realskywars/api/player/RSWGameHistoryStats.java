package joserodpt.realskywars.api.player;

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

import joserodpt.realskywars.api.database.PlayerGameHistoryRow;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.List;

public class RSWGameHistoryStats {

    protected int numberGames, numberWins, numerRanked, shortestTime, longestTime, averageTime, mostKillsInAGame;
    protected double med_kills;

    public RSWGameHistoryStats(Collection<PlayerGameHistoryRow> logs) {
        numberGames = logs.size();
        med_kills = logs.stream().mapToInt(PlayerGameHistoryRow::getKills).average().orElse(0);
        numberWins = (int) logs.stream().filter(PlayerGameHistoryRow::wasWin).count();
        numerRanked = (int) logs.stream().filter(PlayerGameHistoryRow::isRanked).count();
        mostKillsInAGame = logs.stream().mapToInt(PlayerGameHistoryRow::getKills).max().orElse(0);

        IntSummaryStatistics stats = logs.stream()
                .mapToInt(PlayerGameHistoryRow::getTime)
                .summaryStatistics();

        shortestTime = stats.getMin();
        longestTime = stats.getMax();
        averageTime = (int) stats.getAverage();
    }

    public RSWGameHistoryStats() {
    }

    public int getNumberWins() {
        return numberWins;
    }

    public int getNumberLooses() {
        return numberGames - numberWins;
    }

    public double getAverageKills() {
        return med_kills;
    }

    public int getNumberGames() {
        return numberGames;
    }

    public int getNumberRanked() {
        return numerRanked;
    }

    public int getAverageTime() {
        return averageTime;
    }

    public int getLongestTime() {
        return longestTime;
    }

    public int getShortestTime() {
        return shortestTime;
    }

    public int getMostKillsInAGame() {
        return mostKillsInAGame;
    }

    public ItemStack getItem(RSWPlayer rswp) { //TODO TRANSLATE
        return Itens.createItem(Material.BOOKSHELF, 1, "&bStats", getLore());
    }

    private List<String> getLore() {
        return List.of(
                "§bGames: §f" + getNumberGames(),
                "§7> Wins: §f" + getNumberWins() + percentage(getNumberWins(), getNumberGames()),
                "§7> Looses: §f" + getNumberLooses() + percentage(getNumberLooses(), getNumberGames()),
                "§7> Ranked: §f" + getNumberRanked() + percentage(getNumberRanked(), getNumberGames()),
                "§bTimes:",
                "§7> Average Kills: §f" + getAverageKills(),
                "§7> Most Kills in a Game: §f" + getMostKillsInAGame(),
                "§bTimes:",
                "§7> Average Time: §f" + Text.formatSeconds(getAverageTime()),
                "§7> Longest Time: §f" + Text.formatSeconds(getLongestTime()),
                "§7> Shortest Time: §f" + Text.formatSeconds(getShortestTime())
        );
    }

    private String percentage(int small, int total) {
        if (total == 0) return " (0%)";
        return " (" + (int) ((small * 100.0) / total) + "%)";
    }
}
