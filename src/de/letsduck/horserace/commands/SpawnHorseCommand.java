package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.main.Main;

public class SpawnHorseCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.spawn", true)) return true;
		
		Player p = (Player) sender;
		Main.spawnHorse(p, Main.DEFAULT_SPEED);
		
		return false;
	}

}
