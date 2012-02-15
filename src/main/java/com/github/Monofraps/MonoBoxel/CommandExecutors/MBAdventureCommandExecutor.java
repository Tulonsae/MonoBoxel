package com.github.Monofraps.MonoBoxel.CommandExecutors;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.Monofraps.MonoBoxel.MonoBoxel;
import com.github.Monofraps.MonoBoxel.Adventure.MBAdventureWorld;


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
		
		String worldName = "";
		
		if (args[0].equals("create")) {
			
			worldName = args[1];
			
			// WorldDuplicator.DuplicateWorld(master.getMVCore().getMVWorldManager().getMVWorld(worldName).getCBWorld(),
			// master, worldName + ".template");
			if (!master.getMBAdventureManager().addAdventureWorld(
					master.getMVCore().getMVWorldManager()
							.getMVWorld(worldName).getCBWorld())) {}
			
			// TODO: no log messages for now; waiting for new i18n/log manager
		} else
			if (args[0].equals("reset")) {
				worldName = args[1];
				
				for (MBAdventureWorld advW : master.getMBAdventureManager()
						.getAdventureWorlds()) {
					if (advW.getName().equals(worldName)) {
						advW.Reset();
					}
				}
			}
		return true;
	}
}