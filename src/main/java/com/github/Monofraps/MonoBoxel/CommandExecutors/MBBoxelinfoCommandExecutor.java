package com.github.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Executor class for /boxinfo commands.
 * 
 * @author Monofraps
 */
public class MBBoxelinfoCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master;
	
	public MBBoxelinfoCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}
	
	/**
	 * Will parse and execute the /boxinfo commands.
	 * 
	 * @param sender
	 * @param command
	 * @param lable
	 * @param args
	 * @return true if the command execution was successful, otherwise false
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
		
		master.getMBWorldManager().LoadConfig();
		sender.sendMessage(String.valueOf(master.getMBWorldManager()
				.getNumBoxels() + master.getMBWorldManager().getNumGroupBoxels())
				+ " Boxels are currently registered on this server.");
		
		sender.sendMessage("The current Boxel prefix is: "
				+ master.getBoxelPrefix());
		
		return true;
	}
	
}