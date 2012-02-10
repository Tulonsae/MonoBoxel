package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MBBoxelManager {

	MonoBoxel master = null;
	List<MBBoxel> boxels = null;

	public MBBoxelManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
	}

	public void LoadConfig() {

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

	public List<MBBoxel> getBoxels() {
		return boxels;
	}
	
	public long getNumBoxels()
	{
		return boxels.size();
	}
	
	/**
	 * Checks if the given name is an existing Boxel and if it is loaded.
	 * @param name Name to check.
	 * @return A boolean array. Component 1(0) is true if the Boxel exists; Component 2(1) is true if the Boxel is loaded.
	 */
	public boolean[] isBoxel(String name) {
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
}
