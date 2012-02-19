package com.Monofraps.MonoBoxel;


import java.util.logging.Level;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.Monofraps.MonoBoxel.Adventure.MBAdventureWorldManager;
import com.Monofraps.MonoBoxel.CommandExecutors.MBAdventureCommandExecutor;
import com.Monofraps.MonoBoxel.CommandExecutors.MBBoxelCommandExecutor;
import com.Monofraps.MonoBoxel.CommandExecutors.MBBoxelgrpCommandExecutor;
import com.Monofraps.MonoBoxel.CommandExecutors.MBBoxelinfoCommandExecutor;
import com.Monofraps.MonoBoxel.CommandExecutors.MBBoxellookupCommandExecutor;
import com.Monofraps.MonoBoxel.CommandExecutors.MBBoxelremoveCommandExecutor;
import com.Monofraps.MonoBoxel.Config.MBConfiguration;
import com.Monofraps.MonoBoxel.EventHooks.MBEventListener;
import com.Monofraps.MonoBoxel.Utils.LocalizationManager;
import com.onarandombox.MultiverseCore.MultiverseCore;


/**
 * Main class of MonoBoxel plugin.
 * 
 * @author Monofraps
 */
public class MonoBoxel extends JavaPlugin {
	
	private MBLogger						logger					= null;
	
	private LocalizationManager				localizationManager		= null;
	private MBConfiguration					dataConfig				= null;
	
	private MBBoxelCommandExecutor			boxelCmdExecutor		= null;
	private MBBoxelgrpCommandExecutor		boxelgrpCommandExecutor	= null;
	private MBBoxellookupCommandExecutor	boxellookupCmdExecutor	= null;
	private MBBoxelremoveCommandExecutor	boxelremoveCmdExecutor	= null;
	private MBBoxelinfoCommandExecutor		boxelinfoCmdExecutor	= null;
	private MBAdventureCommandExecutor		adventureCmdExecutor	= null;
	
	private MultiverseCore					mvCore					= null;
	private MBBoxelManager					boxelManager			= null;
	private MBAdventureWorldManager			adventureManager		= null;
	private MBPermissionManager				boxelPermManager		= null;
	private String							boxelPrefix				= "BOXEL_";
	private String							boxelGroupPrefix		= "BOXELGRP_";
	
	/**
	 * Hooks up the command executors and initializes the scheduled tasks.
	 */
	public void onEnable() {
	
		logger = new MBLogger("Minecraft", this);
		
		localizationManager = new LocalizationManager(this);
		
		dataConfig = new MBConfiguration(this, "data.yml");
		
		reloadConfig();
		
		boxelPrefix = getConfig().getString("boxel-prefix");
		boxelGroupPrefix = getConfig().getString("boxelgroup-prefix");
		
		boxelCmdExecutor = new MBBoxelCommandExecutor(this);
		boxelgrpCommandExecutor = new MBBoxelgrpCommandExecutor(this);
		boxellookupCmdExecutor = new MBBoxellookupCommandExecutor(this);
		boxelremoveCmdExecutor = new MBBoxelremoveCommandExecutor(this);
		boxelinfoCmdExecutor = new MBBoxelinfoCommandExecutor(this);
		adventureCmdExecutor = new MBAdventureCommandExecutor(this);
		getCommand("boxel").setExecutor(boxelCmdExecutor);
		getCommand("boxelgrp").setExecutor(boxelgrpCommandExecutor);
		getCommand("boxlookup").setExecutor(boxellookupCmdExecutor);
		getCommand("boxremove").setExecutor(boxelremoveCmdExecutor);
		getCommand("boxinfo").setExecutor(boxelinfoCmdExecutor);
		getCommand("adventure").setExecutor(adventureCmdExecutor);
		
		new MBEventListener(this);
		
		boxelManager = new MBBoxelManager(this);
		adventureManager = new MBAdventureWorldManager(this);
		boxelPermManager = new MBPermissionManager(this);
		
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {
					
					public void run() {
					
						boxelManager.LoadConfig();
					}
				}, getConfig().getInt("word-load-delay") * 20);
		
		logger.info("MonoBoxel enabled!");
		logger.debugLog(Level.INFO, "Plugin loaded.");
	}
	
	/**
	 * Tries to get the MultiverseCore instance.
	 * 
	 * @return The MultiverCore or null on failure.
	 */
	public MultiverseCore getMVCore() {
	
		if (mvCore == null) {
			mvCore = (MultiverseCore) getServer().getPluginManager().getPlugin(
					"Multiverse-Core");
			
			if (mvCore == null) {
				logger.severe("Multiverse-Core *NOT* found! Is it installed and enabled?");
				logger.debugLog(Level.SEVERE, "Multiverse Core not found!");
				return null;
			} else
				logger.info("Multiverse-Core found.");
			
			mvCore.incrementPluginCount();
		}
		
		return mvCore;
	}
	
	/**
	 * onDisable hook for Bukkit.
	 */
	public void onDisable() {
	
		saveConfig();
		dataConfig.saveConfig();
		boxelManager.SaveBoxels();
		localizationManager.SaveLocalization();		
		
		if (mvCore != null)
			mvCore.decrementPluginCount();
		
		logger.info("MonoBoxel disabled!");
		logger.debugLog(Level.INFO, "Plugin unloaded.");
	}
	
	/**
	 * 
	 * @return The MonoBoxel chunk generator
	 */
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
	
		if (id != null)
			return new MBBoxelGenerator(getConfig().getLong("max-boxel-size"),
					id.equals("ssehc") ? true : false);
		
		else
			return new MBBoxelGenerator(getConfig().getLong("max-boxel-size"),
					false);
	}
	
	/**
	 * @return The Permission Manager
	 */
	public MBPermissionManager getPermissionManager() {
	
		return boxelPermManager;
	}
	
	/**
	 * @return The Localization Manager
	 */
	public LocalizationManager getLocalizationManager() {
	
		return localizationManager;
	}
	
	/**
	 * 
	 * @return The Boxel prefix
	 */
	public String getBoxelPrefix() {
	
		return boxelPrefix;
	}
	
	/**
	 * @return The Boxel Group prefix
	 */
	public String getBoxelGroupPrefix() {
	
		return boxelGroupPrefix;
	}
	
	/**
	 * 
	 * @return The BoxelManager
	 */
	public MBBoxelManager getMBWorldManager() {
	
		return boxelManager;
	}
	
	/**
	 * 
	 * @return the adventure manager
	 */
	public MBAdventureWorldManager getMBAdventureManager() {
	
		return adventureManager;
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
	 * @return The data configuration
	 */
	public MBConfiguration getDataConfig() {
	
		return dataConfig;
	}
}
