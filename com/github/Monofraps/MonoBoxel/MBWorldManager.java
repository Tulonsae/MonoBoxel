package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MBWorldManager {

	MonoBoxel master = null;	
	List<MBBoxel> boxels = null;

	private boolean worldsCounted = false;

	public MBWorldManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
	}

	public int GetNumberOfBoxels() {
		return boxels.size();
	}

	// should be called CountWorlds
	public void LoadWorlds() {
		if (worldsCounted)
			return;

		worldsCounted = true;

		Collection<MultiverseWorld> worlds = master.GetMVCore()
				.getMVWorldManager().getMVWorlds();
		for (MultiverseWorld w : worlds) {
			if (w.getName().startsWith("BOXEL_")) {
				boxels.add(new MBBoxel(master, w.getName(), "", ""));
			}
		}

		Collection<String> unloadedWorlds = master.GetMVCore()
				.getMVWorldManager().getUnloadedWorlds();
		for (String w : unloadedWorlds) {
			master.logger.info(w);
			if (w.startsWith("BOXEL_")) {
				boxels.add(new MBBoxel(master, w, "", ""));
			}
		}
	}

	public void CheckForUnusedWorlds() {
		for(MBBoxel box : boxels)
		{
			if(box.Unload())
				master.Log("Unloaded Boxel " + box.correspondingWorldName);
		}
	}

	// return[0] -> Boxel exists
	// return[1] -> Boxel is loaded
	public boolean[] IsBoxel(String name) {
		boolean[] result = new boolean[2];

		// @TODO: an option for the Boxel prefix
		if (!name.startsWith("BOXEL_")) {
			name = "BOXEL_" + name;
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
	
	
	public boolean AddBoxel(String name, boolean create, Player player, String generator, String seed)
	{
		if(!name.startsWith("BOXEL_"))
			name = "BOXEL_" + name;
		
		MBBoxel boxel = new MBBoxel(master, name, generator, seed);
		
		
		if(create)
			if(!boxel.Create(player))
				return false;
		
		return boxels.add(boxel);
	}

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
