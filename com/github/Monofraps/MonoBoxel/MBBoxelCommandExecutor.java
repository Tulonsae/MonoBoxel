package com.github.Monofraps.MonoBoxel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		MVWorldManager wm = null;
		String boxelName = "";
		World target = null;
		boolean playersOwnBoxel = true;

		// is the player allowed to visit/create a boxel? - it not, we can exit
		// here
		/*
		 * if ( (!player.hasPermission("monoboxel.boxel.visit") &&
		 * !master.getConfig().getBoolean("per-boxel-permissions", false)) &&
		 * !player.hasPermission("monoboxel.boxel.create")) {
		 * player.sendMessage(
		 * "You don't have permissions to create or visit a boxel. :("); return
		 * false; }
		 */
		// get the MV Core
		if (master.GetMVCore() == null) {
			master.log.info("Failed to get Muliverse-Core.");
			player.sendMessage("Failed to get Multiverse-Core. Please contact a server admin.");
			return false;
		}
		// ...and it's world manager
		wm = master.GetMVCore().getMVWorldManager();

		// boxel names are alwas: BOXEL_<playername>
		boxelName = "BOXEL_" + player.getName();

		if (args.length > 0) {
			playersOwnBoxel = false;
			boxelName = "BOXEL_" + args[0];

			if (args[0].equals("getmeout")) {
				player.teleport(wm.getSpawnWorld().getSpawnLocation());
				return true;
			}
		}

		// validate access permissions for the user per boxel
		if (master.getConfig().getBoolean("per-boxel-permissions", false)) {

			// per boxel permissions
			if (!player.hasPermission("monoboxel.boxel.visit." + boxelName) && !playersOwnBoxel) {
				player.sendMessage("You don't have permissions to visit this boxel.");
				return false;
			}
		} else {
			// global visit permissions
			if (!player.hasPermission("monoboxel.boxel.visit")
					&& !playersOwnBoxel) {
				
				player.sendMessage("You don't have permissions to visit this boxel.");
				return false;
			}
		}

		// create or load the world
		target = master.worldManager.CreateWorld(boxelName, player, wm);

		// something went wrong...
		if (target == null) {
			master.log.info("Boxel \"" + boxelName
					+ "\" could not be found or created.");
			player.sendMessage("The boxel was not found or could not be created. Please contact a server admin.");
			return false;
		}

		// save the players current location and port
		if(master.getConfig().getBoolean("save-exit-location", true))
		{
			// experimental and not finished yet...
			master.getConfig().set("playerloc." + player.getName() + ".world", player.getWorld());
			master.getConfig().set("playerloc." + player.getName() + ".position", player.getWorld());
		}
			
		if (player.teleport(new Location(target, 0, 7, 0)))
			return true;

		return false;
	}
}
