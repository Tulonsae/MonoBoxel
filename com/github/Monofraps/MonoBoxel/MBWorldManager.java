package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
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

	// return[0] -> Boxel exists
	// return[1] -> Boxel is loaded
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

	/**
	 * Creates a new Boxel world
	 * @param name Name of the Boxel
	 * @param owner The player that wants to perform this action
	 * @param generator The generator to use to generate the Boxel (only needed if you don't want to use the MonoBoxel generator)
	 * @param seed The seed to use for generating the Boxel
	 * @return
	 */
	public World CreateWorld(String name, Player owner, String generator, String seed) {

		MVWorldManager wm = master.GetMVCore().getMVWorldManager();
		World result = null;

		if (wm.getMVWorld(name) != null) {
			owner.sendMessage("Found your boxel. Will port you there now...");
			return wm.getMVWorld(name).getCBWorld();
		} else {
			// now check unloaded worlds too
			Collection<String> uworlds = wm.getUnloadedWorlds();
			if (uworlds.contains(name)) {
				owner.sendMessage("Found your boxel. Will have to load it and port you there...");
				wm.loadWorld(name);
				return wm.getMVWorld(name).getCBWorld();
			}
		}

		if (GetNumberOfBoxels() >= master.getConfig().getInt("max-boxel-count", 20)) {
			owner.sendMessage("The maximum number of boxels on this server is reached. Please contact a server admin.");
			return null;
		}

		owner.sendMessage("You don't seem to have a boxel yet. Will create one for you now...");

		if(generator.equals("default"))
			generator = "";
		
		if (wm.addWorld(name, World.Environment.valueOf("NORMAL"), seed,
				WorldType.valueOf("NORMAL"), false, generator)) {

			result = wm.getMVWorld(name).getCBWorld();
			if (result == null)
			master.logger.info("Boxel " + name + " created!");
			wm.getMVWorld(name).setAllowAnimalSpawn(false);
			wm.getMVWorld(name).setAllowMonsterSpawn(false);
			wm.getMVWorld(name).setEnableWeather(false);
			wm.getMVWorld(name).setGameMode("CREATIVE");
			wm.getMVWorld(name).setPVPMode(false);
			wm.getMVWorld(name).setAutoLoad(false);

			master.logger.info("Boxel created for Player: " + owner.getName());
			owner.sendMessage("Boxel created! Will port you there now...");
		}

		return result;
	}
}
