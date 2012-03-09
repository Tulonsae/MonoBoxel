package com.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Executor class for /boxel commands.
 * 
 * @author Monofraps
 */
public class MBBoxelDbgCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the plugin class instance
	 */
	public MBBoxelDbgCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
	
		if (!sender.hasPermission("monoboxel.debug"))
			sender.sendMessage("You need permission monoboxel.debug");
		
		
		if (args[0].equals("version")) {
			sender.sendMessage(master.getDescription().getVersion());
		}
		
		return false;
	}
}
