package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.main.Main;

public class LookupRacetrackCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.lookup", true)) return true;
		
		if(args.length != 2) {
			sender.sendMessage("§cPlease use §6/horserace lookup <id>§c!");
			return true;
		}
		
		Player p = (Player) sender;
		if(Main.loadedTracks.containsKey(args[1])) {
			p.sendMessage("§aYour track was found!");
			Main.raceTracks.put(p, Main.loadedTracks.get(args[1]));
			return false;
		}
		
		p.sendMessage("§cYour track was not found!");
		return false;
	}
}
