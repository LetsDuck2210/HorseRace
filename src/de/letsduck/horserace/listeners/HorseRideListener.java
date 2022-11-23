package de.letsduck.horserace.listeners;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.letsduck.horserace.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class HorseRideListener implements Listener {
	public static int yBase;
	public static final double SPEED_BASE = 7 / 35.0;
	
	@EventHandler
	public void onPlayerTryLeave(VehicleExitEvent event) {
		if(!Main.isEnabled) return;
		if(!(event.getVehicle() instanceof Horse)) return;
		
		Horse horse = (Horse) event.getVehicle();
		
		if(!horse.customName().examinableName().equals(Main.HORSE_NAME)) return; // TODO (examinableName)
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onHorsePassFlag(PlayerMoveEvent event) {
		if(!Main.isEnabled) return;
		
		if(event.getPlayer().getVehicle() == null) return;
		if(!(event.getPlayer().getVehicle() instanceof Horse)) return;
		
		Horse horse = (Horse) event.getPlayer().getVehicle();
		
		if(!horse.customName().examinableName().equals(Main.HORSE_NAME)) return; // TODO (examinableName)
		
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
				event // TODO (title)
					.getPlayer()
					.showTitle(
						Title.title(
							Component.text(
								"§aRunde " + round
							),
							Component.text(
								"§2" + (ticks / 20.0) + "s"
							),
							Times.times(
								Duration.ofMillis(250),
								Duration.ofSeconds(1),
								Duration.ofMillis(250)
							)
						)
					);
//				event.getPlayer().sendTitle("�aRunde " + round, "�2" + (ticks / 20.0) + "s", 5, 20, 5);
				
				if(round >= track.getLaps()) {
					int ticksTotal = 0;
					for(int time : finishLine.getStopWatch().getTimes(horse))
						ticksTotal += time;
					event // TODO (title)
						.getPlayer()
						.showTitle(
							Title.title(
								Component.text(
									"§a" + (track.getInitialCompeting() - track.getCompeting().size()) + ". Platz"
								), 
								Component.text("§2" + (ticksTotal / 20.0) + "s"),
								Times.times(
									Duration.ofMillis(250), 
									Duration.ofSeconds(1), 
									Duration.ofMillis(250)
								)
							)
						); 
//					event.getPlayer().sendTitle("�a" + (track.getInitialCompeting() - track.getCompeting().size() + 1) + ". Platz", "�2" + (ticksTotal / 20.0) + "s", 5, 20, 5); 
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
