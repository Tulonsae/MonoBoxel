package com.github.Monofraps.MonoBoxel;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MBPermissionManager {
	
	private MonoBoxel	master	= null;
	
	public MBPermissionManager(MonoBoxel plugin) {
		master = plugin;
	}
	
	/**
	 * Checks if the player has permissions to create his own Boxel.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean canCreateOwnBoxel(Player player) {
		return player.hasPermission("monoboxel.boxel.create.own");
	}
	
	/**
	 * Checks if the player has permissions to create other players Boxels.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean canCreateOtherBoxel(Player player) {
		return player.hasPermission("monoboxel.boxel.create.other");
	}
	
	/**
	 * Checks if the player has permissions visit his own Boxel.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean canVisitOwnBoxel(Player player) {
		// if the player has permissions to create his own Boxel, he will also
		// be able to visit it
		
		// create permission will also grant visit permissions
		if (player.hasPermission("monoboxel.boxel.create.own"))
			return true;
		
		if (player.hasPermission("monoboxel.boxel.visit.own"))
			return true;
		
		if (player.hasPermission("monoboxel.boxel.visit." + player.getName()))
			return true;
		
		return false;
	}
	
	/**
	 * Checks if the player has permissions to visit other players Boxels.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean canVisitOtherBoxel(Player player, String boxelName) {
		// do not check for visit.BOXEL_<name> permissions but for visit.<name>
		if (boxelName.startsWith(master.getBoxelPrefix()))
			boxelName = boxelName.substring(master.getBoxelPrefix().length());
		
		// create.other will also grant visit.other - ...should we do that?
		if(player.hasPermission("monoboxel.boxel.create.other"))
			return true;
		
		if (master.getConfig().getBoolean("per-boxel-permissions", true))
			return player.hasPermission("monoboxel.boxel.visit." + boxelName);
		else
			return player.hasPermission("monoboxel.boxel.visit.other");
	}
	
	public boolean canCreateGroupBoxel(Player player)
	{
		return player.hasPermission("monoboxel.groupboxel.create");
	}
	
	public boolean canVisitGroupBoxel(Player player)
	{
		// create will also grant visit permissions
		if(player.hasPermission("monoboxel.groupboxel.create"))
			return true;
		
		return (player.hasPermission("monoboxel.groupboxel.visit"));
	}
	
	public void SendNotAllowedMessage(CommandSender sender)
	{
		sender.sendMessage("A divine voice says: 'You are not allowd to do this!'");
	}
}
