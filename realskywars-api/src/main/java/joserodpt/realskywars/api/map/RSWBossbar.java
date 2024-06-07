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
        this.bossBar = Bukkit.createBossBar(TranslatableLine.BOSSBAR_ARENA_WAIT.get(), BarColor.WHITE, BarStyle.SOLID);
    }

    public void tick() {
        if (this.bossBar == null) {
            return;
        }

        this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_RUNTIME.get().replace("%time%", Text.formatSeconds(map.getMapTimer().getSecondsLeft())));
        double div = (double) map.getMapTimer().getSecondsLeft() / (double) map.getMaxTime();
        this.bossBar.setProgress(div);
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
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_WAIT.get());
                this.bossBar.setProgress(0D);
                break;
            case STARTING:
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_STARTING.get().replace("%time%", Text.formatSeconds(map.getMapTimer().getSecondsLeft())));
                double div = (double) map.getMapTimer().getSecondsLeft() / (double) RSWConfig.file().getInt("Config.Time-To-Start");
                this.bossBar.setProgress(div);
                break;
            case FINISHING:
                this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_END.get());
                this.bossBar.setProgress(0);
                this.bossBar.setColor(BarColor.BLUE);
                break;
            case RESETTING:
                this.bossBar.removeAll();
                break;
        }
    }

    public void setProgress(double div) {
        if (this.bossBar == null) {
            return;
        }
        this.bossBar.setProgress(div);
    }

    public void setDeathmatch() {
        if (this.bossBar == null) {
            return;
        }
        this.bossBar.setTitle(TranslatableLine.BOSSBAR_ARENA_DEATHMATCH.get());
        this.bossBar.setColor(BarColor.RED);
        this.bossBar.setProgress(0);
    }
}
