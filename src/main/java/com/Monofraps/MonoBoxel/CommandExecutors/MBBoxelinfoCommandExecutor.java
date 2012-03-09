package com.Monofraps.MonoBoxel.CommandExecutors;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Monofraps.MonoBoxel.MonoBoxel;
import com.Monofraps.MonoBoxel.Utils.GenUtils;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


/**
 * Executor class for /boxinfo commands. (@TODO: this code here is a little bit messy)
 * 
 * @author Monofraps
 */
public class MBBoxelinfoCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	public MBBoxelinfoCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
	
		sender.sendMessage(ChatColor.RED + "=====[ MonoBoxel v" + master.getDescription().getVersion()
				+ " Info ] =====");
		
		List<String> worldNames = new ArrayList<String>();
		for (MultiverseWorld world : master.getMVCore().getMVWorldManager().getMVWorlds())
			worldNames.add(world.getName());
		for (String worldName : master.getMVCore().getMVWorldManager().getUnloadedWorlds())
			worldNames.add(worldName);
		int numBoxels = 0;
		
		sender.sendMessage(ChatColor.WHITE + "Boxels:");
		for (String worldName : worldNames) {
			boolean[] boxResult = master.getMBWorldManager().isBoxel(worldName);
			
			// not a loaded nor an unloaded Boxel
			if (!boxResult[0] && !boxResult[1])
				continue;
			
			String msg = "";
			numBoxels++;
			
			if (boxResult[1])
				msg += ChatColor.WHITE;
			else
				msg += ChatColor.GRAY;
			
			msg += GenUtils.deboxelizeName(worldName, master) + " - ";
			
			if (boxResult[1]) {
				if (master.getMVCore().getMVWorldManager().getMVWorld(worldName).getCBWorld().getPlayers().size() == 0)
					msg += ChatColor.AQUA + "No players inside. UnloadThread: "
							+ master.getMBWorldManager().getUnloadId(worldName);
				else
					msg += ChatColor.GREEN + "Players inside.";
			} else
				msg += ChatColor.GOLD + "UNLOADED";
			
			sender.sendMessage(msg);
		}
		
		sender.sendMessage(String.valueOf(numBoxels) + " Boxels are currently registered on this server.");
		
		sender.sendMessage("The current Boxel prefix is: " + master.getBoxelPrefix());
		sender.sendMessage("The current group Boxel prefix is: " + master.getBoxelGroupPrefix());
		
		return true;
	}
}