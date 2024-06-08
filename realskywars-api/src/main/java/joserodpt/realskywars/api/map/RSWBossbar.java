package joserodpt.realskywars.api.map;

import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class RSWBossbar {

    private final RSWMap map;
    private BossBar bossBar;

    public RSWBossbar(RSWMap map) {
        this.map = map;
        reset();
    }

    public void reset() {
        this.bossBar = Bukkit.createBossBar(TranslatableLine.BOSSBAR_ARENA_WAIT.getSingle(), BarColor.WHITE, BarStyle.SOLID);
    }

    public void tick() {
        if (this.bossBar == null) {
            return;
        }

        switch (map.getState()) {
            case PLAYING:
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_RUNTIME.getSingle().replace("%time%", Text.formatSeconds(map.getMapTimer().getSecondsLeft())));
                double div = (double) map.getMapTimer().getSecondsLeft() / (double) map.getMaxTime();
                this.bossBar.setProgress(div);
                break;
            case FINISHING:
                double div2 = (double) map.getFinishingTimer().getSecondsLeft() / (double) RSWConfig.file().getInt("Config.Time-EndGame");
                this.bossBar.setProgress(div2);
                break;
        }
    }

    public void addPlayer(Player player) {
        if (player != null && this.bossBar != null) {
            this.bossBar.addPlayer(player);
        }
    }

    public void removePlayer(Player player) {
        if (player != null && this.bossBar != null) {
            this.bossBar.removePlayer(player);
        }
    }

    public void setState(RSWMap.MapState w) {
        if (this.bossBar == null) {
            return;
        }

        switch (w) {
            case WAITING:
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_WAIT.getSingle());
                this.bossBar.setProgress(0D);
                break;
            case STARTING:
                int time = map.getStartMapTimer() == null ? 0 : map.getStartMapTimer().getSecondsLeft();
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_STARTING.getSingle().replace("%time%", Text.formatSeconds(time)));
                double div = (double) time / (double) RSWConfig.file().getInt("Config.Time-To-Start");
                if (div <= 1 && div >= 0) {
                    this.bossBar.setProgress(div);
                }
                break;
            case FINISHING:
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_END.getSingle());
                this.bossBar.setColor(BarColor.BLUE);

                int time2 = map.getFinishingTimer() == null ? 0 : map.getFinishingTimer().getSecondsLeft();
                double div2 = (double) time2 / (double) RSWConfig.file().getInt("Config.Time-EndGame");
                if (div2 <= 1 && div2 >= 0) {
                    this.bossBar.setProgress(div2);
                }
                break;
            case RESETTING:
                this.bossBar.removeAll();
                break;
        }
    }

    public void setDeathmatch() {
        if (this.bossBar == null) {
            return;
        }
        this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_DEATHMATCH.getSingle());
        this.bossBar.setColor(BarColor.RED);
        this.bossBar.setProgress(0);
    }
}
