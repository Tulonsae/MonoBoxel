package com.github.Monofraps.MonoBoxel;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


/**
 * Managing all available Permissions for MonoBoxel.
 * 
 * @author MikeMatrix
 */
public class MBPermissionManager {
	
	private MonoBoxel	master	= null;
	
	/**
	 * Enumeration of all Available Generic Permissions.
	 * 
	 * @author MikeMatrix
	 * 
	 */
	public enum MBPermission {
		CAN_CREATE_OWN(new Permission("monoboxel.boxel.create.own")),
		CAN_CREATE_OTHERS(new Permission("monoboxel.boxel.create.other")),
		CAN_CREATE_GROUP_BOXEL(new Permission("monoboxel.groupboxel.create")),
		CAN_VISIT_OWN(new Permission("monoboxel.boxel.visit.own")),
		CAN_VISIT_GROUP_BOXEL(new Permission("monoboxel.groupboxel.visit")),
		CAN_REMOVE_OWN(new Permission("monoboxel.boxremove.own")),
		CAN_REMOVE_OTHER(new Permission("monoboxel.boxremove.other"));
		
		private final Permission	perm;
		
		MBPermission(Permission permission) {
			this.perm = permission;
		}
		
		public Permission getPermission() {
			return perm;
		}
	}
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the main plugin class
	 */
	public MBPermissionManager(MonoBoxel plugin) {
		master = plugin;
	}
	
	/**
	 * Check if a Player has a specific Generic Permission.
	 * 
	 * @param player
	 * @param permission
	 * @return true if Player has the Permission, false if Player doesn't have the Permission
	 */
	public boolean hasPermission(Player player, MBPermission permission) {
		return this.hasPermission(player, permission.getPermission());
	}
	
	/**
	 * Check if a Player has a specific Permission.
	 * 
	 * @param player
	 * @param permission
	 * @return true if Player has the Permission, false if Player doesn't have the Permission
	 */
	public boolean hasPermission(Player player, Permission permission) {
		return player.hasPermission(permission);
	}
	
	/**
	 * Check if a Player has a specific Permission.
	 * 
	 * @param player
	 * @param permission
	 * @return true if Player has the Permission, false if Player doesn't have the Permission
	 */
	public boolean hasPermission(Player player, String permissionNode) {
		return player.hasPermission(permissionNode);
	}
	
	/**
	 * Extra implementation of per-Player check, since enum's do not support Variations.
	 * 
	 * @TODO: Check if there is a possibility to implement this in the current framework.
	 * @param player
	 * @param boxelName
	 * @return true if Player has the Permission to visit the specified Boxel, false if Player
	 *         doesn't have the Permission to visit the specified Boxel
	 */
	public boolean canVisitOtherBoxel(Player player, String boxelName) {
		if (boxelName.startsWith(master.getBoxelPrefix()))
			boxelName = boxelName.substring(master.getBoxelPrefix().length());
		
		return this.hasPermission(player, new Permission(
				"monoboxel.boxel.visit." + boxelName));
	}
	
	/**
	 * Send Notification to the command executer, that he lacks the Permission, to do, what he tried
	 * to do.
	 * 
	 * @param sender
	 */
	public void SendNotAllowedMessage(CommandSender sender) {
		sender.sendMessage("A divine voice says: 'You are not allowed to do this!'");
	}
}