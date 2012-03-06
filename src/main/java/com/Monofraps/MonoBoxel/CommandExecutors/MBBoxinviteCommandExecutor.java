package com.Monofraps.MonoBoxel.CommandExecutors;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Executor class for /boxel commands.
 * 
 * @author Monofraps
 */
public class MBBoxinviteCommandExecutor implements CommandExecutor {
	
	private MonoBoxel		master		= null;
	
	private List<String>	invitations	= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the plugin class instance
	 */
	public MBBoxinviteCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
		invitations = new ArrayList<String>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
	
		Player player = null;
		Player toPlyer = null;
		String toPlayer = "";
		String fromPlayer = "";
		String fromBoxel = "";
		
		if (!(sender instanceof Player)) {
			master.getLogManager().info(
					"You cannot do this from the server console.");
			return true;
		}
		
		player = (Player) sender;
		
		fromPlayer = player.getName();
		fromBoxel = master.getBoxelPrefix() + fromPlayer;
		
		try {
			if (!master.getMBWorldManager().isBoxel(fromBoxel)[0]) {
				fromBoxel = master.getBoxelPrefix() + args[1];
				if (!master.getMBWorldManager().isBoxel(fromBoxel)[0])
					sender.sendMessage("No Boxel " + fromBoxel + " found.");
				
			}
			
			toPlayer = args[0];
		} catch (Exception e) {
			master.getLogManager().debugLog(Level.INFO,
					"Error finding Boxel to invite. (" + fromBoxel + ")");
			sender.sendMessage("Some error occured.");
			return false;
		}
		
		toPlyer = master.getServer().getPlayer(toPlayer);
		if (toPlyer == null) {
			master.getLogManager().debugLog(Level.INFO,
					"Error finding toPlyer to invite. (" + toPlayer + ")");
			sender.sendMessage("Error finding toPlyer to invite. (" + toPlayer
					+ ")");
			return false;
		}
		
		toPlyer.sendMessage(fromPlayer
				+ " invited you into his Boxel! Use /boxaccept to accept this request or /boxdeny to deny.");
		
		invitations.add(fromPlayer + " |invited| " + toPlayer);
		
		return false;
	}
	
	/**
	 * returns invitation list.
	 * 
	 * @return The invitation list
	 */
	public List<String> getInvitations() {
	
		return invitations;
	}
}
