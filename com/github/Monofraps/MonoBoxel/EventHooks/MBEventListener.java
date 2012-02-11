package com.github.Monofraps.MonoBoxel.EventHooks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Monofraps.MonoBoxel.MonoBoxel;

/**
 * MonoBoxel Event Listener for hooking into the Bukkit Event System
 * 
 * @author MikeMatrix
 * 
 */
public class MBEventListener implements Listener {

	private MonoBoxel master = null;

	/**
	 * Register Event Listener with the Bukkit Event System
	 * 
	 * @param plugin
	 */
	public MBEventListener(MonoBoxel plugin) {
		master = plugin;
		master.getServer().getPluginManager().registerEvents(this, master);
	}

	/**
	 * Gets all the Teleport Events from the Bukkit Event System
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		master.getMBWorldManager().CheckForUnusedWorlds();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		// wait two seconds before checking for unused worlds, because if this event handler is called 
		master.getServer().getScheduler()
				.scheduleSyncDelayedTask(master, new Runnable() {
					public void run() {
						master.getMBWorldManager().CheckForUnusedWorlds();
					}
				}, 2 * 20);

	}

}
