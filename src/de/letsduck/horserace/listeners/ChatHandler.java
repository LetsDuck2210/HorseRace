package de.letsduck.horserace.listeners;

import java.util.HashMap;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;

// register a listener to watch out for a message from a player and optionally cancel the event
public class ChatHandler implements Listener {
	private static HashMap<Player, Function<String, Boolean>> handles = new HashMap<>();
	
	public static void register(Player p, Function<String, Boolean> cons) {
		handles.put(p, cons);
	}
	public static Function<String, Boolean> remove(Player p) {
		return handles.remove(p);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		var p = event.getPlayer();
		if(handles.containsKey(p)) {
			event.setCancelled(handles.get(p).apply(event.message().examinableName())); // TODO (examinableName)
			handles.remove(p);
		}
	}
}
