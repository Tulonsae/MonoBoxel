package com.github.Monofraps.MonoBoxel.Adventure;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.github.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Manages adventure worlds.
 * 
 * @author Monofraps
 * 
 */
public class MBAdventureWorldManager {
	
	private List<MBAdventureWorld>	adventureWorlds	= null;
	private MonoBoxel				master			= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the main plugin class
	 */
	public MBAdventureWorldManager(MonoBoxel plugin) {
		master = plugin;
		adventureWorlds = new ArrayList<MBAdventureWorld>();
	}
	
	/**
	 * Creates an adventure world from a craft bukkit world.
	 * 
	 * @param world
	 * @return false
	 */
	public boolean CreateAdventureWorldFromWorld(World world) {
		return false;
	}
}
