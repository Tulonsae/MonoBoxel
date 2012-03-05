package com.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MBBoxel;
import com.Monofraps.MonoBoxel.MBGroupBoxel;
import com.Monofraps.MonoBoxel.MonoBoxel;
import com.Monofraps.MonoBoxel.Utils.HashMD5;


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
	
		
		
		if (args[0].equals("dump")) {
			if (args[1].equals("boxel")) {
				String boxel = args[2];
				
				for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
					if (box.getCorrespondingWorldName().contains(boxel)) {
						sender.sendMessage("==[BINFO]==");
						sender.sendMessage(box.getBoxelGenerator());
						sender.sendMessage(box.getBoxelSeed());
						sender.sendMessage(String.valueOf(box.getUnloadTaskId()));
						
						try {
							sender.sendMessage(box.getCorrespondingWorld().toString());
						} catch (Exception e) {}
					}
				}
			}
		}
		if (args[0].equals("version")) {
			sender.sendMessage(master.getDescription().getVersion());
		}
		
		return false;
	}
}
