package de.letsduck.horserace.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;

import de.letsduck.horserace.main.Main;

public class StopWatch {
	private Map<Horse, List<Integer>> times;
	private int totalTimePassed;
	
	public StopWatch() {
		times = new HashMap<>();
	}
	
	/**
	 * this is called to initialize a timer for the specified horse
	 * 
	 * @param horse	the horse which should be listed
	 * */
	public void init(Horse horse) {
		times.put(horse, new ArrayList<Integer>());
		times.get(horse).add(0);
	}
	/**
	 * used to start all timers
	 * 
	 * @return the task id of the bukkit scheduler task
	 * */
	public int start() {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			times.forEach((horse, list) -> {
				int index = list.size() - 1;
				list.set(index, list.get(index) + 1);
			});
			totalTimePassed++;
		}, 1, 1);
	}
	
	
	/**
	 * This should be called when a Horse passes the finish line in one of the first 2 rounds
	 * 
	 * @param the Horse that passed the finish line
	 * @return the time passed in the last round
	 * */
	public int round(Horse horse) {
		int ticks = times.get(horse).get(times.get(horse).size() - 1); 
		times.get(horse).add(0);
		
		return ticks;
	}
	
	public List<Integer> getTimes(Horse horse) {
		return times.get(horse);
	}
}
