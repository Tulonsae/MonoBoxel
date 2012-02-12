package com.github.Monofraps.MonoBoxel;


import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelgrpCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelinfoCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxellookupCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelremoveCommandExecutor;
import com.github.Monofraps.MonoBoxel.Config.MBDataConfig;
import com.github.Monofraps.MonoBoxel.EventHooks.MBEventListener;
import com.onarandombox.MultiverseCore.MultiverseCore;


/**
 * Main class of MonoBoxel plugin.
 * 
 * @author Monofraps
 */
public class MonoBoxel extends JavaPlugin {
	
	private MBLogger						logger			= null;
	
	private MBDataConfig					dataConfig		= null;
	
	private MBBoxelCommandExecutor			boxelCmdExecutor;
	private MBBoxelgrpCommandExecutor		boxelgrpCommandExecutor;
	private MBBoxellookupCommandExecutor	boxellookupCmdExecutor;
	private MBBoxelremoveCommandExecutor	boxelremoveCmdExecutor;
	private MBBoxelinfoCommandExecutor		boxelinfoCmdExecutor;
	
	private MultiverseCore					mvCore			= null;
	private String							boxelPrefix		= "BOXEL_";
	private MBBoxelManager					boxelManager	= null;
	
	/**
	 * Hooks up the command executors and initializes the scheduled tasks.
	 */
	public void onEnable() {
		this.logger = new MBLogger("Minecraft", this);
		
		dataConfig = new MBDataConfig(this);
		
		reloadConfig();
		
		if (getConfig().getString("version", "no config") == "no config") {
			saveDefaultConfig();
			reloadConfig();
			getConfig().set("version", getDescription().getVersion());
			saveConfig();
		}
		
		boxelPrefix = getConfig().getString("boxel-prefix", "BOXEL_");
		
		boxelCmdExecutor = new MBBoxelCommandExecutor(this);
		boxelgrpCommandExecutor = new MBBoxelgrpCommandExecutor(this);
		boxellookupCmdExecutor = new MBBoxellookupCommandExecutor(this);
		boxelremoveCmdExecutor = new MBBoxelremoveCommandExecutor(this);
		boxelinfoCmdExecutor = new MBBoxelinfoCommandExecutor(this);
		getCommand("boxel").setExecutor(boxelCmdExecutor);
		getCommand("boxelgrp").setExecutor(boxelgrpCommandExecutor);
		getCommand("boxlookup").setExecutor(boxellookupCmdExecutor);
		getCommand("boxremove").setExecutor(boxelremoveCmdExecutor);
		getCommand("boxinfo").setExecutor(boxelinfoCmdExecutor);
		
		new MBEventListener(this);
		
		boxelManager = new MBBoxelManager(this);
		
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {
					
					public void run() {
						boxelManager.LoadConfig();
					}
				}, getConfig().getInt("word-load-delay", 20) * 20);
		
		logger.info("MonoBoxel enabled!");
		logger.debugLog(Level.INFO, "Plugin loaded.");
	}
	
	/**
	 * Tries to get the MultiverseCore instance.
	 * 
	 * @return The MultiverCore or null on failure.
	 */
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
	
	/**
	 * onDisable hook for Bukkit.
	 */
	public void onDisable() {
		saveConfig();
		boxelManager.SaveBoxels();
		logger.info("MonoBoxel disabled!");
	}
	
	/**
	 * 
	 * @return The MonoBoxel chunk generator
	 */
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new MBBoxelGenerator(getConfig().getLong("max-boxel-size", 16));
	}
	
	/**
	 * Checks if the player has permissions to create his own Boxel.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanCreateOwn(Player player) {
		return player.hasPermission("monoboxel.boxel.create.own");
	}
	
	/**
	 * Checks if the player has permissions to create other players Boxels.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanCreateOther(Player player) {
		return player.hasPermission("monoboxel.boxel.create.other");
	}
	
	/**
	 * Checks if the player has permissions visit his own Boxel.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
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
	
	/**
	 * Checks if the player has permissions to visit other players Boxels.
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanVisitOther(Player player, String boxelName) {
		// do not check for visit.BOXEL_<name> permissions but for visit.<name>
		if (boxelName.startsWith(getBoxelPrefix()))
			boxelName = boxelName.substring(getBoxelPrefix().length());
		
		if (getConfig().getBoolean("per-boxel-permissions", true))
			return player.hasPermission("monoboxel.boxel.visit." + boxelName);
		else
			return player.hasPermission("monoboxel.boxel.visit.other");
	}
	
	/**
	 * 
	 * @return The Boxel prefix
	 */
	public String getBoxelPrefix() {
		return boxelPrefix;
	}
	
	/**
	 * 
	 * @return THe BoxelManager
	 */
	public MBBoxelManager getMBWorldManager() {
		return boxelManager;
	}
	
	/**
	 * 
	 * @return The LogManager
	 */
	public MBLogger getLogManager() {
		return logger;
	}
	
	/**
	 * 
	 * @return The data config
	 */
	public MBDataConfig getDataConfig() {
		return dataConfig;
	}
}
