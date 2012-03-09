package com.Monofraps.MonoBoxel;


import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.Monofraps.MonoBoxel.Utils.GenUtils;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


/**
 * Boxel Management class.
 * 
 * @author Monofraps
 */
public class MBBoxelManager {
	
	/**
	 * The Runnable for the delayed unload task.
	 * 
	 * @author Monofraps
	 */
	private class BoxelUnloadRunnable implements Runnable {
		
		private MonoBoxel		master	= null;
		private MultiverseWorld	box		= null;
		
		public BoxelUnloadRunnable(MonoBoxel monoBoxel, MultiverseWorld boxel) {
		
			master = monoBoxel;
			box = boxel;
		}
		
		@Override
		public void run() {
		
			if (!master.getMVCore().getMVWorldManager().unloadWorld(box.getName())) {
				master.getLogManager().debugLog(Level.WARNING, "Failed to unload " + box.getName());
			} else {
				master.getLogManager().debugLog(Level.INFO, box.getName() + " was unloaded due to inactivity.");
			}
			
			master.getMBWorldManager().RemoveUnloadEntry(box.getName());
		}
	}
		
	private HashMap<String, Integer>	unloadIds		= null;
	
	private MonoBoxel					master			= null;
	
	/**
	 * 
	 * @param plugin
	 *            The reference to the core plugin instance
	 */
	public MBBoxelManager(MonoBoxel plugin) {
	
		master = plugin;
		unloadIds = new HashMap<String, Integer>();
	}
	
	/**
	 * Checks if the given name is an existing Boxel and if it is loaded.
	 * 
	 * @param name
	 *            Name to check.
	 * @return A boolean array. Component 1(0) is true if the Boxel exists;
	 *         Component 2(1) is true if the Boxel is loaded.
	 */
	public boolean[] isBoxel(String name) {
	
		boolean[] result = new boolean[2];
		result[0] = false;
		result[1] = false;
		
		if (!name.startsWith(master.getBoxelPrefix()) && !name.startsWith(master.getBoxelGroupPrefix()))
			return result;
		
		for (MultiverseWorld w : master.getMVCore().getMVWorldManager().getMVWorlds()) {
			if (w.getName().equals(name)) {
				result[0] = true;
				result[1] = true;
			}
		}
		
		if (master.getMVCore().getMVWorldManager().getUnloadedWorlds().contains(name)) {
			result[0] = true;
			result[1] = false;
		}
		
		return result;
	}
	
	/**
	 * Checks if the given world is an existing Boxel and if it is loaded.
	 * 
	 * @param world
	 *            World to check.
	 * @return A boolean array. Component 1(0) is true if the Boxel exists;
	 *         Component 2(1) is true if the Boxel is loaded.
	 */
	public boolean[] isBoxel(World world) {
	
		return this.isBoxel(world.getName());
	}
	
	/**
	 * Starts/Cancels a delayed world unload if a Boxel is empty.
	 */
	public void CheckForUnusedWorlds() {
	
		// normal boxels
		for (MultiverseWorld world : master.getMVCore().getMVWorldManager().getMVWorlds()) {
			
			boolean[] isBoxResult = isBoxel(world.getName());
			
			if (!isBoxResult[0]) {
				continue;
			}
			
			if (!isBoxResult[1]) {
				continue;
			}
			
			if ((world.getCBWorld().getPlayers().size() == 0) && !unloadIds.containsKey(world.getName())) {
				unloadIds.put(
						world.getName(),
						master.getServer().getScheduler().scheduleAsyncDelayedTask(master,
								new BoxelUnloadRunnable(master, world),
								master.getConfig().getInt("world-unload-period") * 20));
			}
			
			if ((world.getCBWorld().getPlayers().size() != 0) && unloadIds.containsKey(world.getName())) {
				master.getServer().getScheduler().cancelTask(unloadIds.get(world.getName()));
				unloadIds.remove(world.getName());
			}
		}
	}
	
	/**
	 * Returns a unload ID.
	 * 
	 * @param key
	 * @return the unload ID.
	 */
	public int getUnloadId(String key) {
	
		if (unloadIds.containsKey(key))
			return unloadIds.get(key);
		
		return -1;
	}
	
	/**
	 * Removes an entry from the unload map.
	 * 
	 * @param key
	 */
	public void RemoveUnloadEntry(String key) {
	
		unloadIds.remove(key);
	}
	
	/**
	 * Teleports a player back to his original location.
	 * 
	 * @param player
	 *            The player to teleport
	 * @return true on success, otherwise false
	 */
	public boolean GenericLeave(Player player) {
	
		String msgPlayerNotInBoxel = String.format("Player %s is not in a Boxel! - No leave for %s", player.getName(),
				player.getName());
		
		if (!(isBoxel(player.getWorld().getName())[0])) {
			master.getLogManager().debugLog(Level.INFO, msgPlayerNotInBoxel);
			return false;
		}
		
		return player.teleport(LoadEntryLocation(player));
	}
	
