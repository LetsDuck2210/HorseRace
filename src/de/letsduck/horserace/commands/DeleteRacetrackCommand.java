package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.letsduck.horserace.main.Main;

public class DeleteRacetrackCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!CommandUtil.checkPermission(sender, "Horserace.delete", false))
			return true;

		if (args.length != 2) {
			sender.sendMessage("§cBitte nutze §6/horserace delete <id>§c!");
			return true;
		}

		if (Main.getPlugin().getConfig().contains("RaceTracks." + args[1])) {
			Main.getPlugin().getConfig().set("RaceTracks." + args[1], null);
			sender.sendMessage("§aStrecke gelöscht");
			return false;
		}
		sender.sendMessage("§6Strecke nicht gefunden");

		return false;
	}

}
