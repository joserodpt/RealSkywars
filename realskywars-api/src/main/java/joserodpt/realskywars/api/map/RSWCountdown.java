package joserodpt.realskywars.api.map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * A simple countdown timer using the Runnable interface in seconds! <b>Great
 * for minigames and other shiz?</b>
 * <p>
 * Project created by
 *
 * @author ExpDev
 */
public class RSWCountdown implements Runnable {

    // Main class for bukkit scheduling
    private final JavaPlugin plugin;
    // Seconds and shiz
    private final int seconds;
    // Actions to perform while counting down, before and after
    private final Consumer<RSWCountdown> everySecond;
    private final Runnable beforeTimer;
    private final Runnable afterTimer;
    // Our scheduled task's assigned id, needed for canceling
    private Integer assignedTaskId;
    private int secondsLeft;

    // Construct a timer, you could create multiple so for example if
    // you do not want these "actions"
    public RSWCountdown(JavaPlugin plugin, int seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<RSWCountdown> everySecond) {
        // Initializing fields
        this.plugin = plugin;

        this.seconds = seconds;
        this.secondsLeft = seconds;

        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    /**
     * Runs the timer once, decrements seconds etc... Really wish we could make it
     * protected/private so you couldn't access it
     */
    @Override
    public void run() {
        // Is the timer up?
        if (this.secondsLeft < 1) {
            // Do what was supposed to happen after the timer
            this.afterTimer.run();

            // Cancel timer
            if (this.assignedTaskId != null) Bukkit.getScheduler().cancelTask(this.assignedTaskId);
            return;
        }

        // Are we just starting?
        if (this.secondsLeft == this.seconds) this.beforeTimer.run();

        // Do what's supposed to happen every second
        this.everySecond.accept(this);

        // Decrement the seconds left
        --this.secondsLeft;
    }

    /**
     * Gets the total seconds this timer was set to run for
     *
     * @return Total seconds timer should run
     */
    public int getTotalSeconds() {
        return this.seconds;
    }

    public int getPassedSeconds() {
        return this.getTotalSeconds() - this.secondsLeft;
    }

    /**
     * Gets the seconds left this timer should run
     *
     * @return Seconds left timer should run
     */
    public int getSecondsLeft() {
        return this.secondsLeft;
    }

    public void killTask() {
        Bukkit.getScheduler().cancelTask(this.assignedTaskId);
    }

    /**
     * Schedules this instance to "run" every second
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use so we can cancel
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }
}