package de.letsduck.horserace.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.kyori.adventure.text.Component;

public class ItemBuilder {
	private ItemStack item;
	
	public ItemBuilder(Material mat) {
		item = new ItemStack(mat);
	}
	public ItemBuilder name(String name) {
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(name));
		item.setItemMeta(meta);
		
		return this;
	}
	public ItemBuilder addLore(String lore) {
		ItemMeta meta = item.getItemMeta();
		List<Component> lores = (meta.hasLore() ? meta.lore() : new ArrayList<Component>());
		lores.add(Component.text(lore));
		meta.lore(lores);
		item.setItemMeta(meta);
		
		return this;
	}
	public ItemBuilder setLore(List<Component> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.lore(lore);
		item.setItemMeta(meta);
		
		return this;
	}
	public ItemBuilder addEnchant(Enchantment ench, int level) {
		item.addEnchantment(ench, level);
		
		return this;
	}
	public ItemStack getItem() {
		return item;
	}
	
	public static ItemStack build(Material material, String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(name));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getPlayerHead(Player p, String name) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(p);
		meta.displayName(Component.text(name));
		item.setItemMeta(meta);
		
		return item;
	}
}
