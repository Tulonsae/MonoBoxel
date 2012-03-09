package com.Monofraps.MonoBoxel.CommandExecutors;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MonoBoxel;
import com.Monofraps.MonoBoxel.MBPermissionManager.MBPermission;
import com.onarandombox.MultiverseCore.api.MVWorldManager;


/**
 * Executor class for /boxremove commands.
 * 
 * @author Monofraps
 */
public class MBBoxelremoveCommandExecutor implements CommandExecutor {
	
	private MonoBoxel	master	= null;
	
	public MBBoxelremoveCommandExecutor(MonoBoxel plugin) {
	
		master = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
	
		boolean senderIsPlayer = false;
		Player player = null;
		MVWorldManager wm = master.getMVCore().getMVWorldManager();
		
		String boxelName = "";
		String boxelOwner = "";
		String boxelPrefix = master.getBoxelPrefix();
		
		if (sender instanceof Player) {
			player = (Player) sender;
			senderIsPlayer = true;
		}
		
		// get the boxels name
		if (args.length == 0) {
			if (!senderIsPlayer) {
				sender.sendMessage("You have to specify a Boxel or player name");
				return false;
			}
			
			boxelName = boxelPrefix + player.getName();
		} else {
			if (args[0].startsWith(boxelPrefix))
				boxelName = args[0];
			else
				boxelName = boxelPrefix + args[0];
		}
		
		if (boxelName.startsWith(boxelPrefix))
			boxelOwner = boxelName.substring(boxelPrefix.length());
		
		if (senderIsPlayer) {
			// is this the players own boxel?
			if (boxelName.equals(boxelPrefix + player.getName())) {
				if (!master.getPermissionManager().hasPermission(player, new MBPermission(MBPermission.CAN_REMOVE_OWN))) {
					master.getPermissionManager().SendNotAllowedMessage(player);
					return false;
				}
			} else { // no, it's not
				if (!master.getPermissionManager().hasPermission(player,
						new MBPermission(MBPermission.ROOT_CAN_REMOVE, boxelOwner))) {
					master.getPermissionManager().SendNotAllowedMessage(player);
					return false;
				}
			}
		}
		
		// check if the boxel exists (loaded and unloaded worlds)
		boolean[] boxlupResult = master.getMBWorldManager().isBoxel(boxelName);
		if (!boxlupResult[0]) {
			sender.sendMessage("Boxel \"" + boxelName + "\" does not exists.");
			return false;
		}
		
		// load the boxel if it was not loaded
		if (!boxlupResult[1])
			wm.loadWorld(boxelName);
		
		// Are there still players in this boxel? (there can't be some if the
		if (boxlupResult[1]) {
			List<Player> players = wm.getMVWorld(boxelName).getCBWorld().getPlayers();
			if (players != null) {
				for (Player p : players) {
					p.sendMessage("Ooops... You are in a Boxel that is supposed to be deleted... will port you to the spawn world...");
					p.teleport(wm.getSpawnWorld().getSpawnLocation());
				}
			}
		}
		
		if (wm.deleteWorld(boxelName)) {
			master.getLogManager().info("Successfully removed boxel \"" + boxelName + "\".");
			
			if (senderIsPlayer) {
				sender.sendMessage("Successfully removed boxel \"" + boxelName + "\".");
			}
			
			return true;
		}
		
		return false;
	}
}