package de.letsduck.horserace.util.gui.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.RaceTrack;
import net.kyori.adventure.text.Component;

public class StartAction extends Action {
	private static final int START_COOLDOWN = 2;

	public StartAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv, ItemStack item, String name) {
		if (!Main.checkPlayerHasTrack(player))
			return;

		var track = Main.raceTracks.get(player);
		RaceTrack.queuedTracks.put(track.getID(), track);
		Bukkit.broadcast(Component.text("�2" + track.getID() + " startet in " + START_COOLDOWN + " minuten. (/horserace join " + track.getID() + ")")); // TODO (component.text)
		track.addCompetingPlayer(player);
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
			track.setup();
			track.start();
		}, START_COOLDOWN * 60 * 20); // 2 min
	}

}
