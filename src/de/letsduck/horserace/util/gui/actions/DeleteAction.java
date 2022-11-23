package de.letsduck.horserace.util.gui.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.ItemBuilder;
import de.letsduck.horserace.util.gui.HorseraceGUI;
import net.kyori.adventure.text.Component;

public class DeleteAction extends Action {
	private static final ItemStack 	SUBMIT = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§aLöschen").getItem(),
			CANCEL = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§cAbbruch").getItem();
	private static final String SUBMIT_INV_TITLE = "§cStrecke l�schen?";

	public DeleteAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if (!Main.checkPlayerHasTrack(player))
			return;

		final var gui = HorseraceGUI.getFor(player);
		
		if(name.equals(HorseraceGUI.getDisplayName(SUBMIT))) {
			final var track = Main.raceTracks.get(player);
			track.delete();
			
			player.sendMessage("§6§lStrecke gelöscht");
			player.closeInventory();
			return;
		}
		if(name.equals(HorseraceGUI.getDisplayName(CANCEL))) {
			player.openInventory(gui.get(HorseraceGUI.SETTINGS_TITLE));
			return;
		}
		
		final Inventory submitInv = Bukkit.createInventory(player, 9 * 1, Component.text(SUBMIT_INV_TITLE));
		gui.item(submitInv, 3, CANCEL, null);
		gui.item(submitInv, 5, SUBMIT, null);
		gui.handle(SUBMIT_INV_TITLE, this);
		player.openInventory(submitInv);
		
		
	}

}
