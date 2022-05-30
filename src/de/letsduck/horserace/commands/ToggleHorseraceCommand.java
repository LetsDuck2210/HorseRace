package de.letsduck.horserace.commands;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.RaceTrack;

public class ToggleHorseraceCommand implements CommandExecutor {
	private int task;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.toggle", true)) return true;
		
		Player p = (Player) sender;
		
		if(!checkOnHorse(p)) return true;
		
		RaceTrack track;
		if((track = CommandUtil.checkTrack(p)) == null) return true;
		
		Main.isEnabled = !Main.isEnabled;
		if(Main.isEnabled) {
			var horses = new ArrayList<Horse>();
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(!checkOnHorse(pl)) continue;
				
				horses.add((Horse) pl.getVehicle());
			}
			
			track.spawn(horses);
			
			final AtomicInteger countdown = new AtomicInteger(3);
			if(track.getCompeting().size() < 1) {
				Main.isEnabled = false;
				return true;
			}
			final double speed = track.getCompeting().get(0).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
			track.getCompeting().forEach((horse) -> horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0));
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
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
				
				track.start();
				Bukkit.getScheduler().cancelTask(task);
				task = 0;
				track.getCompeting().forEach((horse) -> {
					horse.getPassengers().forEach((entity) -> {
						if(entity instanceof Player player)
							player.sendTitle("§aRound 1", "§20.0s", 5, 20, 5);
					});
					horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
				});
			}, 20, 20);
		} else {
			sender.sendMessage("§aHorserace is now disabled");
			Main.raceTrackOfHorse.remove((Horse) p.getVehicle());
		}
		
		return false;
	}
	
	public boolean checkOnHorse(Player p) {
		if(p.getVehicle() == null) {
			p.sendMessage("§cGet on your horse!");
			return false;
		}
		if(!(p.getVehicle() instanceof Horse)) {
			p.sendMessage("§cGet on your horse!");
			return false;
		}
		if(!p.getVehicle().getCustomName().equals("Racehorse")) {
			p.sendMessage("§cGet on your racehorse!");
			return false;
		}
		return true;
	}

}
