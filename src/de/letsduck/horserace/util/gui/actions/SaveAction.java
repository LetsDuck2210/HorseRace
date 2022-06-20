package de.letsduck.horserace.util.gui.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;

public class SaveAction extends Action {
	private int cooldown;

	public SaveAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if (!Main.checkPlayerHasTrack(player))
			return;
		if(cooldown > 0)
			player.sendMessage("§6Bitte warte " + cooldown + " sekunden bevor du erneut speicherst");

		var track = Main.raceTracks.get(player);
		track.save();
		player.sendMessage("§aStrecke gespeichert");
		cooldown = 20;
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
			cooldown = 0;
		}, 20 * cooldown);
	}

}
