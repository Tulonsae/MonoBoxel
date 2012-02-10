package com.github.Monofraps.MonoBoxel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MBBoxelCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

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

		if (!(sender instanceof Player)) {
			senderIsPlayer = false;
			return true;
		}

		if (senderIsPlayer)
			player = (Player) sender;

		boxelName = "BOXEL_";

		if (args.length > 0) {
			if (args[0].equals("-") && senderIsPlayer)
				boxelName = boxelName + player.getName();

			else if (args[0].equals("getmeout") && senderIsPlayer) {
				for (MBBoxel box : master.worldManager.boxels) {
					if (box.correspondingWorldName.equals(player.getWorld()
							.getName()))
						return box.Leave(player);
				}
				player.sendMessage("Failed to port you out. - Are you in a Boxel?");
				return false;

			} else
				boxelName = boxelName + args[0];

		} else
			boxelName = boxelName + player.getName();

		// check if the Boxel already exists
		for (MBBoxel box : master.worldManager.boxels) {
			if (box.correspondingWorldName.equals(boxelName))
				return box.Join(player);
		}
		
		// the Boxel does not exist, so we have to create it
		for(String arg : args)
		{
			if(arg.startsWith("-g"))
				boxelGenerator = arg.substring(2);
			if(arg.startsWith("-s"))
				boxelSeed = arg.substring(2);
		}

		
		if (master.worldManager.AddBoxel(boxelName, true, player, boxelGenerator, boxelSeed)) {
			for (MBBoxel box : master.worldManager.boxels) {
				if (box.correspondingWorldName.equals(boxelName))
					return box.Join(player);
			}
		}

		return false;
	}
}
