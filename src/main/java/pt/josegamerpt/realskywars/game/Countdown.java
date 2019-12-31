package pt.josegamerpt.realskywars.game;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * A simple countdown timer using the Runnable interface in seconds! <b>Great
 * for minigames and other shiz?</b>
 *
 * Project created by
 * 
 * @author ExpDev
 */
public class Countdown implements Runnable {

	// Main class for bukkit scheduling
	private JavaPlugin plugin;

	// Our scheduled task's assigned id, needed for canceling
	private Integer assignedTaskId;

	// Seconds and shiz
	private int seconds;
	private int secondsLeft;

	// Actions to perform while counting down, before and after
	private Consumer<Countdown> everySecond;
	private Runnable beforeTimer;
	private Runnable afterTimer;

	// Construct a timer, you could create multiple so for example if
	// you do not want these "actions"
	public Countdown(JavaPlugin plugin, int seconds, Runnable beforeTimer, Runnable afterTimer,
			Consumer<Countdown> everySecond) {
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
		if (secondsLeft < 1) {
			// Do what was supposed to happen after the timer
			afterTimer.run();

			// Cancel timer
			if (assignedTaskId != null)
				Bukkit.getScheduler().cancelTask(assignedTaskId);
			return;
		}

		// Are we just starting?
		if (secondsLeft == seconds)
			beforeTimer.run();

		// Do what's supposed to happen every second
		everySecond.accept(this);

		// Decrement the seconds left
		secondsLeft--;
	}

	/**
	 * Gets the total seconds this timer was set to run for
	 *
	 * @return Total seconds timer should run
	 */
	public int getTotalSeconds() {
		return seconds;
	}

	/**
	 * Gets the seconds left this timer should run
	 *
	 * @return Seconds left timer should run
	 */
	public int getSecondsLeft() {
		return secondsLeft;
	}

	public Integer getTaskId() {
		return assignedTaskId;
	}

	public void killTask() {
		Bukkit.getScheduler().cancelTask(assignedTaskId);
	}

	/**
	 * Schedules this instance to "run" every second
	 */
	public void scheduleTimer() {
		// Initialize our assigned task's id, for later use so we can cancel
		this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
	}
}