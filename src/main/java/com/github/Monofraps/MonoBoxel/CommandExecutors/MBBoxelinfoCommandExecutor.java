package com.github.Monofraps.MonoBoxel.CommandExecutors;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.Monofraps.MonoBoxel.MBBoxel;
import com.github.Monofraps.MonoBoxel.MBGroupBoxel;
import com.github.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Executor class for /boxinfo commands.
 * 
 * @author Monofraps
 */
public class MBBoxelinfoCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	public MBBoxelinfoCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
		
		master.getMBWorldManager().LoadConfig();
		
		sender.sendMessage(ChatColor.RED + "=====[ MonoBoxel Info ] =====");
		
		sender.sendMessage(ChatColor.WHITE + "Boxels:");
		for (MBBoxel box : master.getMBWorldManager().getBoxels()) {
			String msg = "";
			
			if (box.isLoaded())
				msg += ChatColor.WHITE;
			else
				msg += ChatColor.GRAY;
			
			msg += box.getCorrespondingWorldName() + " - ";
			
			if (box.isLoaded()) {
				if (box.isEmpty())
					msg += ChatColor.AQUA + "No players inside. UnloadThread: "
							+ box.getUnloadTaskId();
				else
					if (!box.isEmpty())
						msg += ChatColor.GREEN + "Players inside.";
			}
			else
				msg += ChatColor.GOLD + "UNLOADED";
			
			sender.sendMessage(msg);
		}
		
		sender.sendMessage(ChatColor.WHITE + "Group Boxels:");
		for (MBGroupBoxel box : master.getMBWorldManager().getGroupBoxels()) {
			String msg = "";
			
			if (box.isLoaded())
				msg += ChatColor.WHITE;
			else
				msg += ChatColor.GRAY;
			
			msg += box.getCorrespondingWorldName() + " - ";
			
			if (box.isLoaded()) {
				if (box.isEmpty())
					msg += ChatColor.AQUA + "No players inside. UnloadThread: "
							+ box.getUnloadTaskId();
				else
					if (!box.isEmpty())
						msg += ChatColor.GREEN + "Players inside.";
			}
			else
				msg += ChatColor.GOLD + "UNLOADED";
			
			sender.sendMessage(msg);
		}
		
		sender.sendMessage(String.valueOf(master.getMBWorldManager()
				.getNumBoxels())
				+ " Boxels are currently registered on this server.");
		
		sender.sendMessage(String.valueOf(master.getMBWorldManager()
				.getNumGroupBoxels())
				+ " group Boxels are currently registered on this server.");
		
		sender.sendMessage("The current Boxel prefix is: "
				+ master.getBoxelPrefix());
		
		return true;
	}
}