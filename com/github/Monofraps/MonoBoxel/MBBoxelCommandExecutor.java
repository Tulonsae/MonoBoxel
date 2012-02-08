package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class MBBoxelCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

	public MBBoxelCommandExecutor(MonoBoxel plugin) {
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
		
		if(!player.hasPermission("monoboxel.boxel.visit") && !player.hasPermission("monoboxel.boxel.create"))
		{
			player.sendMessage("You don't have permissions to create or visit a boxel. :(");
			return false;
		}
		
				
		if(master.GetMVCore() == null)
		{
			master.log.info("Failed to get Muliverse-Core.");
			player.sendMessage("Failed to get Multiverse-Core. Please contact a server admin.");
			return false;
		}
		MVWorldManager wm = master.GetMVCore().getMVWorldManager();			

		String boxelName = "BOXEL_" + player.getName();

		if (args.length > 0) {
			boxelName = "BOXEL_" + args[0];
			
			if(args[0] == "getmeout")
			{
				master.log.info(args[0]);
				player.teleport(wm.getSpawnWorld().getSpawnLocation());
				return true;
			}
		}

		World target = null;

		target = master.worldManager.CreateWorld(boxelName, player, wm);

		if (target == null) {
			master.log.info("Boxel \"" + boxelName
					+ "\" could not be found or created.");
			player.sendMessage("The boxel was not found or could not be created. Please contact a server admin.");
			return false;
		}

		if (player.teleport(new Location(target, 0, 7, 0)))
			return true;

		return false;
	}

}
