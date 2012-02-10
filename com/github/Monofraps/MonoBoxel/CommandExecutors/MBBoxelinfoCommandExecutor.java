package com.github.Monofraps.MonoBoxel.CommandExecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.Monofraps.MonoBoxel.MonoBoxel;

public class MBBoxelinfoCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

	public MBBoxelinfoCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {

		master.getMBWorldManager().LoadWorlds();
		sender.sendMessage(String.valueOf(master.getMBWorldManager().GetNumberOfBoxels()) + " Boxels are currently registered on this server.");
		sender.sendMessage("The current Boxel prefix is: " + master.getBoxelPrefix());

		return true;
	}

}