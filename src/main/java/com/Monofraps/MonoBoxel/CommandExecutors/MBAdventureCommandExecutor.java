package com.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Monofraps.MonoBoxel.MonoBoxel;
import com.Monofraps.MonoBoxel.Adventure.MBAdventureWorld;


/**
 * Executes /adventure commands.
 * 
 * @author Monofraps
 * 
 */
public class MBAdventureCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	public MBAdventureCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
	
		if (master.getConfig().getBoolean("enable-adventure", false)) {
			sender.sendMessage("This feature is not fully implemented! "
					+ "If you want to try this feature out, set 'enable-adventure' in the config file to true.");
			return true;
		}
		
		String worldName = "";
		boolean resetDone = false;
		
		if (args.length < 2) {
			sender.sendMessage("This function needs exactly two (2) arguments.");
			return false;
		}
		
		if (args[0].equals("create")) {
			
			worldName = args[1];
			
			// WorldDuplicator.DuplicateWorld(master.getMVCore().getMVWorldManager().getMVWorld(worldName).getCBWorld(),
			// master, worldName + ".template");
			if (!master.getMBAdventureManager().addAdventureWorld(
					master.getMVCore().getMVWorldManager().getMVWorld(worldName).getCBWorld())) {
				sender.sendMessage("Could not create adventure world!");
				return false;
			} else {
				sender.sendMessage("Adventure world created.");
				master.getLogManager().info(
						"Created adventure world: " + worldName);
				return true;
			}
			
			// TODO: no log messages for now; waiting for new i18n/log manager
		} else
			if (args[0].equals("reset")) {
				worldName = args[1];
				
				for (MBAdventureWorld advW : master.getMBAdventureManager().getAdventureWorlds()) {
					if (advW.getName().equals(worldName)) {
						resetDone = advW.Reset();
					}
				}
				
				if (resetDone) {
					sender.sendMessage("World reset successfully.");
					master.getLogManager().info(
							"Reset adventure world: " + worldName);
					return true;
				} else {
					sender.sendMessage("Failed to reset world. Does the world exist? - Is it marked as an advenure world?");
					return false;
				}
			}
		return true;
	}
}