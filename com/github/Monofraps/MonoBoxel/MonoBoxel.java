package com.github.Monofraps.MonoBoxel;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MonoBoxel extends JavaPlugin {

	MBLogger logger = new MBLogger("Minecraft", this);	
	MBBoxelCommandExecutor boxelCmdExecutor;
	MBBoxellookupCommandExecutor boxellookupCmdExecutor;
	MBBoxelremoveCommandExecutor boxelremoveCmdExecutor;
	MBBoxelinfoCommandExecutor boxelinfoCmdExecutor;
	MBWorldManager worldManager;

	private MultiverseCore mvCore = null;
	private Logger log = Logger.getLogger("Minecraft");

	public void onEnable() {
		// missuse the version to find out if this is the first run
		String versionInfo = getConfig().getString("Version", "first run");
		if(versionInfo.equals("first run"))
			saveDefaultConfig();
		
		// we may use the version for incompatibility checks later
		getConfig().set("Version", getDescription().getVersion());
		//saveConfig();

		boxelCmdExecutor = new MBBoxelCommandExecutor(this);
		boxellookupCmdExecutor = new MBBoxellookupCommandExecutor(this);
		boxelremoveCmdExecutor = new MBBoxelremoveCommandExecutor(this);
		boxelinfoCmdExecutor = new MBBoxelinfoCommandExecutor(this);
		getCommand("boxel").setExecutor(boxelCmdExecutor);
		getCommand("boxlookup").setExecutor(boxellookupCmdExecutor);
		getCommand("boxremove").setExecutor(boxelremoveCmdExecutor);
		getCommand("boxinfo").setExecutor(boxelinfoCmdExecutor);

		worldManager = new MBWorldManager(this);

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

		logger.info("MonoBoxel enabled!");
	}

	public MultiverseCore GetMVCore() {
		if (mvCore == null) {
			Plugin[] plugins = getServer().getPluginManager().getPlugins();
			Plugin mv = null;
			for (Plugin p : plugins) {
				if (p.toString().contains("Multiverse-Core")) {
					mv = p;
					logger.info("Multiverse Core found.");
				}
			}
			if (mv == null) {
				logger.info("Multiverse-Core *NOT* found! Is it installed and enabled?");
				return null;
			}

			mvCore = (MultiverseCore) mv;
		}

		return mvCore;
	}

	public void onDisable() {
		saveConfig();
		logger.info("MonoBoxel disabled!");
	}

	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new MBBoxelGenerator(getConfig().getLong("max-boxel-size", 5));
	}

	// returns true if the player has the permission
	public boolean CheckPermCanCreateOwn(Player player) {
		return player.hasPermission("monoboxel.boxel.create.own");
	}

	public boolean CheckPermCanCreateOther(Player player) {
		return player.hasPermission("monoboxel.boxel.create.other");
	}

	public boolean CheckPermCanVisitOwn(Player player) {
		// if the player has permissions to create his own Boxel, he will also
		// be able to visit it
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
}
