package com.github.Monofraps.MonoBoxel.CommandExecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.Monofraps.MonoBoxel.MonoBoxel;
import com.github.Monofraps.MonoBoxel.Utils.WorldDuplicator;

/**
 * Executes /adventure commands.
 * @author Monofraps
 *
 */
public class MBAdventureCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master = null;
	
	public MBAdventureCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
		
		String worldName = args[0];
		
		WorldDuplicator.DuplicateWorld(master.getMVCore().getMVWorldManager().getMVWorld(worldName).getCBWorld(), master, worldName + ".template");
		
		
		return true;
	}
	
}