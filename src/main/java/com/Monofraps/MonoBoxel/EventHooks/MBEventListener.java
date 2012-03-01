package com.Monofraps.MonoBoxel.EventHooks;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.Monofraps.MonoBoxel.MBBoxel;
import com.Monofraps.MonoBoxel.MonoBoxel;


/**
 * MonoBoxel Event Listener for hooking into the Bukkit Event System.
 * 
 * @author MikeMatrix
 * 
 */
public class MBEventListener implements Listener {
	
	private MonoBoxel	master					= null;
	
	// Message limiters
	private long		lastBordercheckMessage	= 0;
	
	/**
	 * Register Event Listener with the Bukkit Event System.
	 * 
	 * @param plugin
	 */
	public MBEventListener(MonoBoxel plugin) {
	
		master = plugin;
		master.getServer().getPluginManager().registerEvents(this, master);
	}
	
	/**
	 * Listen for players changing their world.
	 * If a player changes his world, we can have a look if a Boxel gets empty and if we can unload
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
	
		master.getMBWorldManager().CheckForUnusedWorlds();
	}
	
	/**
	 * Also handle the auto unload if a player left the server.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
	
		// wait two seconds before checking for unused worlds
		if (master.getMBWorldManager().isBoxel(event.getPlayer().getWorld())[0]) {
			for (MBBoxel boxel : master.getMBWorldManager().getBoxels()) {
				if (boxel.getCorrespondingWorldName().equals(
						event.getPlayer().getWorld().getName()))
					boxel.Leave(event.getPlayer());
			}
		}
		
		master.getServer().getScheduler().scheduleSyncDelayedTask(master,
				new Runnable() {
					
					public void run() {
					
						master.getMBWorldManager().CheckForUnusedWorlds();
					}
				}, 2 * 20);
		
	}
	
	/**
	 * Also handle the auto unload if a player joind the server.
	 * (the auto unload check will also check if it has to cancel a planned unload)
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
	
		master.getMBWorldManager().CheckForUnusedWorlds();
	}
	
	/**
	 * Do a border check if a player moves.
	 * NOTES: May be CPU intensive, but we'll keep it as long as it works well
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
	
		if (master.getMBWorldManager().isBoxel(event.getTo().getWorld())[0]) {
			int maxBoxelSize = master.getConfig().getInt("max-boxel-size");
			
			Player player = event.getPlayer();
			Location targetLocation = event.getTo();
			Chunk targetChunk = targetLocation.getChunk();
			
			if (targetChunk.getX() > maxBoxelSize / 2) {
				event.setCancelled(true);
			} else
				if (targetChunk.getX() < -maxBoxelSize / 2) {
					event.setCancelled(true);
				}
			
			if (targetChunk.getZ() > maxBoxelSize / 2) {
				event.setCancelled(true);
			} else
				if (targetChunk.getZ() < -maxBoxelSize / 2) {
					event.setCancelled(true);
				}
			
			// Notify the player if the event is canceled (border is reached).
			if (event.isCancelled()
					&& lastBordercheckMessage <= System.currentTimeMillis() - 1000) {
				lastBordercheckMessage = System.currentTimeMillis();
				player.sendMessage("[A divine voice] You reached the border of the World.");
			}
		}
	}
	
	/**
	 * Do a border check if a player gets pushed by any kind.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerVelocityEvent(PlayerVelocityEvent event) {
	
		Player player = event.getPlayer();
		if (master.getMBWorldManager().isBoxel(player.getWorld())[0]) {
			int maxBoxelSize = master.getConfig().getInt("max-boxel-size");
			Location targetLocation = player.getLocation();
			targetLocation.add(event.getVelocity());
			Chunk targetChunk = targetLocation.getChunk();
			
			if (targetChunk.getX() > maxBoxelSize / 2) {
				event.setCancelled(true);
			} else
				if (targetChunk.getX() < -maxBoxelSize / 2) {
					event.setCancelled(true);
				}
			
			if (targetChunk.getZ() > maxBoxelSize / 2) {
				event.setCancelled(true);
			} else
				if (targetChunk.getZ() < -maxBoxelSize / 2) {
					event.setCancelled(true);
				}
			
			// Notify the player if the event is canceled (border is reached).
			if (event.isCancelled()
					&& lastBordercheckMessage <= System.currentTimeMillis() - 1000) {
				lastBordercheckMessage = System.currentTimeMillis();
				player.sendMessage("[A divine voice] You reached the border of the World.");
			}
		}
	}
}
