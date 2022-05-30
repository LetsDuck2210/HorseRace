package de.letsduck.horserace.util.gui.actions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.letsduck.horserace.main.Main;
import de.letsduck.horserace.util.ItemBuilder;
import de.letsduck.horserace.util.gui.HorseraceGUI;

public class BuildersAction extends Action {

	public BuildersAction(Player player) {
		super(player);
	}

	@Override
	public void clicked(Inventory inv_, ItemStack item, String name) {
		if(!Main.checkPlayerHasTrack(player)) return;
		
		var track = Main.raceTracks.get(player);
		
		final String invTitle = ChatColor.DARK_RED + "Builders";
		var inv = Bukkit.createInventory(player, 9 * 5, invTitle);
		
		for(Player p : track.getBuilders()) {
			inv.addItem(ItemBuilder.getPlayerHead(p, ChatColor.DARK_RED + p.getName()));
		}
		final String addBuilderName = ChatColor.DARK_RED + "add" + "  ";
		inv.addItem(new ItemBuilder(Material.PLAYER_HEAD).name(addBuilderName).getItem());
		
		player.openInventory(inv);
		
		HorseraceGUI.getFor(player).handle(invTitle, new Action(player) {
			@Override
			public void clicked(Inventory inv, ItemStack item, String name) {
				if(name.length() <= 2)
					return;
				if(name.equals(addBuilderName)) {
					final String addBuilderTitle = ChatColor.DARK_RED + "Add Builder";
					final var select = Bukkit.createInventory(player, 9 * 5, addBuilderTitle);
					
					for(Player pl : Bukkit.getOnlinePlayers()) {
						if(!track.getBuilders().contains(pl)) {
							select.addItem(ItemBuilder.getPlayerHead(pl, ChatColor.DARK_RED + pl.getName()));
						}
					}
					
					player.openInventory(select);
					HorseraceGUI.getFor(player).handle(addBuilderTitle, new Action(player) {
						@Override
						public void clicked(Inventory inv, ItemStack item, String name) {
							if(name.length() <= 2) return;
							Player p = Bukkit.getPlayer(name.substring(2));
							if(p == null) {
								player.sendMessage("§cThis player does not exist!");
								return;
							}
							
							track.addBuilder(p);
							select.remove(item);
							select.clear();
							for(Player pl : Bukkit.getOnlinePlayers()) {
								if(!track.getBuilders().contains(pl)) {
									select.addItem(ItemBuilder.getPlayerHead(pl, ChatColor.DARK_RED + pl.getName()));
								}
							}
							
							player.sendMessage("§aBuilder added");
						}
					});
					
					return;
				}
				
				Player p = Bukkit.getPlayer(name.substring(2));
				if(p == null) {
					player.sendMessage("§cThis player does not exist!");
					return;
				}
				if(track.getBuilders().get(0) == p) {
					player.sendMessage("§cCan't remove the owner of the track!");
					return;
				}
				track.removeBuilder(p);
				inv.remove(item);
				inv.clear();
				for(Player pl : track.getBuilders()) {
					inv.addItem(ItemBuilder.getPlayerHead(pl, ChatColor.DARK_RED + pl.getName()));
				}
				inv.addItem(new ItemBuilder(Material.PLAYER_HEAD).name(ChatColor.DARK_RED + "add").getItem());
				player.sendMessage("§aBuilder removed!");
			}
		});
	}
}
