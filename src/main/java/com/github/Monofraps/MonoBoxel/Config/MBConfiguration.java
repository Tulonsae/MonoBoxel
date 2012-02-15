package com.github.Monofraps.MonoBoxel.Config;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.Monofraps.MonoBoxel.MonoBoxel;


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
		
		InputStream defDataConfigStream = master.getResource(configFileName);
		if (defDataConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defDataConfigStream);
			config.setDefaults(defConfig);
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
