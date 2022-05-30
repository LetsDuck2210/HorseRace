package de.letsduck.horserace.util.gui.actions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Action {
	protected Player player;
	
	public Action(Player player) {
		this.player = player;
	}
	
	public abstract void clicked(Inventory inv, ItemStack item, String name);
	
	public Player getPlayer() {
		return player;
	}
}
