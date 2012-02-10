package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;


/**
 * MonoBoxel WorldManager class
 * This class holds a list of all registered Boxels. It also unloads unused Boxels and creates new.
 * @version 0.4
 * @author Monofraps
 */
public class MBWorldManager {

	MonoBoxel master = null;	
	List<MBBoxel> boxels = null;

	private boolean worldsCounted = false;

	/**
	 * 
	 * @param plugin Reference to the plugin class
	 */
	public MBWorldManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
	}

	/**
	 * Returns the number of Boxels currently registered
	 * @return Number of registered Boxels
	 */
	public int GetNumberOfBoxels() {
		return boxels.size();
	}

	/**
	 * Called on startup. Will find Boxels in the Multiverse-Core config.
	 * Searches all loaded and unloaded worlds for worlds with the name BOXEL_<someName> and will populate the boxels list.
	 */
	public void LoadWorlds() {
		if (worldsCounted)
			return;

		worldsCounted = true;

		Collection<MultiverseWorld> worlds = master.GetMVCore()
				.getMVWorldManager().getMVWorlds();
		for (MultiverseWorld w : worlds) {
			if (w.getName().startsWith(master.getBoxelPrefix())) {
				boxels.add(new MBBoxel(master, w.getName(), "", ""));
			}
		}

		Collection<String> unloadedWorlds = master.GetMVCore()
				.getMVWorldManager().getUnloadedWorlds();
		for (String w : unloadedWorlds) {
			master.logger.info(w);
			if (w.startsWith(master.getBoxelPrefix())) {
				boxels.add(new MBBoxel(master, w, "", ""));
			}
		}
	}

	/**
	 * Unloads unused worlds to save RAM.
	 */
	public void CheckForUnusedWorlds() {
		for(MBBoxel box : boxels)
		{
			if(box.Unload())
				master.logger.info("Unloaded Boxel " + box.correspondingWorldName);
		}
	}

	/**
	 * Checks if the given name is an existing Boxel and if it is loaded.
	 * @param name Name to check.
	 * @return A boolean array. Component 1(0) is true if the Boxel exists; Component 2(1) is true if the Boxel is loaded.
	 */
	public boolean[] IsBoxel(String name) {
		boolean[] result = new boolean[2];

		// @TODO: an option for the Boxel prefix
		if (!name.startsWith(master.getBoxelPrefix())) {
			name = master.getBoxelPrefix() + name;
		}

		Collection<MultiverseWorld> worlds = master.GetMVCore()
				.getMVWorldManager().getMVWorlds();
		for (MultiverseWorld w : worlds) {
			if (w.getName().equals(name)) {
				result[0] = true;
				result[1] = true;
			}
		}

		Collection<String> unloadedWorlds = master.GetMVCore()
				.getMVWorldManager().getUnloadedWorlds();
		for (String w : unloadedWorlds) {
			if (w.equals(name)) {
				result[0] = true;
				result[1] = false;
			}
		}

		return result;
	}
	
	/**
	 * Adds a Boxel to the boxels list
	 * @param name Name of the Boxel
	 * @param create If true the Boxels create function will be called
	 * @param player The player that wants to perform this action (only needed if create is true)
	 * @param generator The generator to use to generate the Boxel (only needed if create is true and you don't want to use the MonoBoxel generator)
	 * @param seed The seed to use for generating the Boxel (only needed if create is true)
	 * @return true if the Boxel was added successfully
	 */
	public boolean AddBoxel(String name, boolean create, Player player, String generator, String seed)
	{
		if(!name.startsWith(master.getBoxelPrefix()))
			name = master.getBoxelPrefix() + name;
		
		MBBoxel boxel = new MBBoxel(master, name, generator, seed);
		
		
		if(create)
			if(!boxel.Create(player))
				return false;
		
		return boxels.add(boxel);
	}

		
	public List<MBBoxel> getBoxels()
	{
		return boxels;
	}
}
