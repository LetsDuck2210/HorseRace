package de.letsduck.horserace.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.commands.CommandUtil;
import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.ItemBuilder;
import de.letsduck.horserace.util.RaceTrack;
import de.letsduck.horserace.util.RecipeBuilder;
import de.letsduck.horserace.util.gui.HorseraceGUI;
import net.kyori.adventure.text.Component;

public class HorseraceBuilderListener implements Listener {
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		
		String name = HorseraceGUI.getDisplayName(event.getItemInHand());
		if(name == null) return;
		var blockPlaced = event.getBlockPlaced();
		
		if(name.equals(RecipeBuilder.FINISH_LINE_BLACK) || name.equals(RecipeBuilder.FINISH_LINE_WHITE)) {
			RaceTrack track = CommandUtil.checkTrack(p);
			if(track == null) {
				p.sendMessage("�cStrecke hat keinen ");
				return;
			}
			
			track.getFinishLine().addFinishLineBlock(blockPlaced.getLocation());
			Color c;
			if(name.endsWith("BLACK")) c = Color.BLACK;
			else c = Color.WHITE;
			blockPlaced.getWorld().spawnParticle(Particle.REDSTONE, blockPlaced.getLocation().add(0.5, 0.5, 0.5), 2, new Particle.DustOptions(c, 2));
		}
		else if(name.startsWith(RecipeBuilder.FLAG) && !name.equals(RecipeBuilder.FLAG)) {
			RaceTrack track = CommandUtil.checkTrack(p);
			if(track == null) return;
			
			var finishLine = track.getFinishLine();
			int flagNum = Integer.parseInt(name.substring(RecipeBuilder.FLAG.length() + 1));
			if(flagNum >= finishLine.getFlags().size()) {
				p.sendMessage("�cBitte w�hle erst eine Flagge");
				return;
			}

			finishLine.addFlag(flagNum, blockPlaced.getLocation());
			blockPlaced.setType(Material.AIR);
			blockPlaced.getWorld().spawnParticle(Particle.REDSTONE, blockPlaced.getLocation().add(0.5, 0.5, 0.5), 2, new Particle.DustOptions(Color.RED, 2));
		}
		else if(name.equals(RecipeBuilder.STARTING_POINT)) {
			RaceTrack track = CommandUtil.checkTrack(p);
			if(track == null) return;
			
			track.addStartingPoint(blockPlaced.getLocation());
			blockPlaced.setType(Material.AIR);
			blockPlaced.getWorld().spawnParticle(Particle.REDSTONE, blockPlaced.getLocation().add(0.5, 0.5, 0.5), 2, new Particle.DustOptions(Color.RED, 2));
		}
		else if(name.equals(RecipeBuilder.BREAKER)) {
			RaceTrack track = CommandUtil.checkTrack(p);
			if(track == null) return;
			
			var finishLine = track.getFinishLine();
			
			var startPoints = track.getStartPoints();
			var line = finishLine.getLine();
			var flags = finishLine.getFlags();
			
			var loc = blockPlaced.getLocation();
			
			if(startPoints.contains(loc)) 
				startPoints.remove(loc);
			if(line.contains(loc))
				line.remove(loc);
			for(int i = 0; i < flags.size(); i++) {
				if(flags.get(i).contains(loc)) {
					flags.get(i).remove(loc);
					if(flags.get(i).size() < 1)
						flags.remove(i);
				}
			}
			
			event.setCancelled(true);
		}
	}
	public static final String CHOOSE_FLAG_TITLE = "�6W�hle eine Flagge";
	
	@EventHandler
	public void onPlayerSelectFlag(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(event.getAction() != Action.RIGHT_CLICK_AIR) return;
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!item.getItemMeta().displayName().toString().startsWith(RecipeBuilder.FLAG)) return;
		
		RaceTrack track = CommandUtil.checkTrack(p);
		if(track == null) return;
		
		Inventory select = Bukkit.createInventory(p, 9 * 5, Component.text(CHOOSE_FLAG_TITLE)); // TODO (Component.text)
		int flagCount = track.getFinishLine().getFlags().size();
		for(int i = 0; i < flagCount && i < 9 * 5; i++) {
			select.addItem(new ItemBuilder(Material.ORANGE_WOOL).name(RecipeBuilder.FLAG + " " + i).getItem());
		}
		select.addItem(new ItemBuilder(Material.RED_WOOL).name(RecipeBuilder.FLAG + " " + flagCount).getItem());
		p.openInventory(select);
	}
	@EventHandler
	public void onPlayerSelectFlagFromInventory(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		
		Player p = (Player) event.getWhoClicked();
		
		String title = event.getView().title().toString();
		if(!title.equals(CHOOSE_FLAG_TITLE)) return;
		
		ItemStack item = event.getCurrentItem();
		event.setCancelled(false);
		
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!item.getItemMeta().displayName().examinableName().startsWith(RecipeBuilder.FLAG)) return; // TODO (examinableName)
		
		p.closeInventory();
		
		ItemStack hand = p.getInventory().getItemInMainHand();
		var meta = hand.getItemMeta();
		meta.displayName(item.getItemMeta().displayName());
		hand.setItemMeta(meta);
		
		if(item.getType() == Material.RED_WOOL) {
			RaceTrack track;
			if((track = CommandUtil.checkTrack(p)) == null) return;
			
			track.getFinishLine().getFlags().add(new ArrayList<>());
		}
	}
	
	private int taskShowBlocks;
	@EventHandler
	public void onPlayerHoldBarrier(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		
		var item = p.getInventory().getItemInMainHand();
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!item.getItemMeta().displayName().examinableName().equals(RecipeBuilder.BREAKER)) return; // TODO (examinableName)
		
		RaceTrack track = CommandUtil.checkTrack(p);
		if(track == null) return;
		
		var finishLine = track.getFinishLine();
		
		if(taskShowBlocks == 0)
		taskShowBlocks = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			var hand = p.getInventory().getItemInMainHand();
			if(hand == null) return;
			if(!hand.hasItemMeta()) return;
			if(!hand.getItemMeta().hasDisplayName()) return;
			if(!hand.getItemMeta().displayName().examinableName().equals(RecipeBuilder.BREAKER)) { // TODO (examinableName)
				Bukkit.getScheduler().cancelTask(taskShowBlocks);
				taskShowBlocks = 0;
				return;
			}
			
			finishLine.getFlags().forEach((flags) -> {
				flags.forEach((loc) -> {
					loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.5, 0.5), 30, 0.01, 0.7, 0.01, new Particle.DustOptions(Color.ORANGE, 1.5f));
					loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.5, 0.5), 4, new Particle.DustOptions(Color.ORANGE, 2.9f));
				});
			});
			for(int i = 0; i < finishLine.getLine().size(); i++) {
				var loc = finishLine.getLine().get(i);
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.5, 0.5), 30, 0.01, 0.7, 0.01, new Particle.DustOptions(i % 2 == 0 ? Color.BLACK : Color.WHITE, 1.5f));
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.5, 0.5), 4, new Particle.DustOptions(i % 2 == 0 ? Color.BLACK : Color.WHITE, 2.9f));
			}
			
			track.getStartPoints().forEach((loc) -> {
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.5, 0.5), 30, 0.01, 0.7, 0.01, new Particle.DustOptions(Color.BLUE, 1.5f));
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.5, 0.5), 4, new Particle.DustOptions(Color.BLUE, 2.9f));
			});
		}, 10, 10);
	}
}
