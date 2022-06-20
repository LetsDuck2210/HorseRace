package de.letsduck.horserace.util.gui.actions;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.gui.HorseraceGUI;

public class LapHandlerAction extends Action {
	private static HashMap<Player, LapHandlerAction> handlers = new HashMap<>();
	private int laps = 0;
	private ItemStack item;

	private LapHandlerAction(Player player) {
		this(player, null);
	}
	private LapHandlerAction(Player player, ItemStack item) {
		super(player);
		this.item = item;
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if(name.startsWith("§2Runden erhöhen um "))
			laps += Integer.parseInt(name.substring(20));
		else if(name.startsWith("§6Runden verringern um "))
			laps -= Integer.parseInt(name.substring(23));
		
		// search the referenced item in the given inventory
		AtomicReference<ItemStack> itemRef = new AtomicReference<>();
		final String itemName = HorseraceGUI.getDisplayName(this.item);
		inv.forEach((itm) -> {
			String n = HorseraceGUI.getDisplayName(itm);
			if(n == null) return;
			if(itemName.equals(n) && itm.getType() == this.item.getType())
				itemRef.set(itm);
		});
		
		// update display name of the searched item
		var meta = itemRef.get().getItemMeta();
		meta.setLore(List.of("§r§fRunden: " + laps));
		itemRef.get().setItemMeta(meta);
	}
	public int getLaps() {
		return laps;
	}
	public void reset() {
		laps = 0;
	}
	
	// get or create the laphandler for a player or update the item 
	public static LapHandlerAction getFor(Player p, ItemStack item) {
		if(!handlers.containsKey(p))
			if(item != null)
				handlers.put(p, new LapHandlerAction(p));
			else
				Main.warn("Item can't be null");
		if(item != null)
			handlers.get(p).item = item;
		return handlers.get(p);
	}
	public static void delete(Player p) {
		var handler = handlers.get(p);
		
		var meta = handler.item.getItemMeta();
		meta.setLore(List.of("§r§fRunden: 0"));
		handler.item.setItemMeta(meta);
		handlers.remove(p);
		HorseraceGUI.getFor(p).makeCreateInventory();
	}
}