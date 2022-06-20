package de.letsduck.horserace.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import de.letsduck.horserace.listeners.HorseRideListener;
import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.gui.actions.LapHandlerAction;

public class RaceTrack {
	public static Map<String, RaceTrack> queuedTracks = new HashMap<>();
	
	// serialization required:
	private List<Location> startPoints;
	private FinishLine finishLine;
	private int laps;
	private String id;
	private List<Player> builders;
	
	// no serialization required:
	private int taskID;
	private List<Horse> competingHorses;
	private List<Player> competingPlayers;
	private int initialCompeting;
	public HashMap<Horse, Integer> passedFlags;
	
	public RaceTrack(int laps) {
		finishLine = new FinishLine();
		competingHorses = new ArrayList<>();
		competingPlayers = new ArrayList<>();
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
		if(horses != competingHorses) {
			competingHorses.clear();
			competingHorses.addAll(horses);
		}
		initialCompeting = horses.size();
		
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
	public boolean addCompetingPlayer(Player p) {
		if(competingPlayers.size() < startPoints.size()
				&& !competingPlayers.contains(p)) {
			competingPlayers.add(p);
			return true;
		}
		return false;
	}
	public void removeCompetingPlayer(Player p) {
		competingPlayers.remove(p);
	}
	private int countdownTask = 0;
	public void setup() {
		competingPlayers.forEach(p -> {
			Horse h;
			// check if not riding horse, if so, spawn one
			if(!p.isInsideVehicle() || !(p.getVehicle() instanceof Horse)) {
				h = Main.spawnHorse(p, Main.DEFAULT_SPEED);
			} else
				h = (Horse) p.getVehicle();
			
			Main.info("" + h);
			competingHorses.add(h);
		});
		Main.isEnabled = !Main.isEnabled;
		if(Main.isEnabled) {
			
			spawn(competingHorses);
			
			final AtomicInteger countdown = new AtomicInteger(3);
			if(getCompeting().size() < 1) {
				Main.isEnabled = false;
				return;
			}
			final double speed = getCompeting().get(0).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
			getCompeting().forEach((horse) -> horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0));
			countdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
				if(countdown.get() > 0) {
					if(countdown.get() == 3)
						Bukkit.getOnlinePlayers().forEach((player) -> player.sendTitle("§a3", "", 5, 20, 5));
					else if(countdown.get() == 2)
						Bukkit.getOnlinePlayers().forEach((player) -> player.sendTitle("§62", "", 5, 20, 5));
					else if(countdown.get() == 1)
						Bukkit.getOnlinePlayers().forEach((player) -> player.sendTitle("§c1", "", 5, 20, 5));
					
					countdown.getAndDecrement();
					return;
				}
				
				start();
				Bukkit.getScheduler().cancelTask(countdownTask);
				countdownTask = 0;
				getCompeting().forEach((horse) -> {
					horse.getPassengers().forEach((entity) -> {
						if(entity instanceof Player player)
							player.sendTitle("§aRunde 1", "§20.0s", 5, 20, 5);
					});
					horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
				});
			}, 20, 20);
		}
	}
	public int start() {
		queuedTracks.remove(getID());
		return taskID = finishLine.start(competingHorses);
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
	public int getInitialCompeting() {
		return initialCompeting;
	}
	public List<Horse> getCompeting() {
		return competingHorses;
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
		competingHorses.clear();
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
