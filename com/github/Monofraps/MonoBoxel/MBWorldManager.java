package com.github.Monofraps.MonoBoxel;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class MBWorldManager {

	MonoBoxel master;

	public MBWorldManager(MonoBoxel plugin) {
		master = plugin;
	}

	public World CreateWorld(String name, Player owner, MVWorldManager wm) {

		World result = null;

		if (wm.getMVWorld(name) != null) {
			owner.sendMessage("Found your boxel. Will port you there now...");
			return wm.getMVWorld(name).getCBWorld();
		}

		if (!name.endsWith(owner.getName())) {
			owner.sendMessage("You requested a boxel that is not created and does not belong to your username.");
			return null;
		}

		if (!owner.hasPermission("monoboxel.boxel.create"))
		{
			owner.sendMessage("You don't seem to have a boxel yet. You also don't have permissions to create one... :(");
			return null;
		}

		owner.sendMessage("You don't seem to have a boxel yet. Will create one for you now...");

		if (wm.addWorld(name, World.Environment.valueOf("NORMAL"), "seed",
				WorldType.valueOf("FLAT"), false, "MonoBoxel")) {

			result = wm.getMVWorld(name).getCBWorld();
			master.log.info("Boxel " + name + " created!");

			wm.getMVWorld(name).setAllowAnimalSpawn(false);
			wm.getMVWorld(name).setAllowMonsterSpawn(false);
			wm.getMVWorld(name).setEnableWeather(false);
			wm.getMVWorld(name).setGameMode("CREATIVE");
			wm.getMVWorld(name).setPVPMode(false);

			master.log.info("Boxel created for Player: " + owner.getName());
			owner.sendMessage("Boxel created! Will port you there now...");
		}

		return result;
	}
}
