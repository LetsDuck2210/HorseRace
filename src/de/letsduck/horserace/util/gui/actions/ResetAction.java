
package de.letsduck.horserace.util.gui.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.ItemBuilder;
import de.letsduck.horserace.util.gui.HorseraceGUI;

public class ResetAction extends Action {
	private static final ItemStack 	SUBMIT = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("§aZurücksetzen").getItem(),
									CANCEL = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§cAbbrechen").getItem();
	private static final String SUBMIT_INV_TITLE = "§6Strecke zurücksetzen?";

	public ResetAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if (!Main.checkPlayerHasTrack(player))
			return;

		final var gui = HorseraceGUI.getFor(player);
		
		if(name.equals(HorseraceGUI.getDisplayName(SUBMIT))) {
			final var track = Main.raceTracks.get(player);
			track.reset();
			player.sendMessage("§6§lStrecke zurückgesetzt");
			player.closeInventory();
			return;
		}
		if(name.equals(HorseraceGUI.getDisplayName(CANCEL))) {
			player.openInventory(gui.get(HorseraceGUI.SETTINGS_TITLE));
			return;
		}
		
		final Inventory submitInv = Bukkit.createInventory(player, 9 * 1, SUBMIT_INV_TITLE);
		gui.item(submitInv, 3, CANCEL, null);
		gui.item(submitInv, 5, SUBMIT, null);
		gui.handle(SUBMIT_INV_TITLE, this);
		player.openInventory(submitInv);
	}

}
