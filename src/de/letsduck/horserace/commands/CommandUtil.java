package de.letsduck.horserace.commands;

import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.RaceTrack;

public class CommandUtil {
	public static boolean checkPermission(CommandSender sender, String permission, boolean checkIsPlayer) {
		if(!sender.hasPermission("HorseRace.spawnhorse")) {
			sender.sendMessage("§cYou don't have the required Permissions to use this command!");
			return false;
		}
		if(checkIsPlayer)
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cYou gotta be a player to use this command!");
				return false;
			}
		
		return true;
	}
	public static RaceTrack checkTrack(Player p) {
		var trackReference = new AtomicReference<RaceTrack>();
		
		if(!Main.raceTracks.containsKey(p)) {
			Main.raceTracks.forEach((player, track) -> {
				if(track.getBuilders().contains(p))
					trackReference.set(track);
			});
			if(trackReference.get() == null)
				p.sendMessage("§cYou haven't created a race track yet! Create one with §2/horserace-create§a!");
		} else
			trackReference.set(Main.raceTracks.get(p));
		
		return trackReference.get();
	}
}
