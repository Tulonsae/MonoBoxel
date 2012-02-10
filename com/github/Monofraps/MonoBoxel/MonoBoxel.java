package com.github.Monofraps.MonoBoxel;

import java.util.logging.Logger;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

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
		
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				worldManager.LoadWorlds();
			}
		}, getConfig().getInt("word-load-delay", 20) * 20);
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run()
			{
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

}
