package com.github.Monofraps.MonoBoxel;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

//@TODO[important]: class for Boxels

public class MonoBoxel extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");
	MBBoxelCommandExecutor boxelCmdExecutor;
	MBBoxellookupCommandExecutor boxellookupCmdExecutor;
	MBBoxelremoveCommandExecutor boxelremoveCmdExecutor;
	MBBoxelinfoCommandExecutor boxelinfoCmdExecutor;
	MBWorldManager worldManager;

	private MultiverseCore mvCore = null;

	public void onEnable() {
		getConfig().set("Version", "0.2");
		saveConfig();

		boxelCmdExecutor = new MBBoxelCommandExecutor(this);
		boxellookupCmdExecutor = new MBBoxellookupCommandExecutor(this);
		boxelremoveCmdExecutor = new MBBoxelremoveCommandExecutor(this);
		boxelinfoCmdExecutor = new MBBoxelinfoCommandExecutor(this);
		getCommand("boxel").setExecutor(boxelCmdExecutor);
		getCommand("boxlookup").setExecutor(boxellookupCmdExecutor);
		getCommand("boxremove").setExecutor(boxelremoveCmdExecutor);
		getCommand("boxinfo").setExecutor(boxelinfoCmdExecutor);

		worldManager = new MBWorldManager(this);

		// @todo: make this functional or delete it
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {
					public void run() {
						worldManager.LoadWorlds();
					}
				}, getConfig().getInt("word-load-delay", 20) * 20);

		getServer().getScheduler().scheduleAsyncRepeatingTask(this,
				new Runnable() {
					public void run() {
						worldManager.CheckForUnusedWorlds();
					}
				}, 60 * 20, getConfig().getInt("world-unload-period", 60) * 20);

		log.info("MonoBoxel enabled!");
	}

	public MultiverseCore GetMVCore() {
		if (mvCore == null) {
			Plugin[] plugins = getServer().getPluginManager().getPlugins();
			Plugin mv = null;
			for (Plugin p : plugins) {
				if (p.toString().contains("Multiverse-Core")) {
					mv = p;
					log.info("Multiverse Core found.");
				}
			}
			if (mv == null) {
				log.info("Multiverse-Core *NOT* found! Is it installed and enabled?");
				return null;
			}

			mvCore = (MultiverseCore) mv;
		}

		return mvCore;
	}

	public void onDisable() {
		saveConfig();
		log.info("MonoBoxel disabled!");
	}

	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new MBBoxelGenerator();
	}

	// returns true if the player has the permission
	public boolean CheckPermCanCreateOwn(Player player) {
		return player.hasPermission("monoboxel.boxel.create.own");
	}

	public boolean CheckPermCanCreateOther(Player player) {
		// same permission as createown for the time
		return player.hasPermission("monoboxel.boxel.create.other");
	}

	public boolean CheckPermCanVisitOwn(Player player) {
		// of the player has permissions to create his own Boxel, he will be
		// able to visit it
		if (player.hasPermission("monoboxel.boxel.create.own"))
			return true;

		if (player.hasPermission("monoboxel.boxel.visit.own"))
			return true;

		if (player.hasPermission("monoboxel.boxel.visit." + player.getName()))
			return true;

		return false;
	}

	public boolean CheckPermCanVisitOther(Player player, String boxelName) {
		// do not check for visit.BOXEL_<name> permissions but for visit.<name>
		if (boxelName.startsWith("BOXEL_"))
			boxelName = boxelName.substring(6);

		if (getConfig().getBoolean("per-boxel-permissions", true))
			return player.hasPermission("monoboxel.boxel.visit." + boxelName);
		else
			return player.hasPermission("monoboxel.boxel.visit.other");
	}

	public void Log(String message) {
		PluginDescriptionFile pdFile = this.getDescription();
		log.info("[" + pdFile.getName() + " " + pdFile.getVersion() + "] "
				+ message);
	}

}
