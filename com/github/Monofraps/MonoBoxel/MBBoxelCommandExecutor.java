package com.github.Monofraps.MonoBoxel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MBBoxelCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

	public MBBoxelCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return true;
		}

		Player player = (Player) sender;
		MVWorldManager wm = null;
		String boxelName = "";
		World target = null;
		boolean playersOwnBoxel = true;


		// get the MV Core
		if (master.GetMVCore() == null) {
			master.log.info("Failed to get Muliverse-Core.");
			player.sendMessage("Failed to get Multiverse-Core. Please contact a server admin.");
			return false;
		}
		// ...and it's world manager
		wm = master.GetMVCore().getMVWorldManager();

		// boxel names are always: BOXEL_<playername>
		boxelName = "BOXEL_" + player.getName();

		if (args.length > 0) {
			playersOwnBoxel = false;
			boxelName = "BOXEL_" + args[0];

			// failures in the following block will result in teleporting the
			// player to the default spawn world and returning "true"
			if (args[0].equals("getmeout")) {

				// if we have saved the "exit" location, we can load it from the
				// config
				if (master.getConfig().getBoolean("save-exit-location", true)) {
					
					String outWorld = master.getConfig().getString(
							"playeroloc." + player.getName() + ".world", "");
					String outPosition = master.getConfig().getString(
							"playeroloc." + player.getName() + ".position", "");

					// the saved location could not be loaded correctly
					if (outWorld.isEmpty() || outPosition.isEmpty()) {
						master.log
								.info("save-exit-location was set, but no entry location for player "
										+ player.getName() + " was found.");
						player.teleport(wm.getSpawnWorld().getSpawnLocation());
						return true;
					}

					// we have load the entry location, now see if the entry
					// world is loaded
					MultiverseWorld entryWorld = wm.getMVWorld(outWorld);
					if (entryWorld == null) {
						// Multiverse getMVWorld returned null, check the
						// unloaded worlds
						if (!wm.getUnloadedWorlds().contains(outWorld)) {
							// the saved world could not be found, so port the
							// player to the default spawn world
							master.log
									.info("save-exit-location was set, but no entry world "
											+ outWorld
											+ " for player "
											+ player.getName() + " was found.");
							player.teleport(wm.getSpawnWorld()
									.getSpawnLocation());
							return true;
						} else {
							// the entry world of the player is in the
							// Multiverse config, but not loaded; load it!
							if (!wm.loadWorld(outWorld)) {
								master.log
										.info("Failed to load entry world for player "
												+ player.getName());
								player.sendMessage("Failed to load entry world");
								player.teleport(wm.getSpawnWorld()
										.getSpawnLocation());
								return true;
							} else {
								// Multiverse has load the world
								entryWorld = wm.getMVWorld(outWorld);
							}
						}
					}

					// DEBUG:
					if (entryWorld == null) {
						master.log.info("entryWorld is still null");
						return false;
					}

					// @TODO: the position does not seem to be the exact player position
					// we found the world, now extract the position
					String[] pos = outPosition.split(",");
					player.teleport(new Location(entryWorld.getCBWorld(),
							Double.valueOf(pos[0]), Double.valueOf(pos[1]),
							Double.valueOf(pos[2])));

				}

				return true;
			}
		}

		// validate access permissions for the user per boxel
		if (master.getConfig().getBoolean("per-boxel-permissions", false)) {

			// per boxel permissions
			if (!player.hasPermission("monoboxel.boxel.visit." + boxelName)
					&& !playersOwnBoxel) {
				player.sendMessage("You don't have permissions to visit this boxel.");
				return false;
			}
		} else {
			// global visit permissions
			if (!player.hasPermission("monoboxel.boxel.visit")
					&& !playersOwnBoxel) {

				player.sendMessage("You don't have permissions to visit this boxel.");
				return false;
			}
		}

		// create or load the world
		target = master.worldManager.CreateWorld(boxelName, player);

		// something went wrong...
		if (target == null) {
			master.log.info("Boxel \"" + boxelName
					+ "\" could not be found or created.");
			player.sendMessage("The Boxel was not found or could not be created. Please contact a server admin.");
			return false;
		}

		// save the players current location and teleport
		if (master.getConfig().getBoolean("save-exit-location", true)) {
			// do not save the return/entry location if the player is in a Boxel
			if(!master.worldManager.IsBoxel(player.getWorld().getName())[0])
			{			
				master.getConfig().set("playeroloc." + player.getName() + ".world",
						player.getWorld().getName());
	
				master.getConfig().set(
						"playeroloc." + player.getName() + ".position",
						String.valueOf(player.getLocation().getX()) + ","
								+ String.valueOf(player.getLocation().getY()) + ","
								+ String.valueOf(player.getLocation().getZ()));
	
				master.saveConfig();
			}
		}

		if (player.teleport(new Location(target, 0, 7, 0)))
			return true;

		return false;
	}
}
