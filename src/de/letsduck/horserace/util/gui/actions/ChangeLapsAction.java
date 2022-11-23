package de.letsduck.horserace.util.gui.actions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.ItemBuilder;
import de.letsduck.horserace.util.gui.HorseraceGUI;
import net.kyori.adventure.text.Component;

public class ChangeLapsAction extends Action {
	public static final ItemStack SUBMIT = new ItemBuilder(Material.IRON_HORSE_ARMOR).name("�aFertig").getItem();
	public static final String CHANGE_LAPS_TITLE = ChatColor.DARK_PURPLE + "Rundenzahl �ndern";
	private Inventory changeLaps;
	
	public ChangeLapsAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if(!Main.checkPlayerHasTrack(player)) return;

		var track = Main.raceTracks.get(player);
		
		// updates the tracks laps when SUBMIT is clicked
		if(name.equals(HorseraceGUI.getDisplayName(SUBMIT))) {
			track.setLaps(LapHandlerAction.getFor(player, SUBMIT).getLaps());
			player.sendMessage("�aRundenzahl ge�ndert");
			player.openInventory(HorseraceGUI.getFor(player).get(HorseraceGUI.SETTINGS_TITLE));
			return;
		} else if(name.equals(HorseraceGUI.getDisplayName(HorseraceGUI.LAPS))) {
			// initialy set lore of submit-item
			var meta = SUBMIT.getItemMeta();
			meta.lore(List.of(Component.text("�r�fRunden: " + track.getLaps())));
			SUBMIT.setItemMeta(meta);
			
			changeLaps = Bukkit.createInventory(player, 9 * 3, Component.text(CHANGE_LAPS_TITLE));
			Action lapHandler = LapHandlerAction.getFor(player, SUBMIT);
			var gui = HorseraceGUI.getFor(player);
			
			// lapHandler never getting executed
			gui.register(CHANGE_LAPS_TITLE, changeLaps);
			gui.item(changeLaps, 9, HorseraceGUI.LAPS_DEC_10, lapHandler);
			gui.item(changeLaps, 11, HorseraceGUI.LAPS_DEC_1, lapHandler);
			gui.item(changeLaps, 13, SUBMIT, this);
			gui.item(changeLaps, 15, HorseraceGUI.LAPS_INC_1, lapHandler);
			gui.item(changeLaps, 17, HorseraceGUI.LAPS_INC_10, lapHandler);
			gui.handle(CHANGE_LAPS_TITLE, this);
			
			player.openInventory(changeLaps);
		}
	}

}
