package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.util.gui.HorseraceGUI;

public class HorseraceCreateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.create", true)) return true;
		
		Player p = (Player) sender;
		p.openInventory(HorseraceGUI.getFor(p).get(HorseraceGUI.CREATE_TRACK_TITLE));
		return false;
	}

}
