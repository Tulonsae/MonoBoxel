package com.github.Monofraps.MonoBoxel;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Monofraps.MonoBoxel.Utils.HashMD5;


public class MBGroupBoxel extends MBBoxel {
	
	private String	passwdMD5	= "";
	
	public MBGroupBoxel(MonoBoxel plugin, String worldName, String generator,
			String seed) {
		super(plugin, worldName, generator, seed);
		
	}
	
	@Override
	public boolean Create(Player player) {
		
		// check permissions - waiting for new permission manager
		
		if (super.DoCreate(player)) {
			
			correspondingWorld = master.GetMVCore().getMVWorldManager()
					.getMVWorld(correspondingWorldName).getCBWorld();
			return true;
			
		} else {
			player.sendMessage("Failed to create group Boxel.");
			master.logger.severe("Failed to create group Boxel "
					+ correspondingWorldName);
			return false;
		}
		
	}
	
	public void setPasswd(String passwd) {
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
		master.getLogManager().warning("Join was called on a goup box.");
		return false;
	}
	
	public boolean JoinGroupBoxel(Player player, String passwd) {
		// validate password
		if (!HashMD5.Hash(passwd).equals(passwdMD5)) {
			player.sendMessage("The security man said: NO! - Have you entered the right password?");
			return false;
		}
		
		// check permissions - waiting for perm manager
		
		if (!isLoaded())
			if (!Load()) {
				master.getLogManager()
						.severe("Failed to load group boxel: "
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
		
		if (player.teleport(new Location(correspondingWorld, 0, 7, 0)))
			return true;
		
		master.getLogManager().severe("Player teleport failed!");
		player.sendMessage("Failed to teleport!");
		return false;
	}
	
	public String getPassword() {
		return passwdMD5;
	}
	
}
