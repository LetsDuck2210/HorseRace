package de.letsduck.horserace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.letsduck.horserace.util.RaceTrack;

public class JoinHorseraceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2) {
			sender.sendMessage("§cBitte nutze §6/horserace join <track-id>§c!");
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cNur Spieler können diesen Befehl nutzen");
			return true;
		}
		
		var track = RaceTrack.queuedTracks.get(args[1]);
		var p = (Player) sender;
		if(track == null) {
			p.sendMessage("§cStrecke nicht gefunden");
			return true;
		}
		if(track.addCompetingPlayer(p))
			p.sendMessage("§aRennen beigetreten!");
		else
			p.sendMessage("§6Rennen voll!");
		
		return false;
	}

}