	/**
	 * Teleports [player] to Boxel [boxelName] or to his own if [boxelName] is empty.
	 * 
	 * @param player
	 * @param boxelName
	 * @param create
	 * @return true on success, otherwise false
	 */
	public boolean GenericJoin(Player player, String boxelName, boolean create, String seed, String generator) {
	
		MVWorldManager mvWorldManager = master.getMVCore().getMVWorldManager();
		
		// check if boxelName is empty and create the boxelName from the player name then
		if (boxelName.isEmpty())
			boxelName = GenUtils.boxelizeName(player.getName(), master);
		else
			boxelName = GenUtils.boxelizeName(boxelName, master);
		
		// ++++++++++++++++++++++++++
		// Messages
		String msgNoMVWorld = String.format("Boxel/World %s are no multiverse worlds.",
				GenUtils.boxelizeName(boxelName, master));
		String msgFailedNoMVWorldFound = String.format("Boxel/World %s was not found. Strange error!",
				GenUtils.boxelizeName(boxelName, master));
		String msgLoadedWorld = String.format("Boxel/World %s was loaded", GenUtils.boxelizeName(boxelName, master));
		String msgFailedLoadWorld = String.format("Boxel/World %s was NOT loaded",
				GenUtils.boxelizeName(boxelName, master));
		String msgPlayerTeleported = String.format("Player %s teleported to %s", player.getName(), boxelName);
		String msgFailedPlayerTeleport = String.format("Failed to teleport player %s to %s", player.getName(),
				boxelName);
		String msgFailedNoPermissionsV = String.format("Player %s has not enaugth permissions. (Visit)",
				player.getName());
		String msgFailedNoPermissionsC = String.format("Player %s has not enaugth permissions. (Create)",
				player.getName());
		String msgFailedCreateFailed = String.format(
				"Generic joind failed because Boxel/World %s could not be created.", boxelName);
		// ++++++++++++++++++++++++++
		
		// check visit permissions
		if (!master.getPermissionManager().canVisitBoxel(player, boxelName)) {
			master.getLogManager().debugLog(Level.WARNING, msgFailedNoPermissionsV);
			master.getPermissionManager().SendNotAllowedMessage(player);
			return false;
		}
		
		// check if multiverse knows about this world, and create it if not and create==true
		if (!mvWorldManager.isMVWorld(boxelName)) {
			master.getLogManager().debugLog(Level.INFO, msgNoMVWorld);
			if (create) {
				// check create permissions
				if (!master.getPermissionManager().canCreateBoxel(player, boxelName)) {
					master.getLogManager().debugLog(Level.WARNING, msgFailedNoPermissionsC);
					master.getPermissionManager().SendNotAllowedMessage(player);
					return false;
				} else {
					if (!GenericCreate(player, boxelName, seed, generator)) {
						master.getLogManager().debugLog(Level.WARNING, msgFailedCreateFailed);
						return false;
					}
				}
			} else
				return false;
		}
		
		// MV says it knows about this world, so try to get it
		MultiverseWorld boxel = mvWorldManager.getMVWorld(boxelName);
		
		// ok, the world is not loaded; try and load it
		if (boxel == null) {
			if (mvWorldManager.loadWorld(boxelName)) {
				master.getLogManager().debugLog(Level.INFO, msgLoadedWorld);
			} else {
				master.getLogManager().debugLog(Level.INFO, msgFailedLoadWorld);
				return false;
			}
		}
		
		// try to get the Boxel world again
		boxel = mvWorldManager.getMVWorld(boxelName);
		
		// the Boxel world could not be found the second time... that's strange!
		if (boxel == null) {
			master.getLogManager().debugLog(Level.SEVERE, msgFailedNoMVWorldFound);
			return false;
		}
		
		SaveEntryLocation(player);
		
		// teleport the player
		if (player.teleport(boxel.getSpawnLocation())) {
			master.getLogManager().debugLog(Level.INFO, msgPlayerTeleported);
			return true;
		} else {
			master.getLogManager().debugLog(Level.INFO, msgFailedPlayerTeleport);
			return false;
		}
	}
	
