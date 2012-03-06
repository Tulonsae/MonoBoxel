package com.Monofraps.MonoBoxel.CommandExecutors;


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
public class MBBoxacceptCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	/**
	 * 
	 * @param plugin
	 *            A reference to the plugin class instance
	 */
	public MBBoxacceptCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {
	
		Player player = null;
		Player toPlayer = null;
		
		if (!(sender instanceof Player)) {
			master.getLogManager().info(
					"You cannot do this from the server console.");
			return true;
		}
		
		player = (Player) sender;
		String invitation = "";
		
		for (String inv : master.getInviteCmdExecutor().getInvitations()) {
			if (inv.substring(0, inv.indexOf(" ")).equals(player.getName())) {
				toPlayer = master.getServer().getPlayer(
						inv.substring(inv.indexOf(" ")));
				
				if (toPlayer == null) {
					master.getLogManager().info(
							"Not found player "
									+ inv.substring(inv.indexOf(" ")));
					return false;
				}
				
				invitation = inv;
				player.teleport(toPlayer.getLocation());
			}
		}
		
		if (!invitation.isEmpty())
			master.getInviteCmdExecutor().getInvitations().remove(invitation);
		
		return false;
	}
	
}
