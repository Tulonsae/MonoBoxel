package com.github.Monofraps.MonoBoxel.EventHooks;

import org.bukkit.Chunk;
import org.bukkit.Location;
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
				
				
				// correct the players position (-10 units if the player left the border)
				
				int x = playerLocationChunk.getX();
				int z = playerLocationChunk.getZ();
				
				int newX = 0;
				int newZ = 0;
				
				boolean playerNeedsPort = false;
				
				
				if(x > maxBoxelSize / 2)
				{
					newX = (x - (x - maxBoxelSize / 2) - 1) * 16;
					playerNeedsPort = true;
				}
				else if(x < -maxBoxelSize / 2)
				{
					newZ = (x - (x + maxBoxelSize / 2) + 1) * 16;
					playerNeedsPort = true;
				}
				
				if(z > maxBoxelSize / 2)
				{
					newZ= (z - (z - maxBoxelSize / 2) - 1) * 16;
					playerNeedsPort = true;
				}
				else if(z < -maxBoxelSize / 2)
				{
					newZ = (z - (z + maxBoxelSize / 2) + 1) * 16;
					playerNeedsPort = true;
				}				
				
				master.getLogManager().info(String.valueOf(x));
				
				if (playerNeedsPort) {
					event.getPlayer().sendMessage(
							"[MonoBoxel] You reached the border of the Boxel.");
					event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), newX, 7, newZ));
				}
			}
		}
	}
}
