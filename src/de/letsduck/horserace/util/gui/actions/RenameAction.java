package de.letsduck.horserace.util.gui.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.listeners.ChatHandler;
import de.letsduck.horserace.main.Main;

public class RenameAction extends Action {
	public RenameAction(Player player) {
		super(player);
	}

	// asks the user to input a new name
	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if (!Main.checkPlayerHasTrack(player))
			return;

		final var timeout = 20;
		final var track = Main.raceTracks.get(player);
		
		player.sendMessage("§a§lSchreibe den neuen Name in den Chat innerhalb von " + timeout + " sekunden...");
		player.closeInventory();
		ChatHandler.register(player, (message) -> {
			player.sendMessage("§2" + message);
			if(track.setID(message))
				player.sendMessage("§a§lStrecke umbenannt");
			else
				player.sendMessage("§c§lDieser name existiert bereits");
			
			return true;
		});
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), () -> {
			if(ChatHandler.remove(player) != null)
				player.sendMessage("§c§lZu lange gebraucht, umbenennen abgebrochen");
		}, timeout * 20);
	}
}
