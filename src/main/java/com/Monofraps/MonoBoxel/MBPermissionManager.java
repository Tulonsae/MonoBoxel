package com.Monofraps.MonoBoxel;


import java.util.ArrayList;

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
	 * Class Wrapper for Bukkit's Permission to represent and check MonoBoxel Permissions.
	 * 
	 * @author MikeMatrix
	 */
	public static class MBPermission {
		
		// Template Permissions
		/**
		 * Permission Template.
		 * monoboxel.boxel.create.own
		 */
		public static final Permission	CAN_CREATE_OWN			= new Permission("monoboxel.boxel.create.own");
		/**
		 * Permission Template.
		 * monoboxel.boxel.create.own
		 */
		public static final Permission	CAN_CREATE_GROUP_BOXEL	= new Permission("monoboxel.groupboxel.create");
		/**
		 * Permission Template.
		 * monoboxel.boxel.create.own
		 */
		public static final Permission	CAN_VISIT_OWN			= new Permission("monoboxel.boxel.visit.own");
		/**
		 * Permission Template.
		 * monoboxel.boxel.create.own
		 */
		public static final Permission	CAN_VISIT_GROUP_BOXEL	= new Permission("monoboxel.groupboxel.visit");
		/**
		 * Permission Template.
		 * monoboxel.boxel.create.own
		 */
		public static final Permission	CAN_REMOVE_OWN			= new Permission("monoboxel.boxel.remove.own");
		
		// Root Template Permissions
		/**
		 * Permission Template.
		 * monoboxel.boxel.create
		 */
		public static final Permission	ROOT_CAN_CREATE			= new Permission("monoboxel.boxel.create");
		/**
		 * Permission Template.
		 * monoboxel.boxel.visit
		 */
		public static final Permission	ROOT_CAN_VISIT			= new Permission("monoboxel.boxel.visit");
		/**
		 * Permission Template.
		 * monoboxel.boxel.remove
		 */
		public static final Permission	ROOT_CAN_REMOVE			= new Permission("monoboxel.boxel.remove");
		
		private Permission				permission				= null;
		private String					permissionNode			= "";
		
		/**
		 * @param permission
		 */
		public MBPermission(Permission permission) {
		
			this.permission = permission;
			this.permissionNode = this.permission.getName();
		}
		
		/**
		 * @param node
		 *            The Permission Node.
		 */
		public MBPermission(String node) {
		
			this.permission = new Permission(node);
			this.permissionNode = this.permission.getName();
		}
		
		/**
		 * @param perm
		 * @param subnode
		 *            The Subnode to add to the Permission's node. (e.g. example.visit and test will
		 *            create the permission example.visit.test)
		 */
		public MBPermission(Permission perm, String subnode) {
		
			this.permission = new Permission(perm.getName() + "." + subnode);
			this.permissionNode = this.permission.getName();
		}
		
		/**
		 * @param node
		 *            The Permission Node.
		 * @param subnode
		 *            The Subnode to add to the Permission's node. (e.g. example.visit and test will
		 *            create the permission example.visit.test)
		 */
		public MBPermission(String node, String subnode) {
		
			this.permission = new Permission(node + "." + subnode);
			this.permissionNode = this.permission.getName();
		}
		
		/**
		 * @return The permission.
		 */
		public Permission getPermission() {
		
			return this.permission;
		}
		
		/**
		 * Generates and returns all Wildcard Permission to check for this node.
		 * Only needed to add Wildcard support for PermissionsBukkit.
		 * 
		 * @return The List of Wildcard Permissions for this node.
		 */
		public ArrayList<Permission> getWildcardPermissions() {
		
			ArrayList<Permission> wildcards = new ArrayList<Permission>();
			
			String[] nodeSplit = null;
			String wildcardBuilder = "";
			
			nodeSplit = this.permissionNode.split("\\.");
			for (String string : nodeSplit) {
				wildcardBuilder += string + ".";
				wildcards.add(new Permission(wildcardBuilder + "*"));
			}
			return wildcards;
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
	
		if (player.hasPermission(permission.getPermission()))
			return true;
		ArrayList<Permission> wildcards = permission.getWildcardPermissions();
		for (Permission wildcard : wildcards) {
			if (player.hasPermission(wildcard))
				return true;
		}
		return false;
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