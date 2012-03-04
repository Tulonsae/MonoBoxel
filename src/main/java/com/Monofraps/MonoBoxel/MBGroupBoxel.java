package com.Monofraps.MonoBoxel;


import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MBPermissionManager.MBPermission;
import com.Monofraps.MonoBoxel.Utils.HashMD5;


/**
 * A class for holding information about group boxels.
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
		
		boxelPrefix = master.getBoxelGroupPrefix();
		
		if (correspondingWorldName.startsWith(boxelPrefix))
			boxelOwner = correspondingWorldName.substring(boxelPrefix.length());
		
		msgFoundBoxel = master.getLocalizationManager().getMessage("found").setMessageVariable(
				"boxeltype", "Groupboxel").setMessageVariable("boxelname",
				boxelOwner).toString();
		msgLoading = master.getLocalizationManager().getMessage("loading").setMessageVariable(
				"boxeltype", "Groupboxel").setMessageVariable("boxelname",
				boxelOwner).toString();
		msgLoaded = master.getLocalizationManager().getMessage("loaded").setMessageVariable(
				"boxeltype", "Groupboxel").setMessageVariable("boxelname",
				boxelOwner).toString();
		msgFailedToLoad = master.getLocalizationManager().getMessage(
				"failed-to-load").setMessageVariable("boxeltype", "Groupboxel").setMessageVariable(
				"boxelname", boxelOwner).toString();
		msgCreating = master.getLocalizationManager().getMessage("creating").setMessageVariable(
				"boxeltype", "Groupboxel").setMessageVariable("boxelname",
				boxelOwner).toString();
		msgCreated = master.getLocalizationManager().getMessage("created").setMessageVariable(
				"boxeltype", "Groupboxel").setMessageVariable("boxelname",
				boxelOwner).toString();
		msgFailedToCreate = master.getLocalizationManager().getMessage(
				"failed-to-create").setMessageVariable("boxeltype",
				"Groupboxel").setMessageVariable("boxelname", boxelOwner).toString();
		msgTeleporting = master.getLocalizationManager().getMessage(
				"teleporting").toString();
		msgTooManyBoxels = master.getLocalizationManager().getMessage(
				"maximum-reached").setMessageVariable("maximum",
				String.valueOf(master.getConfig().getInt("max-boxel-count"))).setMessageVariable(
				"type", "Boxels").toString();
		
	}
	
	@Override
	public boolean Create(Player player) {
	
		if (!master.getPermissionManager().hasPermission(player,
				new MBPermission(MBPermission.CAN_CREATE_GROUP_BOXEL))) {
			master.getPermissionManager().SendNotAllowedMessage(player);
			return false;
		}
		
		if (isExisting() && isLoaded()) {
			player.sendMessage(msgFoundBoxel);
			
			correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
					correspondingWorldName).getCBWorld();
			return true;
		}
		
		if (isExisting() && !isLoaded()) {
			player.sendMessage(msgFoundBoxel);
			player.sendMessage(msgLoading);
			
			if (Load()) {
				player.sendMessage(msgLoaded);
				return true;
			} else {
				player.sendMessage(msgFailedToLoad);
				return false;
			}
		}
		
		if (!isExisting()) {
			if (DoCreate(player)) {
				correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
						correspondingWorldName).getCBWorld();
				
				return true;
			} else {
				master.getLogManager().debugLog(Level.WARNING,
						msgFailedToCreate);
				return false;
			}
		}
		
		if (super.DoCreate(player)) {
			correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
					correspondingWorldName).getCBWorld();
			return true;
		} else {
			player.sendMessage(msgFailedToCreate);
			master.getLogManager().severe(msgFailedToCreate);
			return false;
		}
		
	}
	
	/**
	 * Sets the Group Boxel's password hash.
	 * 
	 * @param passwd
	 */
	public void setPasswordHash(String passwd) {
	
		// let's make group Boxels at least a little bit safe - md5-sum the password, so we don't
		// have to store the plaintext password in the config
		passwdMD5 = HashMD5.Hash(passwd);
	}
	
	/**
	 * Override the base class Join. We can not use it since we have to pass a password to the Join
	 * function.
	 * 
	 * @return false
	 */
	@Override
	public boolean Join(Player player) {
	
		master.getLogManager().debugLog(Level.WARNING,
				"Boxel-Join was called on a goup Boxel.");
		return false;
	}
	
	/**
	 * Teleports the player to the Boxel.
	 * 
	 * @param player
	 *            The player to teleport
	 * @param passwd
	 *            The password that the player has given to join the Boxel
	 * @return true on success, otherwise false
	 */
	public boolean Join(Player player, String passwd) {
	
		if (!master.getPermissionManager().hasPermission(player,
				new MBPermission(MBPermission.CAN_VISIT_GROUP_BOXEL))) {
			master.getPermissionManager().SendNotAllowedMessage(player);
			return false;
		}
		
		// validate password
		if (!HashMD5.Hash(passwd).equals(passwdMD5)) {
			player.sendMessage("The security man said: NO! - Have you entered the correct password?");
			return false;
		}
		
		if (!isExisting()) {
			master.getLogManager().severe(
					"The Boxel " + correspondingWorldName + " does not exist.");
			player.sendMessage("The Boxel " + correspondingWorldName
					+ " does not exist.");
			return false;
		}
		
		if (!isLoaded()) {
			player.sendMessage(msgFoundBoxel);
			player.sendMessage(msgLoading);
			
			if (Load()) {
				player.sendMessage(msgLoaded);
			} else {
				master.getLogManager().severe(msgFailedToLoad);
				player.sendMessage(msgFailedToLoad);
				return false;
			}
		}
		
		// before porting the player, save his location
		// do not save the return/entry location if the player is in a Boxel
		if (!master.getMBWorldManager().isBoxel(player.getWorld().getName())[0]) {
			master.getDataConfig().getConfig().set(
					"playeroloc." + player.getName() + ".world",
					player.getWorld().getName());
			master.getDataConfig().getConfig().set(
					"playeroloc." + player.getName() + ".position",
					player.getLocation().toVector());
			master.getDataConfig().getConfig().set(
					"playeroloc." + player.getName() + ".yaw",
					player.getLocation().getYaw());
			master.getDataConfig().getConfig().set(
					"playeroloc." + player.getName() + ".pitch",
					player.getLocation().getPitch());
			master.getDataConfig().saveConfig();
		}
		
		player.sendMessage(msgTeleporting);
		return player.teleport(correspondingWorld.getSpawnLocation());
	}
	
	/**
	 * 
	 * @return the password hash
	 */
	public String getPasswordHash() {
	
		return passwdMD5;
	}
	
}
