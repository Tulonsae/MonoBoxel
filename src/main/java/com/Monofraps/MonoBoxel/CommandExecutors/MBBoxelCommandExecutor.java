package com.Monofraps.MonoBoxel.CommandExecutors;


import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MonoBoxel;
import com.Monofraps.MonoBoxel.Utils.GenUtils;


/**
 * Executor class for /boxel commands.
 * 
 * @author Monofraps
 */
public class MBBoxelCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the plugin class instance
	 */
	public MBBoxelCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
	
		Player player = null;
		String boxelName = "";
		String boxelGenerator = "MonoBoxel";
		String boxelSeed = "";
		
		if (!(sender instanceof Player)) {
			master.getLogManager().info("You cannot do this from the console.");
			return true;
		}
		
		player = (Player) sender;
		
		// if no arguments are given, set the boxelName to the players own Boxel
		if (args.length == 0)
			boxelName = GenUtils.boxelizeName(player.getName(), master);
		
		// try and parse the parameters
		try {
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("-s"))
						boxelSeed = args[i + 1];
					if (args[i].equals("-g"))
						boxelGenerator = args[i + 1];
				}
				
				if (args[0].equals("getmeout"))
					return master.getMBWorldManager().GenericLeave(player);
				
				if (args[0].equals("-"))
					boxelName = GenUtils.boxelizeName(player.getName(), master);
				
				if ((!args[0].equals("-s")) && (!args[0].equals("-g")))
					boxelName = GenUtils.boxelizeName(args[0], master);
			}
		} catch (Exception e) {
			master.getLogManager().debugLog(Level.SEVERE, e.getMessage());
			player.sendMessage("Something went wrong: Too few parameters!");
			return false;
		}
		
		// ++++++++++++++++++++++++++
		// Messages
		String msgJoinedBoxel = String.format("%s joined Boxel/World %s.", player.getName(), boxelName);
		String msgFailedJoinBoxel = String.format("%s failed to join Boxel/World %s.", player.getName(), boxelName);
		// ++++++++++++++++++++++++++
		
		if (master.getMBWorldManager().GenericJoin(player, boxelName, true, boxelSeed, boxelGenerator)) {
			master.getLogManager().debugLog(Level.INFO, msgJoinedBoxel);
			return true;
		} else {
			master.getLogManager().debugLog(Level.INFO, msgFailedJoinBoxel);
			return false;
		}
		
	}
}
