package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import de.letsduck.horserace.util.RaceTrack;

public class ToggleHorseraceCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.toggle", true)) return true;
		
		Player p = (Player) sender;
		
		if(!checkOnHorse(p)) return true;
		
		RaceTrack track;
		if((track = CommandUtil.checkTrack(p)) == null) return true;
		
		track.setup();
		
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
