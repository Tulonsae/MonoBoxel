package com.github.Monofraps.MonoBoxel;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;


public class MBBoxelManager {
	
	/**
	 * The Runnable for the delayed unload task
	 * 
	 * @author Monofraps
	 */
	private class BoxelUnloadRunnable implements Runnable {
		
		MBBoxel		box		= null;
		MonoBoxel	master	= null;
		
		public BoxelUnloadRunnable(MonoBoxel monoBoxel, MBBoxel boxel) {
			master = monoBoxel;
			box = boxel;
		}
		
		@Override
		public void run() {
			master.getLogManager().info("runable execute");
			if (box.Unload()) master.logger.info("Unloaded Boxel "
					+ box.correspondingWorldName);
			box.unloadTaskId = -1;
		}
	}
	
	List<MBBoxel>		boxels			= null;
	
	private MonoBoxel	master			= null;
	private boolean		worldsCounted	= false;
	
	/**
	 * 
	 * @param plugin
	 *            The reference to the core plugin instance
	 */
	public MBBoxelManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
	}
	
	/**
	 * Loads the Boxels. (@TODO: store Boxels in a separate config file)
	 */
	public void LoadConfig() {
		if (worldsCounted) return;
		
		worldsCounted = true;
		
		Collection<MultiverseWorld> worlds = master.GetMVCore()
				.getMVWorldManager().getMVWorlds();
		for (MultiverseWorld w : worlds) {
			if (w.getName().startsWith(master.getBoxelPrefix())) {
				AddBoxel(w.getName(), false, null, "", "");
			}
		}
		
		Collection<String> unloadedWorlds = master.GetMVCore()
				.getMVWorldManager().getUnloadedWorlds();
		for (String w : unloadedWorlds) {
			if (w.startsWith(master.getBoxelPrefix())) {
				AddBoxel(w, false, null, "", "");
			}
		}
	}
	
	/**
	 * Adds a Boxel to the boxels list
	 * 
	 * @param name
	 *            Name of the Boxel
	 * @param create
	 *            If true the Boxels create function will be called
	 * @param player
	 *            The player that wants to perform this action (only needed if
	 *            create is true)
	 * @param generator
	 *            The generator to use to generate the Boxel (only needed if
	 *            create is true and you don't want to use the MonoBoxel
	 *            generator)
	 * @param seed
	 *            The seed to use for generating the Boxel (only needed if
	 *            create is true)
	 * @return true if the Boxel was added successfully
	 */
	public boolean AddBoxel(String name, boolean create, Player player,
			String generator, String seed) {
		// check for duplicates
		for (MBBoxel b : boxels) {
			if (b.getCorrespondingWorldName().equals(name)) return true;
		}
		
		master.getLogManager().info("Created new entry in boxels.");
		
		if (!name.startsWith(master.getBoxelPrefix())) name = master
				.getBoxelPrefix() + name;
		
		MBBoxel boxel = new MBBoxel(master, name, generator, seed);
		
		if (create) if (!boxel.Create(player)) return false;
		
		return boxels.add(boxel);
	}
	
	/**
	 * 
	 * @return A reference to the list of all registered (loaded + unloaded) Boxels
	 */
	public List<MBBoxel> getBoxels() {
		return boxels;
	}
	
	/**
	 * 
	 * @return The total number of all registered (loaded + unloaded) Boxels
	 */
	public long getNumBoxels() {
		return boxels.size();
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
	
	public boolean[] isBoxel(World world) {
		return this.isBoxel(world.getName());
	}
	
	/**
	 * Starts/Cancels a delayed world unload if a Boxel is empty
	 */
	public void CheckForUnusedWorlds() {
		for (MBBoxel box : boxels) {
			if (box.isEmpty() && box.unloadTaskId == -1 && box.isLoaded()) {
				box.unloadTaskId = master
						.getServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(
								master,
								new BoxelUnloadRunnable(master, box),
								master.getConfig().getInt(
										"world-unload-period", 60) * 20);
			} else if (!box.isEmpty() && box.unloadTaskId != -1) {
				master.getServer().getScheduler().cancelTask(box.unloadTaskId);
				box.unloadTaskId = -1;
			}
		}
	}
}
