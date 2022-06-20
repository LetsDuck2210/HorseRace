package de.letsduck.horserace.main;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.letsduck.horserace.commands.ClearConfigCommand;
import de.letsduck.horserace.commands.DeleteRacetrackCommand;
import de.letsduck.horserace.commands.HorseraceBuildCommand;
import de.letsduck.horserace.commands.HorseraceCommand;
import de.letsduck.horserace.commands.HorseraceCreateCommand;
import de.letsduck.horserace.commands.HorseraceTabCompleter;
import de.letsduck.horserace.commands.JoinHorseraceCommand;
import de.letsduck.horserace.commands.LookupRacetrackCommand;
import de.letsduck.horserace.commands.SpawnHorseCommand;
import de.letsduck.horserace.commands.ToggleHorseraceCommand;
import de.letsduck.horserace.listeners.ChatHandler;
import de.letsduck.horserace.listeners.HorseRideListener;
import de.letsduck.horserace.listeners.HorseraceBuilderListener;
import de.letsduck.horserace.util.RaceTrack;
import de.letsduck.horserace.util.RecipeBuilder;

public class Main extends JavaPlugin {
	public static boolean isEnabled;
	public static final float DEFAULT_SPEED = 7;
	public static final double SPEED_CHANGE = 0.01;
	public static final String HORSE_NAME = "Racehorse";
	
	public static final HashMap<Player, RaceTrack> raceTracks = new HashMap<>();
	public static final HashMap<Horse, RaceTrack> raceTrackOfHorse = new HashMap<>();
	public static final HashMap<String, RaceTrack> loadedTracks = new HashMap<>();
	
	private static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		info("---<: HorseRace-Plugin enabled :>---");
		
		getCommand("spawnhorse").setExecutor(new SpawnHorseCommand());
		getCommand("horserace").setExecutor(new HorseraceCommand());
		getCommand("horserace").setTabCompleter(new HorseraceTabCompleter());
		getCommand("clear-config").setExecutor(new ClearConfigCommand());
		
		// register commands to be available in horserace command and tab-completer
		HorseraceCommand.COMMANDS.put("create", new HorseraceCreateCommand());
		HorseraceCommand.COMMANDS.put("build", new HorseraceBuildCommand());
		HorseraceCommand.COMMANDS.put("lookup", new LookupRacetrackCommand());
		HorseraceCommand.COMMANDS.put("toggle", new ToggleHorseraceCommand());
		HorseraceCommand.COMMANDS.put("delete", new DeleteRacetrackCommand());
		HorseraceCommand.COMMANDS.put("join", new JoinHorseraceCommand());
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new HorseRideListener(), this);
		manager.registerEvents(new HorseraceBuilderListener(), this);
		manager.registerEvents(new ChatHandler(), this);
		
		RecipeBuilder.build();
		
		// load all racetracks saved in plugin.yml file
		loadedTracks.clear();
		loadedTracks.putAll(RaceTrack.load());
	}
	@Override
	public void onDisable() {
		// save each track
		raceTracks.forEach((player, track) -> track.save());
		loadedTracks.forEach((id, track) -> {
			if(!raceTracks.containsValue(track))
				track.save();
		});
		saveConfig();
	}
	public static void info(String message) {
		getPlugin().getLogger().log(Level.INFO, message);
	}
	public static void warn(String message) {
		getPlugin().getLogger().log(Level.WARNING, message);
	}
	
	// linear search, not efficient
	public static boolean existsTrackByID(String id) {
		for(var track : raceTracks.values()) {
			if(track.getID().equals(id))
				return true;
		}
		return false;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	// spawn horse with default stats
	public static Horse spawnHorse(Player p, float speed) {
		if(speed < 0 || speed > 10) {
			p.sendMessage("§cUngültige geschwindigkeit " + speed);
			return null;
		}
		
		Horse horse = (Horse) p.getWorld().spawnEntity(p.getLocation(), EntityType.HORSE);
		horse.setAI(false);
		horse.setTamed(true);
		horse.setJumpStrength(0.8);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.addPassenger(p);
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		horse.setInvulnerable(true);
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 35.0);
		horse.setCustomName(HORSE_NAME);
		return horse;
	}
	public static boolean checkPlayerHasTrack(Player p) {
		if(!Main.raceTracks.containsKey(p)) {
			p.sendMessage("§cDu hast noch keine Strecke (/horserace)");
			return false;
		}
		return true;
	}
}
