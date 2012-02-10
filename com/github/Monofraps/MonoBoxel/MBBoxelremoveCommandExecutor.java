package com.github.Monofraps.MonoBoxel;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class MBBoxelremoveCommandExecutor implements CommandExecutor {

	private MonoBoxel master;

	public MBBoxelremoveCommandExecutor(MonoBoxel plugin) {
		master = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String lable, String[] args) {

		boolean senderIsPlayer = false;
		Player player = null;
		MVWorldManager wm = master.GetMVCore().getMVWorldManager();
		;
		String boxelName = "";

		if (sender instanceof Player) {
			player = (Player) sender;
			senderIsPlayer = true;
		}

		// get the boxels name
		if (args.length == 0) {
			if (!senderIsPlayer) {
				master.logger.info("You have to specify a boxel or player name");
				return false;
			}

			boxelName = "BOXEL_" + player.getName();
		} else {
			if (args[0].startsWith("BOXEL_"))
				boxelName = args[0];
			else
				boxelName = "BOXEL_" + args[0];
		}

		if (senderIsPlayer) {
			// is this the players own boxel?
			if (boxelName.equals("BOXEL_" + player.getName())) {
				if (!player.hasPermission("monoboxel.boxremove.own")) {
					sender.sendMessage("You don't have permissions to do this!");
					return false;
				}
			}
			// no, it's not
			else {
				if (!player.hasPermission("monoboxel.boxremove.other")) {
					sender.sendMessage("You don't have permissions to do this!");
					return false;
				}
			}
		}

		// check if the boxel exists (loaded and unloaded worlds)
		boolean[] boxlupResult = master.worldManager.IsBoxel(boxelName);
		if (!boxlupResult[0]) {
			sender.sendMessage("Boxel \"" + boxelName + "\" does not exists.");
			return false;
		}

		// load the boxel if it was not loaded
		if (!boxlupResult[1])
			wm.loadWorld(boxelName);

		// Are there still players in this boxel? (there can't be some if the
		// boxel was unloaded
		if (boxlupResult[1]) {
			List<Player> players = wm.getMVWorld(boxelName).getCBWorld()
					.getPlayers();
			if (players != null) {
				for (Player p : players) {
					p.sendMessage("Ooops... seems like you are in a boxel that is supposed to be deleted... will port you to the spawn world...");
					p.teleport(wm.getSpawnWorld().getSpawnLocation());
				}
			}
		}

		if (wm.deleteWorld(boxelName)) {
			master.logger
					.info("Successfully removed boxel \"" + boxelName + "\".");

			if (senderIsPlayer) {
				sender.sendMessage("Successfully removed boxel \"" + boxelName
						+ "\".");
			}
			
			MBBoxel box = null;
			for (MBBoxel b : master.worldManager.boxels) {
				if(b.correspondingWorldName.equals(boxelName))
					box = b;
			}
			master.worldManager.boxels.remove(box);

			return true;
		}

		return false;
	}
}