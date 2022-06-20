package de.letsduck.horserace.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import de.letsduck.horserace.main.Main;

public class RecipeBuilder {
	private ItemStack res;
	private HashMap<Character, Material> ingredients;
	private String[] shape;
	private String namespace;
	private static ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	
	public RecipeBuilder(ItemStack res) {
		this.res = res;
		ingredients = new HashMap<Character, Material>();
	}
	public RecipeBuilder setIngredient(char c, Material mat) {
		ingredients.put(c, mat);
		return this;
	}
	public RecipeBuilder shape(String...shape) {
		this.shape = shape;
		return this;
	}
	public RecipeBuilder namespace(String namespace) {
		this.namespace = namespace;
		return this;
	}
	
	public Recipe getRecipe() {
		if(shape == null) {
			ShapelessRecipe r = new ShapelessRecipe(new NamespacedKey(Main.getPlugin(), namespace), res);
			
			ingredients.values().stream().forEach(r::addIngredient);
			
			return r;
		} else {
			ShapedRecipe r = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), namespace), res);
			r.shape(shape);
			for(char c : ingredients.keySet()) {
				r.setIngredient(c, ingredients.get(c));
			}
			
			return r;
		}
	}
	
	public static final String FINISH_LINE_BLACK = ChatColor.DARK_GRAY + "Ziellinie",
							   FINISH_LINE_WHITE = ChatColor.WHITE + "Ziellinie",
							   FLAG = ChatColor.GOLD + "Flagge",
							   STARTING_POINT = ChatColor.BLUE + "Startpunkt",
							   BREAKER = ChatColor.RED + "Entfernen";
	public static void build() {
		ItemStack[] items = new ItemStack[] {
			new ItemBuilder(Material.BLACK_WOOL)
			.name(FINISH_LINE_BLACK)
			.getItem(),
			new ItemBuilder(Material.WHITE_WOOL)
			.name(FINISH_LINE_WHITE)
			.getItem(),
			new ItemBuilder(Material.ORANGE_WOOL)
			.name(FLAG)
			.getItem(),
			new ItemBuilder(Material.BLUE_WOOL)
			.name(STARTING_POINT)
			.getItem(),
			new ItemBuilder(Material.BARRIER)
			.name(BREAKER)
			.getItem()
		};
		
		for(ItemStack item : items)
			RecipeBuilder.items.add(item);
	}
	
	// ItemInv-Plugin compatibility
	public static ItemStack[] getItems() {
		return items.toArray(new ItemStack[0]);
	}
	public static Recipe[] addAll(Recipe[]...recipes) {
		int len = 0;
		for(Recipe[] r : recipes) {
			len += r.length;
		}
		Recipe[] res = new Recipe[len];
		
		int i = 0;
		for(Recipe[] ra : recipes) {
			for(Recipe r : ra) {
				res[i++] = r;
			}
		}
		
		return res;
	}
	public static ItemStack[] addAll(ItemStack[]...items) {
		int len = 0;
		for(ItemStack[] r : items) {
			len += r.length;
		}
		ItemStack[] res = new ItemStack[len];
		
		int i = 0;
		for(ItemStack[] ra : items) {
			for(ItemStack r : ra) {
				res[i++] = r;
			}
		}
		
		return res;
	}
}
