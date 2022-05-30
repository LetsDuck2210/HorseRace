package de.letsduck.horserace.util.gui.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;

public class StartAction extends Action {

	public StartAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if (!Main.checkPlayerHasTrack(player))
			return;

		var track = Main.raceTracks.get(player);

	}

}
