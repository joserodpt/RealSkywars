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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.config.TranslatableList;
import joserodpt.realskywars.api.database.PlayerGameHistoryRow;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.List;

import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.AVERAGE_KILLS;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.AVERAGE_TIME;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.GAMES;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.LONGEST_TIME;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.LOOSES;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.LOOSES_PERCENTAGE;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.MOST_KILLS;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.RANKED;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.RANKED_PERCENTAGE;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.SHORTEST_TIME;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.WINS;
import static joserodpt.realskywars.api.config.TranslatableList.TranslatableListPlaceholder.WINS_PERCENTAGE;

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

        shortestTime = logs.isEmpty() ? 0 : stats.getMin();
        longestTime = logs.isEmpty() ? 0 : stats.getMax();
        averageTime = logs.isEmpty() ? 0 : (int) stats.getAverage();
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

    public ItemStack getItem(RSWPlayer rswp) {
        return Itens.createItem(Material.BOOKSHELF, 1, TranslatableLine.STATISTIC_NAME.get(rswp), getLore(rswp));
    }

    private List<String> getLore(RSWPlayer rswp) {
        return TranslatableList.STATISTIC_GAMES_LIST
                .with(GAMES, getNumberGames())
                .with(WINS, getNumberWins())
                .with(WINS_PERCENTAGE, percentage(getNumberWins(), getNumberGames()))
                .with(LOOSES, getNumberLooses())
                .with(LOOSES_PERCENTAGE, percentage(getNumberLooses(), getNumberGames()))
                .with(RANKED, getNumberRanked())
                .with(RANKED_PERCENTAGE, percentage(getNumberRanked(), getNumberGames()))
                .with(AVERAGE_KILLS, getAverageKills())
                .with(MOST_KILLS, getMostKillsInAGame())
                .with(AVERAGE_TIME, Text.formatSeconds(getAverageTime()))
                .with(LONGEST_TIME, Text.formatSeconds(getLongestTime()))
                .with(SHORTEST_TIME, Text.formatSeconds(getShortestTime()))
                .get(rswp);
    }

    private String percentage(int small, int total) {
        if (total == 0) return " (0%)";
        return "(" + (int) ((small * 100.0) / total) + "%)";
    }
}
