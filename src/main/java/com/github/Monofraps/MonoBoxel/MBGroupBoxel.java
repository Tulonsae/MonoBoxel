package com.github.Monofraps.MonoBoxel;


import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.github.Monofraps.MonoBoxel.MBPermissionManager.MBPermission;
import com.github.Monofraps.MonoBoxel.Utils.HashMD5;


/**
 * A class for holding information about group boxels
 * 
 * @author Monofraps
 */
public class MBGroupBoxel extends MBBoxel {
	
	private String	passwdMD5	= "";
	
	/**
	 * 
	 * @param plugin
	 *            The reference to the main plugin class
	 * @param worldName
	 *            The name of the new Boxel
	 * @param generator
	 *            The world generator to use for this Boxel
	 * @param seed
	 *            The seed for the new Boxel
	 */
	public MBGroupBoxel(MonoBoxel plugin, String worldName, String generator,
			String seed) {
		super(plugin, worldName, generator, seed);
		
	}
	
	@Override
	public boolean Create(Player player) {
		
		if (!master.getPermManager().hasPermission(player,
				MBPermission.CAN_CREATE_GROUP_BOXEL)) {
			master.getPermManager().SendNotAllowedMessage(player);
			return false;
		}
		
		if (super.DoCreate(player)) {
			correspondingWorld = master.GetMVCore().getMVWorldManager()
					.getMVWorld(correspondingWorldName).getCBWorld();
			return true;
		} else {
			player.sendMessage("Failed to create group Boxel.");
			master.getLogManager().severe(
					"Failed to create group Boxel " + correspondingWorldName);
			return false;
		}
		
	}
	
	public void setPasswordHash(String passwd) {
		// let's make group Boxels at least a little bit safe - md5-sum the password, so we don't
		// have to store the plaintext password in the config
		passwdMD5 = HashMD5.Hash(passwd);
	}
	
	/**
	 * Override the base class Join. We can not use it since we have to pass a password to the Join
	 * function.
	 */
	@Override
	public boolean Join(Player player) {
		master.getLogManager().warning("Join was called on a goup Boxel.");
		return false;
	}
	
	/**
	 * Teleports the player to the Boxel
	 * 
	 * @param player
	 *            The player to teleport
	 * @param passwd
	 *            The password that the player has given to join the Boxel
	 * @return true on success, otherwise false
	 */
	public boolean Join(Player player, String passwd) {
		// validate password
		if (!HashMD5.Hash(passwd).equals(passwdMD5)) {
			player.sendMessage("The security man said: NO! - Have you entered the correct password?");
			return false;
		}
		
		if (!master.getPermManager().hasPermission(player,
				MBPermission.CAN_VISIT_GROUP_BOXEL)) {
			master.getPermManager().SendNotAllowedMessage(player);
			return false;
		}
		
		if (!isLoaded())
			if (!Load()) {
				master.getLogManager()
						.severe("Failed to load group Boxel: "
								+ correspondingWorldName);
				master.getLogManager()
						.debugLog(
								Level.WARNING,
								"Failed to load group Boxel: "
										+ correspondingWorldName);
				return false;
			}
		
		// before porting the player, save his location
		if (master.getConfig().getBoolean("save-exit-location", true)) {
			// do not save the return/entry location if the player is in a Boxel
			if (!master.getMBWorldManager()
					.isBoxel(player.getWorld().getName())[0]) {
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".world",
								player.getWorld().getName());
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".position",
								player.getLocation().toVector());
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".yaw",
								player.getLocation().getYaw());
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".pitch",
								player.getLocation().getPitch());
				master.getDataConfig().saveDataConfig();
			}
		}
		
		if (player.teleport(correspondingWorld.getSpawnLocation()))
			return true;
		
		master.getLogManager().debugLog(Level.WARNING,
				"Player teleport failed!");
		player.sendMessage("Teleport failed!");
		return false;
	}
	
	/**
	 * 
	 * @return the password hash
	 */
	public String getPasswordHash() {
		return passwdMD5;
	}
	
}
