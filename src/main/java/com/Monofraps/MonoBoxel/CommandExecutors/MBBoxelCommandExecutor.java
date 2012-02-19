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
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
	
		boolean senderIsPlayer = true;
		Player player = null;
		String boxelName = "";
		String boxelGenerator = "MonoBoxel";
		String boxelSeed = "";
		String boxelPrefix = master.getBoxelPrefix();
		
		if (!(sender instanceof Player)) {
			master.getLogManager().info(
					"You cannot do this from the server console.");
			
			senderIsPlayer = false;
			
			// @TODO: implement/enable Boxel creation from server console
			return true;
		}
		
		master.getLogManager().info(boxelPrefix);
		
		if (senderIsPlayer)
			player = (Player) sender;
		
		if (args.length > 0) {
			if (args[0].equals("-") && senderIsPlayer)
				boxelName = boxelPrefix + player.getName();
			
			if (boxelName.equals("-s") || boxelName.equals("-g"))
				boxelName = boxelPrefix + player.getName();
			
			else
				if (args[0].equals("getmeout") && senderIsPlayer) {
					for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
						if (box.getCorrespondingWorldName().equals(
								player.getWorld().getName()))
							return box.Leave(player);
					}
					for (MBGroupBoxel box : master.getMBWorldManager().getGroupBoxels()) {
						if (box.getCorrespondingWorldName().equals(
								player.getWorld().getName()))
							return box.Leave(player);
					}
					player.sendMessage("Failed to port you out. - Are you in a Boxel?");
					return false;
					
				} else
					boxelName = boxelPrefix + args[0];
			
		} else
			boxelName = boxelPrefix + player.getName();
		
		// check if the Boxel already exists
		for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
			master.getLogManager().info(boxelName);
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
		
		if (HashMD5.Hash(boxelSeed).equals("051d785c41b4605c174054d194d4e136")) {
			sender.sendMessage("Really? Do you? Well, I'll create something to play with...");
			boxelGenerator = "MonoBoxel:ssehc";
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
