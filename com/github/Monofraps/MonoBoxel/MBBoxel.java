package com.github.Monofraps.MonoBoxel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MBBoxel {

	MonoBoxel master = null;

	World correspondingWorld = null;
	String correspondingWorldName = "";
	boolean worldLoaded = false;

	public MBBoxel(MonoBoxel plugin, String worldName) {
		master = plugin;
		correspondingWorldName = worldName;
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
			master.log.info("MB was supposed to create Boxel "
					+ correspondingWorldName
					+ " but it already exists. Will load it...");
			if (!Load()) {
				// something went wrong
				master.log.info("Could not load Boxel.");
				return false;
			}

			// the world exists, but we have no reference to it
			if (correspondingWorld == null) {
				correspondingWorld = master.GetMVCore().getMVWorldManager()
						.getMVWorld(correspondingWorldName).getCBWorld();
			}

			return true;
		}

		if (isPlayersOwnBoxel) {

			if (master.worldManager.CreateWorld(correspondingWorldName, player) != null) {

				if (Create(player)) {
					return player.teleport(new Location(correspondingWorld, 0,
							7, 0));
				} else {
					player.sendMessage("Failed to create Boxel.");
					master.log.info("Failed to create Boxel "
							+ correspondingWorldName);
					return false;
				}

			} else {
				player.sendMessage("You don't have permissions to create your own Boxel!");
				return false;
			}

		} else {
			if (master.CheckPermCanCreateOther(player)) {

				if (master.worldManager.CreateWorld(correspondingWorldName,
						player) != null) {
					return true;
				} else {
					player.sendMessage("Failed to create Boxel.");
					master.log.info("Failed to create Boxel "
							+ correspondingWorldName);
					return false;
				}

			} else {
				player.sendMessage("You don't have permissions to create other Boxels!");
				return false;
			}
		}
	}

	// load the Boxel/world if it was unloaded
	public boolean Load() {
		if (!Exists()) {
			master.log.info("Tried to load a not existsing Boxel.");
			return false;
		}

		if (IsLoaded()) {
			master.log.info("Boxel is already loaded.");
			return true;
		}

		if (!master.GetMVCore().getMVWorldManager()
				.loadWorld(correspondingWorldName)) {
			// failed to load Boxel
			master.log.info("Failed to load Boxel.");
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
						master.log.info("Failed to load Boxel "
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

						master.log.info("Failed to load Boxel "
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
			if(Create(player))
				return player.teleport(new Location(correspondingWorld,
						0, 7, 0));

		}

		return false;
	}
}
