package com.github.Monofraps.MonoBoxel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MBBoxelinfoCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

	public MBBoxelinfoCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {

		master.worldManager.LoadWorlds();
		sender.sendMessage(String.valueOf(master.worldManager.GetNumberOfBoxels()) + " Boxels are currently registered on this server.");

		return true;
	}

}