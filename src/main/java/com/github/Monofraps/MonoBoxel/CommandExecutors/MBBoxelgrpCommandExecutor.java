package com.github.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Monofraps.MonoBoxel.MBGroupBoxel;
import com.github.Monofraps.MonoBoxel.MBPermissionManager.MBPermission;
import com.github.Monofraps.MonoBoxel.MonoBoxel;


public class MBBoxelgrpCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the plugin class instance
	 */
	public MBBoxelgrpCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
		
		Player player = null;
		String boxelPassword = "";
		String boxelName = "";
		String boxelGenerator = "MonoBoxel";
		String boxelSeed = "";
		
		if (!(sender instanceof Player)) {
			master.getLogManager().info(
					"You cannot do this from the server console.");
			return false;
		}
		
		player = (Player) sender;
		
		if (!master.getPermManager().hasPermission(player,
				MBPermission.CAN_VISIT_GROUP_BOXEL)) {
			master.getPermManager().SendNotAllowedMessage(player);
			return false;
		}
		
		if (args.length <= 1)
			player.sendMessage("You have to specify a Boxel name and a password to join/create a group Boxel.");
		
		boxelName = master.getBoxelPrefix() + args[0];
		boxelPassword = args[1];
		
		if (!master.getPermManager().hasPermission(player,
				MBPermission.CAN_CREATE_GROUP_BOXEL)) {
			master.getPermManager().SendNotAllowedMessage(player);
			return false;
		}
		
		// check if the Boxel already exists
		for (MBGroupBoxel box : master.getMBWorldManager().getGroupBoxels()) {
			if (box.getCorrespondingWorldName().equals(boxelName))
				return box.Join(player, boxelPassword);
		}
		
		// the Boxel does not exist, so we have to create it
		// get generator and seed if specified
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-g"))
				boxelGenerator = args[i + 1];
			
			if (args[i].equals("-s"))
				boxelSeed = args[i + 1];
		}
		
		if (master.getMBWorldManager().AddGroupBoxel(boxelName, boxelPassword,
				true, player, boxelGenerator, boxelSeed)) {
			for (MBGroupBoxel box : master.getMBWorldManager().getGroupBoxels()) {
				if (box.getCorrespondingWorldName().equals(boxelName))
					return box.Join(player, boxelPassword);
			}
		}
		
		return false;
	}
}