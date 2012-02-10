package com.github.Monofraps.MonoBoxel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MBBoxel {

	MonoBoxel master = null;

	World correspondingWorld = null;
	String correspondingWorldName = "";
	boolean worldLoaded = false;

	String boxelGenerator = "MonoBoxel";
	String boxelSeed = "ThisSeedIsCool";

	public MBBoxel(MonoBoxel plugin, String worldName, String generator,
			String seed) {
		master = plugin;
		correspondingWorldName = worldName;

		if (master.GetMVCore().getMVWorldManager().getMVWorld(worldName) != null) {
			correspondingWorld = master.GetMVCore().getMVWorldManager()
					.getMVWorld(correspondingWorldName).getCBWorld();
			worldLoaded = true;
		}

		if (generator.isEmpty())
			boxelGenerator = "MonoBoxel";
		else
			boxelGenerator = generator;

		boxelSeed = seed;
	}

	public boolean Create(Player player) {

		boolean isPlayersOwnBoxel = false;
		String boxelOwner = "";

		// should always be true
		if (correspondingWorldName.startsWith("BOXEL_"))
			boxelOwner = correspondingWorldName.substring(6);

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
					master.logger.info("Failed to create Boxel "
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

	// load the Boxel/world if it was unloaded
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
			master.logger.info("Failed to load Boxel.");
			return false;
		}

		correspondingWorld = master.GetMVCore().getMVWorldManager()
				.getMVWorld(correspondingWorldName).getCBWorld();

		return true;
	}

	public boolean Exists() {
		return master.worldManager.IsBoxel(correspondingWorldName)[0];
	}

	public boolean IsLoaded() {
		return master.worldManager.IsBoxel(correspondingWorldName)[1];
	}

	public boolean Join(Player player) {
		boolean isPlayersOwnBoxel = false;
		String boxelOwner = "";

		// should always be true
		if (correspondingWorldName.startsWith("BOXEL_"))
			boxelOwner = correspondingWorldName.substring(6);

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
					player.sendMessage("You don't have permissions to visit your own Boxel! 1");
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
					player.sendMessage("You don't have permissions to visit your own Boxel! 2");
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

	// unload the Boxel if no player is in
	public boolean Unload() {

		if (isEmpty()) {
			if (master.GetMVCore().getMVWorldManager()
					.unloadWorld(correspondingWorldName)) {
				worldLoaded = false;
				master.logger.info("Unloaded world " + correspondingWorldName
						+ " due to inactivity.");
				return true;
			} else
				return false;
		}

		return false;
	}

	public boolean isEmpty() {
		if (!worldLoaded)
			return true;

		if (correspondingWorld.getPlayers().size() == 0)
			return true;
		else
			return false;
	}
}
