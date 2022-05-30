package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.util.RecipeBuilder;

public class HorseraceBuildCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.build", true)) return true;
		
		Player p = (Player) sender;
		p.getInventory().addItem(RecipeBuilder.getItems());
		
		return false;
	}

}
