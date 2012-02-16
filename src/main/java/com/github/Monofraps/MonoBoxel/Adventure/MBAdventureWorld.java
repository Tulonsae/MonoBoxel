package com.github.Monofraps.MonoBoxel.Adventure;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.World;

import com.github.Monofraps.MonoBoxel.MonoBoxel;
import com.github.Monofraps.MonoBoxel.Utils.WorldDuplicator;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


/**
 * Holds information of adventure worlds.
 * 
 * - not implemented yet
 * 
 * @author Monofraps
 * 
 */
public class MBAdventureWorld {
	
	private String			worldName		= "";
	private MultiverseWorld	adventureWorld	= null;
	private MonoBoxel		master			= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the main plugin class
	 * @param worldName
	 *            The name of the adventure world (without .template)
	 */
	public MBAdventureWorld(MonoBoxel plugin, String worldName) {
		master = plugin;
		this.worldName = worldName;
		
		if (isWorldLoaded())
			adventureWorld = master.getMVCore().getMVWorldManager()
					.getMVWorld(worldName);
		
	}
	
	/**
	 * Checks if the world was loaded.
	 * 
	 * @return true if the world is loaded by multiverse, otherwise false
	 */
	private boolean isWorldLoaded() {
		if (master.getMVCore().getMVWorldManager().getMVWorld(worldName) != null)
			return true;
		
		if (master.getMVCore().getMVWorldManager().getUnloadedWorlds()
				.contains(worldName))
			return false;
		
		return false;
	}
	
	/**
	 * 
	 * @return true on success, otherwise false
	 */
	public boolean Reset() {
		if(!master.getMVCore().getMVWorldManager().deleteWorld(worldName))
		{
			master.getLogManager().debugLog(Level.SEVERE, "Failed to delete used adventure world.");
			return false;
		}
		
		try {
			WorldDuplicator.copyFolder(new File(master.getServer()
					.getWorldContainer()
					+ File.separator
					+ worldName
					+ ".template"), new File(master.getServer().getWorldContainer() + File.separator + worldName));
		} catch (IOException e) {
			master.getLogManager().debugLog(Level.SEVERE, "Failed to copy world folder for " + worldName);
			e.printStackTrace();
			return false;
		}
		
		return master.getMVCore().getMVWorldManager().addWorld(worldName, World.Environment.valueOf("NORMAL"), null, null, null, "", false);
	}
	
	/**
	 * 
	 * @return the name of the world
	 */
	public String getName() {
		return worldName;
	}
}