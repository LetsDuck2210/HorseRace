package de.letsduck.horserace.util.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.ItemBuilder;
import de.letsduck.horserace.util.gui.actions.Action;
import de.letsduck.horserace.util.gui.actions.BuildersAction;
import de.letsduck.horserace.util.gui.actions.ChangeLapsAction;
import de.letsduck.horserace.util.gui.actions.CreateTrackAction;
import de.letsduck.horserace.util.gui.actions.DeleteAction;
import de.letsduck.horserace.util.gui.actions.LapHandlerAction;
import de.letsduck.horserace.util.gui.actions.RenameAction;
import de.letsduck.horserace.util.gui.actions.ResetAction;
import de.letsduck.horserace.util.gui.actions.SaveAction;
import de.letsduck.horserace.util.gui.actions.StartAction;
import net.kyori.adventure.text.Component;

public class HorseraceGUI implements Listener {
	private static final HashMap<Player, HorseraceGUI> GUIS = new HashMap<>();
	
	// items for edit-inventory
	public static final ItemStack BUILDERS = new ItemBuilder(Material.PLAYER_HEAD).name("§aMitbauer").getItem(),
								  RESET = new ItemBuilder(Material.PAPER).name("§6Zurücksetzen").getItem(),
								  LAPS = new ItemBuilder(Material.IRON_HORSE_ARMOR).name(ChatColor.GREEN + "Runden").setLore(new ArrayList<>()).getItem(),
								  START = new ItemBuilder(Material.FIREWORK_ROCKET).name(ChatColor.BLUE + "Start").getItem(),
								  DELETE = new ItemBuilder(Material.LAVA_BUCKET).name("§cLöschen").getItem(),
								  RENAME = new ItemBuilder(Material.NAME_TAG).name("§aUmbenennen").getItem(),
								  SAVE = new ItemBuilder(Material.BOOK).name("§aSpeichern").getItem();
	
	// items for create-inventory
	public static final ItemStack CREATE = new ItemBuilder(Material.IRON_HORSE_ARMOR).name("§aErstellen").setLore(new ArrayList<>()).getItem(),
								  LAPS_INC_1 = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§2Runden erhöhen um 1").getItem(),
								  LAPS_DEC_1 = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name("§6Runden verringern um 1").getItem(),
								  LAPS_INC_10 = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§2Runden erhöhen um 10").getItem(),
								  LAPS_DEC_10 = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name("§6Runden verringern um 10").getItem();
	
	public static final String SETTINGS_TITLE = ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "Strecke";
	public static final String CREATE_TRACK_TITLE = ChatColor.DARK_PURPLE + "Strecke erstellen";
	private final Player p;
	private final HashMap<String, Action> actions;
	private final HashMap<String, Inventory> inventories;
	
	// intialize inventories and register events
	private HorseraceGUI(Player p) {
		this.p = p;
		actions = new HashMap<>();
		inventories = new HashMap<>();
		
		// inventory to edit the track
		var meta = LAPS.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		LAPS.setItemMeta(meta);
		var settings = Bukkit.createInventory(p, 9 * 3, Component.text(SETTINGS_TITLE));
		inventories.put(SETTINGS_TITLE, settings);
		item(settings, 2, BUILDERS, new BuildersAction(p));
		item(settings, 6, RESET, new ResetAction(p));
		item(settings, 10, LAPS, new ChangeLapsAction(p));
		item(settings, 13, START, new StartAction(p));
		item(settings, 16, DELETE, new DeleteAction(p));
		item(settings, 20, RENAME, new RenameAction(p));
		item(settings, 24, SAVE, new SaveAction(p));

		meta = CREATE.getItemMeta();
		var lore = new ArrayList<Component>();
		lore.add(Component.text("�r�fRunden: 0"));
		meta.lore(lore);
		CREATE.setItemMeta(meta);
		
		// inventory to create a track
		var create = Bukkit.createInventory(p, 9 * 3, Component.text(CREATE_TRACK_TITLE));
		inventories.put(CREATE_TRACK_TITLE, create);
		
		makeCreateInventory();
		item(create, 13, CREATE, new CreateTrackAction(p));
		
		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
	}
	public void makeCreateInventory() {
		Action lapHandler = LapHandlerAction.getFor(p, CREATE);
		var create = inventories.get(CREATE_TRACK_TITLE); 
		item(create, 9, LAPS_DEC_10, lapHandler);
		item(create, 11, LAPS_DEC_1, lapHandler);
		item(create, 15, LAPS_INC_1, lapHandler);
		item(create, 17, LAPS_INC_10, lapHandler);
	}
	
	// get inventory by name and update item-lores 
	public Inventory get(String name) {
		if(Main.raceTracks.containsKey(p)) {
			var meta = LAPS.getItemMeta();
			meta.lore(List.of(Component.text("§r§fRunden: " + Main.raceTracks.get(p).getLaps())));
		}
		return inventories.get(name);
	}
	
	// add an item to the inventory and register its action
	public void item(Inventory inv, int slot, ItemStack item, Action action) {
		inv.setItem(slot, item);
		actions.put(getDisplayName(item), action);
	}
	
	public void register(String invName, Inventory inv) {
		inventories.put(invName, inv);
	}
	
	// handle clicks on the default inventories
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(!event.getWhoClicked().equals(p))  return;
		
		ItemStack item = event.getCurrentItem();
		
		String name = getDisplayName(item);
		if(name == null) return;
		
		if(inventories.containsKey(event.getView().title().examinableName())) {
			event.setCancelled(true);
			if(actions.containsKey(name)) {
				actions.get(name).clicked(inventories.get(event.getView().title().examinableName()), item, name);
			}
		}
	}
	
	private HashMap<String, Action> handles = new HashMap<>();
	// register action to handle for inventory(specified by title)
	public void handle(String title, Action action) {
		handles.put(title, action);
	}
	// handle clicks for external inventories
	@EventHandler
	public void handleExternalClicks(InventoryClickEvent event) {
		if(!event.getWhoClicked().equals(p)) return;
 		
		String name;
		if((name = getDisplayName(event.getCurrentItem())) == null) return;
		
		final String title = event.getView().title().examinableName();
		if(handles.containsKey(title)) {
			handles.get(title).clicked(event.getInventory(), event.getCurrentItem(), name);
			event.setCancelled(true);
		}
	}
	
//	public boolean inventoryEquals(Inventory inv0, Inventory inv1) {
//		if(inv0.getSize() != inv1.getSize()) return false;
//		
//		return Arrays.equals(inv0.getStorageContents(), inv1.getStorageContents());
//	}
	
	public static String getDisplayName(ItemStack item) {
		if(item == null) return null;
		if(!item.hasItemMeta()) return null;
		if(!item.getItemMeta().hasDisplayName()) return null;
		
		return item.getItemMeta().displayName().examinableName();
	}
	
	
	public static HorseraceGUI getFor(Player p) {
		if(!GUIS.containsKey(p))
			GUIS.put(p, new HorseraceGUI(p));

		return GUIS.get(p);
	}
}
