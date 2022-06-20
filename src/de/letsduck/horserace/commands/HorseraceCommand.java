package de.letsduck.horserace.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.gui.HorseraceGUI;

public class HorseraceCommand implements CommandExecutor {
	public static final HashMap<String, CommandExecutor> COMMANDS = new HashMap<>(); 

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!CommandUtil.checkPermission(sender, "Horserace.horserace", false))
			return true;
		
		if(args.length < 1) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cNur Spieler können diesen Befehl nutzen");
				return true;
			}
			Player p = (Player) sender;
			if(!Main.raceTracks.containsKey(p)) {
				p.openInventory(HorseraceGUI.getFor(p).get(HorseraceGUI.CREATE_TRACK_TITLE));
				return true;
			}
			p.openInventory(HorseraceGUI.getFor(p).get(HorseraceGUI.SETTINGS_TITLE));
			
			return true;
		}
		
		if(COMMANDS.containsKey(args[0]))
			return COMMANDS.get(args[0]).onCommand(sender, command, label, args);
		
		sender.sendMessage("§cUnbekannte Option: §6" + args[0]);
		
		return false;
	}

}
