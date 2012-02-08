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
	MBWorldManager worldManager;

	private MultiverseCore mvCore = null;

	public void onEnable() {
		getConfig().set("Version", "0.1");

		boxelCmdExecutor = new MBBoxelCommandExecutor(this);
		boxellookupCmdExecutor = new MBBoxellookupCommandExecutor(this);
		boxelremoveCmdExecutor = new MBBoxelremoveCommandExecutor(this);
		getCommand("boxel").setExecutor(boxelCmdExecutor);
		getCommand("boxlookup").setExecutor(boxellookupCmdExecutor);
		getCommand("boxremove").setExecutor(boxelremoveCmdExecutor);

		worldManager = new MBWorldManager(this);
		
		log.info("MonoBoxel enabled!");
	}

	public MultiverseCore GetMVCore() {
		if (mvCore == null) {
			Plugin[] plugins = getServer().getPluginManager().getPlugins();
			Plugin mv = null;
			for (Plugin p : plugins) {
				if (p.toString().contains("Multiverse")) {
					mv = p;
					log.info("Multiverse Core found.");
				}
			}
			if (mv == null) {
				log.info("Multiver-Core *NOT* found! Is it installed and enabled?");
				return null;
			}

			mvCore = (MultiverseCore) mv;
		}
		
		return mvCore;
	}

	public void onDisable() {
		log.info("MonoBoxel disabled!");
	}

	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new MBBoxelGenerator();
	}

}
