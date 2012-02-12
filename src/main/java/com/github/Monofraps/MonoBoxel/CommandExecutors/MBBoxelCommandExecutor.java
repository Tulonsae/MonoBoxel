package com.github.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Monofraps.MonoBoxel.MBBoxel;
import com.github.Monofraps.MonoBoxel.MBGroupBoxel;
import com.github.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Executor class for /boxel commands
 * 
 * @author Monofraps
 */
public class MBBoxelCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the plugin class instance
	 */
	public MBBoxelCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}
	
	/**
	 * Will parse and execute the /boxel commands
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
		
		boolean senderIsPlayer = true;
		Player player = null;
		String boxelName = "";
		String boxelGenerator = "MonoBoxel";
		String boxelSeed = "";
		
		if (!(sender instanceof Player)) {
			senderIsPlayer = false;
			return true;
		}
		
		if (senderIsPlayer)
			player = (Player) sender;
		
		boxelName = master.getBoxelPrefix();
		
		if (args.length > 0) {
			if (args[0].equals("-") && senderIsPlayer)
				boxelName = boxelName + player.getName();
			
			else
				if (args[0].equals("getmeout") && senderIsPlayer) {
					for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
						if (box.getCorrespondingWorldName().equals(
								player.getWorld().getName()))
							return box.Leave(player);
					}
					for (MBGroupBoxel box : master.getMBWorldManager()
							.getGroupBoxels()) {
						if (box.getCorrespondingWorldName().equals(
								player.getWorld().getName()))
							return box.Leave(player);
					}
					player.sendMessage("Failed to port you out. - Are you in a Boxel?");
					return false;
					
				} else
					boxelName = boxelName + args[0];
			
		} else
			boxelName = boxelName + player.getName();
		
		// check if the Boxel already exists
		for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
			if (box.getCorrespondingWorldName().equals(boxelName))
				return box.Join(player);
		}
		
		// the Boxel does not exist, so we have to create it
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-g"))
				boxelGenerator = args[i + 1];
			
			if (args[i].equals("-s"))
				boxelSeed = args[i + 1];
		}
		
		if (master.getMBWorldManager().AddBoxel(boxelName, true, player,
				boxelGenerator, boxelSeed)) {
			for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
				if (box.getCorrespondingWorldName().equals(boxelName))
					return box.Join(player);
			}
		}
		
		return false;
	}
}
