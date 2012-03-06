package com.Monofraps.MonoBoxel.Config;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Generic Configuration Handler.
 * Can load any correctly formatted yml from the plugin's data folder.
 * 
 * @author MikeMatrix
 */
public class MBConfiguration {
	
	private MonoBoxel			master			= null;
	
	private FileConfiguration	config			= null;
	private File				configFile		= null;
	private String				configFileName	= null;
	
	public MBConfiguration(MonoBoxel plugin, String fileName) {
	
		master = plugin;
		this.configFileName = fileName;
	}
	
	/**
	 * Reload Configuration from File.
	 */
	public void reloadConfig() {
	
		if (configFile == null) {
			configFile = new File(master.getDataFolder(), configFileName);
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		
		if (configFileName.equals("localization.yml")) {
			InputStream defDataConfigStream = master.getResource("localization.yml");
			if (defDataConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defDataConfigStream);
				
				try {
					defConfig.save(configFile);
					config.load(configFile);
					config.save(configFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @return the DataConfig
	 */
	public FileConfiguration getConfig() {
	
		if (config == null) {
			reloadConfig();
		}
		return config;
	}
	
	/**
	 * Save the Configuration to the Disk.
	 */
	public void saveConfig() {
	
		if (config == null || configFile == null)
			return;
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			master.getLogManager().severe(
					"Could not save Data Configuration File to " + configFile);
		}
	}
}
