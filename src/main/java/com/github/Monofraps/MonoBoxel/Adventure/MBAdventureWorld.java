package com.github.Monofraps.MonoBoxel.Adventure;


import com.github.Monofraps.MonoBoxel.MonoBoxel;
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
	
	String			worldName		= "";
	MultiverseWorld	adventureWorld	= null;
	MonoBoxel		master			= null;
	
	/**
	 * 
	 * @param plugin
	 * @param worldName
	 */
	public MBAdventureWorld(MonoBoxel plugin, String worldName) {
		master = plugin;
		this.worldName = worldName;
		
		if (isWorldLoaded())
			adventureWorld = master.getMVCore().getMVWorldManager()
					.getMVWorld(worldName);
		
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isWorldLoaded() {
		if (master.getMVCore().getMVWorldManager().getMVWorld(worldName) != null)
			return true;
		
		if (master.getMVCore().getMVWorldManager().getUnloadedWorlds()
				.contains(worldName))
			return false;
		
		return false;
	}
}
