package de.letsduck.horserace.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import de.letsduck.horserace.listeners.HorseRideListener;
import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.gui.actions.LapHandlerAction;

public class RaceTrack {
	// serialization required:
	private List<Location> startPoints;
	private FinishLine finishLine;
	private int laps;
	private String id;
	private List<Player> builders;
	
	// no serialization required:
	private int taskID;
	private List<Horse> competing;
	public HashMap<Horse, Integer> passedFlags;
	
	public RaceTrack(int laps) {
		finishLine = new FinishLine();
		competing = new ArrayList<>();
		passedFlags = new HashMap<>();
		startPoints = new ArrayList<>();
		builders = new ArrayList<>();
		this.laps = laps;
		id = Integer.toHexString(hashCode());
	}
	public void addStartingPoint(Location loc) {
		if(!startPoints.contains(loc))
			startPoints.add(loc);
	}
	public void spawn(List<Horse> horses) {
		if(horses.size() > startPoints.size()) {
			horses.forEach((horse) -> horse.getPassengers().forEach((entity) -> entity.sendMessage("§cNot enough start points!")));
			return;
		}
		
		competing.clear();
		competing.addAll(horses);
		
		for(int i = 0; i < horses.size(); i++) {
			var passengers = horses.get(i).getPassengers();
			
			horses.get(i).setCustomName("teleporting...");
			horses.get(i).eject();
			horses.get(i).teleport(startPoints.get(i).clone().add(0.5, 0, 0.5));
			horses.get(i).setCustomName(Main.HORSE_NAME);
			
			final int j = i;
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
				passengers.forEach(horses.get(j)::addPassenger);
			}, 5);
			Main.raceTrackOfHorse.put(horses.get(i), this);
		}
		int y = 0;
		int i = 0;
		for(Location l : finishLine.getLine()) {
			y += l.getY();
			i++;
		}
		y /= i;
		HorseRideListener.yBase = y;
		System.out.println(y);
	}
	public List<Player> getBuilders() {
		return builders;
	}
	public void addBuilder(Player p) {
		if(!builders.contains(p))
			builders.add(p);
	}
	public void removeBuilder(Player p) {
		builders.remove(p);
	}
	public int start() {
		taskID = finishLine.start(competing);
		return taskID;
	}
	public int getLaps() {
		return laps;
	}
	public int getTaskID() {
		return taskID;
	}
	public List<Location> getStartPoints() {
		return startPoints;
	}
	public List<Horse> getCompeting() {
		return competing;
	}
	public FinishLine getFinishLine() {
		return finishLine;
	}
	public String getID() {
		return id;
	}
	public void setLaps(int laps) {
		this.laps = laps;
	}
	public boolean setID(String id) {
		FileConfiguration con = Main.getPlugin().getConfig();
		if((con.contains("RaceTracks." + id) || Main.existsTrackByID(id)) && !id.equals(this.id))
			return false;
		
		var section = con.get("RaceTracks." + this.id); // get section of current id
		con.set("RaceTracks." + this.id, null);			// remove section of current id
		con.set("RaceTracks." + id, section);			// set section to new id
		this.id = id;
		return true;
	}
	
	// clear all properties of the track, except for the id
	public void reset() {
		var owner = builders.get(0);
		startPoints.clear();
		finishLine.getLine().clear();
		
		builders.clear();
		builders.add(owner); // owner must remain
		
		laps = 0;
		competing.clear();
		passedFlags.clear();
	}
	// deletes all resources associated to this track
	public void delete() {
		var owner = builders.get(0);
		Main.raceTracks.remove(owner);
		Main.getPlugin().getConfig().set("RaceTracks." + getID(), null);
		Main.loadedTracks.remove(getID());
		Main.getPlugin().saveConfig();
		LapHandlerAction.delete(owner);
	}
	
	public void save() {
		FileConfiguration con = Main.getPlugin().getConfig();
		var section = con.createSection("RaceTracks." + id);
		section.set("rounds", laps);
		
		section.set("startPoints.size", startPoints.size());
		for(int i = 0; i < startPoints.size(); i++) {
			section.set("startPoints." + i, startPoints.get(i));
		}
		
		finishLine.save(section);
	}
	public static Map<String, RaceTrack> load() {
		var tracks = new HashMap<String, RaceTrack>();
		var section = Main.getPlugin().getConfig().getConfigurationSection("RaceTracks");
		
		if(section == null)
			return tracks;
		
		for(String value : section.getValues(false).keySet()) {
			var sec = section.getConfigurationSection(value);

			var track = new RaceTrack(sec.getInt("rounds"));
			track.id = value;
			for(int i = 0; i < sec.getInt("startPoints.size"); i++) {
				track.startPoints.add(sec.getLocation("startPoints." + i));
			}
			
			track.finishLine = FinishLine.load(sec);
			tracks.put(value, track);
		}
		
		return tracks;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(finishLine, laps, startPoints);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RaceTrack other = (RaceTrack) obj;
		return Objects.equals(finishLine, other.finishLine) && laps == other.laps
				&& Objects.equals(startPoints, other.startPoints);
	}
}