	/**
	 * Creates a Boxel.
	 * 
	 * @param player
	 * @param boxelName
	 * @return true on success, otherwise false
	 */
	public boolean GenericCreate(Player player, String boxelName, String seed, String generator) {
	
		MVWorldManager mvWorldManager = master.getMVCore().getMVWorldManager();
		
		// check if boxelName is empty and create the boxelName from the player name then
		if (boxelName.isEmpty())
			boxelName = GenUtils.boxelizeName(player.getName(), master);
		else
			boxelName = GenUtils.boxelizeName(boxelName, master);
		
		// ++++++++++++++++++++++++++
		// Messages
		String msgIsMVWorld = String.format("Multiverse does already know about the world %s.", boxelName);
		String msgLoadedWorld = String.format("Boxel/World %s was loaded", boxelName);
		String msgFailedLoadWorld = String.format("Boxel/World %s was NOT loaded", boxelName);
		String msgCreatedWorld = String.format("Boxel/World %s was created", boxelName);
		String msgFailedCreateWorld = String.format("Boxel/World %s was NOT created", boxelName);
		String msgFailedGetWorld = String.format("Could not get Boxel/World %s, that's odd!", boxelName);
		// ++++++++++++++++++++++++++
		
		// if MV does already knows about this world, load it and return
		if (mvWorldManager.isMVWorld(boxelName)) {
			master.getLogManager().debugLog(Level.WARNING, msgIsMVWorld);
			if (mvWorldManager.loadWorld(boxelName)) {
				master.getLogManager().debugLog(Level.INFO, msgLoadedWorld);
				return true;
			} else {
				master.getLogManager().debugLog(Level.INFO, msgFailedLoadWorld);
				return false;
			}
		}
		
		if (generator.equals("default"))
			generator = "";
		
		// try to create the world
		if (mvWorldManager.addWorld(boxelName, World.Environment.valueOf("NORMAL"), seed, WorldType.valueOf("NORMAL"),
				false, generator)) {
			
			master.getLogManager().debugLog(Level.INFO, msgCreatedWorld);
		} else {
			master.getLogManager().debugLog(Level.INFO, msgFailedCreateWorld);
			return false;
		}
		
		MultiverseWorld world = mvWorldManager.getMVWorld(boxelName);
		
		// now try to set the world parameters
		/*
		 * Creative Mode
		 * Peaceful
		 * No Weather
		 * Disallow monsters
		 * No Autoload
		 * Auto Heal
		 * No PVP
		 */
		if (world == null) {
			master.getLogManager().debugLog(Level.INFO, msgFailedGetWorld);
			return false;
		} else {
			world.setGameMode("CREATIVE");
			world.setDifficulty("PEACEFUL");
			world.setEnableWeather(false);
			world.setAllowMonsterSpawn(false);
			world.setAutoLoad(false);
			world.setAutoHeal(true);
			world.setPVPMode(false);
			return true;
		}
	}
	
	private void SaveEntryLocation(Player player) {
	
		// ++++++++++++++++++++++++++
		// Messages
		String msgSavedLocation = String.format("Saved entry location for player %s.", player.getName());
		String msgFailedSaveLocation = String.format("Failed to save entry location for player %s.", player.getName());
		// ++++++++++++++++++++++++++
		
		if (!master.getMBWorldManager().isBoxel(player.getWorld().getName())[0]) {
			master.getDataConfig().getConfig().set("playerloc." + player.getName() + ".world",
					player.getWorld().getName());
			master.getDataConfig().getConfig().set("playerloc." + player.getName() + ".position",
					player.getLocation().toVector().add(new Vector(0, 1, 0)));
			master.getDataConfig().getConfig().set("playerloc." + player.getName() + ".yaw",
					player.getLocation().getYaw());
			master.getDataConfig().getConfig().set("playerloc." + player.getName() + ".pitch",
					player.getLocation().getPitch());
			master.getDataConfig().saveConfig();
			
			master.getLogManager().debugLog(Level.INFO, msgSavedLocation);
			return;
		}
		
		master.getLogManager().debugLog(Level.INFO, msgFailedSaveLocation);
	}
	
	private Location LoadEntryLocation(Player player) {
	
		// ++++++++++++++++++++++++++
		// Messages
		String msgLoadEntryLocation = String.format("Load entry location for player %s.", player.getName());
		String msgFailedLoadEntryLocation = String.format("Failed to load entry location location for player %s.",
				player.getName());
		// ++++++++++++++++++++++++++
		
		String outWorld = master.getDataConfig().getConfig().getString("playerloc." + player.getName() + ".world", "");
		Vector outPosition = master.getDataConfig().getConfig().getVector(
				"playerloc." + player.getName() + ".position", new org.bukkit.util.Vector());
		double outPitch = master.getDataConfig().getConfig().getDouble("playerloc." + player.getName() + ".pitch", 0.0);
		
		double outYaw = master.getDataConfig().getConfig().getDouble("playerloc." + player.getName() + ".yaw", 0.0);
		
		if (outWorld.isEmpty()) {
			master.getLogManager().debugLog(Level.INFO, msgFailedLoadEntryLocation);
			return master.getMVCore().getMVWorldManager().getSpawnWorld().getSpawnLocation();
		}
		
		MultiverseWorld entryWorld = master.getMVCore().getMVWorldManager().getMVWorld(outWorld);
		
		if (entryWorld == null) {
			master.getLogManager().debugLog(Level.INFO, msgFailedLoadEntryLocation);
			return master.getMVCore().getMVWorldManager().getSpawnWorld().getSpawnLocation();
		}
		
		master.getLogManager().debugLog(Level.INFO, msgLoadEntryLocation);
		return new Location(entryWorld.getCBWorld(), outPosition.getX(), outPosition.getY(), outPosition.getZ(),
				(float) outYaw, (float) outPitch);
	}
}