package de.letsduck.horserace.listeners;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.letsduck.horserace.main.Main;

public class HorseRideListener implements Listener {
	public static int yBase;
	public static final double SPEED_BASE = 7 / 35.0;
	
	@EventHandler
	public void onPlayerTryLeave(VehicleExitEvent event) {
		if(!Main.isEnabled) return;
		if(!(event.getVehicle() instanceof Horse)) return;
		
		Horse horse = (Horse) event.getVehicle();
		
		if(!horse.getCustomName().equals(Main.HORSE_NAME)) return;
		
		event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onHorseChangeY(PlayerMoveEvent event) {
		if(!Main.isEnabled) return;
		
		if(event.getPlayer().getVehicle() == null) return;
		if(!(event.getPlayer().getVehicle() instanceof Horse)) return;
		
		Horse horse = (Horse) event.getPlayer().getVehicle();
		
		if(!horse.getCustomName().equals(Main.HORSE_NAME)) return;

		yChange(horse, event.getTo().getY());
	}
	@EventHandler
	public void onHorseChangeYTeleport(PlayerTeleportEvent event) {
		if(!Main.isEnabled) return;
		
		if(event.getPlayer().getVehicle() == null) return;
		if(!(event.getPlayer().getVehicle() instanceof Horse)) return;
		
		Horse horse = (Horse) event.getPlayer().getVehicle();
		
		if(!horse.getCustomName().equals(Main.HORSE_NAME)) return;
		
		yChange(horse, event.getTo().getY());
	}
	private void yChange(Horse horse, double to) {
		double yDis = to - yBase;
		
		yDis = Math.min(15, yDis);
		yDis = Math.max(-6, yDis);
		
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(SPEED_BASE - yDis * Main.SPEED_CHANGE);
	}
	
	
	@EventHandler
	public void onHorsePassFlag(PlayerMoveEvent event) {
		if(!Main.isEnabled) return;
		
		if(event.getPlayer().getVehicle() == null) return;
		if(!(event.getPlayer().getVehicle() instanceof Horse)) return;
		
		Horse horse = (Horse) event.getPlayer().getVehicle();
		
		if(!horse.getCustomName().equals(Main.HORSE_NAME)) return;
		
		if(!Main.raceTrackOfHorse.containsKey(horse)) return;
		
		var track = Main.raceTrackOfHorse.get(horse);
		var finishLine = track.getFinishLine();
		
		if(!track.passedFlags.containsKey(horse)) {
			track.passedFlags.put(horse, 0);
			return;
		}
		int passed = track.passedFlags.get(horse);
		if(passed < finishLine.getFlags().size()) {
			var flags = finishLine.getFlag(track.passedFlags.get(horse));
			if(finishLine.checkPassedFlag(flags, horse))
				track.passedFlags.put(horse, passed + 1);
		}
		
		if(finishLine.checkPassed(horse)) {
			if(passed >= finishLine.getFlags().size()) {
				int ticks = finishLine.getStopWatch().round(horse);
				
				int round = finishLine.getStopWatch().getTimes(horse).size();
				event.getPlayer().sendTitle("§aRunde " + round, "§2" + (ticks / 20.0) + "s", 5, 20, 5);
				
				if(round >= track.getLaps()) {
					int ticksTotal = 0;
					for(int time : finishLine.getStopWatch().getTimes(horse))
						ticksTotal += time;
					event.getPlayer().sendTitle("§a" + (track.getInitialCompeting() - track.getCompeting().size() + 1) + ". Platz", "§2" + (ticksTotal / 20.0) + "s", 5, 20, 5);
					Main.raceTrackOfHorse.remove(horse);
					
					track.getCompeting().remove(horse);
					if(track.getCompeting().size() <= 0) {
						Bukkit.getScheduler().cancelTask(track.getTaskID());
						Main.isEnabled = false;
					}
				}
			}
			
			track.passedFlags.put(horse, 0);
		}
	}
}
