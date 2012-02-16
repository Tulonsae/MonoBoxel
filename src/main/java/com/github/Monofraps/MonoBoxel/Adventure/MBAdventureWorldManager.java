package com.github.Monofraps.MonoBoxel.Adventure;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.github.Monofraps.MonoBoxel.MonoBoxel;
import com.github.Monofraps.MonoBoxel.Utils.WorldDuplicator;


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
	public boolean addAdventureWorld(World world) {
		WorldDuplicator.DuplicateWorld(world, master, world.getName()
				+ ".template");
		return adventureWorlds.add(new MBAdventureWorld(master, world.getName()));
	}
	
	/**
	 * 
	 * @return a list of all adventure worlds
	 */
	public List<MBAdventureWorld> getAdventureWorlds() {
		return adventureWorlds;
	}
	
}
