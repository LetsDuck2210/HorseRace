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
		
		player.sendMessage("§a§lType new name in chat, you have " + timeout + " seconds...");
		player.closeInventory();
		ChatHandler.register(player, (message) -> {
			player.sendMessage("§2" + message);
			if(track.setID(message))
				player.sendMessage("§a§lTrack renamed");
			else
				player.sendMessage("§c§lThis name already exists");
			
			return true;
		});
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), () -> {
			if(ChatHandler.remove(player) != null)
				player.sendMessage("§c§lTook too long, rename cancelled");
		}, timeout * 20);
	}
}
