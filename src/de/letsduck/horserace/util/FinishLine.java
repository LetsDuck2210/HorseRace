package de.letsduck.horserace.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Horse;

public class FinishLine {
	// serialization required:
	private List<List<Location>> flags;
	private List<Location> line;
	
	// no serialization required:
	private StopWatch stopWatch;
	
	public FinishLine() {
		line = new ArrayList<>();
		flags = new ArrayList<>();
		stopWatch = new StopWatch();
	}
	public int start(List<Horse> horses) {
		horses.forEach(stopWatch::init);
		
		return stopWatch.start();
	}
	public boolean checkPassed(Horse horse) {
		AtomicBoolean passed = new AtomicBoolean(false);
		
		if(checkPassedFlag(line.toArray(new Location[0]), horse))
			passed.set(true);
		
		return passed.get();
	}
	public boolean checkPassedFlag(Location[] locations, Horse horse) {
		for(Location loc : locations) {
			Location l0 = horse.getLocation().clone(),
					 l1 = loc.clone();
			l0.setY(0);
			l1.setY(0);
			
			if(l0.distance(l1) <= 1) return true;
		}
		
		return false;
	}
	public void addFinishLineBlock(Location loc) {
		if(!line.contains(loc))
			line.add(loc);
	}
	public Location[] getFlag(int index) {
		return flags.get(index).toArray(new Location[0]);
	}
	public List<List<Location>> getFlags() {
		return flags;
	}
	public List<Location> getLine() {
		return line;
	}
	public StopWatch getStopWatch() {
		return stopWatch;
	}
	public void addFlag(int index, Location loc) {
		if(flags.size() < index) flags.add(new ArrayList<Location>());
		
		flags.get(index).add(loc);
	}
	
	
	public void save(ConfigurationSection sec) {
		var section = sec.createSection("FinishLine");
		section.set("flags.size", flags.size());
		for(int i = 0; i < flags.size(); i++) {
			section.set("flags." + i + ".size", flags.get(i).size());
			for(int j = 0; j < flags.get(i).size(); j++) {
				section.set("flags." + i + "." + j, flags.get(i).get(j));
			}
		}
		
		section.set("line.size", line.size());
		for(int i = 0; i < line.size(); i++) {
			section.set("line." + i, line.get(i));
		}
	}
	public static FinishLine load(ConfigurationSection sec) {
		var section = sec.getConfigurationSection("FinishLine");
		FinishLine finishLine = new FinishLine();
		if(section == null)
			return finishLine;
		
		for(int i = 0; i < section.getInt("flags.size"); i++) {
			var flags = new ArrayList<Location>();
			for(int j = 0; j < section.getInt("flags." + i + ".size"); j++) {
				flags.add(section.getLocation("flags." + i + "." + j));
			}
			finishLine.flags.add(flags);
		}
		for(int i = 0; i < section.getInt("line.size"); i++) {
			finishLine.line.add(section.getLocation("line." + i));
		}
		
		return finishLine;
	}
}
