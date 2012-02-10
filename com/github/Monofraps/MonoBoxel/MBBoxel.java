package com.github.Monofraps.MonoBoxel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * This class will hold information of a specific Boxel
 * Boxel name and the corresponding world (if loaded) will be stored here. This class also provides Create/Load/Join/Leave functions.
 * @version 0.4
 * @author Monofraps
 */
public class MBBoxel {

	MonoBoxel master = null;

	World correspondingWorld = null;
	String correspondingWorldName = "";
	boolean worldLoaded = false;
	
	String boxelGenerator = "MonoBoxel";
	String boxelSeed = "ThisSeedIsCool";

	/**
	 * 
	 * @param plugin The reference to the MonoBoxel plugin class
	 * @param worldName The name of the Boxel
	 * @param generator The generator to use for Boxel generation (default for the default Minecraft/Bukkit world generator, empty for the MonoBoxel generator)
	 * @param seed The seed to use for Boxel generation
	 */
	public MBBoxel(MonoBoxel plugin, String worldName, String generator, String seed) {
		master = plugin;
		correspondingWorldName = worldName;

		if (master.GetMVCore().getMVWorldManager().getMVWorld(worldName) != null) {
			correspondingWorld = master.GetMVCore().getMVWorldManager()
					.getMVWorld(correspondingWorldName).getCBWorld();
			worldLoaded = true;
		}
		
		if(generator.isEmpty())
			boxelGenerator = "MonoBoxel";
		else
			boxelGenerator = generator;
		
		boxelSeed = seed;
	}

	/**
	 * Will create the Boxel.
	 * Check if the given player has the right permissions and the perform the create action.
	 * @param player The player that wants to perform the creation.
	 * @return true on success, otherwise false
	 */
	public boolean Create(Player player) {

		boolean isPlayersOwnBoxel = false;
		String boxelOwner = "";

		// should always be true
		if (correspondingWorldName.startsWith(master.getBoxelPrefix()))
			boxelOwner = correspondingWorldName.substring(master.getBoxelPrefix().length());

		if (boxelOwner.equals(player.getName()))
			isPlayersOwnBoxel = true;

		// first check if the Boxel does not exist already
		if (Exists()) {
			master.logger.info("MB was supposed to create Boxel "
					+ correspondingWorldName
					+ " but it already exists. Will load it...");
			if (!Load()) {
				// something went wrong
				master.logger.info("Could not load Boxel.");
				return false;
			}

			// the world exists, but we have no reference to it
			if (correspondingWorld == null) {
				correspondingWorld = master.GetMVCore().getMVWorldManager()
						.getMVWorld(correspondingWorldName).getCBWorld();
			}
		}

		if (isPlayersOwnBoxel) {

			if (master.CheckPermCanCreateOwn(player)) {
				if (master.worldManager.CreateWorld(correspondingWorldName,
						player, boxelGenerator, boxelSeed) != null) {

					correspondingWorld = master.GetMVCore().getMVWorldManager()
							.getMVWorld(correspondingWorldName).getCBWorld();
					return true;

				}
			} else {
				player.sendMessage("You don't have permissions to create your own Boxel!");
				return false;
			}

		} else {
			if (master.CheckPermCanCreateOther(player)) {

				if (master.worldManager.CreateWorld(correspondingWorldName,
						player, boxelGenerator, boxelSeed) != null) {

					correspondingWorld = master.GetMVCore().getMVWorldManager()
							.getMVWorld(correspondingWorldName).getCBWorld();
					return true;

				} else {
					player.sendMessage("Failed to create Boxel.");
					master.logger.severe("Failed to create Boxel "
							+ correspondingWorldName);
					return false;
				}

			} else {
				player.sendMessage("You don't have permissions to create other Boxels!");
				return false;
			}
		}

		return false;
	}

	/**
	 * Will load the world if it exists and is not loaded.
	 * @return true on success, otherwise false
	 */
	public boolean Load() {
		if (!Exists()) {
			master.logger.info("Tried to load a not existsing Boxel.");
			return false;
		}

		if (IsLoaded()) {
			master.logger.info("Boxel is already loaded.");
			return true;
		}

		if (!master.GetMVCore().getMVWorldManager()
				.loadWorld(correspondingWorldName)) {
			// failed to load Boxel
			master.logger.severe("Failed to load Boxel.");
			return false;
		}

		correspondingWorld = master.GetMVCore().getMVWorldManager()
				.getMVWorld(correspondingWorldName).getCBWorld();
		
		return true;
	}

	/**
	 * Checks if the Boxel exists
	 * @return true if the Boxel exists, otherwise false
	 */
	public boolean Exists() {
		return master.worldManager.IsBoxel(correspondingWorldName)[0];
	}

	/**
	 * Checks if the Boxel is loaded
	 * @return true if the Boxel is loaded, otherwise false
	 */
	public boolean IsLoaded() {
		return master.worldManager.IsBoxel(correspondingWorldName)[1];
	}

