package com.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Executor class for /boxlookup commands.
 * 
 * @author Monofraps
 */
public class MBBoxellookupCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	public MBBoxellookupCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
	
		if (!(sender instanceof Player)) {
			master.getLogManager().info(
					"You cannot do this from the server console.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!master.getMBWorldManager().isBoxel(player.getWorld().getName())[0]) {
			player.sendMessage("You are not in a Boxel!");
			return true;
		}
		
		String boxelOwner = player.getWorld().getName().substring(
				master.getBoxelPrefix().length());
		player.sendMessage("This boxel belongs to " + boxelOwner);
		
		return true;
	}
	
}