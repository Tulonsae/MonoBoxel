package com.github.Monofraps.MonoBoxel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MBBoxellookupCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

	public MBBoxellookupCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return true;
		}

		Player player = (Player) sender;

		String boxelOwner = player.getWorld().getName().substring(6);		
		player.sendMessage("This boxel belongs to " + boxelOwner);
		

		return false;
	}

}