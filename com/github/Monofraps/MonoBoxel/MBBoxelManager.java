package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MBBoxelManager {
	
	private class BoxelUnloadRunnable implements Runnable {

		MBBoxel box = null;
		MonoBoxel master = null;

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

	MonoBoxel master = null;
	List<MBBoxel> boxels = null;
	
	private boolean worldsCounted = false;

	public MBBoxelManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
	}

	public void LoadConfig() {
		if (worldsCounted)
			return;

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
			master.logger.info(w);
			if (w.startsWith(master.getBoxelPrefix())) {
				AddBoxel(w, false, null, "", "");
			}
		}
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
		master.getLogManager().info("Created new entry in boxels.");
		
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
	
	/**
	 * Unloads unused worlds to save RAM.
	 */
	public void CheckForUnusedWorlds() {
		for (MBBoxel box : boxels) {
			if (box.isEmpty() && box.unloadTaskId == -1 && box.IsLoaded()) {
				master.logger.info("started delayed task");
				box.unloadTaskId = master
						.getServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(
								master,
								new BoxelUnloadRunnable(master, box),
								master.getConfig().getInt(
										"world-unload-period", 60) * 20);
			} else if (!box.isEmpty() && box.unloadTaskId != -1) {
				master.logger.info("canceled delayed task" + String.valueOf(box.isEmpty()) + String.valueOf(box.unloadTaskId));
				master.getServer().getScheduler().cancelTask(box.unloadTaskId);
			}
		}
	}
}
