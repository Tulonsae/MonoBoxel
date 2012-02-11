package com.github.Monofraps.MonoBoxel.EventHooks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		
		master.getLogManager().info("changedWorld - player is in:" + event.getPlayer().getWorld().getName());
		
		master.getMBWorldManager().CheckForUnusedWorlds();
	}

}