	/**
	 * Teleports a specific player to the Boxel
	 * Checks the players permissions and sends him to the Boxel
	 * @param player The player that should be ported
	 * @return true on success, otherwise false
	 */
	public boolean Join(Player player) {
		boolean isPlayersOwnBoxel = false;
		String boxelOwner = "";

		// should always be true
		if (correspondingWorldName.startsWith(master.getBoxelPrefix()))
			boxelOwner = correspondingWorldName.substring(master.getBoxelPrefix().length());

		if (boxelOwner.equals(player.getName()))
			isPlayersOwnBoxel = true;

		// before porting the player, save his location
		// save the players current location and teleport
		if (master.getConfig().getBoolean("save-exit-location", true)) {
			// do not save the return/entry location if the player is in a Boxel
			if (!master.worldManager.IsBoxel(player.getWorld().getName())[0]) {
				master.getConfig().set(
						"playeroloc." + player.getName() + ".world",
						player.getWorld().getName());

				master.getConfig().set(
						"playeroloc." + player.getName() + ".position",
						String.valueOf(player.getLocation().getX()) + ","
								+ String.valueOf(player.getLocation().getY())
								+ ","
								+ String.valueOf(player.getLocation().getZ()));

				master.saveConfig();
			}
		}

		// only port the player if the Boxel exists and is loaded and the player
		// has permissions
		if (Exists() && IsLoaded()) {
			if (isPlayersOwnBoxel) {
				if (master.CheckPermCanVisitOwn(player)) {
					return player.teleport(new Location(correspondingWorld, 0,
							7, 0));
				} else {
					player.sendMessage("You don't have permissions to visit your own Boxel!");
					return false;
				}
			} else {
				if (master.CheckPermCanVisitOther(player, boxelOwner)) {
					return player.teleport(new Location(correspondingWorld, 0,
							7, 0));
				} else {
					player.sendMessage("You don't have permissions to visit this Boxel!");
					return false;
				}
			}
		}

		// the Boxel exists, but is not loaded
		if (Exists() && !IsLoaded()) {

			if (isPlayersOwnBoxel) {

				if (master.CheckPermCanVisitOwn(player)) {

					if (Load()) {
						return player.teleport(new Location(correspondingWorld,
								0, 7, 0));
					} else {
						master.logger.info("Failed to load Boxel "
								+ correspondingWorldName + " to join");
					}

				} else {
					player.sendMessage("You don't have permissions to visit your own Boxel!");
					return false;
				}

			} else {

				if (master.CheckPermCanVisitOther(player, boxelOwner)) {
					if (Load()) {
						return player.teleport(new Location(correspondingWorld,
								0, 7, 0));
					} else {

						master.logger.info("Failed to load Boxel "
								+ correspondingWorldName + " to join");
					}
				} else {
					player.sendMessage("You don't have permissions to visit this Boxel!");
					return false;
				}

			}

			return false;
		}

		// the Boxel does not exist, create it - or just return with error:
		// "This Boxel does not exists." ?
		if (!Exists()) {

			player.sendMessage("Boxel does not exists yet. I'll try to create one for you...");
			if (Create(player))
				return player
						.teleport(new Location(correspondingWorld, 0, 7, 0));

		}

		return false;
	}

	/**
	 * Teleports a player back to his original location
	 * @param player The player to teleport
	 * @return true on success, otherwise false
	 */
	public boolean Leave(Player player) {
		MVWorldManager wm = master.GetMVCore().getMVWorldManager();
		MultiverseWorld entryWorld = null;

		if (master.getConfig().getBoolean("save-exit-location", true)) {

			String outWorld = master.getConfig().getString(
					"playeroloc." + player.getName() + ".world", "");
			String outPosition = master.getConfig().getString(
					"playeroloc." + player.getName() + ".position", "");

			// the saved location could not be loaded correctly
			if (outWorld.isEmpty() || outPosition.isEmpty()) {
				master.logger
						.info("save-exit-location was set, but no entry location for player "
								+ player.getName() + " was found.");
				player.teleport(wm.getSpawnWorld().getSpawnLocation());
				return true;
			}

			// we have load the entry location, now see if the entry
			// world is loaded
			entryWorld = wm.getMVWorld(outWorld);
			if (entryWorld == null) {
				// Multiverse getMVWorld returned null, check the
				// unloaded worlds
				if (!wm.getUnloadedWorlds().contains(outWorld)) {
					// the saved world could not be found, so port the
					// player to the default spawn world
					master.logger
							.info("save-exit-location was set, but no entry world "
									+ outWorld
									+ " for player "
									+ player.getName() + " was found.");
					player.teleport(wm.getSpawnWorld().getSpawnLocation());
					return true;
				} else {
					// the entry world of the player is in the
					// Multiverse config, but not loaded; load it!
					if (!wm.loadWorld(outWorld)) {
						master.logger
								.info("Failed to load entry world for player "
										+ player.getName());
						player.sendMessage("Failed to load entry world");
						player.teleport(wm.getSpawnWorld().getSpawnLocation());
						return true;
					} else {
						// Multiverse has load the world
						entryWorld = wm.getMVWorld(outWorld);
					}
				}
			}

			// DEBUG:
			if (entryWorld == null) {
				master.logger.info("entryWorld is still null");
				return false;
			}

			// @TODO: the position does not seem to be the exact player position
			// we found the world, now extract the position
			String[] pos = outPosition.split(",");
			return player.teleport(new Location(entryWorld.getCBWorld(), Double
					.valueOf(pos[0]), Double.valueOf(pos[1]), Double
					.valueOf(pos[2])));

		}

		return false;
	}

	/**
	 * Unloads the Boxel if no player is inside.
	 * @return true on success, otherwise false (will also return false if there are players in this Boxel)
	 */
	public boolean Unload() {
		if(!worldLoaded)
			return false;
		
		if (correspondingWorld.getPlayers().size() == 0) {
			master.logger.info("Unloaded world " + correspondingWorldName
					+ " due to inactivity.");

			worldLoaded = false;

			return master.GetMVCore().getMVWorldManager()
					.unloadWorld(correspondingWorldName);

		}
		return false;
	}
}
