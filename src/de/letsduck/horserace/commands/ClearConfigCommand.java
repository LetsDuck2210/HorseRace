package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.letsduck.horserace.main.Main;

public class ClearConfigCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.clearconfig", false)) return true;
		
		Main.getPlugin().getConfig().set("RaceTracks", null);
		Main.getPlugin().saveConfig();
		sender.sendMessage("§2Config has been cleared!");
		
		return false;
	}

}
