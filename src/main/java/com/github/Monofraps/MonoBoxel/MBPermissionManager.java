package com.github.Monofraps.MonoBoxel;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class MBPermissionManager {
	
	private MonoBoxel	master	= null;
	
	public enum MBPermission {
		CAN_CREATE_OWN(new Permission("monoboxel.boxel.create.own")),
		CAN_CREATE_OTHERS(new Permission("monoboxel.boxel.create.other")),
		CAN_CREATE_GROUP_BOXEL(new Permission("monoboxel.groupboxel.create")),
		CAN_VISIT_OWN(new Permission("monoboxel.boxel.visit.own")),
		CAN_VISIT_OTHERS(new Permission("monoboxel.boxel.create.other")),
		CAN_VISIT_GROUP_BOXEL(new Permission("monoboxel.groupboxel.visit"));
		
		private final Permission	perm;
		
		MBPermission(Permission permission) {
			this.perm = permission;
		}
		
		public Permission getPermission() {
			return perm;
		}
	}
	
	public MBPermissionManager(MonoBoxel plugin) {
		master = plugin;
	}
	
	public boolean hasPermission(Player player, MBPermission permission) {
		return this.hasPermission(player, permission.getPermission());
	}
	
	public boolean hasPermission(Player player, Permission permission) {
		return player.hasPermission(permission);
	}
	
	public boolean canVisitOtherBoxel(Player player, String boxelName) {
		if (boxelName.startsWith(master.getBoxelPrefix())) boxelName = boxelName
				.substring(master.getBoxelPrefix().length());
		
		return this.hasPermission(player, new Permission(
				"monoboxel.boxel.visit." + boxelName));
	}
	
	public void SendNotAllowedMessage(CommandSender sender)
	{
		sender.sendMessage("A divine voice says: 'You are not allowd to do this!'");
	}
}
