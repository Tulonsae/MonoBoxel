package com.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Monofraps.MonoBoxel.MBBoxel;
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
		
		if (args[0].equals("dump")) {
			if (args[1].equals("boxel")) {
				String boxel = args[2];
				
				for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
					if (box.getCorrespondingWorldName().contains(boxel)) {
						box.Refresh(false);
						
						try {
							
							sender.sendMessage("==[BINFO]==");
							sender.sendMessage("Generator: "
									+ box.getBoxelGenerator());
							sender.sendMessage("Seed: " + box.getBoxelSeed());
							sender.sendMessage("UTID: "
									+ String.valueOf(box.getUnloadTaskId()));
							sender.sendMessage("WorldName: "
									+ box.getCorrespondingWorldName());
							sender.sendMessage("World.toString: "
									+ box.getCorrespondingWorld().toString());
							sender.sendMessage("World.getGenerator().toString: "
									+ String.valueOf(box.getCorrespondingWorld().getGenerator()));
							
							sender.sendMessage(box.getCorrespondingWorld().toString());
						} catch (Exception e) {
							sender.sendMessage(e.getMessage());
						}
					}
				}
			}
		}
		if (args[0].equals("version")) {
			sender.sendMessage(master.getDescription().getVersion());
		}
		if (args[0].equals("reload")) {
			sender.sendMessage("Going to reload...");
			sender.sendMessage("Terminating and reloading MBWorldManager...");
			master.getMBWorldManager().Reload();
			sender.sendMessage("Reloaded MBWorldManager!");
		}
		
		return false;
	}
}
