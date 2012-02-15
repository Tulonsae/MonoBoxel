package com.github.Monofraps.MonoBoxel.Adventure;


import java.io.File;
import java.io.IOException;

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
		master.getMVCore().getMVWorldManager().deleteWorld(worldName);
		
		try {
			WorldDuplicator.copyFolder(new File(master.getServer()
					.getWorldContainer()
					+ File.separator
					+ worldName
					+ ".template"), new File(master.getServer().getWorldContainer() + File.separator + worldName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		master.getMVCore().getMVWorldManager().addWorld(worldName, World.Environment.valueOf("NORMAL"), null, null, null, "", false);
		
		return false;
	}
}