package de.letsduck.horserace.util.gui.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.RaceTrack;

public class CreateTrackAction extends Action {

	public CreateTrackAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		var track = new RaceTrack(LapHandlerAction.getFor(player, null).getLaps());
		track.addBuilder(player);
		track.save();
		Main.getPlugin().saveConfig();
		Main.raceTracks.put(player, track);
		player.sendMessage("§aRennstrecke erstellt: §2" + track.getID());
		player.closeInventory();
	}
}
