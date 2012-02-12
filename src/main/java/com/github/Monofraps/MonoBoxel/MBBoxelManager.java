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
			if (box.Unload())
				master.logger.info("Unloaded Boxel "
						+ box.correspondingWorldName);
			box.unloadTaskId = -1;
		}
	}
	
	private List<MBBoxel>		boxels			= null;
	private List<MBGroupBoxel>	groupBoxels		= null;
	
	private MonoBoxel			master			= null;
	private boolean				worldsCounted	= false;
	
	/**
	 * 
	 * @param plugin
	 *            The reference to the core plugin instance
	 */
	public MBBoxelManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
		groupBoxels = new ArrayList<MBGroupBoxel>();
	}
	
	/**
	 * Loads the Boxels. (@TODO: store Boxels in a separate config file)
	 */
	public void LoadConfig() {
		if (worldsCounted)
			return;
		
		worldsCounted = true;
		
		/*
		 * Collection<MultiverseWorld> worlds = master.GetMVCore()
		 * .getMVWorldManager().getMVWorlds();
		 * for (MultiverseWorld w : worlds) {
		 * if (w.getName().startsWith(master.getBoxelPrefix())) {
		 * AddBoxel(w.getName(), false, null, "", "");
		 * }
		 * }
		 * 
		 * Collection<String> unloadedWorlds = master.GetMVCore()
		 * .getMVWorldManager().getUnloadedWorlds();
		 * for (String w : unloadedWorlds) {
		 * if (w.startsWith(master.getBoxelPrefix())) {
		 * AddBoxel(w, false, null, "", "");
		 * }
		 * }
		 */
		
		List<String> boxelNames = master.getDataConfig().getDataConfig()
				.getStringList("boxels.boxels");
		List<String> groupBoxelNames = master.getDataConfig().getDataConfig()
				.getStringList("boxels.groupboxels");
		
		if (boxelNames != null)
			for (String s : boxelNames)
				AddBoxel(s, false, null, "", "");
		
		if (groupBoxelNames != null)
			for (String s : groupBoxelNames)
				AddGroupBoxel(s, master.getDataConfig().getDataConfig()
						.getString("boxels.groupboxels." + s), false, null, "",
						"");
		
	}
	
	public void SaveBoxels() {
		if ((getNumBoxels() == 0) && (getNumGroupBoxels() == 0))
			return;
		
		// save all "normal" Boxels to the data confif
		List<String> boxelNames = new ArrayList<String>();
		for (MBBoxel box : boxels)
			boxelNames.add(box.getCorrespondingWorldName());
		
		List<String> groupBoxelNames = new ArrayList<String>();
		for (MBGroupBoxel box : groupBoxels)
			groupBoxelNames.add(box.getCorrespondingWorldName());
		
		master.getDataConfig().getDataConfig()
				.set("boxels.boxels", boxelNames.toArray());
		
		master.getDataConfig().getDataConfig()
				.set("boxels.groupboxels", groupBoxelNames.toArray());
		
		for (MBGroupBoxel box : groupBoxels)
			master.getDataConfig()
					.getDataConfig()
					.set("boxels.groupboxels.passwords"
							+ box.getCorrespondingWorldName(),
							box.getPassword());
		
		master.getDataConfig().saveDataConfig();
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
			if (b.getCorrespondingWorldName().equals(name))
				return true;
		}
		
		master.getLogManager().info("Created new entry in boxels.");
		
		if (!name.startsWith(master.getBoxelPrefix()))
			name = master.getBoxelPrefix() + name;
		
		MBBoxel boxel = new MBBoxel(master, name, generator, seed);
		
		if (create)
			if (!boxel.Create(player))
				return false;
		
		return boxels.add(boxel);
	}
	
	public boolean AddGroupBoxel(String name, String boxelPassword,
			boolean create, Player player, String generator, String seed) {
		
		for (MBGroupBoxel b : groupBoxels) {
			if (b.getCorrespondingWorldName().equals(name))
				return true;
		}
		
		if (!name.startsWith(master.getBoxelPrefix()))
			name = master.getBoxelPrefix() + name;
		
		MBGroupBoxel boxel = new MBGroupBoxel(master, name, generator, seed);
		boxel.setPasswd(boxelPassword);
		
		if (create)
			if (!boxel.Create(player))
				return false;
		
		master.getLogManager().info("Create new entry in boxels.");
		
		return groupBoxels.add(boxel);
	}
	
	/**
	 * 
	 * @return A reference to the list of all registered (loaded + unloaded) Boxels
	 */
	public List<MBBoxel> getBoxels() {
		return boxels;
	}
	
	public List<MBGroupBoxel> getGroupBoxels() {
		return groupBoxels;
	}
	
	/**
	 * 
	 * @return The total number of all registered (loaded + unloaded) Boxels
	 */
	public long getNumBoxels() {
		return boxels.size();
	}
	
	public long getNumGroupBoxels() {
		return groupBoxels.size();
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
		
		if (master.GetMVCore().getMVWorldManager().getUnloadedWorlds()
				.contains(name)) {
			result[0] = true;
			result[1] = false;
		}
		
		/*
		 * Collection<String> unloadedWorlds = master.GetMVCore()
		 * .getMVWorldManager().getUnloadedWorlds();
		 * for (String w : unloadedWorlds) {
		 * if (w.equals(name)) {
		 * 
		 * }
		 * }
		 */
		return result;
	}
	
	public boolean[] isBoxel(World world) {
		return this.isBoxel(world.getName());
	}
	
	/**
	 * Starts/Cancels a delayed world unload if a Boxel is empty
	 */
	public void CheckForUnusedWorlds() {
		
		// normal boxels
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
			} else
				if (!box.isEmpty() && box.unloadTaskId != -1) {
					master.getServer().getScheduler()
							.cancelTask(box.unloadTaskId);
					box.unloadTaskId = -1;
				}
		}
		
		// group boxels
		for (MBGroupBoxel box : groupBoxels) {
			if (box.isEmpty() && box.unloadTaskId == -1 && box.isLoaded()) {
				box.unloadTaskId = master
						.getServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(
								master,
								new BoxelUnloadRunnable(master, box),
								master.getConfig().getInt(
										"world-unload-period", 60) * 20);
			} else
				if (!box.isEmpty() && box.unloadTaskId != -1) {
					master.getServer().getScheduler()
							.cancelTask(box.unloadTaskId);
					box.unloadTaskId = -1;
				}
		}
	}
}