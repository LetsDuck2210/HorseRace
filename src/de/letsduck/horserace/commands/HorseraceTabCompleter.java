package de.letsduck.horserace.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class HorseraceTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.horserace", true)) return new ArrayList<>();
		
		var list = new ArrayList<String>();
		if(args.length == 1)
			list.addAll(HorseraceCommand.COMMANDS.keySet());
		
		return list;
	}

}
