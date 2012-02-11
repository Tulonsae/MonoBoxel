package com.github.Monofraps.MonoBoxel.EventHooks;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
		master.getLogManager().info("player is in world:" + event.getPlayer().getWorld().getName());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		// wait two seconds before checking for unused worlds, because if this
		// event handler is called
		master.getServer().getScheduler()
				.scheduleSyncDelayedTask(master, new Runnable() {
					public void run() {
						master.getMBWorldManager().CheckForUnusedWorlds();
					}
				}, 2 * 20);

	}
	
	@EventHandler
	public void onPlayerQuit(PlayerJoinEvent event) {

		// wait two seconds before checking for unused worlds, because if this
		// event handler is called
		master.getMBWorldManager().CheckForUnusedWorlds();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		int maxBoxelSize = master.getConfig().getInt("max-boxel-size", 16);
		if (maxBoxelSize > 0) {
			if (master.getMBWorldManager()
					.isBoxel(event.getPlayer().getWorld())[0]) {
				Chunk playerLocationChunk = event.getPlayer().getLocation()
						.getChunk();
				if (playerLocationChunk.getX() >= maxBoxelSize
						|| playerLocationChunk.getX() <= -maxBoxelSize
						|| playerLocationChunk.getZ() >= maxBoxelSize
						|| playerLocationChunk.getZ() <= -maxBoxelSize) {
					event.getPlayer().sendMessage(
							"[MonoBoxel] You reached the border of the Boxel.");
					event.getPlayer().teleport(
							event.getPlayer().getWorld().getSpawnLocation());
				}
			}
		}
	}
}
