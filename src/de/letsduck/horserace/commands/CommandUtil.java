package de.letsduck.horserace.commands;

import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.RaceTrack;

public class CommandUtil {
	public static boolean checkPermission(CommandSender sender, String permission, boolean checkIsPlayer) {
		if(!sender.hasPermission("HorseRace.spawnhorse")) {
			sender.sendMessage("§cDu hast keine Rechte diesen Befehl zu nutzen");
			return false;
		}
		if(checkIsPlayer)
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cNur Spieler können diesen Befehl nutzen");
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
				p.sendMessage("§cDu hast noch keine Strecke (/horserace)");
		} else
			trackReference.set(Main.raceTracks.get(p));
		
		return trackReference.get();
	}
}
